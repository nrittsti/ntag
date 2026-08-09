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
 *   Copyright 2021, Nico Rittstieg
 *
 */

package ntag.fx.util;

import ntag.Category;
import ntag.NTagException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.TyerTdatAggregatedFrame;
import org.jaudiotagger.tag.mp4.field.Mp4DiscNoField;
import org.jaudiotagger.tag.mp4.field.Mp4TagTextField;
import org.jaudiotagger.tag.mp4.field.Mp4TrackField;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag(Category.Unit)
class TagFieldInputDialogsTest {

  @Test
  void resolveEditorTyerTdatAggregatedFrame() throws NTagException {
    assertThat(TagFieldInputDialogs.resolveEditor(new TyerTdatAggregatedFrame()))
            .isEqualTo(TagFieldInputDialogs.EditorType.TYER_TDAT);
  }

  @Test
  void resolveEditorFrameBodyTXXX() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("TXXX");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.TXXX);
  }

  @Test
  void resolveEditorFrameBodyCOMM() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("COMM");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.COMM);
  }

  @Test
  void resolveEditorFrameBodyTextInfo() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("TIT2");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.TEXT_INFO);
  }

  @Test
  void resolveEditorFrameBodyPOPM() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("POPM");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.POPM);
  }

  @Test
  void resolveEditorFrameBodyUSLT() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("USLT");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.USLT);
  }

  @Test
  void resolveEditorFrameBodyPCNT() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("PCNT");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.PCNT);
  }

  @Test
  void resolveEditorFrameBodyTPOS() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("TPOS");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.DISC);
  }

  @Test
  void resolveEditorFrameBodyTRCK() throws NTagException {
    AbstractID3v2Frame frame = (AbstractID3v2Frame) new ID3v24Tag().createFrame("TRCK");
    assertThat(TagFieldInputDialogs.resolveEditor(frame))
            .isEqualTo(TagFieldInputDialogs.EditorType.TRACK);
  }

  @Test
  void resolveEditorMp4DiscNoField() throws FieldDataInvalidException, NTagException {
    assertThat(TagFieldInputDialogs.resolveEditor(new Mp4DiscNoField(1)))
            .isEqualTo(TagFieldInputDialogs.EditorType.DISC);
  }

  @Test
  void resolveEditorMp4TrackField() throws FieldDataInvalidException, NTagException {
    assertThat(TagFieldInputDialogs.resolveEditor(new Mp4TrackField(1)))
            .isEqualTo(TagFieldInputDialogs.EditorType.TRACK);
  }

  @Test
  void resolveEditorTagTextFieldLyrics() throws NTagException {
    TagField field = new Mp4TagTextField("©lyr", "lyrics");
    assertThat(TagFieldInputDialogs.resolveEditor(field))
            .isEqualTo(TagFieldInputDialogs.EditorType.TEXT_AREA);
  }

  @Test
  void resolveEditorTagTextFieldPlain() throws NTagException {
    TagField field = new Mp4TagTextField("©nam", "title");
    assertThat(TagFieldInputDialogs.resolveEditor(field))
            .isEqualTo(TagFieldInputDialogs.EditorType.TEXT_FIELD);
  }

  @Test
  void resolveEditorUnsupportedTagField() {
    TagField field = (AbstractID3v2Frame) new ID3v24Tag().createFrame("PRIV");
    assertThatThrownBy(() -> TagFieldInputDialogs.resolveEditor(field))
            .isInstanceOf(NTagException.class)
            .hasMessageContaining("Unsupported TagField");
  }

  @Test
  void createDescriptionForKnownField() {
    assertThat(TagFieldInputDialogs.createDescription("TIT2")).isNotEqualTo("TIT2");
  }

  @Test
  void createDescriptionForUnknownField() {
    assertThat(TagFieldInputDialogs.createDescription("UNKNOWN_FIELD")).isEqualTo("UNKNOWN_FIELD");
  }
}
