/**
 * This file is part of NTag (audio file tag editor).
 * <p>
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2016, Nico Rittstieg
 */
package toolbox.io.log;

import toolbox.fx.FxUtil;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggingUtil {

    private static final Logger LOGGER = Logger.getLogger(LoggingUtil.class.getName());

    private LoggingUtil() {

    }

    public static void setup(String configFile) {
        // init Java logging
        System.setProperty("java.util.logging.config.file", configFile);
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (Exception e) {
            LOGGER.log(Level.CONFIG, "Loading Log Configuration failed:", e);
        }

    }

    public static void show(Path logDirPath) throws IOException {
        if (logDirPath == null) {
            throw new IllegalArgumentException("logDirPath cannot be null");
        }
        List<Path> files = new ArrayList<Path>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath)) {
            for (Path p : stream) {
                files.add(p);
            }
        }
        if (files.isEmpty()) {
            throw new IOException(String.format("Directory %s is empty!", logDirPath.toString()));
        } else {
            Collections.sort(files, new LogFileComparator());
            new Thread(() -> {
                try {
                    java.awt.Desktop.getDesktop().browse(files.get(0).toUri());
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, String.format("Failed to launch this URI='%s'", files.get(0).toUri()), ex);
                    FxUtil.showException(String.format("Failed to launch this URI='%s'", files.get(0).toUri()), ex);
                }
            }).start();
        }
    }

    public static void registerHandler(Logger logger, Handler handler) {
        if (logger == null) {
            throw new IllegalArgumentException("logger cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }
        // check that the given handler is not already registered
        for (Handler h : logger.getHandlers()) {
            if (h.getClass().equals(handler.getClass())) {
                return;
            }
        }
        logger.addHandler(handler);
    }

    private static class LogFileComparator implements Comparator<Path> {
        @Override
        public int compare(Path o1, Path o2) {
            try {
                return Files.getLastModifiedTime(o2).compareTo(Files.getLastModifiedTime(o1));
            } catch (IOException e) {
                return 0;
            }
        }
    }
}
