package ntag.io;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Unit)
class HashUtilTest extends AbstractAudioFileTest {

  @Test
  void createFromByteArray() throws Exception {
    // given
    byte[] given = Files.readAllBytes(getArtwork());
    String expected = "D4EAD0DCA3D7AA4D3E034AE09BC41740";
    // when
    String actual = HashUtil.createFromByteArray("MD5", given);
    // then
    assertEquals(expected, actual);
  }
}
