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
 * Copyright 2017, Nico Rittstieg
 */
package ntag.model;

public enum Atoms {

	// @formatter:off

	Album("©alb", "Album", true),
	ALBUM_SORT("soal", "Album sorting", false),
	AlbumArtist("aART", "Album Artist", true),
	ALBUM_ARTIST_SORT("soaa", "Album Artist Sorting", false),
	Artist("©ART", "Artist", true),
	Artwork("covr", "Artwork", true),
	BPM("tmpo", "Beats per minute", true),
	Comment("©cmt", "Comment", true),
	Compilation("cpil", "Compilation Flag", true),
	Composer("©wrt", "Composer", true),
	COMPOSER_SORT("soco", "Composer", false),
	Date("©day", "Date", true),
	Disc("disk", "Discnumber", true),
	Encoder("©too", "Encoder", false),
	Genre("gnre", "Genre", true),
	Language("----:com.apple.iTunes:LANGUAGE", "Language", true),
	Lyrics("©lyr", "Lyrics", true),
	Rating("rate", "Rating", true),
	Title("©nam", "Title", true),
	Track("trkn", "Track number", true);

	// @formatter:on

	private Atoms(String code, String label, boolean common) {
		this.code = code;
		this.label = label;
		this.common = common;
	}

	private final String code;
	private final String label;
	private final boolean common;

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public boolean isCommon() {
		return common;
	}
}