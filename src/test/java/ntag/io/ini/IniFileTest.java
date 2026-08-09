package ntag.io.ini;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(iniFile.getSection("gui").size()).isEqualTo(1);
    assertThat(iniFile.getSection("mp3").size()).isEqualTo(1);
    assertThat(iniFile.getValue("gui", "language", null)).isEqualTo("en");
    assertThat(iniFile.getValues("mp3", "rating_conversion").size()).isEqualTo(10);
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
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void setAndGetValues() {
    // given
    List<String> expected = List.of("a", "b", "c");
    // when
    iniFile.setValues("gui", "misc", expected, false);
    List<String> actual = iniFile.getValues("gui", "misc");
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void appendValues() {
    // given
    List<String> given = List.of("a", "b", "c");
    List<String> append = List.of("d", "e");
    List<String> expected = List.of("a", "b", "c", "d", "e");
    // when
    iniFile.setValues("gui", "misc", given, false);
    iniFile.setValues("gui", "misc", append, true);
    List<String> actual = iniFile.getValues("gui", "misc");
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void setAndGetDoubleValues() {
    // given
    List<Double> expected = List.of(1.1, 2.3, 3.4);
    // when
    iniFile.setDoubleValues("gui", "misc", expected, false);
    List<Double> actual = iniFile.getDoubleValues("gui", "misc");
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void setAndGetIntegerValues() {
    // given
    List<Integer> expected = List.of(1, 2, 3);
    // when
    iniFile.setIntegerValues("gui", "misc", expected, false);
    List<Integer> actual = iniFile.getIntegerValues("gui", "misc");
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void getAndSetInteger() {
    // given
    int expected = 100;
    // when
    iniFile.setValue("gui", "misc", expected);
    int actual = iniFile.getInteger("gui", "misc", null);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void getAndSetFloat() {
    // given
    float expected = 100f;
    // when
    iniFile.setValue("gui", "misc", expected);
    float actual = iniFile.getFloat("gui", "misc", null);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void getAndSetDouble() {
    // given
    double expected = 100f;
    // when
    iniFile.setValue("gui", "misc", expected);
    double actual = iniFile.getDouble("gui", "misc", null);
    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void getAndSetBoolean() {
    // given
    boolean expected = true;
    // when
    iniFile.setValue("gui", "misc", expected);
    boolean actual = iniFile.getBoolean("gui", "misc", null);
    // then
    assertThat(actual).isEqualTo(expected);
  }
}
