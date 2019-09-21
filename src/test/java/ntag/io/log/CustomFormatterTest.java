package ntag.io.log;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(Category.Unit)
class CustomFormatterTest {

  CustomFormatter customFormatter;

  @BeforeEach
  void before() {
    customFormatter = new CustomFormatter();
  }

  @Test
  void format() {
    LogRecord record = new LogRecord(Level.FINE, "Hello World");
    record.setSourceClassName("ntag.io.log.CustomFormatterTest");
    record.setSourceMethodName("format");
    String actual = customFormatter.format(record);
    assertTrue(actual.trim().endsWith("Hello World"));
  }
}
