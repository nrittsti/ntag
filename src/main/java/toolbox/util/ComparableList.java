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
package toolbox.util;

import java.util.ArrayList;
import java.util.Collection;

public class ComparableList<T extends Comparable<T>> extends ArrayList<T> implements Comparable<ComparableList<T>> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ComparableList() {
        super();
    }

    public ComparableList(T value) {
        this.add(value);
    }

    public ComparableList(Collection<? extends T> c) {
        super(c);
    }

    @Override
    public int compareTo(ComparableList<T> o) {
        if (o == null) {
            return 1;
        } else if (this.size() == 0 && o.size() == 0) {
            return 0;
        } else if (this.size() > o.size()) {
            return 1;
        } else if (this.size() < o.size()) {
            return -1;
        } else {
            for (int i = 0; i < size(); i++) {
                int v = get(i).compareTo(o.get(i));
                if (v != 0) {
                    return v;
                }
            }
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        } else {
            return compareTo((ComparableList<T>) obj) == 0;
        }
    }
}
