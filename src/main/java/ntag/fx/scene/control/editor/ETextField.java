/**
 * This file is part of NTag (audio file tag editor).
 * <p>
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2016, Nico Rittstieg
 */
package ntag.fx.scene.control.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ntag.io.Resources;

public class ETextField extends HBox {

    private final TextField textField;
    private final Button button;
    private EventHandler<ActionEvent> clearEventHandler = null;

    public ETextField() {
        this.setSpacing(3);
        textField = new TextField();
        textField.setMinWidth(50);
        textField.setPrefWidth(100);
        setHgrow(textField, Priority.ALWAYS);
        this.getChildren().add(textField);
        button = new Button();
        button.setOnAction((ActionEvent event) -> {
            if (clearEventHandler != null) {
                clearEventHandler.handle(event);
            }
        });
        button.setText("X");
        button.setMinSize(24, 24);
        button.setPadding(new Insets(2, 2, 2, 2));
        button.setTooltip(new Tooltip(Resources.get("ntag", "tip_delete_tag")));
        this.getChildren().add(button);
    }

    public TextField getTextField() {
        return textField;
    }

    public EventHandler<ActionEvent> getClearEventHandler() {
        return clearEventHandler;
    }

    public void setClearEventHandler(EventHandler<ActionEvent> clearEventHandler) {
        this.clearEventHandler = clearEventHandler;
    }
}
