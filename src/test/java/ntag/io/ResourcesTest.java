package ntag.io;

import ntag.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

@Tag(Category.Unit)
class ResourcesTest {

  @Test
  void getResourceBundle() {
    ResourceBundle bundle = Resources.getResourceBundle("ntag");
    assertNotNull(bundle);
    assertTrue(bundle.containsKey("identical"));
  }

  @Test
  void get() {
    String expected = "identical";
    String actual = Resources.getResourceBundle("ntag").getString("identical");
    assertEquals(expected, actual);
  }

  @Test
  void format() {
    String expected = "Reading audiofile 1 of 2";
    String actual = Resources.format("ntag", "msg_reading_file", 1, 2);
    assertEquals(expected, actual);
  }

  @Test
  void getAndSetLocale() {
    Resources.setLocale(Locale.CHINA);
    assertEquals(Locale.CHINA, Resources.getLocale());
  }
}
