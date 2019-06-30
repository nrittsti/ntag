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
package ntag.fx.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import ntag.NTagException;
import ntag.model.ASF;
import ntag.model.Atoms;
import ntag.model.ID3v2Frames;
import ntag.model.VorbisComments;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.asf.AsfFieldKey;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.TyerTdatAggregatedFrame;
import org.jaudiotagger.tag.id3.framebody.*;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.mp4.field.Mp4DiscNoField;
import org.jaudiotagger.tag.mp4.field.Mp4GenreField;
import org.jaudiotagger.tag.mp4.field.Mp4TagByteField;
import org.jaudiotagger.tag.mp4.field.Mp4TrackField;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import toolbox.fx.FxUtil;
import toolbox.fx.control.RegexTextfield;
import toolbox.io.Resources;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TagFieldInputDialogs {

    public static final Logger LOGGER = Logger.getLogger(TagFieldInputDialogs.class.getName());

    static Set<String> commonFields = new HashSet<>();
    static Map<String, String> fieldDescMap = new HashMap<>();

    static {
        for (ID3v2Frames e : ID3v2Frames.values()) {
            commonFields.add(e.name());
            fieldDescMap.put(e.name(), String.format("%s (%s)", e.name(), e.getLabel()));
        }
        for (VorbisComments e : VorbisComments.values()) {
            commonFields.add(e.name());
            fieldDescMap.put(e.name(), String.format("%s (%s)", e.name(), e.getLabel()));
        }
        for (ASF e : ASF.values()) {
            commonFields.add(e.getCode());
            fieldDescMap.put(e.getCode(), String.format("%s (%s)", e.getCode(), e.getLabel()));
        }
        for (Atoms e : Atoms.values()) {
            commonFields.add(e.getCode());
            fieldDescMap.put(e.getCode(), String.format("%s (%s)", e.getCode(), e.getLabel()));
        }
    }

    private TagFieldInputDialogs() {

    }

    public static boolean showNewTagFieldWizard(AudioFile audioFile) throws NTagException {
        Tag tag = audioFile.getTagOrCreateAndSetDefault();
        ObservableList<TagField> tagFields = createTagFieldList(tag);
        FilteredList<TagField> filteredList = new FilteredList<>(tagFields, null);

        ListView<TagField> listView = new ListView<>(filteredList);
        listView.setPrefSize(150, 200);
        listView.setCellFactory((ListView<TagField> param) -> {
            return new TagFieldListCell();
        });

        CheckBox commonCheckBox = new CheckBox(Resources.get("ntag", "lbl_common_tags"));
        TextField filterTextField = new TextField();
        filterTextField.setPromptText("Quick Filter");

        filterTextField.textProperty().addListener(obs -> {
            final String filter = filterTextField.getText();
            if (filter == null || filter.length() == 0) {
                filteredList.setPredicate(s -> (commonCheckBox.isSelected() ? commonFields.contains(s.getId()) : true));
            } else {
                filteredList.setPredicate(s -> createDescription(s.getId()).toUpperCase().contains(filter.toUpperCase()) && (commonCheckBox.isSelected() ? commonFields.contains(s.getId()) : true));
            }
        });

        commonCheckBox.setOnAction(event -> {
            final String filter = filterTextField.getText();
            if (filter == null || filter.length() == 0) {
                filteredList.setPredicate(s -> (commonCheckBox.isSelected() ? commonFields.contains(s.getId()) : true));
            } else {
                filteredList.setPredicate(s -> createDescription(s.getId()).toUpperCase().contains(filter.toUpperCase()) && (commonCheckBox.isSelected() ? commonFields.contains(s.getId()) : true));
            }
        });

        VBox vbox = new VBox(4);
        vbox.getChildren().add(filterTextField);
        vbox.getChildren().add(listView);
        vbox.getChildren().add(commonCheckBox);
        VBox.setVgrow(listView, Priority.ALWAYS);

        Dialog<TagField> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(Resources.get("ntag", "mnu_new_tag"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(300, 300);
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });
        Optional<TagField> tagFieldResult = dialog.showAndWait();
        if (!tagFieldResult.isPresent()) {
            return false;
        }
        TagField tagField = tagFieldResult.get();
        if (showTagFieldEditor(tagField)) {
            try {
                audioFile.getTagOrCreateAndSetDefault().addField(tagField);
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info(String.format("Created new %s Tag in file '%s'", tagField.getId(), audioFile.getFile().getName()));
                }
            } catch (FieldDataInvalidException e) {
                throw new NTagException("Can't create TagField  " + tagField.getId(), e);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean showTagFieldEditor(final TagField tagField) throws NTagException {
        if (tagField instanceof TyerTdatAggregatedFrame) {
            return showTyerTdatInputDialog((TyerTdatAggregatedFrame) tagField);
        } else if (tagField instanceof AbstractID3v2Frame) {
            AbstractTagFrameBody body = ((AbstractID3v2Frame) tagField).getBody();
            if (body instanceof FrameBodyTXXX) {
                FrameBodyTXXX txxxBody = (FrameBodyTXXX) body;
                return showTxxxInputDialog(txxxBody);
            } else if (body instanceof FrameBodyCOMM) {
                FrameBodyCOMM comm = (FrameBodyCOMM) body;
                Optional<String> result = showSimpleTextInputDialog(tagField.getId(), comm.getFirstTextValue());
                if (result.isPresent()) {
                    comm.setText(result.get().trim());
                    return true;
                } else {
                    return false;
                }
            } else if (body instanceof AbstractFrameBodyTextInfo) {
                AbstractFrameBodyTextInfo textBody = (AbstractFrameBodyTextInfo) body;
                Optional<String> result = showSimpleTextInputDialog(tagField.getId(), textBody.getFirstTextValue());
                if (result.isPresent()) {
                    textBody.setText(result.get().trim());
                    return true;
                } else {
                    return false;
                }
            } else if (body instanceof FrameBodyPOPM) {
                return showPopmInputDialog((FrameBodyPOPM) body);
            } else if (body instanceof FrameBodyUSLT) {
                FrameBodyUSLT textBody = (FrameBodyUSLT) body;
                Optional<String> result = showTextAreaDialog(tagField.getId(), textBody.getLyric());
                if (result.isPresent()) {
                    textBody.setLyric(result.get());
                    return true;
                } else {
                    return false;
                }
            } else if (body instanceof FrameBodyPCNT) {
                FrameBodyPCNT pcnt = (FrameBodyPCNT) body;
                Optional<String> result = showSimpleTextInputDialog(tagField.getId(), Long.toString(pcnt.getCounter()), "[0-9]*");
                if (result.isPresent()) {
                    pcnt.setCounter(Long.valueOf(result.get()));
                    return true;
                } else {
                    return false;
                }
            } else if (body instanceof FrameBodyTPOS) {
                FrameBodyTPOS tpos = (FrameBodyTPOS) body;
                return showDiscInputDialog(tpos);
            } else if (body instanceof FrameBodyTRCK) {
                FrameBodyTRCK tpos = (FrameBodyTRCK) body;
                return showTrackInputDialog(tpos);
            }
        } else if (tagField instanceof Mp4DiscNoField) {
            Mp4DiscNoField disc = (Mp4DiscNoField) tagField;
            return showDiscInputDialog(disc);
        } else if (tagField instanceof Mp4TrackField) {
            Mp4TrackField track = (Mp4TrackField) tagField;
            return showTrackInputDialog(track);
        } else if (tagField instanceof TagTextField) {
            TagTextField textField = (TagTextField) tagField;
            Optional<String> result = null;
            if (tagField.getId().toLowerCase().contains("lyr")) {
                result = showTextAreaDialog(tagField.getId(), textField.getContent());
            } else {
                result = showSimpleTextInputDialog(tagField.getId(), textField.getContent());
            }
            if (result.isPresent()) {
                textField.setContent(result.get().trim());
                return true;
            } else {
                return false;
            }
        }
        throw new NTagException("Unsupported TagField: " + tagField.getId());
    }

    private static Optional<String> showSimpleTextInputDialog(String id, String value) {
        return showSimpleTextInputDialog(id, value, null);
    }

    private static Optional<String> showSimpleTextInputDialog(String id, String value, String regex) {
        TextInputDialog dialog = new TextInputDialog(value);
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle("Tag Editor");
        dialog.setHeaderText(createDescription(id));
        dialog.setContentText("");
        dialog.setResizable(true);
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 160);
        if (regex != null) {
            UnaryOperator<Change> filter = change -> {
                String text = change.getText();
                if (text.matches(regex)) {
                    return change;
                }
                return null;
            };
            dialog.getEditor().setTextFormatter(new TextFormatter<>(filter));
        }
        return dialog.showAndWait();
    }

    private static boolean showPopmInputDialog(FrameBodyPOPM popm) {
        Dialog<FrameBodyPOPM> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle("Tag Editor POPM");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(290, 180);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        TextField email = new TextField();
        email.setText(popm.getEmailToUser());
        email.setMinWidth(200);
        RegexTextfield rating = new RegexTextfield();
        rating.setText(Long.toString(popm.getRating()));
        rating.setRegex("[0-9]");
        rating.setMaxLength(3);
        RegexTextfield counter = new RegexTextfield();
        counter.setRegex("[0-9]");
        counter.setMaxLength(6);
        counter.setText(Long.toString(popm.getCounter()));

        gridPane.add(new Label("EMail"), 0, 0);
        gridPane.add(email, 1, 0);
        gridPane.add(new Label("Rating"), 0, 1);
        gridPane.add(rating, 1, 1);
        gridPane.add(new Label("Counter"), 0, 2);
        gridPane.add(counter, 1, 2);

        Platform.runLater(() -> rating.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                popm.setEmailToUser(email.getText().trim());
                long ratingValue = Long.valueOf(rating.getText().trim());
                popm.setRating(ratingValue > 255 ? 255 : ratingValue);
                popm.setCounter(Long.valueOf(counter.getText().trim()));
                return popm;
            }
            return null;
        });
        Optional<FrameBodyPOPM> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static boolean showDiscInputDialog(FrameBodyTPOS tpos) {
        Dialog<FrameBodyTPOS> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(fieldDescMap.get(tpos.getIdentifier()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(290, 160);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        RegexTextfield disc = new RegexTextfield();
        disc.setText(tpos.getDiscNoAsText());
        disc.setMinWidth(200);
        disc.setRegex("[0-9]");
        disc.setMaxLength(2);
        RegexTextfield discTotal = new RegexTextfield();
        discTotal.setText(tpos.getDiscTotalAsText());
        discTotal.setRegex("[0-9]");
        discTotal.setMaxLength(2);

        gridPane.add(new Label("Disc"), 0, 0);
        gridPane.add(disc, 1, 0);
        gridPane.add(new Label("Disc Total"), 0, 1);
        gridPane.add(discTotal, 1, 1);

        Platform.runLater(() -> disc.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String discStr = disc.getText().trim();
                String discTotalStr = discTotal.getText().trim();
                if (discStr.length() > 0) {
                    tpos.setDiscNo(discStr);
                } else {
                    tpos.setDiscNo(0);
                }
                if (discTotalStr.length() > 0) {
                    tpos.setDiscTotal(discTotalStr);
                } else {
                    tpos.setDiscTotal(0);
                }
                return tpos;
            }
            return null;
        });
        Optional<FrameBodyTPOS> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static boolean showDiscInputDialog(Mp4DiscNoField tpos) {
        Dialog<Mp4DiscNoField> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(fieldDescMap.get(tpos.getId()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(290, 160);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        RegexTextfield disc = new RegexTextfield();
        disc.setText(Integer.toString(tpos.getDiscNo()));
        disc.setMinWidth(200);
        disc.setRegex("[0-9]");
        disc.setMaxLength(2);
        RegexTextfield discTotal = new RegexTextfield();
        discTotal.setText(Integer.toString(tpos.getDiscTotal()));
        discTotal.setRegex("[0-9]");
        discTotal.setMaxLength(2);

        gridPane.add(new Label("Disc"), 0, 0);
        gridPane.add(disc, 1, 0);
        gridPane.add(new Label("Disc Total"), 0, 1);
        gridPane.add(discTotal, 1, 1);

        Platform.runLater(() -> disc.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                StringBuilder sb = new StringBuilder();
                String discStr = disc.getText().trim();
                String discTotalStr = discTotal.getText().trim();
                if (discStr.length() > 0) {
                    tpos.setDiscNo(Integer.parseInt(discStr));
                    sb.append(discStr);
                } else {
                    tpos.setDiscNo(0);
                }
                if (discTotalStr.length() > 0) {
                    tpos.setDiscTotal(Integer.parseInt(discTotalStr));
                    if (sb.length() > 0) {
                        sb.append("/");
                    }
                    sb.append(discTotalStr);
                } else {
                    tpos.setDiscTotal(0);
                }
                tpos.setContent(sb.toString());
                return tpos;
            }
            return null;
        });
        Optional<Mp4DiscNoField> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static boolean showTrackInputDialog(FrameBodyTRCK trck) {
        Dialog<FrameBodyTRCK> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(fieldDescMap.get(trck.getIdentifier()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(290, 160);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        RegexTextfield track = new RegexTextfield();
        track.setText(trck.getTrackNoAsText());
        track.setMinWidth(200);
        track.setRegex("[0-9]");
        track.setMaxLength(2);
        RegexTextfield trackTotal = new RegexTextfield();
        trackTotal.setText(trck.getTrackTotalAsText());
        trackTotal.setRegex("[0-9]");
        trackTotal.setMaxLength(2);

        gridPane.add(new Label("Track"), 0, 0);
        gridPane.add(track, 1, 0);
        gridPane.add(new Label("Track Total"), 0, 1);
        gridPane.add(trackTotal, 1, 1);

        Platform.runLater(() -> track.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String trackStr = track.getText().trim();
                String trackTotalStr = trackTotal.getText().trim();
                if (trackStr.length() > 0) {
                    trck.setTrackNo(trackStr);
                } else {
                    trck.setTrackNo(0);
                }
                if (trackTotalStr.length() > 0) {
                    trck.setTrackTotal(trackTotalStr);
                } else {
                    trck.setTrackTotal(0);
                }
                return trck;
            }
            return null;
        });
        Optional<FrameBodyTRCK> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static boolean showTrackInputDialog(Mp4TrackField trackField) {
        Dialog<Mp4TrackField> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(fieldDescMap.get(trackField.getId()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(290, 160);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        RegexTextfield track = new RegexTextfield();
        track.setText(Integer.toString(trackField.getTrackNo()));
        track.setMinWidth(200);
        track.setRegex("[0-9]");
        track.setMaxLength(2);
        RegexTextfield trackTotal = new RegexTextfield();
        trackTotal.setText(Integer.toString(trackField.getTrackTotal()));
        trackTotal.setRegex("[0-9]");
        trackTotal.setMaxLength(2);

        gridPane.add(new Label("Track"), 0, 0);
        gridPane.add(track, 1, 0);
        gridPane.add(new Label("Track Total"), 0, 1);
        gridPane.add(trackTotal, 1, 1);

        Platform.runLater(() -> track.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                StringBuilder sb = new StringBuilder();
                String trackStr = track.getText().trim();
                String trackTotalStr = trackTotal.getText().trim();
                if (trackStr.length() > 0) {
                    trackField.setTrackNo(Integer.parseInt(trackStr));
                    sb.append(trackStr);
                } else {
                    trackField.setTrackNo(0);
                }
                if (trackTotalStr.length() > 0) {
                    trackField.setTrackTotal(Integer.parseInt(trackTotalStr));
                    if (sb.length() > 0) {
                        sb.append("/");
                    }
                    sb.append(trackTotalStr);
                } else {
                    trackField.setTrackTotal(0);
                }
                trackField.setContent(sb.toString());
                return trackField;
            }
            return null;
        });
        Optional<Mp4TrackField> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static Optional<String> showTextAreaDialog(String id, String value) {
        Dialog<String> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(fieldDescMap.get(id));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(350, 600);
        dialog.setResizable(true);
        TextArea textArea = new TextArea(value);
        dialog.getDialogPane().setContent(textArea);
        Platform.runLater(() -> textArea.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return textArea.getText().trim();
            }
            return null;
        });
        return dialog.showAndWait();
    }

    private static boolean showTyerTdatInputDialog(TyerTdatAggregatedFrame frame) {
        Iterator<AbstractID3v2Frame> iterator = frame.getFrames().iterator();
        FrameBodyTYER tyer = (FrameBodyTYER) iterator.next().getBody();
        FrameBodyTDAT tdat = (FrameBodyTDAT) iterator.next().getBody();

        Dialog<Pair<FrameBodyTYER, FrameBodyTDAT>> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle("Tag Editor TYER + TDAT");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(200, 140);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        RegexTextfield tyerField = new RegexTextfield();
        tyerField.setText(tyer.getText());
        tyerField.setMaxWidth(80);
        tyerField.setRegex("[0-9]");
        tyerField.setMaxLength(4);
        RegexTextfield tdatField = new RegexTextfield();
        tdatField.setText(tdat.getText());
        tdatField.setMaxWidth(80);
        tdatField.setRegex("[0-9]");
        tdatField.setMaxLength(4);

        gridPane.add(new Label("TYER"), 0, 0);
        gridPane.add(tyerField, 1, 0);
        gridPane.add(new Label("TDAT"), 0, 1);
        gridPane.add(tdatField, 1, 1);

        Platform.runLater(() -> tyerField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                tyer.setText(tyerField.getText());
                tdat.setText(tdatField.getText());
                return new Pair<>(tyer, tdat);
            }
            return null;
        });
        Optional<Pair<FrameBodyTYER, FrameBodyTDAT>> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static boolean showTxxxInputDialog(FrameBodyTXXX txxx) {
        Dialog<FrameBodyTXXX> dialog = new Dialog<>();
        dialog.initOwner(FxUtil.getPrimaryStage());
        dialog.setTitle(fieldDescMap.get(txxx.getIdentifier()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefSize(290, 160);

        GridPane gridPane = new GridPane();
        dialog.getDialogPane().setContent(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));
        TextField desc = new TextField();
        desc.setText(txxx.getDescription());
        TextField value = new TextField();
        value.setText(txxx.getFirstTextValue());

        gridPane.add(new Label("Description"), 0, 0);
        gridPane.add(desc, 1, 0);
        gridPane.add(new Label("Value"), 0, 1);
        gridPane.add(value, 1, 1);

        Platform.runLater(() -> desc.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                txxx.setDescription(desc.getText().trim());
                txxx.setText(value.getText().trim());
                return txxx;
            }
            return null;
        });
        Optional<FrameBodyTXXX> result = dialog.showAndWait();
        return result.isPresent();
    }

    private static String createDescription(String id) {
        String result = fieldDescMap.get(id);
        return result == null ? id : result;
    }

    private static ObservableList<TagField> createTagFieldList(Tag tag) {
        ObservableList<TagField> tagFields = FXCollections.observableArrayList();
        if (tag instanceof AbstractID3v2Tag) {
            AbstractID3v2Tag id3Tag = (AbstractID3v2Tag) tag;
            for (ID3v2Frames id3 : ID3v2Frames.values()) {
                if (id3.isSupportedByVersion(id3Tag.getMajorVersion())) {
                    tagFields.add(id3Tag.createFrame(id3.name()));
                }
            }
        } else if (tag instanceof VorbisCommentTag) {
            VorbisCommentTag vorbisTag = (VorbisCommentTag) tag;
            for (VorbisComments vorbis : VorbisComments.values()) {
                tagFields.add(vorbisTag.createField(vorbis.name(), ""));
            }
        } else if (tag instanceof AsfTag) {
            AsfTag asfTag = (AsfTag) tag;
            for (ASF asf : ASF.values()) {
                tagFields.add(asfTag.createField(AsfFieldKey.getAsfFieldKey(asf.getCode()), ""));
            }
        } else if (tag instanceof Mp4Tag) {
            Mp4Tag mp4Tag = (Mp4Tag) tag;
            for (Atoms atom : Atoms.values()) {
                if (atom == Atoms.Genre) {
                    tagFields.add(new Mp4GenreField("1"));
                } else if (atom == Atoms.Track) {
                    tagFields.add(new Mp4TrackField(0));
                } else if (atom == Atoms.Disc) {
                    tagFields.add(new Mp4DiscNoField(0));
                } else if (atom == Atoms.BPM) {
                    try {
                        tagFields.add(new Mp4TagByteField(Mp4FieldKey.BPM, "0"));
                    } catch (FieldDataInvalidException e) {
                    }
                } else {
                    for (Mp4FieldKey fk : Mp4FieldKey.values()) {
                        if (fk.getFieldName().equals(atom.getCode())) {
                            try {
                                tagFields.add(mp4Tag.createField(fk, ""));
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "Can't create TagField", e);
                                FxUtil.showException("Can't create TagField", e);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return tagFields;
    }
}