package ntag.io;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.model.TagFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag(Category.Unit)
class TagFileReaderTest extends AbstractAudioFileTest {

  private TagFileReader reader;

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    copyFilesToTempDir();
    reader = new TagFileReader();
  }

  @ParameterizedTest
  @CsvSource({SAMPLE_FLAC, SAMPLE_WMA, SAMPLE_M4A, SAMPLE_ID3V23_MP3, SAMPLE_ID3V24_MP3})
  void createTagFile(String file) throws Exception {
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(file));
    assertNotNull(tagFile);
  }

  @ParameterizedTest
  @CsvSource({SAMPLE_FLAC, SAMPLE_WMA, SAMPLE_M4A, SAMPLE_ID3V23_MP3, SAMPLE_ID3V24_MP3})
  void updateTagFile(String file) throws Exception {
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(file));
    tagFile.setTitle("test");
    reader.updateTagFile(tagFile, true);
    assertEquals("", tagFile.getTitle());
  }

}
