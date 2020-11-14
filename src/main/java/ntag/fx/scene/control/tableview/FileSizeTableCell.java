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

import ntag.fx.util.NTagFormat;
import ntag.io.NTagProperties;

public class FileSizeTableCell extends EnhancedTableCell<Object, Long> {

  @Override
  protected void updateItem(Long value, boolean empty) {
    super.updateItem(value, empty);
    boolean binaryUnit = NTagProperties.instance().isBinaryUnit();
    setText(value != null ? NTagFormat.fileSize(value, binaryUnit) : "");
  }
}
