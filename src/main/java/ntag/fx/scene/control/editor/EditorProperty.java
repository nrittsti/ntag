/**
 * This file is part of NTag (audio file tag editor).
 *
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016, Nico Rittstieg
 */
package ntag.fx.scene.control.editor;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import ntag.model.TagFile;
import toolbox.io.Resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorProperty<T> {

	private static final Logger LOGGER = Logger.getLogger(EditorProperty.class.getName());

	private static final String DIFF_STYLE = "-fx-effect: dropshadow( three-pass-box ,skyblue , 8, 0.0 , 0 , 0 );";
	private static final String CLR_STYLE = "-fx-effect: dropshadow( three-pass-box ,crimson , 8, 0.0 , 0 , 0 );";

	// ***
	//
	// Instance Attribute
	//
	// ***

	private final EmptyCheck<T> emptyCheck;
	private final String name;
	private final Method setter;
	private final Method getter;
	private final EventHandler<ActionEvent> clearEventHandler;
	private boolean cleared = false;
	private List<TagFile> objects = null;
	private boolean valueChangeListenerEnabled = true;

	// ***
	//
	// Properties
	//
	// ***

	// *** changedObjects

	private static ObservableList<TagFile> changedObjects = FXCollections.observableArrayList();

	public static ObservableList<TagFile> getChangedObjects() {
		return changedObjects;
	}

	// *** Value

	private ObjectProperty<T> valueProp = new SimpleObjectProperty<>(this, "value", null);

	public ObjectProperty<T> valueProperty() {
		return valueProp;
	}

	public T getValue() {
		return valueProp.get();
	}

	public void setValue(T value) {
		valueProp.set(value);
	}

	// *** Style

	private StringProperty styleProp = new SimpleStringProperty(this, "");

	public StringProperty styleProperty() {
		return styleProp;
	}

	public String getStyle() {
		return styleProp.get();
	}

	public void setStyle(String value) {
		styleProp.set(value);
	}

	// *** Different

	private BooleanProperty differentProp = new SimpleBooleanProperty(this, "different", false);

	public BooleanProperty differentProperty() {
		return differentProp;
	}

	public boolean isDifferent() {
		return differentProp.get();
	}

	public void setDifferent(boolean value) {
		differentProp.set(value);
	}

	// *** Values

	private ObservableList<T> values = FXCollections.observableArrayList();

	public ObservableList<T> getValues() {
		return values;
	}

	// *** Tooltip

	private ObjectProperty<Tooltip> tooltipProp = new SimpleObjectProperty<>(this, null);

	public ObjectProperty<Tooltip> tooltipProperty() {
		return tooltipProp;
	}

	public Tooltip getTooltip() {
		return tooltipProp.get();
	}

	protected void setTooltip(Tooltip info) {
		tooltipProp.set(info);
	}

	// ***
	//
	// Construction
	//
	// ***

	public EditorProperty(String name, EmptyCheck<T> emptyCheck) throws NoSuchMethodException, SecurityException {
		super();
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("a valid property name is required");
		}
		this.name = name;
		this.emptyCheck = emptyCheck;

		Method[] methods = TagFile.class.getMethods();

		Method method = findMethod(methods, "get" + name);
		if (method == null) {
			method = findMethod(methods, "is" + name);
		}
		getter = method;
		setter = findMethod(methods, "set" + name);

		valueProperty().addListener((ObservableValue<? extends T> observable, T oldValue, T newValue) -> {
			try {
				if (valueChangeListenerEnabled) {
					commit(newValue);
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, String.format("Cannot set new value to field %s", getName()), e);
			}
		});

		clearEventHandler = (ActionEvent) -> {
			cleared = true;
			setValue(null);
			setStyle(CLR_STYLE);
		};

		changedObjects.addListener((Change<? extends TagFile> change) -> {
			if (change.getList().size() == 0 && !cleared) {
				setStyle(values.size() > 1 ? DIFF_STYLE : "");
			}
		});
	}

	// ***
	//
	// public API
	//
	// ***

	public String getName() {
		return name;
	}

	public Method getSetter() {
		return setter;
	}

	public Method getGetter() {
		return getter;
	}

	public EventHandler<ActionEvent> getClearEventHandler() {
		return clearEventHandler;
	}

	@SuppressWarnings("unchecked")
	public void setObjects(List<TagFile> objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		valueChangeListenerEnabled = false;
		this.objects = objects;
		if (objects == null || objects.size() == 0) {
			setValue(null);
			setDifferent(false);
			setStyle("");
			values.clear();
		} else {
			SortedSet<T> valueSet = new TreeSet<>();
			for (TagFile object : objects) {
				T value = (T) getGetter().invoke(object);
				if (!isNullOrEmpty(value)) {
					valueSet.add(value);
				}
			}
			setDifferent(valueSet.size() > 1);
			setValue(valueSet.size() != 1 ? null : valueSet.first());
			setStyle(valueSet.size() > 1 ? DIFF_STYLE : "");
			values.setAll(valueSet);
		}
		createTooltip();
		valueChangeListenerEnabled = true;
	}

	public List<TagFile> getObjects() {
		return this.objects;
	}

	@Override
	public String toString() {
		return name;
	}

	// ***
	//
	// hidden implementation
	//
	// ***

	protected void createTooltip() {
		if (values.size() < 2) {
			setTooltip(null);
		} else {
			setTooltip(new Tooltip(Resources.format("ntag", "differences", values.size())));
		}
	}

	@SuppressWarnings("unchecked")
	protected void commit(T newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (objects == null || objects.isEmpty()) {
			return;
		}
		if (newValue != null) {
			cleared = false;
		}
		if (!isNullOrEmpty(newValue) || cleared) {
			setDifferent(false);
			for (TagFile object : objects) {
				if (object.isReadOnly()) {
					continue;
				}
				T oldValue = (T) getGetter().invoke(object);
				if (oldValue == null && getValue() == null) {
					continue;
				}
				if (oldValue == null || !oldValue.equals(newValue)) {
					getSetter().invoke(object, newValue);
					if (!changedObjects.contains(object)) {
						changedObjects.add(object);
						object.setDirty(true);
					}
				}
			}
			if (!cleared) {
				setStyle(values.size() > 1 ? DIFF_STYLE : "");
			}
		}
	}

	protected Method findMethod(Method[] methods, String name) {
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(name)) {
				return method;
			}
		}
		return null;
	}

	protected boolean isNullOrEmpty(T value) {
		if (emptyCheck == null) {
			return value == null;
		} else {
			return emptyCheck.isNullOrEmpty(value);
		}
	}
}
