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
package ntag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ntag.fx.scene.NTagViewModel;
import ntag.fx.scene.NTagWindowController;
import ntag.io.NTagProperties;
import ntag.model.TagFile;
import toolbox.fx.FxUtil;
import toolbox.fx.dialog.DialogResponse;
import toolbox.io.Resources;
import toolbox.io.log.LoggingUtil;
import toolbox.util.CommandLineParser;

public class NTag extends Application {

	private static final Logger LOGGER = Logger.getLogger(NTag.class.getName());

	@Override
	public void start(Stage primaryStage) {
		try {
			// first init FXUtil bevor any API calls
			FxUtil.setPrimaryStage(primaryStage);
			final NTagProperties appProps = new NTagProperties();
			appProps.distribute();
			// init MainWindow with fxml
			final FXMLLoader loader = new FXMLLoader();
			// set resource bundle by locale
			loader.setResources(Resources.getResourceBundle("ntag"));
			loader.setLocation(NTagWindowController.class.getResource("NTagWindow.fxml"));
			BorderPane root = (BorderPane) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add("ntag.css");
			primaryStage.setTitle(appProps.getTitle());
			primaryStage.getIcons().add(new Image("icons/ntag.png"));
			primaryStage.setScene(scene);
			// setup fx main controller
			NTagWindowController controller = loader.getController();
			controller.setStage(primaryStage);
			// create ViewModel
			NTagViewModel viewModel = new NTagViewModel(appProps);
			controller.setViewModel(viewModel);
			// auto save window state
			FxUtil.getPrimaryStage().setOnCloseRequest((WindowEvent event) -> {
				controller.onCloseRequest(event);
			});
			// restore window state
			appProps.restoreMainWindowState(controller);
			// show main window
			primaryStage.show();
		} catch(Exception e) {
			String msg = "Error on loading Application MainWindow";
			LOGGER.log(Level.SEVERE, "msg", e);
			FxUtil.showException(msg, e);
		}
	}

	public static void main(String[] args) {
		// parse main args
		CommandLineParser clp = new CommandLineParser();
		clp.addOption('h', "home", true);
		try {
			clp.parse(args);
			if (clp.hasOption('h')) {
				NTagProperties.setHomeDir(clp.getOptionValue('h'));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			LOGGER.log(Level.SEVERE, "Invalid main args", e);
			System.exit(-1);
		}
		// init Java logging
		try {
			Files.createDirectory(Paths.get(NTagProperties.getHomeDir(), "logs"));
		} catch (Exception e) {
		}
		LoggingUtil.setup("ntag_logging.properties");
		// read app propperties
		NTagProperties appProps = new NTagProperties();
		// change app language
		Resources.setLocale(appProps.getLanguage());
		// log hello world
		LOGGER.log(Level.INFO, String.format("Starting %s Version %s ...", appProps.getTitle(), appProps.getVersion()));
		// configure ImageIO
		ImageIO.setUseCache(false);
		launch(args);
	}

	public static List<TagFile> showAndWait(String title, Window owner, List<Path> pathList) throws IOException {
		// read app propperties
		final NTagProperties appProps = new NTagProperties();
		appProps.distribute();
		// Load the fxml file and create a new stage for the popup dialog.
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(Resources.getResourceBundle("ntag"));
		loader.setLocation(NTagWindowController.class.getResource("NTagWindow.fxml"));
		Parent page = loader.load();
		// Create the dialog Stage (=Window)
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.setTitle(title);
		stage.getIcons().add(new Image("icons/ntag.png"));
		Scene scene = new Scene(page);
		scene.getStylesheets().add("ntag.css");
		stage.setScene(scene);
		// set modality
		stage.initModality(Modality.WINDOW_MODAL);
		if (owner != null) {
			stage.initOwner(owner);
		}
		// setup fx main controller
		NTagWindowController controller = loader.getController();
		controller.setStage(stage);
		// create ViewModel
		NTagViewModel viewModel = new NTagViewModel(appProps);
		controller.setViewModel(viewModel);
		// auto save window state
		stage.setOnCloseRequest((WindowEvent event) -> {
			controller.onCloseRequest(event);
		});
		// restore window state
		appProps.restoreMainWindowState(controller);
		// load data
		controller.readFiles(pathList);
		// Show the dialog and wait until the user closes it
		stage.showAndWait();
		if (controller.getDialogResponse() == DialogResponse.OK) {
			return new ArrayList<>(viewModel.getUpdatedFiles());
		} else {
			return new ArrayList<>();
		}
	}
}