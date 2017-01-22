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
package ntag.io;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TagFileConst {

	public static final String TXXX_MEDIA_KIND = "Emmi Media Kind";
	public static final String TXXX_INSTRUMENTAL = "Emmi Instrumental";
	public static final String TXXX_MONITORING = "Emmi Monitoring";
	public static final String TXXX_GENRES = "Emmi Genres";

	public static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK);

	private TagFileConst() {

	}
}
