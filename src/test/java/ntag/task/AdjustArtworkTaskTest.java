package ntag.task;

import javafx.embed.swing.JFXPanel;
import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.fx.scene.AdjustArtworkViewModel;
import ntag.io.NTagProperties;
import ntag.io.TagFileReader;
import ntag.io.util.ImageUtil;
import ntag.model.ArtworkTag;
import ntag.model.TagFile;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Tag(Category.Unit)
class AdjustArtworkTaskTest extends AbstractAudioFileTest {

  private ArtworkTag artworkTag;

  private NTagProperties appProperties;

  private TagFileReader reader;

  @BeforeAll
  static void beforeClass() {
    new JFXPanel();
  }

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    this.artworkTag = getArtworkTagSample();
    this.appProperties = new NTagProperties();
    this.reader = new TagFileReader();
    copyFilesToTempDir();
  }

  @Test
  @DisplayName("artwork.jpg does not need to be changed")
  void adjust_nothing() throws Exception {
    // given
    String expected = artworkTag.getImageHash();
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    tagFile.setArtwork(artworkTag);
    AdjustArtworkViewModel viewModel = new AdjustArtworkViewModel();
    viewModel.setQuality(appProperties.getArtworkQuality());
    viewModel.setMaxResolution(appProperties.getArtworkMaxResolution());
    viewModel.setMaxKilobytes(appProperties.getArtworkMaxKilobytes());
    viewModel.setImageType(appProperties.getArtworkImageType());
    viewModel.setEnforceImageType(appProperties.isArtworkEnforceImageFormat());
    viewModel.setEnforceSingle(appProperties.isArtworkEnforceSingle());
    viewModel.getFiles().add(tagFile);
    // when
    AdjustArtworkTask task = new AdjustArtworkTask(viewModel);
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(expected, tagFile.getArtwork().getImageHash());
  }

  @Test
  @DisplayName("Shrink artwork.jpg to 60 KiB")
  void adjust_filesize() throws Exception {
    // given
    int expected = 60;
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    tagFile.setArtwork(artworkTag);
    AdjustArtworkViewModel viewModel = new AdjustArtworkViewModel();
    viewModel.setQuality(appProperties.getArtworkQuality());
    viewModel.setMaxResolution(appProperties.getArtworkMaxResolution());
    viewModel.setMaxKilobytes(expected);
    viewModel.setImageType(appProperties.getArtworkImageType());
    viewModel.setEnforceImageType(appProperties.isArtworkEnforceImageFormat());
    viewModel.setEnforceSingle(appProperties.isArtworkEnforceSingle());
    viewModel.getFiles().add(tagFile);
    // when
    AdjustArtworkTask task = new AdjustArtworkTask(viewModel);
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertTrue(tagFile.getArtwork().getImageData().length / 1024 <= expected);
  }

  @Test
  @DisplayName("Shrink artwork.jpg to 200px")
  void adjust_size() throws Exception {
    // given
    int expected = 200;
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    tagFile.setArtwork(artworkTag);
    AdjustArtworkViewModel viewModel = new AdjustArtworkViewModel();
    viewModel.setQuality(appProperties.getArtworkQuality());
    viewModel.setMaxResolution(expected);
    viewModel.setMaxKilobytes(appProperties.getArtworkMaxKilobytes());
    viewModel.setImageType(appProperties.getArtworkImageType());
    viewModel.setEnforceImageType(appProperties.isArtworkEnforceImageFormat());
    viewModel.setEnforceSingle(appProperties.isArtworkEnforceSingle());
    viewModel.getFiles().add(tagFile);
    // when
    AdjustArtworkTask task = new AdjustArtworkTask(viewModel);
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(expected, tagFile.getArtwork().getWidth());
    assertEquals(expected, tagFile.getArtwork().getHeight());
  }

  @Test
  @DisplayName("change artwork.jpg to .png")
  void adjust_image_type() throws Exception {
    // given
    ImageUtil.ImageType expected = ImageUtil.ImageType.JPG;
    TagFile tagFile = reader.createTagFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));
    tagFile.setArtwork(artworkTag);
    AdjustArtworkViewModel viewModel = new AdjustArtworkViewModel();
    viewModel.setQuality(appProperties.getArtworkQuality());
    viewModel.setMaxResolution(appProperties.getArtworkMaxResolution());
    viewModel.setMaxKilobytes(appProperties.getArtworkMaxKilobytes());
    viewModel.setImageType(expected);
    viewModel.setEnforceImageType(appProperties.isArtworkEnforceImageFormat());
    viewModel.setEnforceSingle(appProperties.isArtworkEnforceSingle());
    viewModel.getFiles().add(tagFile);
    // when
    AdjustArtworkTask task = new AdjustArtworkTask(viewModel);
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(expected, tagFile.getArtwork().getImageType());
  }
}
