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
package ntag;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ntag.fx.scene.NTagViewModel;
import ntag.fx.scene.NTagWindowController;
import ntag.fx.util.FxUtil;
import ntag.fx.util.TagFieldInputDialogs;
import ntag.io.NTagProperties;
import ntag.io.Resources;
import ntag.io.TagFileReader;
import ntag.io.TagFileWriter;
import ntag.io.log.LoggingUtil;
import ntag.io.util.CommandLineParser;
import ntag.model.TagFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NTag extends Application {

  private static final Logger LOGGER = Logger.getLogger(NTag.class.getName());

  @Override
  public void start(Stage primaryStage) {
    Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
      LOGGER.log(Level.SEVERE, "Uncaught Exception", throwable);
      FxUtil.showException("Uncaught Exception", throwable);
    });
    try {
      initPrimaryStage(primaryStage);
    } catch (Exception e) {
      String msg = "Error on loading Application MainWindow";
      LOGGER.log(Level.SEVERE, msg, e);
      FxUtil.showException(msg, e);
    }
  }

  void initPrimaryStage(Stage primaryStage) throws IOException {
    // first init FXUtil bevor any API calls
    FxUtil.setPrimaryStage(primaryStage);
    final NTagProperties appProps = NTagProperties.instance();
    appProps.distribute();
    // init MainWindow with fxml
    final FXMLLoader loader = new FXMLLoader();
    // set resource bundle by locale
    loader.setResources(Resources.getResourceBundle("ntag"));
    loader.setLocation(getClass().getResource("/fxml/NTagWindow.fxml"));
    BorderPane root = loader.load();
    Scene scene = new Scene(root);
    // set light or dark css theme
    scene.getStylesheets().add(appProps.getTheme().getCSS());
    // register CTRL+Q shorcut for exit
    scene.getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), primaryStage::close);
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
    FxUtil.getPrimaryStage().setOnCloseRequest(controller::onCloseRequest);
    // restore window state
    appProps.restoreMainWindowState(controller);
    // show main window
    primaryStage.show();
  }

  public static void main(String[] args) {
    // parse main args
    CommandLineParser clp = new CommandLineParser();
    clp.addOption('h', "home", true);
    clp.addOption('p', "portable", false);
    try {
      clp.parse(args);
      if (clp.hasOption('h')) {
        NTagProperties.instance().setHomeDir(Path.of(clp.getOptionValue('h')));
      } else if (clp.hasOption('p')) {
        NTagProperties.instance().setHomeDir(Path.of(System.getProperty("user.dir")));
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      LOGGER.log(Level.SEVERE, "Invalid main args", e);
      System.exit(-1);
    }
    // init home path
    try {
      Files.createDirectories(NTagProperties.instance().getHomeDir());
    } catch (Exception ignored) {
    }
    LoggingUtil.setup("ntag_logging.properties");
    // read app propperties
    NTagProperties appProps = NTagProperties.instance();
    // register logging handler
    LoggingUtil.registerHandler(TagFileWriter.LOGGER, appProps.getActionLogHandler());
    LoggingUtil.registerHandler(TagFileReader.LOGGER, appProps.getActionLogHandler());
    LoggingUtil.registerHandler(TagFile.LOGGER, appProps.getActionLogHandler());
    LoggingUtil.registerHandler(TagFieldInputDialogs.LOGGER, appProps.getActionLogHandler());
    // change app language
    Resources.setLocale(appProps.getLanguage());
    // log hello world
    LOGGER.log(Level.INFO, String.format("Starting %s Version %s ...", appProps.getTitle(), appProps.getVersion()));
    LOGGER.log(Level.INFO, String.format("Using home directory %s", NTagProperties.instance().getHomeDir()));
    // configure ImageIO
    ImageIO.setUseCache(false);
    launch(args);
  }
}
