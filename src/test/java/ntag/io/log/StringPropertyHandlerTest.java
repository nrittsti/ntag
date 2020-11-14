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

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(Category.Unit)
class StringPropertyHandlerTest {

  StringPropertyHandler handler;

  @BeforeEach
  void before() {
    handler = new StringPropertyHandler();
  }

  @Test
  void publish() throws Exception {
    handler.publish(new LogRecord(Level.SEVERE, "Test123"));
    Thread.sleep(50);
    assertTrue(handler.getText().contains("Test123"));
  }

  @Test
  void clear() throws Exception {
    handler.publish(new LogRecord(Level.SEVERE, "Test123"));
    Thread.sleep(50);
    handler.clear();
    assertEquals("", handler.getText());
  }

  @Test
  void flush() {
    handler.flush();
  }

  @Test
  void close() {
    handler.close();
  }
}
