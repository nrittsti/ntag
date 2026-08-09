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
package ntag.fx.scene.control.editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.TyerTdatAggregatedFrame;
import org.jaudiotagger.tag.id3.framebody.*;

public class TagFieldCellFactory implements Callback<TableColumn.CellDataFeatures<TagField, String>, ObservableValue<String>> {

  @Override
  public ObservableValue<String> call(CellDataFeatures<TagField, String> p) {
    TagField tagField = p.getValue();
    if (tagField instanceof AbstractID3v2Frame frame) {
      AbstractTagFrameBody body = frame.getBody();
      return switch (body.getIdentifier()) {
        case "USLT" -> new SimpleStringProperty("%d Characters".formatted(body.getUserFriendlyValue().length()));
        case "TXXX" -> {
          FrameBodyTXXX txxx = (FrameBodyTXXX) body;
          yield new SimpleStringProperty("%s: %s".formatted(txxx.getDescription(), txxx.getText()));
        }
        case "PCNT" -> {
          FrameBodyPCNT pcnt = (FrameBodyPCNT) body;
          yield new SimpleStringProperty("%d".formatted(pcnt.getCounter()));
        }
        case "TPOS" -> {
          FrameBodyTPOS tpos = (FrameBodyTPOS) body;
          if (tpos.getDiscTotal() != null && tpos.getDiscTotal() > 0) {
            yield new SimpleStringProperty("%d/%d".formatted(tpos.getDiscNo(), tpos.getDiscTotal()));
          } else {
            yield new SimpleStringProperty("%d".formatted(tpos.getDiscNo()));
          }
        }
        case "TRCK" -> {
          FrameBodyTRCK trck = (FrameBodyTRCK) body;
          if (trck.getTrackTotal() != null && trck.getTrackTotal() > 0) {
            yield new SimpleStringProperty("%d/%d".formatted(trck.getTrackNo(), trck.getTrackTotal()));
          } else {
            yield new SimpleStringProperty("%d".formatted(trck.getTrackNo()));
          }
        }
        default -> {
          if (body instanceof AbstractFrameBodyTextInfo text) {
            if (text.getNumberOfValues() > 1) {
              yield new SimpleStringProperty("%s [+%d more strings]".formatted(text.getFirstTextValue(), text.getNumberOfValues()));
            } else {
              yield new SimpleStringProperty(text.getFirstTextValue());
            }
          }
          yield new SimpleStringProperty(body.getUserFriendlyValue());
        }
      };
    } else if (tagField instanceof TyerTdatAggregatedFrame aggregated) {
      AbstractID3v2Frame[] frames = new AbstractID3v2Frame[2];
      frames = aggregated.getFrames().toArray(frames);
      return new SimpleStringProperty(frames[0].getContent() + " / " + frames[1].getContent());
    } else if ("METADATA_BLOCK_PICTURE".equalsIgnoreCase(tagField.getId())) {
      return new SimpleStringProperty("Artwork image");
    }
    return new SimpleStringProperty(tagField.toString());
  }
}
