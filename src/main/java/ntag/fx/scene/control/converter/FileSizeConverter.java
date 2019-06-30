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
package ntag.fx.scene.control.converter;

import javafx.util.StringConverter;
import ntag.fx.util.NTagFormat;

public class FileSizeConverter extends StringConverter<Long> {

    public static boolean binaryMode = true;

    public FileSizeConverter() {
        super();
    }

    @Override
    public String toString(Long object) {
        if (object == null) {
            return "";
        } else {
            return NTagFormat.fileSize(object, binaryMode);
        }
    }

    @Override
    public Long fromString(String string) {
        return null;
    }
}
