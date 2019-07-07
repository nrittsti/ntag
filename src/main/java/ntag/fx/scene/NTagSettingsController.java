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
package ntag.fx.scene;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;
import ntag.fx.scene.control.NTagThemeEnum;
import ntag.fx.util.NTagFormat;
import ntag.io.NTagProperties;
import ntag.io.util.ImageUtil.ImageType;
import toolbox.fx.control.RegexTextfield;
import toolbox.fx.dialog.AbstractDialogController;
import toolbox.fx.dialog.DialogResponse;
import toolbox.fx.util.IntegerListConverter;
import toolbox.fx.util.StringListConverter;
import toolbox.fx.util.StringLocaleConverter;

import java.net.URL;
import java.util.*;

public class NTagSettingsController extends AbstractDialogController<NTagProperties> {

    // ***
    //
    // Attributes
    //
    // ***

    private IntegerListConverter integerListConverter = new IntegerListConverter(",");
    private StringListConverter stringListConverter = new StringListConverter(",");

    // ***
    //
    // FXML
    //
    // ***

    @FXML
    private Label errorLabel;

    // *** General

    @FXML
    private ComboBox<NTagThemeEnum> themeComboBox;
    @FXML
    private ComboBox<Locale> languageComboBox;
    @FXML
    private RadioButton binaryRadioButton;
    @FXML
    private RadioButton decimalRadioButton;
    @FXML
    private TextField filenameFormatTextField;
    @FXML
    private CheckBox filenameStripUnsafeCharsCheckBox;
    @FXML
    private TextField genreFavTextField;
    @FXML
    private ComboBox<String> dateFormatComboBox;

    // *** MP3

    @FXML
    private CheckBox id3v1CheckBox;
    @FXML
    private RadioButton id3v23RadioButton;
    @FXML
    private RadioButton id3v24RadioButton;
    @FXML
    private TextField id3RatingMailTextField;
    @FXML
    private CheckBox id3RatingEnforceSingleFrameCheckBox;
    @FXML
    private TextField id3RatingConversionTextField;
    @FXML
    private TextField id3FrameBlackListTextField;
    @FXML
    private CheckBox tdorCheckBox;
    @FXML
    private CheckBox tdrcCheckBox;
    @FXML
    private CheckBox tdrlCheckBox;

    // *** MP4

    @FXML
    private TextField mp4RatingConversionTextField;

    // *** OGG

    @FXML
    private TextField oggRatingConversionTextField;

    // *** FLAC

    @FXML
    private TextField flacRatingConversionTextField;

    // *** WMA

    @FXML
    private TextField wmaRatingConversionTextField;

    // *** Artwork

    @FXML
    private ComboBox<ImageType> artworkImageFormatComboBox;
    @FXML
    private Slider artworkQualitySlider;
    @FXML
    private RegexTextfield artworkQualityTextField;
    @FXML
    private Slider artworkKilobytesSlider;
    @FXML
    private RegexTextfield artworkKilobytesTextField;
    @FXML
    private Slider artworkResolutionSlider;
    @FXML
    private RegexTextfield artworkResolutionTextField;
    @FXML
    private CheckBox artworkEnforceImageTypeCheckBox;
    @FXML
    private CheckBox artworkEnforceSingleArtworkCheckBox;

    // *** Lyrics

    @FXML
    private TextArea lyricProviderTextArea;

    // ***
    //
    // Construction
    //
    // ***

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeComboBox.getItems().addAll(NTagThemeEnum.ModenaLight, NTagThemeEnum.ModenaDark);
        languageComboBox.getItems().addAll(Locale.ENGLISH, Locale.GERMAN);
        languageComboBox.setConverter(new StringLocaleConverter());
        artworkImageFormatComboBox.getItems().addAll(ImageType.values());
        dateFormatComboBox.getItems().addAll(NTagFormat.DATE_FORMATS);
        Bindings.bindBidirectional(artworkQualityTextField.textProperty(), artworkQualitySlider.valueProperty(), new NumberStringConverter("0.00"));
        Bindings.bindBidirectional(artworkKilobytesTextField.textProperty(), artworkKilobytesSlider.valueProperty(), new NumberStringConverter("###"));
        Bindings.bindBidirectional(artworkResolutionTextField.textProperty(), artworkResolutionSlider.valueProperty(), new NumberStringConverter("####"));
    }

    // ***
    //
    // hidden implementation
    //
    // ***

    private boolean validate() {
        // Rating Conversion
        if (!validateRatingConversion(id3RatingConversionTextField.getText(), "MP3")) {
            return false;
        }
        if (!validateRatingConversion(mp4RatingConversionTextField.getText(), "MP4")) {
            return false;
        }
        if (!validateRatingConversion(oggRatingConversionTextField.getText(), "OGG")) {
            return false;
        }
        if (!validateRatingConversion(flacRatingConversionTextField.getText(), "FLAC")) {
            return false;
        }
        if (!validateRatingConversion(wmaRatingConversionTextField.getText(), "WMA")) {
            return false;
        }
        // ID3 Rating EMail
        if (id3RatingMailTextField.getText().trim().length() == 0) {
            errorLabel.setText("ID3 Rating EMail is required");
            return false;
        }
        return true;
    }

    private boolean validateRatingConversion(String text, String format) {
        try {
            List<Integer> list = integerListConverter.fromString(text);
            if (list.size() != 10) {
                errorLabel.setText(String.format("%s Rating Conversion: invalid item size %d", format, list.size()));
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                int value = list.get(i);
                if (value < 0 || value > 255) {
                    errorLabel.setText(String.format("%s Rating Conversion: invalid item %d=%d", format, i + 1, value));
                    return false;
                }
                if (i > 0 && value < list.get(i - 1)) {
                    errorLabel.setText(String.format("%s Rating Conversion: invalid item %d=%d", format, i + 1, value));
                    return false;
                }
            }
        } catch (Exception e) {
            errorLabel.setText(String.format("%s Rating Conversion: %s", format, e.getMessage()));
            return false;
        }
        return true;
    }

    // ***
    //
    // public API
    //
    // ***

    @Override
    public void setViewModel(NTagProperties viewModel) {
        super.setViewModel(viewModel);
        // General
        themeComboBox.setValue(viewModel.getTheme());
        languageComboBox.setValue(viewModel.getLanguage());
        binaryRadioButton.setSelected(viewModel.isBinaryUnit());
        decimalRadioButton.setSelected(!viewModel.isBinaryUnit());
        filenameFormatTextField.setText(viewModel.getFilenameFormat());
        filenameStripUnsafeCharsCheckBox.setSelected(viewModel.isFilenameStripUnsafeChars());
        genreFavTextField.setText(stringListConverter.toString(viewModel.getGenreFavorites()));
        dateFormatComboBox.setValue(viewModel.getDateFormat());
        // ID3 Version
        id3v1CheckBox.setSelected(viewModel.isID3v11());
        id3v24RadioButton.setSelected(viewModel.isID3v24());
        id3v23RadioButton.setSelected(!viewModel.isID3v24());
        // ID3 Rating
        id3RatingMailTextField.setText(viewModel.getRatingEMail());
        id3RatingEnforceSingleFrameCheckBox.setSelected(viewModel.isRatingEnforceSingleFrame());
        id3RatingConversionTextField.setText(integerListConverter.toString(viewModel.getID3RatingConversion()));
        // ID3 Black List
        id3FrameBlackListTextField.setText(stringListConverter.toString(viewModel.getID3FrameBlackList()));
        // ID3 Release Date
        tdorCheckBox.setSelected(viewModel.isID3ReleaseDateTDOR());
        tdrlCheckBox.setSelected(viewModel.isID3ReleaseDateTDRL());
        tdrcCheckBox.setSelected(viewModel.isID3ReleaseDateTDRC());
        // MP4
        mp4RatingConversionTextField.setText(integerListConverter.toString(viewModel.getMP4RatingConversion()));
        // OGG
        oggRatingConversionTextField.setText(integerListConverter.toString(viewModel.getOGGRatingConversion()));
        // FLAC
        flacRatingConversionTextField.setText(integerListConverter.toString(viewModel.getFLACRatingConversion()));
        // WMA
        wmaRatingConversionTextField.setText(integerListConverter.toString(viewModel.getWMARatingConversion()));
        // Artwork
        artworkImageFormatComboBox.setValue(viewModel.getArtworkImageType());
        artworkQualitySlider.setValue(viewModel.getArtworkQuality());
        artworkKilobytesSlider.setValue(viewModel.getArtworkMaxKilobytes());
        artworkResolutionSlider.setValue(viewModel.getArtworkMaxResolution());
        artworkEnforceImageTypeCheckBox.setSelected(viewModel.isArtworkEnforceImageFormat());
        artworkEnforceSingleArtworkCheckBox.setSelected(viewModel.isArtworkEnforceSingle());
        // Lyrics
        lyricProviderTextArea.setText(toText(viewModel.getLyricProvider()));
    }

    // ***
    //
    // Events
    //
    // ***

    @Override
    protected void unbindViewModel() {

    }

    @FXML
    private void handleOKAction(final ActionEvent event) {
        if (!validate()) {
            return;
        }
        this.dialogResponse = DialogResponse.SELECTION;
        // General
        viewModel.setTheme(themeComboBox.getValue());
        viewModel.setLanguage(languageComboBox.getValue());
        viewModel.setBinaryUnit(binaryRadioButton.isSelected());
        viewModel.setFilenameFormat(filenameFormatTextField.getText());
        viewModel.setFilenameStripUnsafeChars(filenameStripUnsafeCharsCheckBox.isSelected());
        viewModel.setGenreFavorites(stringListConverter.fromString(genreFavTextField.getText().trim()));
        viewModel.setDateFormat(dateFormatComboBox.getValue());
        // ID3 Version
        viewModel.setID3v11(id3v1CheckBox.isSelected());
        viewModel.setID3v24(id3v24RadioButton.isSelected());
        // ID3 Rating
        viewModel.setRatingEMail(id3RatingMailTextField.getText());
        viewModel.setRatingEnforceSingleFrame(id3RatingEnforceSingleFrameCheckBox.isSelected());
        viewModel.setID3RatingConversion(integerListConverter.fromString(id3RatingConversionTextField.getText()));
        // ID3 Black List
        viewModel.setID3FrameBlackList(stringListConverter.fromString(id3FrameBlackListTextField.getText().toUpperCase()));
        // ID3 Release Date
        viewModel.setID3ReleaseDateTDOR(tdorCheckBox.isSelected());
        viewModel.setID3ReleaseDateTDRC(tdrcCheckBox.isSelected());
        viewModel.setID3ReleaseDateTDRL(tdrlCheckBox.isSelected());
        // MP4
        viewModel.setMP4RatingConversion(integerListConverter.fromString(mp4RatingConversionTextField.getText()));
        // OGG
        viewModel.setOGGRatingConversion(integerListConverter.fromString(oggRatingConversionTextField.getText()));
        // FLAC
        viewModel.setFLACRatingConversion(integerListConverter.fromString(flacRatingConversionTextField.getText()));
        // WMA
        viewModel.setWMARatingConversion(integerListConverter.fromString(wmaRatingConversionTextField.getText()));
        // Artwork
        viewModel.setArworkImageType(artworkImageFormatComboBox.getValue());
        viewModel.setArtworkQuality((float) artworkQualitySlider.getValue());
        viewModel.setArtworkMaxKilobytes((int) artworkKilobytesSlider.getValue());
        viewModel.setArtworkMaxResolution((int) artworkResolutionSlider.getValue());
        viewModel.setArtworkEnforceImageFormat(artworkEnforceImageTypeCheckBox.isSelected());
        viewModel.setArtworkEnforceSingle(artworkEnforceSingleArtworkCheckBox.isSelected());
        // Lyrics
        viewModel.setLyricProvider(toList(lyricProviderTextArea.getText()));

        viewModel.distribute();
        viewModel.distributeFx();
        viewModel.savePreferences();

        this.getStage().close();
        unbindViewModel();
    }

    // ***
    //
    // hidden Implementation
    //
    // ***

    private String toText(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String value : list) {
            sb.append(value).append("\n");
        }
        return sb.toString();
    }

    private List<String> toList(String value) {
        List<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(value);
        while (st.hasMoreTokens()) {
            String line = st.nextToken().trim();
            if (line.length() > 0) {
                list.add(line);
            }
        }
        return list;
    }
}
