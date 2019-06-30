/**
 * This file is part of NTagDB (tag-based database for audio files).
 * <p>
 * NTagDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTagDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTagDB.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2017, Nico Rittstieg
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

    /*
     * (non-Javadoc)
     *
     * @see javafx.util.Callback#call(java.lang.Object)
     */
    @Override
    public ObservableValue<String> call(CellDataFeatures<TagField, String> p) {
        TagField tagField = p.getValue();
        if (tagField instanceof AbstractID3v2Frame) {
            AbstractTagFrameBody body = ((AbstractID3v2Frame) tagField).getBody();
            if ("USLT".equals(body.getIdentifier())) {
                return new SimpleStringProperty(String.format("%d Characters", body.getUserFriendlyValue().length()));
            } else if ("TXXX".equals(body.getIdentifier())) {
                FrameBodyTXXX txxx = (FrameBodyTXXX) body;
                return new SimpleStringProperty(String.format("%s: %s", txxx.getDescription(), txxx.getText()));
            } else if ("PCNT".equals(body.getIdentifier())) {
                FrameBodyPCNT pcnt = (FrameBodyPCNT) body;
                return new SimpleStringProperty(String.format("%d", pcnt.getCounter()));
            } else if ("TPOS".equals(body.getIdentifier())) {
                FrameBodyTPOS tpos = (FrameBodyTPOS) body;
                if (tpos.getDiscTotal() != null && tpos.getDiscTotal() > 0) {
                    return new SimpleStringProperty(String.format("%d/%d", tpos.getDiscNo(), tpos.getDiscTotal()));
                } else {
                    return new SimpleStringProperty(String.format("%d", tpos.getDiscNo()));
                }
            } else if ("TRCK".equals(body.getIdentifier())) {
                FrameBodyTRCK tpos = (FrameBodyTRCK) body;
                if (tpos.getTrackTotal() != null && tpos.getTrackTotal() > 0) {
                    return new SimpleStringProperty(String.format("%d/%d", tpos.getTrackNo(), tpos.getTrackTotal()));
                } else {
                    return new SimpleStringProperty(String.format("%d", tpos.getTrackNo()));
                }
            } else if (body instanceof AbstractFrameBodyTextInfo) {
                AbstractFrameBodyTextInfo text = (AbstractFrameBodyTextInfo) body;
                if (text.getNumberOfValues() > 1) {
                    return new SimpleStringProperty(String.format("%s [+%d more strings]", text.getFirstTextValue(), text.getNumberOfValues()));
                } else {
                    return new SimpleStringProperty(text.getFirstTextValue());
                }
            } else {
                return new SimpleStringProperty(body.getUserFriendlyValue());
            }
        } else if (tagField instanceof TyerTdatAggregatedFrame) {
            TyerTdatAggregatedFrame body = (TyerTdatAggregatedFrame) tagField;
            AbstractID3v2Frame[] frames = new AbstractID3v2Frame[2];
            frames = body.getFrames().toArray(frames);
            return new SimpleStringProperty(frames[0].getContent() + " / " + frames[1].getContent());
        } else if ("METADATA_BLOCK_PICTURE".equalsIgnoreCase(tagField.getId())) {
            return new SimpleStringProperty("Artwork image");
        }
        return new SimpleStringProperty(tagField.toString());
    }
}