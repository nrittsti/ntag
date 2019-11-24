package ntag.fx.scene.control.converter;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class StringListConverterTest {

  StringListConverter converter;

  @BeforeEach
  void setup() {
    this.converter = new StringListConverter(",", true);
  }

  @Test
  void testToString() {
    // given
    List<String> given = Arrays.asList("a", "b", "c");
    String expected = "a, b, c";
    // when
    String result = this.converter.toString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void fromString() {
    // given
    String given = "a, b,c";
    List<String> expected = Arrays.asList("a", "b", "c");
    // when
    List<String> result = this.converter.fromString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }
}