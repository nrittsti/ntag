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
package ntag.io.util;

import ntag.io.NTagProperties;
import ntag.model.AudioFormat;

import java.util.List;

public final class RatingConverter {

    private static List<Integer> flacConversion;
    private static List<Integer> mp3Conversion;
    private static List<Integer> mp4Conversion;
    private static List<Integer> oggConversion;
    private static List<Integer> wmaConversion;

    static {
        NTagProperties props = NTagProperties.instance();
        flacConversion = props.getFLACRatingConversion();
        mp3Conversion = props.getID3RatingConversion();
        mp4Conversion = props.getMP4RatingConversion();
        oggConversion = props.getOGGRatingConversion();
        wmaConversion = props.getWMARatingConversion();
    }

    private RatingConverter() {
    }

    public static List<Integer> getConversion(final AudioFormat fileFormat) {
        if (fileFormat == null) {
            throw new IllegalArgumentException("AudioFormat is not given");
        }
        return switch (fileFormat) {
            case FLAC -> flacConversion;
            case MP3 -> mp3Conversion;
            case MP4 -> mp4Conversion;
            case OGG -> oggConversion;
            case WMA -> wmaConversion;
        };
    }

    public static void setConversion(final AudioFormat fileFormat, List<Integer> values) {
        if (fileFormat == null) {
            throw new IllegalArgumentException("AudioFormat is not given");
        }
        if (values == null) {
            throw new IllegalArgumentException("conversion values are not given");
        }
        if (values.size() != 10) {
            throw new IllegalArgumentException(String.format("invalid conversion values length: %d", values.size()));
        }
        switch (fileFormat) {
            case FLAC -> flacConversion = values;
            case MP3 -> mp3Conversion = values;
            case MP4 -> mp4Conversion = values;
            case OGG -> oggConversion = values;
            case WMA -> wmaConversion = values;
            default -> throw new IllegalArgumentException(String.format("Format %s is not supported ", fileFormat));
        }
    }

    /**
     * @param fileFormat AudioFormat
     * @param value      audio file rating value
     * @return 0 - 10
     */
    public static int internalToHalfStars(final AudioFormat fileFormat, final int value) {
        List<Integer> conversions = getConversion(fileFormat);
        for (int i = conversions.size() - 1; i >= 0; i--) {
            if (value >= conversions.get(i)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * @param fileFormat AudioFormat
     * @param value      rating value (0 - 10)
     * @return audio file rating value
     */
    public static int halfStarsToInternal(final AudioFormat fileFormat, final int value) {
        List<Integer> conversions = getConversion(fileFormat);
        if (value < 1) {
            return 0;
        } else if (value > 10) {
            return conversions.get(conversions.size() - 1);
        } else {
            return conversions.get(value - 1);
        }
    }
}
