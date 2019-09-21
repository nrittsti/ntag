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

@Tag(Category.Unit)
class TagFileWriterTest extends AbstractAudioFileTest {

  private TagFileReader reader;
  private TagFileWriter writer;

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    copyFilesToTempDir();
    reader = new TagFileReader();
    writer = new TagFileWriter();
  }

  @ParameterizedTest
  @CsvSource({SAMPLE_FLAC, SAMPLE_WMA, SAMPLE_M4A, SAMPLE_ID3V23_MP3, SAMPLE_ID3V24_MP3})
  void update(String file) throws Exception {
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(file));

    tagFile.setTitle("t");
    tagFile.setArtist("ar");
    tagFile.setAlbum("a");
    tagFile.setAlbumArtist("at");
    tagFile.setComposer("c");
    tagFile.setYear(2019);
    tagFile.setGenre("Rock");
    tagFile.setLanguage("ENG");
    tagFile.setRating(3);
    tagFile.setDisc(1);
    tagFile.setDiscTotal(2);
    tagFile.setTrack(3);
    tagFile.setTrackTotal(12);
    tagFile.setComment("test");
    tagFile.setLyrics("bla bla bla");

    writer.update(tagFile);
    tagFile = reader.createTagFile(tempDirPath.resolve(file));

    assertEquals("t", tagFile.getTitle());
    assertEquals("ar", tagFile.getArtist());
    assertEquals("a", tagFile.getAlbum());
    assertEquals("at", tagFile.getAlbumArtist());
    assertEquals("c", tagFile.getComposer());
    assertEquals(2019, tagFile.getYear());
    assertEquals("Rock", tagFile.getGenre());
    assertEquals("ENG", tagFile.getLanguage());
    assertEquals(3, tagFile.getRating());
    assertEquals(1, tagFile.getDisc());
    assertEquals(2, tagFile.getDiscTotal());
    assertEquals(3, tagFile.getTrack());
    assertEquals(12, tagFile.getTrackTotal());
    assertEquals("test", tagFile.getComment());
    assertEquals("bla bla bla", tagFile.getLyrics());
  }
}
