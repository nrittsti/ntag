package ntag.fx.scene.control.converter;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class FileSizeConverterTest {

  FileSizeConverter converter;

  @BeforeEach
  void setup() {
    this.converter = new FileSizeConverter();
  }

  @Test
  void testToString() {
    // given
    long given = 100000;
    String expected = "97.7 KiB";
    // when
    String result = this.converter.toString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void fromString() {
    // given
    String given = "test";
    Long expected = null;
    // when
    Long result = this.converter.fromString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }
}