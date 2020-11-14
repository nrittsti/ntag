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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableView;

public class EnhancedTableView<T> extends TableView<T> {

  // *** Current Drag Index

  private final IntegerProperty dropIndex = new SimpleIntegerProperty(this, "dropIndex", -1);

  @SuppressWarnings("unused")
  public IntegerProperty dropIndexProperty() {
    return dropIndex;
  }

  @SuppressWarnings("unused")
  public int getDropIndex() {
    return dropIndex.getValue();
  }

  public void setDropIndex(int index) {
    dropIndex.set(index);
  }
}
