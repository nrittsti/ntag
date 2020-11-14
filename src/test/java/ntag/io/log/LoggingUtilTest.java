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

import javafx.embed.swing.JFXPanel;
import ntag.Category;
import ntag.io.NTagProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(Category.Unit)
class LoggingUtilTest {

  private static final Logger LOGGER = Logger.getLogger(LoggingUtil.class.getName());

  @BeforeAll
  static void initPlatform() {
    new JFXPanel();
  }

  @Test
  void setup() {
    LoggingUtil.setup("ntag_logging.properties");
  }

  @Test
  void registerHandler() throws InterruptedException {
    NTagProperties props = NTagProperties.instance();
    LoggingUtil.registerHandler(LOGGER, props.getActionLogHandler());
    LOGGER.log(Level.SEVERE, "Test123");
    Thread.sleep(100);
    assertTrue(props.getActionLogHandler().getText().contains("Test123"));
  }
}
