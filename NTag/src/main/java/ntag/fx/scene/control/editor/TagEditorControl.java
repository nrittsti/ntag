/**
 * This file is part of NTag (audio file tag editor).
 *
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016, Nico Rittstieg
 */
package ntag.fx.scene.control.editor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import ntag.fx.scene.NTagViewModel;
import ntag.fx.scene.control.ArtworkControl;
import ntag.fx.scene.control.rating.RatingControl;
import ntag.io.NTagProperties;
import ntag.model.Genre;
import ntag.model.TagFile;
import toolbox.fx.FxUtil;
import toolbox.fx.collections.ObservableListLink;
import toolbox.fx.control.ButtonLink;
import toolbox.fx.dialog.DialogResponse;
import toolbox.fx.dialog.DialogResult;
import toolbox.fx.dialog.ItemChoiceViewModel;
import toolbox.fx.util.DummyStringConverter;
import toolbox.fx.validator.SimpleTextFieldValidator;
import toolbox.fx.validator.SimpleTextFieldValidator.ValidationMode;
import toolbox.io.FileUtil;
import toolbox.io.Resources;

public class TagEditorControl extends TabPane implements Initializable {

	private static final Logger LOGGER = Logger.getLogger(TagEditorControl.class.getName());

	// ***
	//
	// Instance Attributes
	//
	// ***

	private NTagViewModel viewModel = null;

	private List<EditorProperty<?>> editorProperties = new ArrayList<>();

	private EmptyCheck<String> emptyStringCheck = null;
	private EmptyCheck<Integer> emptyIntegerCheck = null;

	private ArtworkEditorProperty artworkEditorProperty = null;
	private EditorProperty<Integer> ratingEditorProperty = null;
	private EditorProperty<String> titleEditorProperty = null;
	private EditorProperty<String> artistEditorProperty = null;
	private EditorProperty<String> albumEditorProperty = null;
	private EditorProperty<Integer> trackEditorProperty = null;
	private EditorProperty<Integer> trackTotalEditorProperty = null;
	private EditorProperty<String> albumArtistEditorProperty = null;
	private EditorProperty<Integer> discEditorProperty = null;
	private EditorProperty<Integer> discTotalEditorProperty = null;
	private EditorProperty<String> composerEditorProperty = null;
	private EditorProperty<String> languageEditorProperty = null;
	private EditorProperty<String> commentEditorProperty = null;
	private EditorProperty<Integer> yearEditorProperty = null;
	private EditorProperty<LocalDate> dateEditorProperty = null;
	private EditorProperty<String> genreEditorProperty = null;
	private EditorProperty<Boolean> compilationEditorProperty = null;
	private EditorProperty<String> lyricsEditorProperty = null;

	private NTagProperties appProps;

	// ***
	//
	// FX Attributes
	//
	// ***

	@FXML
	private ScrollPane editorScrollPane;

	@FXML
	private Tab infoTab;
	@FXML
	private Tab lyricsTab;
	@FXML
	private Tab editorTab;

	@FXML
	private ArtworkControl artworkControl;
	@FXML
	private RatingControl ratingControl;
	@FXML
	private TextField filenameTextField;
	@FXML
	private Button showFileButton;
	@FXML
	private Label fileInfoLabel;
	@FXML
	private ETextField titleTextField;
	@FXML
	private EComboBox<String> artistComboBox;
	@FXML
	private EComboBox<String> albumComboBox;
	@FXML
	private EComboBox<Integer> trackComboBox;
	@FXML
	private EComboBox<Integer> trackTotalComboBox;
	@FXML
	private EComboBox<String> albumArtistComboBox;
	@FXML
	private EComboBox<Integer> discComboBox;
	@FXML
	private EComboBox<Integer> discTotalComboBox;
	@FXML
	private EComboBox<String> composerComboBox;
	@FXML
	private EComboBox<String> languageComboBox;
	@FXML
	private ETextField commentTextField;
	@FXML
	private EComboBox<Integer> yearComboBox;
	@FXML
	private Label dateFormatLabel;
	@FXML
	private EComboBox<LocalDate> dateComboBox;
	@FXML
	private EComboBox<String> genreComboBox;
	@FXML
	private CheckBox compilationCheckBox;

	@FXML
	private TextArea lyricsTextArea;
	@FXML
	private VBox lyricsVBox;
	@FXML
	private TextArea infosTextArea;
	@FXML
	private TextArea loggingTextArea;

	// ***
	//
	// Construction
	//
	// ***

	public TagEditorControl() {
		appProps = new NTagProperties();
		// EmptyCheck Implementations
		emptyStringCheck = (String value) -> {
			return value == null || value.length() == 0;
		};
		emptyIntegerCheck = (Integer value) -> {
			return value == null || value <= 0;
		};
		// EditorProperties
		try {
			artworkEditorProperty = new ArtworkEditorProperty();
			editorProperties.add(artworkEditorProperty);
			ratingEditorProperty = new EditorProperty<>("rating", null);
			editorProperties.add(ratingEditorProperty);
			titleEditorProperty = new EditorProperty<>("title", emptyStringCheck);
			editorProperties.add(titleEditorProperty);
			artistEditorProperty = new EditorProperty<>("artist", emptyStringCheck);
			editorProperties.add(artistEditorProperty);
			albumEditorProperty = new EditorProperty<>("album", emptyStringCheck);
			editorProperties.add(albumEditorProperty);
			albumArtistEditorProperty = new EditorProperty<>("albumArtist", emptyStringCheck);
			editorProperties.add(albumArtistEditorProperty);
			trackEditorProperty = new EditorProperty<>("track", emptyIntegerCheck);
			editorProperties.add(trackEditorProperty);
			trackTotalEditorProperty = new EditorProperty<>("trackTotal", emptyIntegerCheck);
			editorProperties.add(trackTotalEditorProperty);
			discEditorProperty = new EditorProperty<>("disc", emptyIntegerCheck);
			editorProperties.add(discEditorProperty);
			discTotalEditorProperty = new EditorProperty<>("discTotal", emptyIntegerCheck);
			editorProperties.add(discTotalEditorProperty);
			composerEditorProperty = new EditorProperty<>("composer", emptyStringCheck);
			editorProperties.add(composerEditorProperty);
			languageEditorProperty = new EditorProperty<>("language", emptyStringCheck);
			editorProperties.add(languageEditorProperty);
			commentEditorProperty = new EditorProperty<>("comment", emptyStringCheck);
			editorProperties.add(commentEditorProperty);
			yearEditorProperty = new EditorProperty<>("year", emptyIntegerCheck);
			editorProperties.add(yearEditorProperty);
			dateEditorProperty = new EditorProperty<>("date", null);
			editorProperties.add(dateEditorProperty);
			genreEditorProperty = new EditorProperty<>("genre", emptyStringCheck);
			editorProperties.add(genreEditorProperty);
			compilationEditorProperty = new EditorProperty<>("compilation", null);
			editorProperties.add(compilationEditorProperty);
			lyricsEditorProperty = new EditorProperty<>("lyrics", emptyStringCheck);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error by creating EditorProperties", e);
		}
		FxUtil.loadControl("ntag", this, "TagEditor.fxml");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editorScrollPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			if (newValue.intValue() > 200) {
				titleTextField.setMinWidth(newValue.doubleValue() - showFileButton.getWidth());
			}
		});
		// Tabs
		lyricsTab.setDisable(true);
		editorTab.setDisable(true);
		infoTab.setDisable(true);
		// Filename
		filenameTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
			if (!newValue && !filenameTextField.isDisabled()) {
				renameSelectedFile();
			}
		});
		// Lyrics
		initialize(lyricsTextArea, lyricsEditorProperty);
		// Artwork
		artworkControl.artworkProperty().bindBidirectional(artworkEditorProperty.valueProperty());
		artworkControl.tooltipProperty().bind(artworkEditorProperty.getTooltip().textProperty());
		artworkControl.setClearEventHandler(artworkEditorProperty.getClearEventHandler());
		// Rating
		initialize(ratingControl, ratingEditorProperty);
		// Title
		initialize(titleTextField, titleEditorProperty);
		// Artist
		initialize(artistComboBox, artistEditorProperty);
		// Album
		initialize(albumComboBox, albumEditorProperty);
		// Album Artist
		initialize(albumArtistComboBox, albumArtistEditorProperty);
		// Track
		initialize(trackComboBox, trackEditorProperty, new IntegerStringConverter());
		new SimpleTextFieldValidator(trackComboBox.getComboBox().getEditor(), ValidationMode.UInteger, 3);
		// TrackTotal
		initialize(trackTotalComboBox, trackTotalEditorProperty, new IntegerStringConverter());
		new SimpleTextFieldValidator(trackTotalComboBox.getComboBox().getEditor(), ValidationMode.UInteger, 3);
		// Disc
		initialize(discComboBox, discEditorProperty, new IntegerStringConverter());
		new SimpleTextFieldValidator(discComboBox.getComboBox().getEditor(), ValidationMode.UInteger, 3);
		// DiscTotal
		initialize(discTotalComboBox, discTotalEditorProperty, new IntegerStringConverter());
		new SimpleTextFieldValidator(discTotalComboBox.getComboBox().getEditor(), ValidationMode.UInteger, 3);
		// Composer
		initialize(composerComboBox, composerEditorProperty);
		// Language
		initialize(languageComboBox, languageEditorProperty);
		// Comment
		initialize(commentTextField, commentEditorProperty);
		// Year
		initialize(yearComboBox, yearEditorProperty, new IntegerStringConverter());
		new SimpleTextFieldValidator(yearComboBox.getComboBox().getEditor(), ValidationMode.UInteger, 4);
		// Date
		dateFormatLabel.setText("  (" + ((SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())).toPattern() + ")");
		initialize(dateComboBox, dateEditorProperty, new LocalDateStringConverter());
		new SimpleTextFieldValidator(dateComboBox.getComboBox().getEditor(), ValidationMode.LocalDate, 10);
		// Genre
		initialize(genreComboBox, genreEditorProperty);
		// Compilation
		initialize(compilationCheckBox, compilationEditorProperty);
		// Lyric Provider
		for (String provider : appProps.getLyricProvider()) {
			ButtonLink button = new ButtonLink();
			try {
				button.setText(new URI(provider).getHost());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Cannot create URI", e);
			}
			button.setUserData(provider);
			button.setOnAction((ActionEvent event) -> {
				handleFindLyricsAction(event);
			});
			lyricsVBox.getChildren().add(button);
		}
	}

	private static <T> void initialize(CheckBox checkBox, EditorProperty<Boolean> editorProp) {
		checkBox.styleProperty().bind(editorProp.styleProperty());
		checkBox.selectedProperty().bindBidirectional(editorProp.valueProperty());
		checkBox.setAllowIndeterminate(true);
		checkBox.tooltipProperty().bind(editorProp.tooltipProperty());
		editorProp.differentProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
			checkBox.setIndeterminate(newValue);
		});
	}

	private static void initialize(ETextField textField, EditorProperty<String> editorProp) {
		textField.styleProperty().bind(editorProp.styleProperty());
		textField.getTextField().textProperty().bindBidirectional(editorProp.valueProperty());
		textField.getTextField().tooltipProperty().bind(editorProp.tooltipProperty());
		textField.setClearEventHandler(editorProp.getClearEventHandler());
	}

	private static void initialize(TextArea textField, EditorProperty<String> editorProp) {
		textField.styleProperty().bind(editorProp.styleProperty());
		textField.textProperty().bindBidirectional(editorProp.valueProperty());
		textField.tooltipProperty().bind(editorProp.tooltipProperty());
	}

	private static void initialize(RatingControl control, EditorProperty<Integer> editorProp) {
		control.styleProperty().bind(editorProp.styleProperty());
		control.ratingProperty().bindBidirectional(editorProp.valueProperty());
		control.tooltipProperty().bind(editorProp.tooltipProperty());
	}

	private static <T> void initialize(EComboBox<T> combo, EditorProperty<T> editorProp) {
		initialize(combo, editorProp, null);
	}

	private static <T> void initialize(EComboBox<T> combo, EditorProperty<T> editorProp, StringConverter<T> converter) {
		combo.styleProperty().bind(editorProp.styleProperty());
		combo.getComboBox().valueProperty().bindBidirectional(editorProp.valueProperty());
		if (converter != null) {
			combo.getComboBox().setConverter(converter);
		}
		new ObservableListLink<>(combo.getComboBox().getItems(), editorProp.getValues());
		combo.getComboBox().setTooltip(new Tooltip());
		combo.getComboBox().tooltipProperty().bind(editorProp.tooltipProperty());
		combo.setClearEventHandler(editorProp.getClearEventHandler());
	}

	// ***
	//
	// public API
	//
	// ***

	public void setViewModel(NTagViewModel viewModel) {
		assert viewModel != null : "viewModel is null";
		this.viewModel = viewModel;
		NTagProperties appProperties = new NTagProperties();
		viewModel.getSelectedFiles().addListener((Change<? extends TagFile> change) -> {
			updateEditorProperties();
		});
		loggingTextArea.textProperty().bind(appProperties.getActionLogHandler().textProperty());
	}

	// ***
	//
	// Event Handler
	//
	// ***

	@FXML
	private void handleShowFileAction(final ActionEvent event) {
		if (viewModel.getSelectedFiles().size() == 0) {
			return;
		}
		try {
			Runtime.getRuntime().exec(String.format("explorer.exe /select,%s", //
					viewModel.getSelectedFiles().get(0).getPath().toString()));
		} catch (Exception e) {
			FxUtil.showException(e.getClass().getSimpleName(), e);
		}
	}

	@FXML
	private void handleSelectGenreButton(final ActionEvent event) {
		ItemChoiceViewModel<String> vm = new ItemChoiceViewModel<>();
		vm.setSingleSelection(true);
		vm.setStringConverter(new DummyStringConverter());
		vm.getItems().addAll(Genre.getAllGenreAsString());
		vm.getFavorites().addAll(appProps.getGenreFavorites());
		if (genreEditorProperty.getValue() != null) {
			vm.getSelection().add(genreEditorProperty.getValue());
		}
		DialogResult<ItemChoiceViewModel<String>> result = null;
		result = FxUtil.showItemChoiceDialog(Resources.get("ntag", "lbl_select_genre"), null, vm);
		if (result.getRespone() == DialogResponse.OK) {
			if (vm.getSelection().isEmpty()) {
				genreEditorProperty.getClearEventHandler().handle(null);
			} else {
				genreEditorProperty.setValue(vm.getSelection().get(0));
			}
		}
	}

	private void handleFindLyricsAction(final ActionEvent event) {
		ButtonLink button = (ButtonLink) event.getSource();
		String provider = button.getUserData().toString();
		TagFile tagFile = this.viewModel.getSelectedFiles().get(0);
		StringBuilder sb = new StringBuilder(80);
		if (tagFile.getTitle() != null && tagFile.getTitle().length() > 0) {
			sb.append(tagFile.getTitle());
		}
		if (tagFile.getArtist() != null && tagFile.getArtist().length() > 0) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(tagFile.getArtist());
		}
		try {
			provider = provider.replace("input", URLEncoder.encode(sb.toString(), "UTF-8"));
			java.awt.Desktop.getDesktop().browse(new URI(provider));
		} catch (Exception ex) {
			FxUtil.showException("Failed to launch this URL", ex);
		}
	}

	// ***
	//
	// hidden implementation
	//
	// ***

	private void updateEditorProperties() {
		for (EditorProperty<?> editorProperty : editorProperties) {
			try {
				editorProperty.setObjects(viewModel.getSelectedFiles());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error on updating Editor Property " + editorProperty.getName(), e);
			}
		}
		if (viewModel.getSelectedFiles().size() == 0) {
			editorTab.setDisable(true);
		} else {
			editorTab.setDisable(false);
		}
		if (viewModel.getSelectedFiles().size() == 1) {
			TagFile selectedFile = viewModel.getSelectedFiles().get(0);
			infoTab.setDisable(false);
			lyricsTab.setDisable(false);
			try {
				lyricsEditorProperty.setObjects(viewModel.getSelectedFiles());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error on updating Editor Property " + lyricsEditorProperty.getName(), e);
			}
			infosTextArea.setText(viewModel.getSelectedFiles().get(0).getInfos());
			filenameTextField.setDisable(false);
			showFileButton.setDisable(false);
			filenameTextField.setText(viewModel.getSelectedFiles().get(0).getName());
			fileInfoLabel.setText(selectedFile.isReadOnly() ? //
					String.format(" (%s)", Resources.get("ntag", "lbl_read_only")) : "");
		} else {
			infoTab.setDisable(true);
			lyricsTab.setDisable(true);
			filenameTextField.setText("");
			filenameTextField.setDisable(true);
			showFileButton.setDisable(true);
			fileInfoLabel.setText("");

		}
	}

	private void renameSelectedFile() {
		TagFile tagFile = viewModel.getSelectedFiles().get(0);
		String oldName = tagFile.getName();
		String fileName = filenameTextField.getText().trim();
		if (new NTagProperties().isFilenameStripUnsafeChars()) {
			fileName = FileUtil.removeInvalidChars(fileName);
		}
		if (!fileName.equals(oldName)) {
			Path path = tagFile.getPath();
			try {
				Files.move(path, path.resolveSibling(fileName));
				tagFile.setName(fileName);
				filenameTextField.setText(fileName);
				viewModel.getUpdatedFiles().add(tagFile);
			} catch (IOException e) {
				FxUtil.showException("Failed to rename selected file: " + e.getMessage(), e);
			}
		}
	}
}
