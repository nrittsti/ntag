/**
 * This file is part of NTagDB (tag-based database for audio files).
 * <p>
 * NTagDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTagDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTagDB.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2017, Nico Rittstieg
 */
package ntag.fx.util;

import javafx.scene.control.ListCell;
import org.jaudiotagger.tag.TagField;


public class TagFieldListCell extends ListCell<TagField> {
    @Override
    public void updateItem(TagField item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setText(createDescription(item));
        } else {
            setText("");
        }
    }

    public static String createDescription(TagField item) {
        String result = TagFieldInputDialogs.fieldDescMap.get(item.getId());
        return result == null ? item.getId() : result;
    }
}