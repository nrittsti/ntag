package ntag.io.ini;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Unit)
class IniFileTest extends AbstractAudioFileTest {

  IniFile iniFile;

  @BeforeEach
  protected void setUp() throws IOException {
    super.setUp();
    this.iniFile = new IniFile();
    Files.copy(getIniSample(), tempDirPath.resolve(SAMPLE_INI));
  }

  @Test
  void read() throws IOException {
    // when
    iniFile.read(tempDirPath.resolve(SAMPLE_INI));
    // then
    assertEquals(1, iniFile.getSection("gui").size());
    assertEquals(1, iniFile.getSection("mp3").size());
    assertEquals("en", iniFile.getValue("gui", "language", null));
    assertEquals(10, iniFile.getValues("mp3", "rating_conversion").size());
  }

  @Test
  void write() throws IOException {
    iniFile.write(tempDirPath.resolve(SAMPLE_INI));
  }

  @Test
  void setAndGetValue() {
    // given
    String expected = "de";
    // when
    iniFile.setValue("gui", "language", expected);
    String actual = iniFile.getValue("gui", "language", null);
    // then
    assertEquals(expected, actual);
  }

  @Test
  void setAndGetValues() {
    // given
    List<String> expected = Arrays.asList("a", "b", "c");
    // when
    iniFile.setValues("gui", "misc", expected, false);
    List<String> actual = iniFile.getValues("gui", "misc");
    // then
    assertEquals(expected, actual);
  }

  @Test
  void appendValues() {
    // given
    List<String> given = Arrays.asList("a", "b", "c");
    List<String> append = Arrays.asList("d", "e");
    List<String> expected = Arrays.asList("a", "b", "c", "d", "e");
    // when
    iniFile.setValues("gui", "misc", given, false);
    iniFile.setValues("gui", "misc", append, true);
    List<String> actual = iniFile.getValues("gui", "misc");
    // then
    assertEquals(expected, actual);
  }

  @Test
  void setAndGetDoubleValues() {
    // given
    List<Double> expected = Arrays.asList(1.1, 2.3, 3.4);
    // when
    iniFile.setDoubleValues("gui", "misc", expected, false);
    List<Double> actual = iniFile.getDoubleValues("gui", "misc");
    // then
    assertEquals(expected, actual);
  }

  @Test
  void setAndGetIntegerValues() {
    // given
    List<Integer> expected = Arrays.asList(1, 2, 3);
    // when
    iniFile.setIntegerValues("gui", "misc", expected, false);
    List<Integer> actual = iniFile.getIntegerValues("gui", "misc");
    // then
    assertEquals(expected, actual);
  }

  @Test
  void getAndSetInteger() {
    // given
    int expected = 100;
    // when
    iniFile.setValue("gui", "misc", expected);
    int actual = iniFile.getInteger("gui", "misc", null);
    // then
    assertEquals(expected, actual);
  }

  @Test
  void getAndSetFloat() {
    // given
    float expected = 100f;
    // when
    iniFile.setValue("gui", "misc", expected);
    float actual = iniFile.getFloat("gui", "misc", null);
    // then
    assertEquals(expected, actual);
  }

  @Test
  void getAndSetDouble() {
    // given
    double expected = 100f;
    // when
    iniFile.setValue("gui", "misc", expected);
    double actual = iniFile.getDouble("gui", "misc", null);
    // then
    assertEquals(expected, actual);
  }

  @Test
  void getAndSetBoolean() {
    // given
    boolean expected = true;
    // when
    iniFile.setValue("gui", "misc", expected);
    boolean actual = iniFile.getBoolean("gui", "misc", null);
    // then
    assertEquals(expected, actual);
  }
}
