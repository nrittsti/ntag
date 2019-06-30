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
package toolbox.fx.collections;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.lang.ref.WeakReference;

public class ObservableListLink<T> implements ListChangeListener<T> {

    private final WeakReference<ObservableList<T>> listA;
    private final WeakReference<ObservableList<T>> listB;

    public ObservableListLink(ObservableList<T> listA, ObservableList<T> listB) {
        super();
        this.listA = new WeakReference<ObservableList<T>>(listA);
        this.listB = new WeakReference<ObservableList<T>>(listB);

        listB.clear();
        listB.addAll(listA);

        listA.addListener(this);
        listB.addListener(this);
    }

    @Override
    public void onChanged(Change<? extends T> change) {
        ObservableList<T> source;
        ObservableList<T> target;
        if (change.getList() == listA.get()) {
            source = listA.get();
            target = listB.get();
        } else {
            source = listB.get();
            target = listA.get();
        }
        target.removeListener(this);
        while (change.next()) {
            if (change.wasRemoved()) {
                target.remove(change.getFrom(), change.getFrom() + change.getRemovedSize());
            }
            if (change.wasAdded()) {
                target.addAll(change.getFrom(), change.getAddedSubList());
            }
            if (change.wasPermutated()) {
                target.clear();
                target.setAll(source);
            }
        }
        target.addListener(this);
    }

    public ObservableList<T> getListA() {
        return listA.get();
    }

    public ObservableList<T> getListB() {
        return listB.get();
    }
}
