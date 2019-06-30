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
 * Copyright 2016, Nico Rittstieg
 */
package toolbox.fx.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableView;

public class EnhancedTableView<T> extends TableView<T> {

    // *** Current Drag Index

    private final IntegerProperty dropIndex = new SimpleIntegerProperty(this, "dropIndex", -1);

    public IntegerProperty dropIndexProperty() {
        return dropIndex;
    }

    public int getDropIndex() {
        return dropIndex.getValue();
    }

    public void setDropIndex(int index) {
        dropIndex.set(index);
    }
}
