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
package ntag.io;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import ntag.fx.scene.AdjustArtworkViewModel;
import ntag.fx.scene.NTagWindowController;
import ntag.fx.scene.control.NTagThemeEnum;
import ntag.fx.scene.control.converter.FileSizeConverter;
import ntag.fx.scene.control.editor.ArtworkEditorProperty;
import ntag.fx.scene.control.tableview.FileSizeTableCell;
import ntag.fx.scene.control.tableview.TagFileTableColumn.ColumnType;
import ntag.io.util.ArtworkAdjuster;
import ntag.io.util.RatingConverter;
import ntag.model.ArtworkTag;
import ntag.model.AudioFormat;
import toolbox.io.FileUtil;
import toolbox.io.ImageUtil.ImageType;
import toolbox.io.Resources;
import toolbox.io.ini.IniFile;
import toolbox.io.log.CustomFormatter;
import toolbox.io.log.StringPropertyHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NTagProperties {

	private static final Logger LOGGER = Logger.getLogger(NTagProperties.class.getName());
	private static final String CONFIG_FILENAME = "ntag.ini";

	private static Attributes attributes = null;
	private static IniFile preferences = null;

	private static String homeDir = null;

	private static StringPropertyHandler actionLogHandler = null;

	static {
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			homeDir = System.getenv("APPDATA") + "\\ntag";
		} else {
			homeDir = System.getProperty("user.home") + "/.config/ntag";
		}
	}

	public NTagProperties() {
		if (attributes == null) {
			try {
				// https://stackoverflow.com/a/1273196
				Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
				while (resources.hasMoreElements()) {
					try (InputStream inputStream = resources.nextElement().openStream()) {
						Manifest manifest = new Manifest(inputStream);
						attributes = manifest.getMainAttributes();
						if ("NTag".equals(attributes.getValue("Implementation-Title"))) {
							break;
						}
					} catch (Exception e) {
						LOGGER.log(Level.CONFIG, "Can't read Manifest", e);
					}
				}
			} catch (IOException e) {
				LOGGER.log(Level.CONFIG, "Can't read Manifest", e);
			}
		}
		if (preferences == null) {
			try {
				preferences = new IniFile();
				Path configPath = getConfigFile();
				if (Files.exists(configPath)) {
					preferences.read(getConfigFile());
				} else {
					List<String> provider = new ArrayList();
					provider.add("https://lyrics.wikia.com/Special:Search?search=input");
					provider.add("https://www.songtexte.com/search?q=input&c=all");
					provider.add("https://www.magistrix.de/search/query?utf8=%E2%9C%93&q=input");
					setLyricProvider(provider);
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Can't read preferences", e);
			}
			// auto save preferences
			Runtime.getRuntime().addShutdownHook(new Thread(() -> savePreferences()));
		}
	}

	public static String getHomeDir() {
		return homeDir;
	}

	public static void setHomeDir(String homeDir) throws IOException {
		FileUtil.checkHomeDirectory(homeDir);
		NTagProperties.homeDir = homeDir;
	}

	public StringPropertyHandler getActionLogHandler() {
		if (actionLogHandler == null) {
			actionLogHandler = new StringPropertyHandler(new CustomFormatter("%1$tH:%1$tM:%1$tS.%1$tL [%4$s]%n%5$s%n%6$s%n"));
		}
		return actionLogHandler;
	}

	// *** Attributes ***

	public String getTitle() {
		return attributes.getValue("Implementation-Title");
	}

	public String getVersion() {
		return attributes.getValue("Implementation-Version");
	}

	public Path getConfigFile() {
		return Paths.get(homeDir, CONFIG_FILENAME);
	}

	public static String getCredits() {
		StringBuilder credits = new StringBuilder(1000);
		credits.append("Nuvola Icon Theme\n");
		credits.append("Autor:\tDavid Vignoni\n");
		credits.append("Licence:\tLGPL\n");
		credits.append("\n");
		credits.append("JAudiotagger Library 2.2.4\n");
		credits.append("Autor:\thttp://www.jthink.net/jaudiotagger/\n");
		credits.append("Licence:\tLGPL\n");
		return credits.toString();
	}

	// *** Preferences ***

	private final StringProperty lastDirectory = new SimpleStringProperty(this, "lastDirectory", "");

	public final ReadOnlyStringProperty lastDirectoryProperty() {
		return lastDirectory;
	}

	public final String getLastDirectory() {
		return preferences.getValue("GUI", "last_directory", System.getProperty("user.home"));
	}

	public final void setLastDirectory(String dir) {
		preferences.setValue("GUI", "last_directory", dir);
		lastDirectory.set(dir);
	}

	public NTagThemeEnum getTheme() {
		String themeStr = preferences.getValue("GUI", "theme", NTagThemeEnum.ModenaLight.name());
		try {
			return NTagThemeEnum.valueOf(themeStr);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, String.format("Can't convert %s to NTagThemeEnum, using default theme!", themeStr), e);
			return NTagThemeEnum.ModenaLight;
		}
	}

	public void setTheme(NTagThemeEnum theme) {
		preferences.setValue("GUI", "theme", theme.name());
	}

	public Locale getLanguage() {
		return new Locale(preferences.getValue("GUI", "language", "en"));
	}

	public void setLanguage(Locale locale) {
		preferences.setValue("GUI", "language", locale.getLanguage());
	}

	public String getDateFormat() {
		return preferences.getValue("GUI", "Date_Format", "yyyy-mm-dd");
	}

	public void setDateFormat(String dateFormat) {
		preferences.setValue("GUI", "Date_Format", dateFormat);
	}

	public int getMaxDepth() {
		return preferences.getInteger("Paths", "depth", 10);
	}

	public int getMaxFiles() {
		return preferences.getInteger("Paths", "files", 1000);
	}

	public boolean isID3v11() {
		return preferences.getBoolean("MP3", "ID3v11", false);
	}

	public void setID3v11(boolean value) {
		preferences.setValue("MP3", "ID3v11", value);
	}

	public boolean isID3v24() {
		return preferences.getBoolean("MP3", "ID3v24", false);
	}

	public void setID3v24(boolean value) {
		preferences.setValue("MP3", "ID3v24", value);
	}

	public boolean isRatingEnforceSingleFrame() {
		return preferences.getBoolean("MP3", "POPM_Enforce_Single_Frame", false);
	}

	public void setRatingEnforceSingleFrame(boolean value) {
		preferences.setValue("MP3", "POPM_Enforce_Single_Frame", value);
	}

	public String getRatingEMail() {
		return preferences.getValue("MP3", "POPM_EMail", "no@mail");
	}

	public void setRatingEMail(String value) {
		preferences.setValue("MP3", "POPM_EMail", value.trim());
	}

	/**
	 * default: iTunes star rating values
	 *
	 * @return
	 */
	public List<Integer> getID3RatingConversion() {
		return preferences.getIntegerValues("MP3", "POPM_Rating_Conversion", 13, 23, 54, 64, 118, 128, 186, 196, 242, 255);
	}

	public void setID3RatingConversion(List<Integer> values) {
		preferences.setIntegerValues("MP3", "POPM_Rating_Conversion", values, false);
	}

	public List<String> getID3FrameBlackList() {
		return preferences.getValues("MP3", "Frame_Black_List");
	}

	public void setID3FrameBlackList(List<String> values) {
		preferences.setValues("MP3", "Frame_Black_List", values, false);
	}

	public boolean isID3ReleaseDateTDRC() {
		return preferences.getBoolean("MP3", "Save_Release_Date_As_TDRC", false);
	}

	public void setID3ReleaseDateTDRC(boolean value) {
		preferences.setValue("MP3", "Save_Release_Date_As_TDRC", value);
	}

	public boolean isID3ReleaseDateTDOR() {
		return preferences.getBoolean("MP3", "Save_Release_Date_As_TDOR", false);
	}

	public void setID3ReleaseDateTDOR(boolean value) {
		preferences.setValue("MP3", "Save_Release_Date_As_TDOR", value);
	}

	public boolean isID3ReleaseDateTDRL() {
		return preferences.getBoolean("MP3", "Save_Release_Date_As_TDRL", true);
	}

	public void setID3ReleaseDateTDRL(boolean value) {
		preferences.setValue("MP3", "Save_Release_Date_As_TDRL", value);
	}

	public List<Integer> getMP4RatingConversion() {
		return preferences.getIntegerValues("MP4", "Rating_Conversion", 1, 20, 30, 40, 50, 60, 70, 80, 90, 100);
	}

	public void setMP4RatingConversion(List<Integer> values) {
		preferences.setIntegerValues("MP4", "Rating_Conversion", values, false);
	}

	public List<Integer> getOGGRatingConversion() {
		return preferences.getIntegerValues("OGG", "Rating_Conversion", 1, 20, 30, 40, 50, 60, 70, 80, 90, 100);
	}

	public void setOGGRatingConversion(List<Integer> values) {
		preferences.setIntegerValues("OGG", "Rating_Conversion", values, false);
	}

	public List<Integer> getFLACRatingConversion() {
		return preferences.getIntegerValues("FLAC", "Rating_Conversion", 1, 20, 30, 40, 50, 60, 70, 80, 90, 100);
	}

	public void setFLACRatingConversion(List<Integer> values) {
		preferences.setIntegerValues("FLAC", "Rating_Conversion", values, false);
	}

	public List<Integer> getWMARatingConversion() {
		return preferences.getIntegerValues("WMA", "Rating_Conversion", 1, 20, 30, 40, 50, 60, 70, 80, 90, 99);
	}

	public void setWMARatingConversion(List<Integer> values) {
		preferences.setIntegerValues("WMA", "Rating_Conversion", values, false);
	}

	public boolean isArtworkEnforceImageFormat() {
		return preferences.getBoolean("Artwork", "Enforce_Image_Type", false);
	}

	public void setArtworkEnforceImageFormat(boolean value) {
		preferences.setValue("Artwork", "Enforce_Image_Type", value);
	}

	public int getArtworkMaxResolution() {
		return preferences.getInteger("Artwork", "Max_Resolution", 500);
	}

	public void setArtworkMaxResolution(int value) {
		preferences.setValue("Artwork", "Max_Resolution", value);
	}

	public int getArtworkMaxKilobytes() {
		return preferences.getInteger("Artwork", "Max_Kilobytes", 256);
	}

	public void setArtworkMaxKilobytes(int value) {
		preferences.setValue("Artwork", "Max_Kilobytes", value);
	}

	public float getArtworkQuality() {
		return preferences.getFloat("Artwork", "Quality", 0.9f);
	}

	public void setArtworkQuality(float value) {
		preferences.setValue("Artwork", "Quality", value);
	}

	public ImageType getArtworkImageType() {
		String format = preferences.getValue("Artwork", "Format", "jpeg");
		ImageType imageType = ImageType.getByFormat(format);
		return imageType == null ? ImageType.JPG : imageType;
	}

	public void setArworkImageType(ImageType imageType) {
		preferences.setValue("Artwork", "Format", imageType.getFormat());
	}

	public boolean isArtworkEnforceSingle() {
		return preferences.getBoolean("Artwork", "enforce_single", true);
	}

	public void setArtworkEnforceSingle(boolean value) {
		preferences.setValue("Artwork", "enforce_single", value);
	}

	public String getFilenameFormat() {
		return preferences.getValue("Tools", "Filename_Format", "%track - %title");
	}

	public void setFilenameFormat(String value) {
		preferences.setValue("Tools", "Filename_Format", value);
	}

	public boolean isFilenameStripUnsafeChars() {
		return preferences.getBoolean("Tools", "Filename_Strip_Unsafe_Characters", true);
	}

	public void setFilenameStripUnsafeChars(boolean value) {
		preferences.setValue("Tools", "Filename_Strip_Unsafe_Characters", value);
	}

	public List<String> getLyricProvider() {
		return preferences.getValues("Lyrics", "provider");
	}

	public void setLyricProvider(List<String> values) {
		preferences.setValues("Lyrics", "provider", values, false);
	}

	public List<String> getGenreFavorites() {
		return preferences.getValues("genre", "favorite");
	}

	public void setGenreFavorites(List<String> values) {
		preferences.setValues("genre", "favorite", values, false);
	}

	public boolean isBinaryUnit() {
		return preferences.getBoolean("GUI", "Binary_Unit", true);
	}

	public void setBinaryUnit(boolean value) {
		preferences.setValue("GUI", "Binary_Unit", value);
	}

	public void setVisibleColumns(List<ColumnType> values) {
		List<String> strList = new ArrayList<>();
		if (values != null) {
			for (ColumnType ct : values) {
				strList.add(ct.name());
			}
		}
		preferences.setValues("GUI", "Visible_Columns", strList, false);
	}

	public List<ColumnType> getVisibleColumns() {
		List<ColumnType> columnList = new ArrayList<>();
		List<String> strList = preferences.getValues("GUI", "Visible_Columns", ColumnType.STATUS.name(), ColumnType.FILENAME.name());
		for (String value : strList) {
			columnList.add(ColumnType.valueOf(value));
		}
		if (columnList.isEmpty()) {
			columnList.add(ColumnType.STATUS);
			columnList.add(ColumnType.FILENAME);
		}
		return columnList;
	}

	public void setAdjustArtworkProfiles(List<AdjustArtworkViewModel> profiles) {
		List<String> jsonList = new ArrayList<>();
		for (AdjustArtworkViewModel profile : profiles) {
			jsonList.add(profile.toJSON());
		}
		preferences.setValues("Artwork", "Profile", jsonList, false);
	}

	public List<AdjustArtworkViewModel> getAdjustArtworkProfiles() {
		List<String> strList = preferences.getValues("Artwork", "Profile");
		List<AdjustArtworkViewModel> result = new ArrayList<>();
		for (String json : strList) {
			try {
				result.add(new AdjustArtworkViewModel(json));
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, String.format("Could not load Artwork profile: %s", json), e);
			}
		}
		return result;
	}

	// ***
	//
	// Public API
	//
	// ***

	public void savePreferences() {
		try {
			preferences.write(getConfigFile());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't save preferences", e);
		}
	}

	public void saveMainWindowState(NTagWindowController controller) {
		final Stage stage = controller.getStage();
		if (!stage.isMaximized()) {
			preferences.setValue("window", "width", stage.getWidth());
			preferences.setValue("window", "height", stage.getHeight());
			preferences.setValue("window", "x", stage.getX());
			preferences.setValue("window", "y", stage.getY());
		}
		preferences.setValue("window", "maximized", stage.isMaximized());
		preferences.setValue("window", "iconified", stage.isIconified());

		List<Double> deviderPosList = new ArrayList<>();
		for (double pos : controller.getDividerPositions()) {
			deviderPosList.add(pos);
		}
		preferences.setDoubleValues("window", "devider", deviderPosList, false);
		savePreferences();
	}

	public void restoreMainWindowState(NTagWindowController controller) {
		final Stage stage = controller.getStage();
		final double width = preferences.getDouble("window", "width", 1100d);
		final double height = preferences.getDouble("window", "height", 830d);
		final double x = preferences.getDouble("window", "x", -1d);
		final double y = preferences.getDouble("window", "y", -1d);
		final boolean maximized = preferences.getBoolean("window", "maximized", false);
		final boolean iconified = preferences.getBoolean("window", "iconified", false);

		List<Double> deviderValues = preferences.getDoubleValues("window", "devider");

		stage.setWidth(width);
		stage.setHeight(height);
		if (x > 0 && y > 0) {
			stage.setX(x);
			stage.setY(y);
		}
		stage.setIconified(iconified);
		stage.setMaximized(maximized);
		if (deviderValues.size() == 1) {
			// http://stackoverflow.com/a/15156651
			// there is still a SplitPane.setDividerPositions bug, a workaround
			// with runLater works fine:
			Platform.runLater(() -> {
				controller.setDividerPositions(deviderValues.get(0));
			});
		}
	}

	public void distribute() {

		RatingConverter.setConversion(AudioFormat.MP3, getID3RatingConversion());
		RatingConverter.setConversion(AudioFormat.MP4, getMP4RatingConversion());
		RatingConverter.setConversion(AudioFormat.OGG, getOGGRatingConversion());
		RatingConverter.setConversion(AudioFormat.FLAC, getFLACRatingConversion());
		RatingConverter.setConversion(AudioFormat.WMA, getWMARatingConversion());

		FileSizeConverter.binaryMode = isBinaryUnit();
		FileSizeTableCell.binaryMode = isBinaryUnit();
		ArtworkEditorProperty.binaryUnit = isBinaryUnit();

		ArtworkAdjuster.defaultImageType = getArtworkImageType();
		ArtworkAdjuster.defaultEnforceImageType = isArtworkEnforceImageFormat();
		ArtworkAdjuster.defaultMaxResolution = getArtworkMaxResolution();
		ArtworkAdjuster.defaultMaxKilobytes = getArtworkMaxKilobytes();
		ArtworkAdjuster.defaultQuality = getArtworkQuality();

		ArtworkTag.defaultImageType = getArtworkImageType();
		ArtworkTag.defaultMaxResolution = getArtworkMaxResolution();
		ArtworkTag.defaultQuality = getArtworkQuality();

		Resources.setLocale(getLanguage());
	}
}
