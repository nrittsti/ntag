package ntag.fx.scene.control.converter;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class PlaytimeConverterTest {

  PlaytimeConverter converter;

  @BeforeEach
  void setup() {
    this.converter = new PlaytimeConverter();
  }

  @Test
  void testToString() {
    // given
    long given = 70;
    String expected = "01:10";
    // when
    String result = this.converter.toString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void fromString() {
    // given
    String given = "01:10";
    Long expected = null;
    // when
    Long result = this.converter.fromString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }
}