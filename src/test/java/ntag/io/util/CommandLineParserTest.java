package ntag.io.util;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag(Category.Unit)
class CommandLineParserTest extends AbstractAudioFileTest {

  CommandLineParser commandLineParser;

  @BeforeEach
  protected void setUp() throws IOException {
    super.setUp();
    commandLineParser = new CommandLineParser();
  }

  @ParameterizedTest
  @CsvSource({"-h,test,-s", "--home,test,--silent"})
  void parseValid(String a, String b, String c) {
    // given
    commandLineParser.addOption('h', "home", true);
    commandLineParser.addOption('s', "silent", false);
    String[] given = new String[]{a, b, c};
    // when
    commandLineParser.parse(given);
    // then
    assertThat(commandLineParser.hasOption('h')).isTrue();
    assertThat(commandLineParser.hasOption("home")).isTrue();
    assertThat(commandLineParser.hasOption('a')).isFalse();
    assertThat(commandLineParser.hasOption("abc")).isFalse();
    assertThat(commandLineParser.getOptionValue('h')).isEqualTo("test");
    assertThat(commandLineParser.getOptionValue("home")).isEqualTo("test");
  }

  @ParameterizedTest
  @CsvSource({"-y,test,-s", "-home,test,--silent"})
  void parseInvalid(String a, String b, String c) {
    // given
    commandLineParser.addOption('h', "home", true);
    commandLineParser.addOption('s', "silent", false);
    String[] given = new String[]{a, b, c};
    // when
    assertThatThrownBy(() -> commandLineParser.parse(given)).isInstanceOf(IllegalArgumentException.class);
  }
}
