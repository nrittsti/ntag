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

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.util.converter.NumberStringConverter;
import toolbox.fx.control.RegexTextfield;
import toolbox.fx.dialog.AbstractDialogController;
import toolbox.fx.dialog.DialogResponse;
import toolbox.io.ImageUtil.ImageType;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AdjustArtworkController extends AbstractDialogController<AdjustArtworkViewModel> {

	private static final Logger LOGGER = Logger.getLogger(AdjustArtworkController.class.getName());

	// ***
	//
	// FXML
	//
	// ***

	@FXML
	private ComboBox<ImageType> imageFormatComboBox;

	@FXML
	private Slider qualitySlider;

	@FXML
	private RegexTextfield qualityTextField;

	@FXML
	private Slider kilobytesSlider;

	@FXML
	private RegexTextfield kilobytesTextField;

	@FXML
	private Slider resolutionSlider;

	@FXML
	private RegexTextfield resolutionTextField;

	@FXML
	private CheckBox enforceImageTypeCheckBox;

	@FXML
	private CheckBox enforceSingleArtworkCheckBox;

	@FXML
	private Button allButton;
	@FXML
	private Button selectedButton;

	// ***
	//
	// Constructor
	//
	// ***

	public AdjustArtworkController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		imageFormatComboBox.getItems().addAll(ImageType.values());
		Bindings.bindBidirectional(qualityTextField.textProperty(), qualitySlider.valueProperty(), new NumberStringConverter("0.00"));
		Bindings.bindBidirectional(kilobytesTextField.textProperty(), kilobytesSlider.valueProperty(), new NumberStringConverter("###"));
		Bindings.bindBidirectional(resolutionTextField.textProperty(), resolutionSlider.valueProperty(), new NumberStringConverter("####"));
		imageFormatComboBox.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends ImageType> observable, //
						ImageType oldValue, //
						ImageType newValue) -> {
					if (newValue != ImageType.JPG) {
						qualitySlider.setDisable(true);
						qualityTextField.setDisable(true);
					}
				});
	}

	// ***
	//
	// public API
	//
	// ***

	@Override
	public void setViewModel(AdjustArtworkViewModel viewModel) {
		super.setViewModel(viewModel);
		imageFormatComboBox.valueProperty().bindBidirectional(viewModel.imageTypeProp());
		qualitySlider.valueProperty().bindBidirectional(viewModel.qualityProperty());
		kilobytesSlider.valueProperty().bindBidirectional(viewModel.maxKilobytesProperty());
		resolutionSlider.valueProperty().bindBidirectional(viewModel.maxResolutionProperty());
		enforceImageTypeCheckBox.selectedProperty().bindBidirectional(viewModel.enforceImageTypeProperty());
		enforceSingleArtworkCheckBox.selectedProperty().bindBidirectional(viewModel.enforceSingleProperty());
		selectedButton.disableProperty().bind(viewModel.emptySelectionProperty());
	}

	// ***
	//
	// Events
	//
	// ***

	@Override
	protected void unbindViewModel() {
		imageFormatComboBox.valueProperty().unbindBidirectional(viewModel.imageTypeProp());
		qualitySlider.valueProperty().unbindBidirectional(viewModel.qualityProperty());
		kilobytesSlider.valueProperty().unbindBidirectional(viewModel.maxKilobytesProperty());
		resolutionSlider.valueProperty().unbindBidirectional(viewModel.maxResolutionProperty());
		enforceImageTypeCheckBox.selectedProperty().unbindBidirectional(viewModel.enforceImageTypeProperty());
		enforceSingleArtworkCheckBox.selectedProperty().unbindBidirectional(viewModel.enforceSingleProperty());
		selectedButton.disableProperty().unbind();
	}

	@FXML
	private void handleAllAction(final ActionEvent event) {
		this.dialogResponse = DialogResponse.ALL;
		this.getStage().close();
		unbindViewModel();
	}

	@FXML
	private void handleSelectionAction(final ActionEvent event) {
		this.dialogResponse = DialogResponse.SELECTION;
		this.getStage().close();
		unbindViewModel();
	}
}
