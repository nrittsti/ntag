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

package ntag.io;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag(Category.Unit)
class JAudiotaggerUtilTest extends AbstractAudioFileTest {

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    copyFilesToTempDir();
  }

  @ParameterizedTest
  @CsvSource({SAMPLE_FLAC, SAMPLE_WMA, SAMPLE_M4A, SAMPLE_ID3V23_MP3, SAMPLE_ID3V24_MP3})
  void readAudioFile(String file) throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(file));
    assertNotNull(actual);
  }

  @Test
  @DisplayName("ID3v23 File --> getID3v23Tag")
  void getID3v2Tag_1() throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) actual, false);
    assertNotNull(tag);
  }

  @Test
  @DisplayName("ID3v23 File --> getID3v24Tag")
  void getID3v2Tag_2() throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) actual, true);
    assertNotNull(tag);
  }

  @Test
  @DisplayName("ID3v24 File --> getID3v24Tag")
  void getID3v2Tag_3() throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V24_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) actual, true);
    assertNotNull(tag);
  }

  @Test
  @DisplayName("ID3v24 File --> getID3v23Tag")
  void getID3v2Tag_4() throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V24_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) actual, false);
    assertNotNull(tag);
  }

  @Test
  void getID3v1Tag() throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    ID3v1Tag tag = JAudiotaggerUtil.getID3v1Tag((MP3File) actual);
    assertNotNull(tag);
  }

  @Test
  void removeID3v1Tag() throws Exception {
    AudioFile actual = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    JAudiotaggerUtil.getID3v1Tag((MP3File) actual);
    JAudiotaggerUtil.removeID3v1Tag((MP3File) actual);
  }

  @Test
  @DisplayName("create Genre from numeric representation")
  void createGenre_1() throws Exception {
    String expected = "Classic Rock";
    AudioFile audioFile = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) audioFile, false);
    tag.setField(FieldKey.GENRE, "(1)");
    String actual = JAudiotaggerUtil.createGenre(tag.getFirstField(FieldKey.GENRE));
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("get Genre from String representation")
  void createGenre_2() throws Exception {
    String expected = "Classic Rock";
    AudioFile audioFile = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) audioFile, false);
    tag.setField(FieldKey.GENRE, expected);
    String actual = JAudiotaggerUtil.createGenre(tag.getFirstField(FieldKey.GENRE));
    assertEquals(expected, actual);
  }

  @Test
  void parseInt() {
    int expected = 100;
    int actual = JAudiotaggerUtil.parseInt("100");
    assertEquals(expected, actual);
  }

  @Test
  void parseInt_2() {
    int expected = -1;
    int actual = JAudiotaggerUtil.parseInt("abc");
    assertEquals(expected, actual);
  }

  @Test
  void parseInt_3() {
    int expected = 5;
    int actual = JAudiotaggerUtil.parseInt(null, expected);
    assertEquals(expected, actual);
  }

  @Test
  void removeTagField() throws Exception {
    AudioFile audioFile = JAudiotaggerUtil.readAudioFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    AbstractID3v2Tag tag = JAudiotaggerUtil.getID3v2Tag((MP3File) audioFile, false);
    tag.setField(FieldKey.TITLE, "Test");
    JAudiotaggerUtil.removeTagField(tag, tag.getFirstField(FieldKey.TITLE));
    List<TagField> actual = tag.getFields(FieldKey.TITLE);
    assertTrue(actual.isEmpty());
  }
}
