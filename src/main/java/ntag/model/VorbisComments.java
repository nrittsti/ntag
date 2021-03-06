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

public enum VorbisComments {

    ALBUM("Album", true),
    ALBUMARTIST("Album Artist", true),
    ARRANGER("the person who arranged the piece", false),
    ARTIST("Artist", true),
    AUTHOR("for text that is spoken, or was originally meant to be spoken", true),
    BPM("Beats per minute", true),
    COMMENT("additional comments of any nature", true),
    COMPILATION("1 indicates if the file is part of a compilation", true),
    COMPOSER("composer of the work", true),
    CONDUCTOR("conductor of the work", false),
    COPYRIGHT("who holds copyright to the track", false),
    DATE("date 'YYYY-MM-DD' of relevance to the track", true),
    DISCNUMBER("disc number of a set", true),
    DISCTOTAL("total discs of a set", true),
    ENCODEDBY("The person who encoded the Ogg file", false),
    ENCODER("Encoder Settings", false),
    ENSEMBLE("the group playing the piece", false),
    GENRE("Genre", true),
    ISRC("International Standard Recording Code", false),
    LABEL("the record label or imprint on the disc", false),
    LABELNO("catalog number of the source media", false),
    LANGUAGE("Language", true),
    LICENSE("the license, or URL for the license the track is under", false),
    LOCATION("location of recording", false),
    LYRICIST("the person who wrote the lyrics", false),
    LYRICS("full lyrics for the track", true),
    OPUS("the number of the work", false),
    PART("a division within a work", false),
    PARTNUMBER("Numeric part number", false),
    PERFORMER("individual performers singled out for mention", false),
    RATING("Rating", true),
    SOURCEMEDIA("the recording media the track came from", false),
    TITLE("Title", true),
    TRACKNUMBER("the track number on the CD", true),
    TRACKTOTAL("the total track number on the CD", true),
    VENDOR("Vendor", false),
    VERSION("Live, Acustic, Radio Edit ...", false),
    WEBSITE("a full URL linking to the album's official web site", false);

    VorbisComments(String label, boolean common) {
        this.label = label;
        this.common = common;
    }

    private final String label;
    private final boolean common;

    public String getLabel() {
        return label;
    }

    @SuppressWarnings("unused")
    public boolean isCommon() {
        return common;
    }
}
