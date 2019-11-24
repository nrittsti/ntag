package ntag.fx.scene.control.converter;

import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class StringLocaleConverterTest {

  StringLocaleConverter stringLocaleConverter;

  @BeforeEach
  void setup() {
    this.stringLocaleConverter = new StringLocaleConverter();
  }

  @Test
  void testToString() {
    // given
    Locale given = Locale.GERMAN;
    String expected = given.getDisplayLanguage();
    // when
    String result = stringLocaleConverter.toString(given);
    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void fromString() {
    // given
    String given = Locale.GERMAN.getDisplayLanguage();
    Locale expected = Locale.GERMAN;
    // when
    Locale result = stringLocaleConverter.fromString(given);
    // then
    assertThat(result.getDisplayName()).isEqualToIgnoringCase(expected.getDisplayName());
  }
}