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
import java.util.HashSet;
import java.util.Set;

public enum AudioFormat {

  MP3(".mp3"), //
  MP4(".m4a", ".m4b", ".mp4"), //
  OGG(".ogg"), //
  FLAC(".flac"), //
  WMA(".wma");

  private final String[] extensions;

  AudioFormat(String... extensions) {
    this.extensions = extensions;
  }

  /**
   * Returns the corresponding AudioFormatType for the given file extension.
   *
   * @param extension audio file extension
   * @return AudioFormatType or null
   */
  public static AudioFormat getTypeByExtension(String extension) {
    if (extension == null || extension.length() == 0) {
      return null;
    }
    if (extension.charAt(0) != '.') {
      extension = '.' + extension;
    }
    for (AudioFormat type : AudioFormat.values()) {
      for (String typeEx : type.extensions) {
        if (typeEx.equalsIgnoreCase(extension)) {
          return type;
        }
      }
    }
    return null;
  }

  /**
   * Returns all supported audio file extensions.
   *
   * @return Set<String>
   */
  public static Set<String> getFileExtensions() {
    Set<String> list = new HashSet<>();
    for (AudioFormat type : AudioFormat.values()) {
      list.addAll(Arrays.asList(type.extensions));
    }
    return list;
  }
}
