package ntag.fx.scene.control.converter;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class DummyStringConverterTest {

  DummyStringConverter dummyStringConverter;

  @BeforeEach
  void setup() {
    this.dummyStringConverter = new DummyStringConverter();
  }

  @Test
  void testToString() {
    // given
    String given = "test";
    String expected = given;
    // when
    String result = dummyStringConverter.toString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void fromString() {
    // given
    String given = "test";
    String expected = given;
    // when
    String result = dummyStringConverter.fromString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }
}