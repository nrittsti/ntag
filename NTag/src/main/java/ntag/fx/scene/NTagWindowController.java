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
package ntag.fx.scene;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.IntegerStringConverter;
import ntag.fx.scene.control.converter.FileSizeConverter;
import ntag.fx.scene.control.converter.PlaytimeConverter;
import ntag.fx.scene.control.editor.EditorProperty;
import ntag.fx.scene.control.editor.TagEditorControl;
import ntag.fx.scene.control.tableview.TagFileTableView;
import ntag.io.NTagProperties;
import ntag.model.TagFile;
import ntag.task.AdjustArtworkTask;
import ntag.task.ReadTagFilesTask;
import ntag.task.RenameFilesTask;
import ntag.task.WriteTagFilesTask;
import toolbox.fx.FxUtil;
import toolbox.fx.control.ButtonLink;
import toolbox.fx.dialog.AbstractDialogController;
import toolbox.fx.dialog.DialogResponse;
import toolbox.fx.dialog.DialogResult;
import toolbox.fx.dialog.ProgressDialog;
import toolbox.io.Resources;



public class NTagWindowController extends AbstractDialogController<NTagViewModel> {

	private static final Logger LOGGER = Logger.getLogger(NTagWindowController.class.getName());

	// ***
	//
	// Instance Attributes
	//
	// ***

	private NTagProperties appProperties = new NTagProperties();

	// ***
	//
	// Properties
	//
	// ***

	// *** Stage

	@Override
	public void setStage(Stage stage) {
		assert stage != null : "stage is null";
		this.stage = stage;
		// Toolbar
		openButton.setVisible(stage.getModality() == Modality.NONE);
		openButton.setManaged(stage.getModality() == Modality.NONE);
		// Keyboard Shortcuts
		stage.getScene().setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.F5) {
				keyEvent.consume();
			}
		});
		stage.getScene().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
			if (keyEvent.getCode() == KeyCode.F5) {
				handleRefreshFilter(new ActionEvent(keyEvent.getSource(), null));
			}
		});
	}

	// *** DividerPositions

	public void setDividerPositions(double... positions) {
		assert splitPane != null : "splitPane is null";
		assert positions != null && positions.length == 1 : "invalid positions";
		splitPane.setDividerPositions(positions);
	}

	public double[] getDividerPositions() {
		assert splitPane != null : "splitPane is null";
		return splitPane.getDividerPositions();
	}

	// *** ViewModel

	@Override
	public void setViewModel(NTagViewModel viewModel) {
		if (viewModel == null) {
			throw new IllegalArgumentException("Parameter viewModel cannot be null!");
		}
		this.viewModel = viewModel;
		this.tagFileTableView.setViewModel(viewModel);
		this.tagEditorControl.setViewModel(viewModel);
		// Toolbar
		adjustArtworkButton.disableProperty().bind(Bindings.isEmpty(viewModel.getFiles()));
		numberTracksButton.disableProperty().bind(Bindings.isEmpty(viewModel.getFiles()));
		renameButton.disableProperty().bind(Bindings.isEmpty(viewModel.getFiles()));
		// Statusbar
		fileCountToolbar.textProperty().bindBidirectional(viewModel.fileCountProperty(), new IntegerStringConverter());
		selFileCountToolbar.textProperty().bindBidirectional(viewModel.selectedFileCountProperty(), new IntegerStringConverter());
		playtimeToolbar.textProperty().bindBidirectional(viewModel.playtimeProperty(), new PlaytimeConverter());
		selPlaytimeToolbar.textProperty().bindBidirectional(viewModel.selectedPlaytimeProperty(), new PlaytimeConverter());
		fileSizeToolbar.textProperty().bindBidirectional(viewModel.fileSizeProperty(), new FileSizeConverter());
		selFileSizeToolbar.textProperty().bindBidirectional(viewModel.selectedFileSizeProperty(), new FileSizeConverter());
	}

	// ***
	//
	// FX Attributes
	//
	// ***

	// *** Toolbar

	@FXML
	private Button openButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button adjustArtworkButton;
	@FXML
	private Button numberTracksButton;
	@FXML
	private Button renameButton;
	@FXML
	private Button settingsButton;

	// *** Content Panel

	@FXML
	private TagFileTableView tagFileTableView;
	@FXML
	private TagEditorControl tagEditorControl;
	@FXML
	private SplitPane splitPane;

	// *** Statusbar

	@FXML
	private ButtonLink filterLink;
	@FXML
	private Label fileCountToolbar;
	@FXML
	private Label playtimeToolbar;
	@FXML
	private Label fileSizeToolbar;
	@FXML
	private Label selFileCountToolbar;
	@FXML
	private Label selPlaytimeToolbar;
	@FXML
	private Label selFileSizeToolbar;
	@FXML
	private ButtonLink directoryLink;

	// ***
	//
	// Construction
	//
	// ***

	public NTagWindowController() {
		this.setDialogResponse(DialogResponse.CLOSE);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Toolbar
		EditorProperty.getChangedObjects().addListener((Change<? extends TagFile> change) -> {
			saveButton.setDisable(EditorProperty.getChangedObjects().isEmpty());
		});
		// Statusbar
		directoryLink.textProperty().bind(appProperties.lastDirectoryProperty());
		filterLink.setUserData(NTagFilterMode.All);

	}

	// ***
	//
	// public API
	//
	// ***

	public void readFiles(List<Path> pathList) {
		// Register special LogHandler
		appProperties.getActionLogHandler().clear();

		ReadTagFilesTask task = new ReadTagFilesTask(pathList, appProperties.getMaxFiles(), appProperties.getMaxDepth());
		ProgressDialog<List<TagFile>> dialog = new ProgressDialog<List<TagFile>>(task);
		Thread th = new Thread(task);
		th.start();
		dialog.showAndWait();
		if (task.getState() == State.FAILED) {
			FxUtil.showException("Read Errors", task.getException());
		} else if (task.hasErrors()) {
			FxUtil.showErrors("Read Errors", task.getErrors());
		} else if (!task.isCancelled()) {
			List<TagFile> result = task.getValue();
			viewModel.getFiles().setAll(result);
			EditorProperty.getChangedObjects().clear();
		}
	}

	// ***
	//
	// Event Handler
	//
	// ***

	@FXML
	private void handleOpenAction(final ActionEvent event) {
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle(Resources.get("ntag", "mnu_open"));
		File folder = new File(appProperties.getLastDirectory());
		if(Files.exists(folder.toPath())) {
			fileChooser.setInitialDirectory(folder);
		}
		File file = fileChooser.showDialog(FxUtil.getPrimaryStage());
		if (file == null) {
			return;
		}
		appProperties.setLastDirectory(file.getAbsolutePath());
		ArrayList<Path> pathList = new ArrayList<>();
		pathList.add(file.toPath());
		readFiles(pathList);
	}

	@FXML
	private void handleOpenDirectory(final ActionEvent event) {
		try {
			java.awt.Desktop.getDesktop().browse(Paths.get(appProperties.getLastDirectory()).toUri());
		} catch (Exception ex) {
			FxUtil.showException("Failed to launch this URL", ex);
		}
	}

	@FXML
	private void handleChangeFilter(final ActionEvent event) {
		ChoiceDialog<NTagFilterMode> dialog = new ChoiceDialog<>((NTagFilterMode) filterLink.getUserData(), NTagFilterMode.values());
		dialog.setHeaderText(Resources.get("ntag", "lbl_change_filtermode"));
		dialog.setContentText(Resources.get("ntag", "tb_filter"));
		Optional<NTagFilterMode> result = dialog.showAndWait();
		if (result.isPresent()) {
			filterLink.setUserData(result.get());
			filterLink.setText(result.get().toString());
			viewModel.setFilterMode(result.get());
		}
	}

	@FXML
	private void handleRefreshFilter(final ActionEvent event) {
		viewModel.setFilterMode((NTagFilterMode) filterLink.getUserData());
	}

	@FXML
	private void handleSaveAction(final ActionEvent event) {
		// Register special LogHandler
		appProperties.getActionLogHandler().clear();

		WriteTagFilesTask task = new WriteTagFilesTask(EditorProperty.getChangedObjects());
		ProgressDialog<Integer> dialog = new ProgressDialog<Integer>(task);
		Thread th = new Thread(task);
		th.start();
		dialog.showAndWait();
		if (task.getState() == State.FAILED) {
			FxUtil.showException("Writing Errors", task.getException());
		} else if (task.hasErrors()) {
			FxUtil.showErrors("Writing Errors", task.getErrors());
		}

		viewModel.getUpdatedFiles().addAll(task.getUpdatedFiles());
		EditorProperty.getChangedObjects().removeAll(task.getUpdatedFiles());

		if (stage.getModality() != Modality.NONE) {
			setDialogResponse(DialogResponse.OK);
			getStage().close();
		}
	}

	@FXML
	private void handleAdjustArtworkAction(final ActionEvent event) {
		// Register special LogHandler
		appProperties.getActionLogHandler().clear();

		if (viewModel.getFiles().isEmpty()) {
			return;
		}

		AdjustArtworkViewModel adjArtworkViewModel = new AdjustArtworkViewModel();

		adjArtworkViewModel.setQuality(appProperties.getArtworkQuality());
		adjArtworkViewModel.setMaxResolution(appProperties.getArtworkMaxResolution());
		adjArtworkViewModel.setMaxKilobytes(appProperties.getArtworkMaxKilobytes());
		adjArtworkViewModel.setImageType(appProperties.getArtworkImageType());
		adjArtworkViewModel.setEnforceImageType(appProperties.isArtworkEnforceImageFormat());
		adjArtworkViewModel.setEnforceSingle(appProperties.isArtworkEnforceSingle());

		adjArtworkViewModel.setEmptySelection(viewModel.getSelectedFiles().isEmpty());

		DialogResult<AdjustArtworkViewModel> dresult = FxUtil.showDialog("ntag", "Adjust Artwork", //
				adjArtworkViewModel, getClass().getResource("AdjustArtwork.fxml"), getStage(), 500, 350);
		if (dresult.getRespone() == DialogResponse.SELECTION) {
			adjArtworkViewModel.getFiles().addAll(viewModel.getSelectedFiles());
		} else if (dresult.getRespone() == DialogResponse.ALL) {
			adjArtworkViewModel.getFiles().addAll(viewModel.getFiles());
		} else {
			return;
		}
		AdjustArtworkTask task = new AdjustArtworkTask(adjArtworkViewModel);
		ProgressDialog<List<TagFile>> dialog = new ProgressDialog<List<TagFile>>(task);
		Thread th = new Thread(task);
		th.start();
		dialog.showAndWait();
		if (task.getState() == State.FAILED) {
			FxUtil.showException("Artwork Errors", task.getException());
		} else if (task.hasErrors()) {
			FxUtil.showErrors("Artwork Errors", task.getErrors());
		}
	}

	@FXML
	private void handleNumberTracksAction(final ActionEvent event) {
		appProperties.getActionLogHandler().clear();
		ButtonType[] buttons = null;
		ButtonType buttonTypeAll = new ButtonType(Resources.get("ntag", "btn_all_files"));
		ButtonType buttonTypeSelection = new ButtonType(Resources.get("ntag", "btn_selected_files"));
		if (viewModel.getSelectedFiles().isEmpty()) {
			buttons = new ButtonType[] { buttonTypeAll, ButtonType.CANCEL };
		} else {
			buttons = new ButtonType[] { buttonTypeAll, buttonTypeSelection, ButtonType.CANCEL };
		}
		Alert alert = new Alert(AlertType.CONFIRMATION, Resources.get("ntag", "mnu_number_Tracks"), buttons);
		Optional<ButtonType> result = alert.showAndWait();
		List<TagFile> files = new ArrayList<TagFile>();
		if (result.get() == buttonTypeAll) {
			files.addAll(viewModel.getFiles());
		} else if (result.get() == buttonTypeSelection) {
			files.addAll(viewModel.getSelectedFiles());
		}
		if (files.isEmpty()) {
			return;
		}
		int trackNumber = 1;
		for (TagFile tagFile : files) {
			tagFile.setTrack(trackNumber++);
			tagFile.setTrackTotal(files.size());
		}
		FxUtil.showNotification(Resources.get("ntag", "msg_changes_made"), null);
		refreshSelection();
	}

	@FXML
	private void handleRenameAction(final ActionEvent event) {
		appProperties.getActionLogHandler().clear();
		RenameFilesViewModel renameFilesViewModel = new RenameFilesViewModel();
		if (viewModel.getFiles().isEmpty()) {
			return;
		}
		renameFilesViewModel.setEmptySelection(viewModel.getSelectedFiles().isEmpty());
		renameFilesViewModel.setFormat(appProperties.getFilenameFormat());
		renameFilesViewModel.setStripUnsafeChars(appProperties.isFilenameStripUnsafeChars());
		renameFilesViewModel.setUpdatedFiles(viewModel.getUpdatedFiles());
		DialogResult<RenameFilesViewModel> dresult = FxUtil.showDialog("ntag", Resources.get("ntag", "lbl_renaming_files"), //
				renameFilesViewModel, getClass().getResource("RenameFiles.fxml"), getStage(), 380, 250);
		if (dresult.getRespone() == DialogResponse.SELECTION) {
			renameFilesViewModel.getFiles().addAll(viewModel.getSelectedFiles());
		} else if (dresult.getRespone() == DialogResponse.ALL) {
			renameFilesViewModel.getFiles().addAll(viewModel.getFiles());
		} else {
			return;
		}
		RenameFilesTask task = new RenameFilesTask(renameFilesViewModel);
		ProgressDialog<List<TagFile>> dialog = new ProgressDialog<List<TagFile>>(task);
		Thread th = new Thread(task);
		th.start();
		dialog.showAndWait();
		if (task.getState() == State.FAILED) {
			FxUtil.showException("Rename Errors", task.getException());
		} else if (task.hasErrors()) {
			FxUtil.showErrors("Rename Errors", task.getErrors());
		}
		refreshSelection();
	}

	@FXML
	private void handleSettingsAction(final ActionEvent event) {
		FxUtil.showDialog("ntag", Resources.get("ntag", "mnu_settings"), //
				appProperties, getClass().getResource("NTagSettings.fxml"), getStage(), 600, 480);
	}

	@FXML
	private void handleAboutAction(final ActionEvent event) {
		FxUtil.showAboutDialog("About NTag", //
				appProperties.getVersion(), //
				"GNU General Public License", //
				"http://www.gnu.org/licenses/gpl-3.0.html", //
				"https://ntag.codeplex.com", //
				NTagProperties.getCredits());
	}

	@Override
	public void onCloseRequest(final WindowEvent event) {
		appProperties.saveMainWindowState(this);
		appProperties.getActionLogHandler().close();
	}

	// ***
	//
	// hidden implementation
	//
	// ***

	private void refreshSelection() {
		if (!viewModel.getSelectedFiles().isEmpty()) {
			ArrayList<TagFile> selection = new ArrayList<TagFile>();
			selection.addAll(viewModel.getSelectedFiles());
			viewModel.getSelectedFiles().clear();
			viewModel.getSelectedFiles().addAll(selection);
		}
	}

	@Override
	protected void unbindViewModel() {

	}
}
