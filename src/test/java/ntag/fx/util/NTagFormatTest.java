package ntag.fx.util;

import ntag.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.Unit)
class NTagFormatTest {

  @Test
  void bitrate() {
    // given
    long bitrate = 128;
    // when
    String result = NTagFormat.bitrate(bitrate);
    // then
    assertThat(result).isEqualTo("128 kbit/s");
  }

  @Test
  void playtime() {
    // given
    long seconds = 125;
    // when
    String result = NTagFormat.playtime(seconds);
    // then
    assertThat(result).isEqualTo("02:05");
  }

  @Test
  @DisplayName("format file size in KB")
  void fileSize_kb() {
    // given
    long bytes = 3500;
    // when
    String result = NTagFormat.fileSize(bytes, false);
    // then
    assertThat(result).isEqualTo("3.5 KB");
  }

  @Test
  @DisplayName("format file size in KiB")
  void fileSize_kib() {
    // given
    long bytes = 3500;
    // when
    String result = NTagFormat.fileSize(bytes, true);
    // then
    assertThat(result).isEqualTo("3.4 KiB");
  }

  @Test
  void round() {
    // given
    double value = 10.337;
    int digits = 2;
    // when
    double result = NTagFormat.round(value, digits);
    // then
    assertThat(result).isEqualTo(10.34);
  }
}