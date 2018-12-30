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
package toolbox.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppDataBackupUtil {

	private static final char DSEP = File.separatorChar;
	private static final Logger LOGGER = Logger.getLogger(AppDataBackupUtil.class.getName());

	public static boolean createDailyBackup(Path appDataFile, int maxBackupRecords) {
		if (appDataFile == null) {
			throw new IllegalArgumentException("appDataFile cannot be null");
		}
		if (maxBackupRecords <= 0 || maxBackupRecords > 100) {
			throw new IllegalArgumentException("maxBackupRecords must be between 1 and 100");
		}
		final Path backupDirPath = Paths.get(appDataFile.getParent().toString() + DSEP + "backup");
		final Path backupFilePath = Paths.get(backupDirPath.toString() + DSEP + String.format("%1$tY-%1$tm-%1$td", new Date()) + ".bak");
		try {
			// Create Backup Directory if needed
			Files.createDirectories(backupDirPath);
			// prüfen, ob heute schon ein Backup erstellt wurde
			if (Files.exists(backupFilePath, LinkOption.NOFOLLOW_LINKS)) {
				return false;
			}
			// copy current appData
			Files.copy(appDataFile, backupFilePath);
			// remove old backup files
			List<Path> oldBackupFiles = listBackupFiles(backupDirPath);
			for (int i = 0; (oldBackupFiles.size() - i) > maxBackupRecords; i++) {
				try {
					Files.delete(oldBackupFiles.get(i));
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, String.format("Deleting old backup file failed: %s", e.getMessage()));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, String.format("Creating backup file failed: %s", e.getMessage()));
			return false;
		}
		return true;
	}

	private static List<Path> listBackupFiles(Path backupDirectoryPath) throws IOException {
		List<Path> files = new ArrayList<Path>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(backupDirectoryPath, "*.{bak}")) {
			for (Path path : directoryStream) {
				files.add(path);
			}
		}
		files.sort(null);
		return files;
	}
}
