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
package ntag.fx.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import ntag.fx.scene.control.button.ButtonLink;
import ntag.fx.scene.dialog.AbstractDialogController;
import ntag.fx.scene.dialog.DialogResponse;
import ntag.fx.scene.dialog.DialogResult;
import ntag.fx.scene.dialog.ItemChoiceViewModel;
import ntag.io.Resources;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FxUtil {

  private static final Logger LOGGER = Logger.getLogger(FxUtil.class.getName());
  private static Stage primaryStage;

  public static Stage getPrimaryStage() {
    return primaryStage;
  }

  public static void setPrimaryStage(Stage primaryStage) {
    assert primaryStage != null : "Fatal: primaryStage is null";
    FxUtil.primaryStage = primaryStage;
  }

  public static void showNotification(String msg, Window owner) {
    showNotification(msg, owner, 1000);
  }

  public static void showNotification(String msg, Window owner, int duration) {
    if (primaryStage == null) {
      return;
    }
    Stage stage = new Stage(StageStyle.TRANSPARENT);
    if (owner != null) {
      stage.initOwner(owner);
    } else {
      stage.initOwner(primaryStage);
    }
    // show popup below the mousepointer
    Point mousePos = MouseInfo.getPointerInfo().getLocation();
    stage.setX(mousePos.x);
    stage.setY(mousePos.y - 80);
    // fx layouting
    HBox hbox = new HBox(10);
    hbox.setStyle("-fx-border-color: black;-fx-border-width: 2;");
    Scene scene = new Scene(hbox);
    stage.setScene(scene);
    hbox.setAlignment(Pos.CENTER_LEFT);
    hbox.setPadding(new Insets(10, 10, 10, 10));
    hbox.getChildren().add(new ImageView(new Image("icons/toolbox_notification.png")));
    Label label = new Label(msg);
    label.setStyle("-fx-font-size: 11pt;");
    hbox.getChildren().add(label);
    // close popup after one second
    Runnable r = () -> {
      try {
        Thread.sleep(duration);
      } catch (InterruptedException ignored) {
      }
      Runnable later = stage::close;
      Platform.runLater(later);
    };
    stage.show();
    new Thread(r).start();
  }

  public static <T> DialogResult<T> showDialog(String bundle, String title, T model, URL fxml, Window owner, double width, double height) {
    if (owner == null) {
      owner = primaryStage;
    }
    try {
      // Load the fxml file and create a new stage for the popup dialog.
      FXMLLoader loader = new FXMLLoader();
      loader.setResources(Resources.getResourceBundle(bundle));
      loader.setLocation(fxml);
      Parent page = loader.load();
      // Create the dialog Stage (=Window)
      Stage dialogStage = new Stage(StageStyle.UTILITY);
      dialogStage.setWidth(width);
      dialogStage.setHeight(height);
      dialogStage.setTitle(title);
      Scene scene = new Scene(page);
      scene.getStylesheets().addAll(owner.getScene().getStylesheets());
      dialogStage.setScene(scene);
      // set modality
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.initOwner(owner);
      dialogStage.setX(owner.getX() + owner.getWidth() / 2 - dialogStage.getWidth() / 2);
      dialogStage.setY(owner.getY() + owner.getHeight() / 2 - dialogStage.getHeight() / 2);
      // Set the model into the controller.
      AbstractDialogController<T> controller = loader.getController();
      assert controller != null : "Fatal: Controller is null";
      controller.setStage(dialogStage);
      controller.setViewModel(model);
      // set handler for OnCloseRequest
      dialogStage.setOnCloseRequest(controller::onCloseRequest);
      // Show the dialog and wait until the user closes it
      dialogStage.showAndWait();

      return new DialogResult<>(controller.getDialogResponse(), controller.getViewModel());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, String.format("Loading FXML '%s' failed:", fxml), e);
    }
    return new DialogResult<>(DialogResponse.NONE, null);
  }

  public static void loadControl(String bundle, Object controller, String fxml) {
    try {
      FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(fxml));
      loader.setResources(Resources.getResourceBundle(bundle));
      loader.setRoot(controller);
      loader.setController(controller);
      loader.load();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, String.format("Loading FXML '%s' failed:", fxml), e);
    }
  }

  public static void showException(String msg, Throwable e) {
    LOGGER.log(Level.SEVERE, msg, e);
    // Create expandable Exception.
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    showErrors(msg, new StringBuilder(sw.toString()));
  }

  public static void showErrors(String msg, Collection<String> errors) {
    StringBuilder sb = new StringBuilder();
    for (String error : errors) {
      sb.append(error).append("\n");
    }
    showErrors(msg, sb);
  }

  public static void showErrors(String msg, StringBuilder sb) {
    Alert alert = new Alert(AlertType.ERROR);
    if (primaryStage != null) {
      alert.getDialogPane().getStylesheets().addAll(primaryStage.getScene().getStylesheets());
    }
    alert.setHeaderText(Resources.get("ntag", "msg_exception"));
    alert.setContentText(msg);
    TextArea textArea = new TextArea(sb.toString());
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    alert.getDialogPane().setExpandableContent(textArea);
    alert.showAndWait();
  }

  public static void showAboutDialog(String title, Stage parent, String version, String licenceShortname, String licenceUrl, String home, String credits) {

    // Create the custom dialog.
    Dialog<Boolean> dialog = new Dialog<>();
    dialog.initOwner(parent);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setResizable(true);
    dialog.setTitle(title);
    dialog.setHeaderText(title);
    dialog.initStyle(StageStyle.UTILITY);
    dialog.getDialogPane().getScene().getStylesheets().addAll(parent.getScene().getStylesheets());

    // Logo Image
    if (primaryStage != null && primaryStage.getIcons().size() > 0) {
      Image choosen = null;
      for (Image icon : primaryStage.getIcons()) {
        if (choosen == null || icon.getWidth() > choosen.getWidth()) {
          choosen = icon;
        }
      }
      dialog.setGraphic(new ImageView(choosen));
    }

    // Set the button types.
    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

    // Create fields
    GridPane grid = new GridPane();
    dialog.getDialogPane().setContent(grid);
    grid.setPadding(new Insets(20, 15, 10, 15));
    grid.setHgap(10);
    grid.setVgap(0);
    // Version
    grid.add(new Label("Version:"), 0, 0);
    Label versionLabel = new Label(version);
    versionLabel.setId("versionLabel");
    grid.add(versionLabel, 1, 0);
    // Lincence
    if (licenceShortname != null && licenceShortname.length() > 0) {
      ButtonLink licenceLink = new ButtonLink();
      if (licenceUrl != null && licenceUrl.length() > 0) {
        licenceLink.setOnAction((ActionEvent e) -> openURI(licenceUrl));
      }
      licenceLink.setText(licenceShortname);
      grid.add(new Label("Licence:"), 0, 1);
      grid.add(licenceLink, 1, 1);
    }
    // Homepage
    if (home != null && home.length() > 0) {
      grid.add(new Label("Home:"), 0, 2);
      ButtonLink homeLink = new ButtonLink();
      homeLink.setOnAction((ActionEvent e) -> openURI(home));
      homeLink.setText(home);
      grid.add(homeLink, 1, 2);
    }
    // Credits
    if (credits != null && credits.length() > 0) {
      grid.add(new Label("Credits:"), 0, 3);
      TextArea ta = new TextArea(credits);
      ta.setEditable(false);
      ta.setWrapText(true);
      grid.add(ta, 0, 4, 2, 1);
    }
    dialog.setWidth(300);
    dialog.setHeight(400);

    dialog.showAndWait();
  }

  public static void openURI(String uri) {
    try {
      openURI(new URI(uri));
    } catch (URISyntaxException e) {
      LOGGER.log(Level.SEVERE, String.format("Failed to launch this URI='%s'", uri), e);
      FxUtil.showException(String.format("Failed to launch this URI='%s'", uri), e);
    }
  }

  public static void openURI(URI uri) {
    new Thread(() -> {
      try {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
          Desktop.getDesktop().browse(uri);
        } else {
          Runtime.getRuntime().exec(new String[] {"xdg-open", uri.toString()});
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, String.format("Failed to launch this URI='%s'", uri), e);
        FxUtil.showException(String.format("Failed to launch this URI='%s'", uri), e);
      }
    }).start();
  }

  public static <T> DialogResult<ItemChoiceViewModel<T>> showItemChoiceDialog(String title, Stage parent, ItemChoiceViewModel<T> viewModel) {
    return FxUtil.showDialog("ntag", title, viewModel, ItemChoiceViewModel.class.getResource("/fxml/ItemChoice.fxml"), parent, 500, 400);
  }
}
