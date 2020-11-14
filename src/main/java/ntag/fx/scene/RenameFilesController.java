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
package ntag.fx.scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import ntag.fx.scene.dialog.AbstractDialogController;
import ntag.fx.scene.dialog.DialogResponse;

import java.net.URL;
import java.util.ResourceBundle;

public class RenameFilesController extends AbstractDialogController<RenameFilesViewModel> {

  // ***
  //
  // FXML
  //
  // ***

  @FXML
  private TextField formatTextField;
  @FXML
  private CheckBox filenameStripUnsafeCharsCheckBox;
  @SuppressWarnings("unused")
  @FXML
  private Button allButton;
  @FXML
  private Button selectedButton;

  // ***
  //
  // Constructor
  //
  // ***

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  // ***
  //
  // public API
  //
  // ***

  @Override
  public void setViewModel(RenameFilesViewModel viewModel) {
    super.setViewModel(viewModel);
    formatTextField.textProperty().bindBidirectional(viewModel.formatProperty());
    selectedButton.disableProperty().bind(viewModel.emptySelectionProperty());
    filenameStripUnsafeCharsCheckBox.selectedProperty().bindBidirectional(viewModel.stripUnsafeCharsProperty());
  }

  // ***
  //
  // Event Handling
  //
  // ***

  @Override
  protected void unbindViewModel() {
    formatTextField.textProperty().unbindBidirectional(viewModel.formatProperty());
    filenameStripUnsafeCharsCheckBox.selectedProperty().unbindBidirectional(viewModel.stripUnsafeCharsProperty());
    selectedButton.disableProperty().unbind();
  }

  @FXML
  private void handleAddPlaceHolderAction(final ActionEvent event) {
    String text = formatTextField.getText();
    if (text == null) {
      text = "";
    }
    int pos = formatTextField.getCaretPosition();
    String placeHolder = "%" + ((Button) event.getSource()).getId();
    text = text.substring(0, pos) + placeHolder + text.substring(pos);
    formatTextField.setText(text);
    formatTextField.positionCaret(pos + placeHolder.length());
  }

  @SuppressWarnings("unused")
  @FXML
  private void handleAllAction(final ActionEvent event) {
    this.dialogResponse = DialogResponse.ALL;
    this.getStage().close();
    unbindViewModel();
  }

  @SuppressWarnings("unused")
  @FXML
  private void handleSelectionAction(final ActionEvent event) {
    this.dialogResponse = DialogResponse.SELECTION;
    this.getStage().close();
    unbindViewModel();
  }
}
