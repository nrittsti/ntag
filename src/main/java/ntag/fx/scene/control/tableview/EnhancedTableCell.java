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
package ntag.fx.scene.control.tableview;

import javafx.scene.control.TableCell;

public class EnhancedTableCell<S, T> extends TableCell<S, T> {
  public EnhancedTableCell() {
    super();
    this.setOnDragEntered(event -> {
      EnhancedTableView<S> tableView = (EnhancedTableView<S>) getTableView();
      if (this.getIndex() < tableView.getItems().size()) {
        tableView.setDropIndex(this.getIndex());
      }
    });
    this.setOnDragExited(event -> {
      EnhancedTableView<S> tableView = (EnhancedTableView<S>) getTableView();
      tableView.setDropIndex(-1);
    });
  }

  @Override
  protected void updateItem(T value, boolean empty) {
    super.updateItem(value, empty);
    if (value == null) {
      setText("");
    } else {
      setText(value.toString());
    }
  }
}
