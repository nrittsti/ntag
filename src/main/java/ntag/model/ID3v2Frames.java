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

import java.util.Arrays;

public enum ID3v2Frames {

  COMM("Comments", true, 3, 4),
  PCNT("Play counter", true, 3, 4),
  POPM("Popularimeter", true, 3, 4),
  TALB("Album", true, 3, 4),
  TBPM("Beats per minute", true, 3, 4),
  TCMP("1 indicates if the file is part of a compilation", true, 3, 4),
  TCOM("Composer", true, 3, 4),
  TCON("Content type / Genre", true, 3, 4),
  TCOP("Copyright message", false, 3, 4),
  TDAT("Date in format DDMM", true, 3),
  TDLY("Playlist delay in milliseconds", false, 3, 4),
  TDOR("Original release time", true, 4),
  TDRC("Recording time", true, 4),
  TDRL("Release time", false, 4),
  TDTG("Tagging time", false, 4),
  TENC("Encoded by", false, 3, 4),
  TEXT("Lyricist(s) / Text writer(s)", false, 3, 4),
  TFLT("Audio file type", false, 3, 4),
  TIME("Recording Time in format MMHH", false, 3),
  TIT1("Content group description", false, 3, 4),
  TIT2("Title / Songname / Content description", true, 3, 4),
  TIT3("Subtitle / Description refinement", false, 3, 4),
  TLAN("Language with three characters according to ISO-639-2", true, 3, 4),
  TLEN("Length of the audiofile in milliseconds", false, 3, 4),
  TMED("Media type of the orginal audio source", false, 3, 4),
  TMOO("Mood", false, 4),
  TOAL("Original album title", false, 3, 4),
  TOFN("Original filename", false, 3, 4),
  TOLY("Original lyricist(s) / text writer(s)", false, 3, 4),
  TOPE("Original artist(s) / performer(s)", false, 3, 4),
  TORY("Original release year", false, 3),
  TOWN("File owner / licensee", false, 3, 4),
  TPE1("Artist", true, 3, 4),
  TPE2("Band, Orchestra, Accompaniment", true, 3, 4),
  TPE3("Conductor", false, 3, 4),
  TPE4("Interpreted, remixed, or otherwise modified by", false, 3, 4),
  TPOS("Part of a set", true, 3, 4),
  TPRO("Produced notice", false, 4),
  TPUB("Publisher", false, 3, 4),
  TRCK("Track number / Position in set", true, 3, 4),
  TRDA("Recording dates", false, 3),
  TRSN("Internet radio station name", false, 3, 4),
  TRSO("Internet radio station owner", false, 3, 4),
  TSIZ("Size of the audiofile in bytes", false, 3),
  TSOA("Album sort order", false, 4),
  TSOP("Performer sort order", false, 4),
  TSOT("Title sort order", false, 4),
  TSRC("International Standard Recording Code (ISRC) (12 characters)", false, 3, 4),
  TSSE("Software/Hardware and settings used for encoding", false, 3, 4),
  TSST("Set subtitle", false, 4),
  TXXX("User defined text information", false, 3, 4),
  TYER("Year", true, 3),
  USLT("Unsychronised lyrics", true, 3, 4);

  ID3v2Frames(String label, boolean common, int... versions) {
    this.label = label;
    this.common = common;
    this.versions = versions;
  }

  private final String label;
  private final boolean common;
  private final int[] versions;

  public String getLabel() {
    return label;
  }

  @SuppressWarnings("unused")
  public boolean isCommon() {
    return common;
  }

  @SuppressWarnings("unused")
  public int[] getVersions() {
    return Arrays.copyOf(versions, versions.length);
  }

  public boolean isSupportedByVersion(int version) {
    return Arrays.binarySearch(versions, version) > -1;
  }
}
