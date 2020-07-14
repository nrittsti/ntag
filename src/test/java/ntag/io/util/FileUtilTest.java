package ntag.io.util;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Unit)
class FileUtilTest extends AbstractAudioFileTest {

  @BeforeEach
  protected void setUp() {
  }

  @Test
  @DisplayName("Check a valid home dir")
  void checkHomeDirectory1() throws Exception {
    FileUtil.checkHomeDirectory(getTempDir());
  }

  @ParameterizedTest
  @CsvSource({"abc1.mp3,abc1.mp3", "aöz.mp3,aoez.mp3", "abc€.mp3,abc.mp3", "abc\t.mp3,abc.mp3", "01 - test.mp3, 01 - test.mp3", "10 - Free Me.mp3,10 - Free Me.mp3"})
  void sanitizeFilename(String given, String expected) {
    // when
    String actual = FileUtil.sanitizeFilename(given);
    // then
    assertEquals(expected, actual);
  }
}
