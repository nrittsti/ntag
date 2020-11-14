/*
 *   This file is part of NTag (audio file tag editor).
 *
 *   NTag is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   NTag is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Copyright 2020, Nico Rittstieg
 *
 */
package ntag.fx.scene.control.editor;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import ntag.io.Resources;

public class EComboBox<T> extends HBox {

  private final ComboBox<T> comboBox;
  private final Button button;
  private EventHandler<ActionEvent> clearEventHandler = null;

  public EComboBox() {
    this.setSpacing(3);
    comboBox = new ComboBox<>();
    comboBox.setMaxWidth(Double.MAX_VALUE);
    comboBox.setMinWidth(50);
    comboBox.setPrefWidth(100);
    comboBox.setEditable(true);
    // Maybe a bug in Java Version "1.8.0_66" --> The value property is
    // updated only when i press enter
    comboBox.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) -> {
      if (!newValue) {
        StringConverter<T> converter = comboBox.getConverter();
        T value;
        if (converter != null) {
          value = converter.fromString(comboBox.getEditor().getText().trim());
        } else {
          value = getEditorValue();
        }
        comboBox.setValue(value);
      }
    });
    setHgrow(comboBox, Priority.ALWAYS);
    this.getChildren().add(comboBox);
    button = new Button();
    button.setOnAction((ActionEvent event) -> {
      if (clearEventHandler != null) {
        clearEventHandler.handle(event);
      }
    });
    button.setPadding(new Insets(2, 2, 2, 2));
    button.setTooltip(new Tooltip(Resources.get("ntag", "tip_delete_tag")));
    button.setText("X");
    button.setMinSize(24, 24);
    this.getChildren().add(button);
  }

  public ComboBox<T> getComboBox() {
    return comboBox;
  }

  public Button getButton() {
    return button;
  }

  public EventHandler<ActionEvent> getClearEventHandler() {
    return clearEventHandler;
  }

  public void setClearEventHandler(EventHandler<ActionEvent> clearEventHandler) {
    this.clearEventHandler = clearEventHandler;
  }

  @SuppressWarnings("unchecked")
  private T getEditorValue() {
    return (T) comboBox.getEditor().getText();
  }
}
