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
package ntag.fx.scene.control.rating;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class RatingControl extends Control {

	// ***
	//
	// Constants
	//
	// ***

	public final static int STEP = 20;
	public final static int MAXIMUM = 200;

	// ***
	//
	// Instance Attributes
	//
	// ***

	private HBox container = new HBox();

	// ***
	//
	// Properties
	//
	// ***

	// *** Value

	private ObjectProperty<Integer> valueProp = new SimpleObjectProperty<Integer>(this, "value", 0);

	public ObjectProperty<Integer> valueProperty() {
		return valueProp;
	}

	int getValue() {
		return valueProp.get();
	}

	void setValue(Integer value) {
		valueProp.set(value);
	}

	// *** Rating

	private ObjectProperty<Integer> ratingProp = new SimpleObjectProperty<Integer>(this, "rating", 0);

	public ObjectProperty<Integer> ratingProperty() {
		return ratingProp;
	}

	public int getRating() {
		return ratingProp.get();
	}

	public void setRating(int rating) {
		ratingProp.set(rating);
	}

	// *** HoverValue

	private ObjectProperty<Integer> hoverValueProp = new SimpleObjectProperty<Integer>(this, "hoverValueProp", 0);

	ObjectProperty<Integer> hoverValueProperty() {
		return hoverValueProp;
	}

	int getHoverValue() {
		return hoverValueProp.get();
	}

	void setHoverValue(int value) {
		hoverValueProp.set(value);
	}

	// ***
	//
	// Construction
	//
	// ***

	public RatingControl() {
		getStyleClass().add("rating-control");
		setPrefSize(80, 16);
		setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		getChildren().setAll(container);
		for (int i = 0; i < 5; i++) {
			Region region = new Region();
			region.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			region.setPrefSize(16, 16);
			region.getStyleClass().add("star-empty");
			container.getChildren().add(region);
		}
		valueProp.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			setHoverValue(getValue());
		});
		hoverValueProp.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			updateStars();
		});
		ratingProp.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			if (newValue == null || newValue.intValue() < 1) {
				setRating(0);
			} else if (newValue.intValue() > 10) {
				setRating(100);
			}
			setValue(getRating() * STEP);
		});
		setOnMouseClicked(this::mouseClicked);
		setOnMouseMoved(this::mouseMoved);
		setOnMouseExited(this::mouseExitedIntern);
	}

	// ***
	//
	// hidden implementation
	//
	// ***

	private void updateStars() {
		int value = getHoverValue();
		int starWeight = STEP * 2;
		for (int i = 0; i < 5; i++) {
			final Region region = (Region) container.getChildren().get(i);
			if (value >= starWeight) {
				region.getStyleClass().setAll("star", "star-full");
			} else if (value > 0) {
				region.getStyleClass().setAll("star", "star-half");
			} else {
				region.getStyleClass().setAll("star", "star-empty");
			}
			value = value - starWeight;
		}
	}

	// ***
	//
	// public API
	//
	// ***

	@Override
	public String getUserAgentStylesheet() {
		return RatingControl.class.getResource("/rating.css").toExternalForm();
	}

	// ***
	//
	// Event Handling
	//
	// ***

	private void mouseClicked(MouseEvent event) {
		event.consume();
		if (isDisabled() || !isVisible()) {
			return;
		}
		int selection = calcSelection(event);
		if (getValue() != selection) {
			setValue(selection);
		}
		setRating(getValue() / RatingControl.STEP);
	}

	private void mouseMoved(MouseEvent event) {
		if (isDisabled() || !isVisible()) {
			return;
		}
		int selection = calcSelection(event);
		if (selection != getHoverValue()) {
			setHoverValue(selection);
		}
	}

	private void mouseExitedIntern(MouseEvent e) {
		if (isDisabled() || !isVisible()) {
			return;
		}
		if (getHoverValue() != getValue()) {
			setHoverValue(getValue());
		}
	}

	private int calcSelection(MouseEvent event) {
		int w = 16 * 5;
		int x = Math.min((int) event.getX(), w);
		int value = (int) ((x / (float) w) * RatingControl.MAXIMUM);
		if (value < 15) {
			value = 0;
		} else {
			value = value + (RatingControl.STEP - (value % RatingControl.STEP));
			value = value >= RatingControl.MAXIMUM ? RatingControl.MAXIMUM : value;
		}
		return value;
	}
}
