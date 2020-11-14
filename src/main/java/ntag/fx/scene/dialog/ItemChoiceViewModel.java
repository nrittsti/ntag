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
package ntag.fx.scene.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.StringConverter;

public class ItemChoiceViewModel<T> {

    // ***
    //
    // Properties
    //
    // ***

    // *** Items

    private final ObservableList<T> items = FXCollections.observableArrayList();

    public ObservableList<T> getItems() {
        return items;
    }

    // *** Filtered Items

    private final FilteredList<T> filteredItems;

    public FilteredList<T> getFilteredItems() {
        return filteredItems;
    }

    // *** Favorites

    private final ObservableList<T> favorites = FXCollections.observableArrayList();

    public ObservableList<T> getFavorites() {
        return favorites;
    }

    // *** Selection

    private final ObservableList<T> selection = FXCollections.observableArrayList();

    public ObservableList<T> getSelection() {
        return selection;
    }

    // *** SingleSelection

    private boolean singleSelection = false;

    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }

    // *** String Converter

    private StringConverter<T> stringConverter = null;

    public StringConverter<T> getStringConverter() {
        return stringConverter;
    }

    public void setStringConverter(StringConverter<T> stringConverter) {
        this.stringConverter = stringConverter;
    }

    // ***
    //
    // Construction
    //
    // ***

    public ItemChoiceViewModel() {
        filteredItems = new FilteredList<>(items);
    }
}
