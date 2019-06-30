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
package ntag.fx.scene.control.tableview;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import ntag.fx.scene.control.tableview.TagFileTableColumn.ColumnType;

public class TagFileTableHeaderMenu extends ContextMenu {

    private ObservableList<TagFileTableColumn> columns;

    public TagFileTableHeaderMenu(ObservableList<TagFileTableColumn> columns) {
        super();
        this.columns = columns;
        this.setOnShown((WindowEvent) -> {
            updateSelectedItems();
        });
        EventHandler<ActionEvent> actionHandler = (ActionEvent event) -> {
            MenuItem menuItem = (MenuItem) event.getSource();
            ColumnType type = (ColumnType) menuItem.getUserData();
            handleColumnAction(type);
        };
        for (ColumnType type : ColumnType.getSortedValues()) {
            CheckMenuItem checkItem = new CheckMenuItem();
            checkItem.setUserData(type);
            checkItem.setText(type.getLabel());
            checkItem.setOnAction(actionHandler);
            this.getItems().add(checkItem);
        }
    }

    private void handleColumnAction(ColumnType type) {
        TagFileTableColumn column = getColumn(type);
        if (column == null) {
            column = new TagFileTableColumn(type);
            column.setContextMenu(this);
            columns.add(column);
        } else {
            columns.remove(column);
        }
    }

    private void updateSelectedItems() {
        for (MenuItem item : this.getItems()) {
            CheckMenuItem checkItem = (CheckMenuItem) item;
            ColumnType type = (ColumnType) item.getUserData();
            checkItem.setSelected(getColumn(type) != null);
        }
    }

    private TagFileTableColumn getColumn(ColumnType userDate) {
        for (TagFileTableColumn column : columns) {
            if (column.getType() == userDate) {
                return column;
            }
        }
        return null;
    }
}
