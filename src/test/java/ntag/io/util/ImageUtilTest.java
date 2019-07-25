package ntag.io.util;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Unit)
class ImageUtilTest extends AbstractAudioFileTest {

  BufferedImage bufferedImage;

  @BeforeEach
  protected void setUp() throws IOException {
    this.bufferedImage = ImageIO.read(new ByteArrayInputStream(getArtworkTagSample().getImageData()));
  }

  @Test
  void write_jpg() throws Exception {
    try (FileOutputStream fos = new FileOutputStream(getTempDir().resolve("test.jpg").toFile())) {
      ImageUtil.write(bufferedImage, fos, ImageUtil.ImageType.JPG, 0.8f);
    }
  }

  @Test
  void write_png() throws Exception {
    try (FileOutputStream fos = new FileOutputStream(getTempDir().resolve("test.png").toFile())) {
      ImageUtil.write(bufferedImage, fos, ImageUtil.ImageType.PNG, 0.8f);
    }
  }

  @Test
  void scale() {
    // given
    int expectedSize = 90;
    // when
    BufferedImage actualImage = ImageUtil.scale(bufferedImage, expectedSize, expectedSize);
    // then
    assertEquals(expectedSize, actualImage.getWidth());
    assertEquals(expectedSize, actualImage.getHeight());
  }
}