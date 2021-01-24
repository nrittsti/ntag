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
package ntag.fx.scene.control.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import ntag.NTagException;
import ntag.fx.scene.NTagViewModel;
import ntag.fx.scene.control.ArtworkControl;
import ntag.fx.scene.control.button.ButtonLink;
import ntag.fx.scene.control.converter.DummyStringConverter;
import ntag.fx.scene.control.rating.RatingControl;
import ntag.fx.scene.dialog.DialogResponse;
import ntag.fx.scene.dialog.DialogResult;
import ntag.fx.scene.dialog.ItemChoiceViewModel;
import ntag.fx.util.FxUtil;
import ntag.fx.util.TagFieldInputDialogs;
import ntag.fx.validator.SimpleTextFieldValidator;
import ntag.fx.validator.SimpleTextFieldValidator.ValidationMode;
import ntag.io.NTagProperties;
import ntag.io.Resources;
import ntag.io.TagFileReader;
import ntag.io.util.FileUtil;
import ntag.model.Genre;
import ntag.model.TagFile;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.reference.ID3V2Version;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ntag.fx.util.FxUtil.openURI;

public class TagEditorControl extends TabPane implements Initializable {

  private static final Logger LOGGER = Logger.getLogger(TagEditorControl.class.getName());

  // ***
  //
  // Instance Attributes
  //
  // ***

  private NTagViewModel viewModel = null;

  private final List<EditorProperty<?>> editorProperties = new ArrayList<>();

  private ArtworkEditorProperty artworkEditorProperty;
  private final EditorProperty<Integer> ratingEditorProperty;
  private final EditorProperty<String> titleEditorProperty;
  private final EditorProperty<String> artistEditorProperty;
  private final EditorProperty<String> albumEditorProperty;
  private final EditorProperty<Integer> trackEditorProperty;
  private final EditorProperty<Integer> trackTotalEditorProperty;
  private final EditorProperty<String> albumArtistEditorProperty;
  private final EditorProperty<Integer> discEditorProperty;
  private final EditorProperty<Integer> discTotalEditorProperty;
  private final EditorProperty<String> composerEditorProperty;
  private final EditorProperty<String> languageEditorProperty;
  private final EditorProperty<String> commentEditorProperty;
  private final EditorProperty<Integer> yearEditorProperty;
  private final EditorProperty<LocalDate> dateEditorProperty;
  private final EditorProperty<String> genreEditorProperty;
  private final EditorProperty<Boolean> compilationEditorProperty;
  private final EditorProperty<String> lyricsEditorProperty;

  private final NTagProperties appProps;

  // ***
  //
  // FX Attributes
  //
  // ***

  @FXML
  @SuppressWarnings("unused")
  private ScrollPane editorScrollPane;

  @FXML
  @SuppressWarnings("unused")
  private Tab headerTab;
  @FXML
  @SuppressWarnings("unused")
  private Tab tagTab;
  @FXML
  @SuppressWarnings("unused")
  private Tab lyricsTab;
  @FXML
  @SuppressWarnings("unused")
  private Tab editorTab;

  @FXML
  @SuppressWarnings("unused")
  private ArtworkControl artworkControl;
  @FXML
  @SuppressWarnings("unused")
  private RatingControl ratingControl;
  @FXML
  @SuppressWarnings("unused")
  private TextField filenameTextField;
  @FXML
  @SuppressWarnings("unused")
  private Button showFileButton;
  @FXML
  @SuppressWarnings("unused")
  private Label fileInfoLabel;
  @FXML
  @SuppressWarnings("unused")
  private ETextField titleTextField;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<String> artistComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<String> albumComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<Integer> trackComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<Integer> trackTotalComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<String> albumArtistComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<Integer> discComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<Integer> discTotalComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<String> composerComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<String> languageComboBox;
  @FXML
  @SuppressWarnings("unused")
  private ETextField commentTextField;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<Integer> yearComboBox;
  @FXML
  @SuppressWarnings("unused")
  private Label dateFormatLabel;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<LocalDate> dateComboBox;
  @FXML
  @SuppressWarnings("unused")
  private EComboBox<String> genreComboBox;
  @FXML
  @SuppressWarnings("unused")
  private CheckBox compilationCheckBox;

  @FXML
  @SuppressWarnings("unused")
  private TextArea lyricsTextArea;
  @FXML
  @SuppressWarnings("unused")
  private TableView<TagField> tagTableView;
  @FXML
  @SuppressWarnings("unused")
  private MenuItem removeTagMenuItem;
  @FXML
  @SuppressWarnings("unused")
  private MenuItem editTagMenuItem;
  @FXML
  @SuppressWarnings("unused")
  private RadioMenuItem id3v23MenuItem;
  @FXML
  @SuppressWarnings("unused")
  private RadioMenuItem id3v24MenuItem;
  @FXML
  @SuppressWarnings("unused")
  private TableColumn<TagField, String> tagIdColumn;
  @FXML
  @SuppressWarnings("unused")
  private TableColumn<TagField, String> tagValueColumn;
  @FXML
  @SuppressWarnings("unused")
  private VBox lyricsVBox;
  @FXML
  @SuppressWarnings("unused")
  private TextArea infosTextArea;
  @FXML
  @SuppressWarnings("unused")
  private TextArea loggingTextArea;

  // ***
  //
  // Construction
  //
  // ***

  public TagEditorControl() {
    appProps = NTagProperties.instance();
    // EmptyCheck Implementations
    EmptyCheck<String> emptyStringCheck = (String value) -> value == null || value.length() == 0;
    EmptyCheck<Integer> emptyIntegerCheck = (Integer value) -> value == null || value <= 0;
    // EditorProperties
    try {
      artworkEditorProperty = new ArtworkEditorProperty();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error by creating ArtworkEditorProperty", e);
    }
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

    FxUtil.loadControl("ntag", this, "/fxml/TagEditor.fxml");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    editorScrollPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
      if (newValue.intValue() > 200) {
        titleTextField.setMinWidth(newValue.doubleValue() - showFileButton.getWidth());
      }
    });
    this.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
      if (viewModel.getSelectedFileCount() == 1) {
        try {
          tagTableView.getItems().setAll(viewModel.getSelectedFiles().get(0).getTags());
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Error on updating TagTable", e);
        }
      } else {
        tagTableView.getItems().clear();
      }
    });
    // Tabs
    lyricsTab.setDisable(true);
    editorTab.setDisable(true);
    headerTab.setDisable(true);
    tagTab.setDisable(true);
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
    dateFormatLabel.setText(" (" + appProps.getDateFormat() + ")");
    initialize(dateComboBox, dateEditorProperty, new LocalDateStringConverter(DateTimeFormatter.ofPattern(appProps.getDateFormat()), null));
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
      button.setOnAction(this::handleFindLyricsAction);
      lyricsVBox.getChildren().add(button);
    }
    // Tag TableView
    tagTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    tagIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    tagValueColumn.setCellValueFactory(new TagFieldCellFactory());
    removeTagMenuItem.disableProperty().bind(tagTableView.getSelectionModel().selectedItemProperty().isNull());
    editTagMenuItem.disableProperty().bind(tagTableView.getSelectionModel().selectedItemProperty().isNull());
    tagTableView.setOnMouseClicked((MouseEvent event) -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !tagTableView.getSelectionModel().isEmpty()) {
        handleEditTagAction(null);
      }
    });
    tagTableView.setOnKeyPressed((KeyEvent event) -> {
      if ((event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.SUBTRACT) && !tagTableView.getSelectionModel().isEmpty()) {
        handleRemoveTagAction(null);
      } else if (event.getCode() == KeyCode.ENTER && !tagTableView.getSelectionModel().isEmpty()) {
        handleEditTagAction(null);
      } else if (event.getCode() == KeyCode.N && event.isControlDown()) {
        handleNewTagAction(null);
      }
    });
  }

  private static void initialize(CheckBox checkBox, EditorProperty<Boolean> editorProp) {
    checkBox.styleProperty().bind(editorProp.styleProperty());
    checkBox.selectedProperty().bindBidirectional(editorProp.valueProperty());
    checkBox.setAllowIndeterminate(true);
    checkBox.tooltipProperty().bind(editorProp.tooltipProperty());
    editorProp.differentProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> checkBox.setIndeterminate(newValue));
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
    Bindings.bindContentBidirectional(editorProp.getValues(), combo.getComboBox().getItems());
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
    NTagProperties appProperties = NTagProperties.instance();
    viewModel.getSelectedFiles().addListener((Change<? extends TagFile> change) -> updateEditorProperties());
    loggingTextArea.textProperty().bind(appProperties.getActionLogHandler().textProperty());
  }

  // ***
  //
  // Event Handler
  //
  // ***

  @FXML
  @SuppressWarnings("unused")
  private void handleShowFileAction(final ActionEvent event) {
    if (viewModel.getSelectedFiles().size() == 0) {
      return;
    }
    try {
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        Runtime.getRuntime().exec(String.format("explorer.exe /select,%s",
                viewModel.getSelectedFiles().get(0).getPath().toString()));
      } else {
        URI uri = viewModel.getSelectedFiles().get(0).getPath().getParent().toUri();
        openURI(uri);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to open URI", e);
      FxUtil.showException(e.getClass().getSimpleName(), e);
    }
  }

  @FXML
  @SuppressWarnings("unused")
  private void handleSelectGenreButton(final ActionEvent event) {
    ItemChoiceViewModel<String> vm = new ItemChoiceViewModel<>();
    vm.setSingleSelection(true);
    vm.setStringConverter(new DummyStringConverter());
    vm.getItems().addAll(Genre.getAllGenreAsString());
    vm.getFavorites().addAll(appProps.getGenreFavorites());
    if (genreEditorProperty.getValue() != null) {
      vm.getSelection().add(genreEditorProperty.getValue());
    }
    DialogResult<ItemChoiceViewModel<String>> result;
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
    final String encodedProvider = provider.replace("input", URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8));
    try {
      openURI(encodedProvider);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to open URI", e);
      FxUtil.showException("Failed to open URI", e);
    }
  }

  @FXML
  @SuppressWarnings({"unused", "SameParameterValue"})
  private void handleRemoveTagAction(final ActionEvent event) {
    TagFile selectedFile = viewModel.getSelectedFiles().get(0);
    TagField tagField = tagTableView.getSelectionModel().getSelectedItem();
    try {
      selectedFile.removeTag(tagField);
      new TagFileReader().updateTagFile(selectedFile, false);
      viewModel.getSelectedFiles().clear();
      viewModel.getSelectedFiles().add(selectedFile);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Delete Tag has failed", e);
      FxUtil.showException("Delete Tag has failed", e);
    }
  }

  @FXML
  @SuppressWarnings({"unused", "SameParameterValue"})
  private void handleEditTagAction(final ActionEvent event) {
    TagFile selectedFile = viewModel.getSelectedFiles().get(0);
    TagField tagField = tagTableView.getSelectionModel().getSelectedItem();
    try {
      if (!TagFieldInputDialogs.showTagFieldEditor(tagField)) {
        return;
      }
    } catch (NTagException e) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Tag Editor Error");
      alert.setHeaderText(e.getMessage());
      alert.showAndWait();
      return;
    }
    try {
      selectedFile.getAudioFile().commit();
      new TagFileReader().updateTagFile(selectedFile, false);
      viewModel.getSelectedFiles().clear();
      viewModel.getSelectedFiles().add(selectedFile);
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info(String.format("Updated %s Tag from file '%s'", tagField.getId(), selectedFile.getName()));
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Edit Tag has failed", e);
      FxUtil.showException("Edit Tag has failed", e);
    }
  }

  @FXML
  @SuppressWarnings({"unused", "SameParameterValue"})
  private void handleNewTagAction(final ActionEvent event) {
    TagFile selectedFile = viewModel.getSelectedFiles().get(0);
    try {
      if (!TagFieldInputDialogs.showNewTagFieldWizard(selectedFile.getAudioFile())) {
        return;
      }
      selectedFile.getAudioFile().commit();
      new TagFileReader().updateTagFile(selectedFile, false);
      viewModel.getSelectedFiles().clear();
      viewModel.getSelectedFiles().add(selectedFile);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "New Tag has failed", e);
      FxUtil.showException("New Tag has failed", e);
    }
  }

  @FXML
  @SuppressWarnings("unused")
  private void handleChangeID3Version(final ActionEvent event) {
    TagFile selectedFile = viewModel.getSelectedFiles().get(0);
    MP3File mp3 = (MP3File) selectedFile.getAudioFile();
    boolean changed = false;
    if (id3v23MenuItem.isSelected() && !selectedFile.getTaggingSystem().equals(ID3v23Tag.class.getSimpleName())) {
      mp3.setTag(mp3.convertTag(mp3.getTagOrCreateDefault(), ID3V2Version.ID3_V23));
      changed = true;
    } else if (id3v24MenuItem.isSelected() && !selectedFile.getTaggingSystem().equals(ID3v24Tag.class.getSimpleName())) {
      mp3.setTag(mp3.convertTag(mp3.getTagOrCreateDefault(), ID3V2Version.ID3_V24));
      changed = true;
    }
    if (changed) {
      try {
        mp3.commit();
        new TagFileReader().updateTagFile(selectedFile, false);
        viewModel.getSelectedFiles().clear();
        viewModel.getSelectedFiles().add(selectedFile);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Can't convert tag", e);
        FxUtil.showException("Can't convert tag", e);
      }
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
    editorTab.setDisable(viewModel.getSelectedFiles().size() == 0);
    if (viewModel.getSelectedFiles().size() == 1) {
      TagFile selectedFile = viewModel.getSelectedFiles().get(0);
      headerTab.setDisable(false);
      tagTab.setDisable(false);
      lyricsTab.setDisable(false);
      try {
        lyricsEditorProperty.setObjects(viewModel.getSelectedFiles());
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error on updating Editor Property " + lyricsEditorProperty.getName(), e);
      }
      infosTextArea.setText(selectedFile.getInfos());
      if (getSelectionModel().getSelectedItem() == tagTab) {
        try {
          tagTableView.getItems().setAll(selectedFile.getTags());
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Error on updating TagTable", e);
        }
      }
      filenameTextField.setDisable(false);
      showFileButton.setDisable(false);
      filenameTextField.setText(viewModel.getSelectedFiles().get(0).getName());
      fileInfoLabel.setText(selectedFile.isReadOnly() ? //
              String.format(" (%s)", Resources.get("ntag", "lbl_read_only")) : "");
      id3v23MenuItem.setVisible(selectedFile.getTaggingSystem().startsWith("ID3v2"));
      id3v24MenuItem.setVisible(selectedFile.getTaggingSystem().startsWith("ID3v2"));
      if (selectedFile.getTaggingSystem().startsWith(ID3v23Tag.class.getSimpleName())) {
        id3v23MenuItem.setSelected(true);
      } else if (selectedFile.getTaggingSystem().startsWith(ID3v24Tag.class.getSimpleName())) {
        id3v24MenuItem.setSelected(true);
      }
    } else {
      tagTab.setDisable(true);
      lyricsTab.setDisable(true);
      filenameTextField.setText("");
      filenameTextField.setDisable(true);
      showFileButton.setDisable(true);
      fileInfoLabel.setText("");
      tagTableView.getItems().clear();
      id3v23MenuItem.setVisible(false);
      id3v24MenuItem.setVisible(false);
    }
  }

  private void renameSelectedFile() {
    TagFile tagFile = viewModel.getSelectedFiles().get(0);
    String oldName = tagFile.getName();
    String fileName = filenameTextField.getText().trim();
    if (NTagProperties.instance().isFilenameStripUnsafeChars()) {
      fileName = FileUtil.sanitizeFilename(fileName);
    }
    if (!fileName.equals(oldName)) {
      Path path = tagFile.getPath();
      try {
        Path newPath = path.resolveSibling(fileName);
        Files.move(path, newPath);
        tagFile.setName(fileName);
        tagFile.setPath(newPath);
        filenameTextField.setText(fileName);
        viewModel.getUpdatedFiles().add(tagFile);
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Failed to rename selected file: " + e.getMessage(), e);
        FxUtil.showException("Failed to rename selected file: " + e.getMessage(), e);
      }
    }
  }
}
