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
package ntag.fx.scene.dialog;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import ntag.fx.util.FxUtil;

public class ProgressDialog<T> extends Dialog<Task<T>> {

  public ProgressDialog(Task<T> task) {
    if (task == null) {
      throw new IllegalArgumentException("task cannot be null");
    }
    initModality(Modality.APPLICATION_MODAL);
    setGraphic(new ImageView(new Image("icons/toolbox_task.png")));
    getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
    setResizable(true);
    initOwner(FxUtil.getPrimaryStage());

    VBox box = new VBox();
    box.setPadding(new Insets(20, 10, 10, 10));
    getDialogPane().setContent(box);

    Label label = new Label();
    label.textProperty().bind(task.messageProperty());
    box.getChildren().add(label);

    ProgressBar bar = new ProgressBar();
    bar.progressProperty().bind(task.progressProperty());
    bar.setMinWidth(300);
    box.getChildren().add(bar);

    setResult(task);

    task.setOnSucceeded((WorkerStateEvent event) -> close());

    task.setOnCancelled((WorkerStateEvent event) -> close());

    task.setOnFailed((WorkerStateEvent event) -> close());

    setOnCloseRequest((DialogEvent event) -> {
      if (task.isRunning()) {
        event.consume();
      }
    });

    Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
    cancelButton.setOnAction((ActionEvent event) -> task.cancel());
  }
}
