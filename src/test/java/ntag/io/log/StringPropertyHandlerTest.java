package ntag.io.log;

import javafx.embed.swing.JFXPanel;
import ntag.Category;
import org.junit.jupiter.api.BeforeAll;
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

  @BeforeAll
  static void initPlatform() {
    new JFXPanel();
  }

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
