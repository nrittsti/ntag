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
    NTagProperties props = new NTagProperties();
    LoggingUtil.registerHandler(LOGGER, props.getActionLogHandler());
    LOGGER.log(Level.SEVERE, "Test123");
    Thread.sleep(100);
    assertTrue(props.getActionLogHandler().getText().contains("Test123"));
  }
}
