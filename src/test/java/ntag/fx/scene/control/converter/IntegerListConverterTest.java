package ntag.fx.scene.control.converter;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class IntegerListConverterTest {

  IntegerListConverter converter;

  @BeforeEach
  void setup() {
    this.converter = new IntegerListConverter(",");
  }

  @Test
  void testToString() {
    // given
    List<Integer> given = Arrays.asList(1, 2, 3, 4, 5, 6);
    String expected = "1, 2, 3, 4, 5, 6";
    // when
    String result = this.converter.toString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void fromString() {
    // given
    String given = "1, 2, 3, 4, 5, 6";
    List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);
    // when
    List<Integer> result = this.converter.fromString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }
}