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
package ntag.io.log;

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
}
