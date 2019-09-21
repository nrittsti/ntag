package ntag.commons;

import ntag.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Unit)
class StringBuilderUtilTest {

  @Test
  void replace() {
    // given
    StringBuilder given = new StringBuilder("The greatest track of the world");
    String expected = "The greatest song of the world";
    // when
    StringBuilderUtil.replace(given, "track", "song");
    // then
    assertEquals(expected, given.toString());
  }
}
