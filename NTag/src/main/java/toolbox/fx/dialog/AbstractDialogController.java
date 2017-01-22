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
package toolbox.fx.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class AbstractDialogController<T> implements Initializable {

	protected DialogResponse dialogResponse = DialogResponse.NONE;
	protected Stage stage = null;
	protected T viewModel = null;

	public T getViewModel() {
		return viewModel;
	}

	public void setViewModel(T viewModel) {
		assert viewModel != null : "model is null";
		this.viewModel = viewModel;
	}

	public DialogResponse getDialogResponse() {
		return dialogResponse;
	}

	protected void setDialogResponse(DialogResponse dialogResponse) {
		this.dialogResponse = dialogResponse;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		assert stage != null : "stage is null";
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	protected abstract void unbindViewModel();

	public void onCloseRequest(WindowEvent event) {
		unbindViewModel();
	}

	@FXML
	protected void handleOkAction(final ActionEvent event) {
		this.dialogResponse = DialogResponse.OK;
		this.getStage().close();
		unbindViewModel();
	}

	@FXML
	protected void handleCancelAction(final ActionEvent event) {
		this.dialogResponse = DialogResponse.CANCEL;
		this.getStage().close();
		unbindViewModel();
	}
}
