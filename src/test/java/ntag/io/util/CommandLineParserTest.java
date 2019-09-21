package ntag.io.util;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
    assertTrue(commandLineParser.hasOption('h'));
    assertTrue(commandLineParser.hasOption("home"));
    assertFalse(commandLineParser.hasOption('a'));
    assertFalse(commandLineParser.hasOption("abc"));
    assertEquals("test", commandLineParser.getOptionValue('h'));
    assertEquals("test", commandLineParser.getOptionValue("home"));
  }

  @ParameterizedTest
  @CsvSource({"-y,test,-s", "-home,test,--silent"})
  void parseInvalid(String a, String b, String c) {
    // given
    commandLineParser.addOption('h', "home", true);
    commandLineParser.addOption('s', "silent", false);
    String[] given = new String[]{a, b, c};
    // when
    assertThrows(IllegalArgumentException.class, () -> {
      commandLineParser.parse(given);
    });
  }
}
