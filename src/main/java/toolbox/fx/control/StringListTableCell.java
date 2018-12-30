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
package toolbox.fx.control;

import java.util.List;

public class StringListTableCell extends EnhancedTableCell<Object, List<String>> {
	@Override
	protected void updateItem(List<String> item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || item.isEmpty()) {
			setText("");
		} else if (item.size() == 1) {
			setText(item.get(0));
		} else {
			setText(String.format("%s (+%d)", item.get(0), item.size() - 1));
		}
	}
}
