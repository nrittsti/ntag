/**
 * This file is part of NTag (audio file tag editor).
 * <p>
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2016, Nico Rittstieg
 */
package ntag.fx.scene.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ntag.io.util.ArtworkAdjuster;
import ntag.model.ArtworkTag;
import toolbox.fx.FxUtil;
import toolbox.fx.control.ButtonLink;
import toolbox.io.ImageUtil.ImageType;
import toolbox.io.Resources;

import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtworkControl extends HBox implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ArtworkControl.class.getName());

    // ***
    //
    // instance attributes
    //
    // ***

    private final Image defaultImage = new Image("icons/artwork.png");

    // ***
    //
    // FXML Bindings
    //
    // ***

    @FXML
    private ImageView imageView;

    @FXML
    private Label tooltipLabel;

    @FXML
    private ButtonLink openImageButtonLink;
    @FXML
    private ButtonLink saveImageButtonLink;
    @FXML
    private ButtonLink pasteImageButtonLink;
    @FXML
    private ButtonLink removeImageButtonLink;

    // ***
    //
    // Properties
    //
    // ***

    // *** Clear Artwork EventHandler

    private ObjectProperty<EventHandler<ActionEvent>> clearEventHandlerProp = new SimpleObjectProperty<>(this, "clearEventHandler", null);

    public ObjectProperty<EventHandler<ActionEvent>> clearEventHandlerProperty() {
        return clearEventHandlerProp;
    }

    public EventHandler<ActionEvent> getClearEventHandler() {
        return clearEventHandlerProp.getValue();
    }

    public void setClearEventHandler(EventHandler<ActionEvent> clearEventHandler) {
        clearEventHandlerProp.setValue(clearEventHandler);
    }

    // *** Artwork

    private ObjectProperty<ArtworkTag> artworkProp = new SimpleObjectProperty<>(this, "artwork", null);

    public ObjectProperty<ArtworkTag> artworkProperty() {
        return artworkProp;
    }

    public ArtworkTag getArtwork() {
        return artworkProp.get();
    }

    public void setArtwork(ArtworkTag value) {
        artworkProp.set(value);
    }

    // *** Tooltip

    private StringProperty tooltipProp = new SimpleStringProperty(this, "");

    public StringProperty tooltipProperty() {
        return tooltipProp;
    }

    public String getTooltip() {
        return tooltipProp.get();
    }

    public void setTooltip(String info) {
        tooltipProp.set(info);
    }

    // ***
    //
    // Construction
    //
    // ***

    public ArtworkControl() {
        FxUtil.loadControl("ntag", this, "/fxml/Artwork.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setSmooth(true);
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        imageView.setImage(defaultImage);
        artworkProp.addListener((ObservableValue<? extends ArtworkTag> observable, ArtworkTag oldValue, ArtworkTag newValue) -> {
            if (newValue == null) {
                imageView.setImage(defaultImage);
            } else {
                ByteArrayInputStream is = new ByteArrayInputStream(newValue.getImageData());
                imageView.setImage(new Image(is));
            }
        });
        tooltipLabel.textProperty().bind(tooltipProp);
        saveImageButtonLink.disableProperty().bind(artworkProp.isNull());
        removeImageButtonLink.disableProperty().bind(Bindings.or(artworkProp.isNull(), clearEventHandlerProp.isNull()));
    }

    // ***
    //
    // Event Handler
    //
    // ***

    @FXML
    private void handleOpenImageAction(final ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Artwork File");
        fileChooser.getExtensionFilters().addAll( //
                new FileChooser.ExtensionFilter("JPG", "*.jpg"), //
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"), //
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(FxUtil.getPrimaryStage());
        if (file == null) {
            return;
        }
        try {
            changeArtwork(new ArtworkTag(file));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Invalid image file", e);
            FxUtil.showException("Invalid image file", e);
        }
    }

    @FXML
    private void handleSaveImageAction(final ActionEvent event) {
        ImageType imageType = getArtwork().getImageType();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Artwork File");

        fileChooser.setInitialFileName("artwork" + imageType.getExtensions()[0]);

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(imageType.getMimeTypes()[0], "*" + imageType.getExtensions()[0]));
        File file = fileChooser.showSaveDialog(FxUtil.getPrimaryStage());
        if (file == null) {
            return;
        }
        try {
            Files.write(file.toPath(), getArtwork().getImageData());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOError writing to file " + file.getAbsolutePath(), e);
            FxUtil.showException("IOError writing to file " + file.getAbsolutePath(), e);
        }
    }

    @FXML
    private void handlePasteImageAction(final ActionEvent event) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasFiles()) {
            try {
                changeArtwork(new ArtworkTag(clipboard.getFiles().get(0)));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Invalid pasted image file", e);
                FxUtil.showException("Invalid pasted image file", e);
            }
        } else if (clipboard.hasImage()) {
            try {
                // FX Clipboard Images causes color problems ...
                // changeArtwork(new ArtworkTag(clipboard.getImage()));
                // ... so java.awt must do the job
                Transferable transfer = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                java.awt.Image image = (java.awt.Image) transfer.getTransferData(java.awt.datatransfer.DataFlavor.imageFlavor);
                changeArtwork(new ArtworkTag(image));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Invalid pasted image data", e);
                FxUtil.showException("Invalid pasted image data", e);
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR, Resources.get("ntag", "msg_clipboard_no_image"), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRemoveImageAction(final ActionEvent event) {
        getClearEventHandler().handle(event);
    }

    private void changeArtwork(ArtworkTag artwork) {
        final ArtworkAdjuster adjuster = new ArtworkAdjuster();
        try {
            artwork = adjuster.adjust(artwork);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error by changing artwork", e);
            FxUtil.showException("Error by changing artwork", e);
        }
        setArtwork(artwork);
    }
}
