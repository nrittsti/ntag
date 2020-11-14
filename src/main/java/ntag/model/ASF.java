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
package ntag.model;

public enum ASF {

  Album("WM/AlbumTitle", "Album", true),
  AlbumArtist("WM/AlbumArtist", "Album Artist", true),
  Artist("AUTHOR", "Artist", true),
  BPM("WM/BeatsPerMinute", "Beats per minute", true),
  Comment("DESCRIPTION", "Comment", true),
  Compilation("WM/IsCompilation", "Compilation Flag", true),
  Composer("WM/Composer", "Composer", true),
  Date("WM/Year", "Date", true),
  Disc("WM/PartOfSet", "Discnumber", true),
  DiscTotal("WM/DiscTotal", "Disc total", true),
  EncodedBy("WM/EncodedBy", "Encoded by", false),
  Encoder("WM/ToolName", "Encoder", false),
  Genre("WM/Genre", "Genre", true),
  Language("WM/Language", "Language", true),
  Lyrics("WM/Lyrics", "Lyrics", true),
  Rating("WM/SharedUserRating", "Rating", true),
  Title("TITLE", "Title", true),
  Track("WM/TrackNumber", "Track", true),
  TrackTotal("WM/TrackTotal", "Track total", true),
  Year("WM/Year", "Year", false);

  ASF(String code, String label, boolean common) {
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

  @SuppressWarnings("unused")
  public boolean isCommon() {
    return common;
  }
}
