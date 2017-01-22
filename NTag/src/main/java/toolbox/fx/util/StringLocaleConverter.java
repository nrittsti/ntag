/**
 * This file is part of NTagDB (tag-based database for audio files).
 *
 * NTagDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NTagDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NTagDB.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016, Nico Rittstieg
 */
package toolbox.fx.util;

import java.util.Locale;

import javafx.util.StringConverter;

public class StringLocaleConverter extends StringConverter<Locale> {

	@Override
	public String toString(Locale object) {
		return object.getDisplayLanguage();
	}

	@Override
	public Locale fromString(String value) {
		return new Locale(value);
	}
}
