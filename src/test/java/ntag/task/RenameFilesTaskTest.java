package ntag.task;

import javafx.embed.swing.JFXPanel;
import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.NTagException;
import ntag.fx.scene.RenameFilesViewModel;
import ntag.io.TagFileReader;
import ntag.model.TagFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag(Category.Unit)
class RenameFilesTaskTest extends AbstractAudioFileTest {

  @BeforeAll
  static void beforeClass() {
    new JFXPanel();
  }

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    copyFilesToTempDir();
  }

  @Test
  void call() throws IOException, NTagException {
    // given
    TagFile tagFile = new TagFileReader().createTagFile(tempDirPath.resolve(SAMPLE_ID3V23_MP3));

    tagFile.setTrack(1);
    tagFile.setTrackTotal(18);
    tagFile.setTitle("te|st");

    RenameFilesViewModel vm = new RenameFilesViewModel();
    vm.getFiles().add(tagFile);
    vm.setFormat("%track - %title");
    vm.setStripUnsafeChars(true);
    vm.getFiles().add(tagFile);

    RenameFilesTask task = new RenameFilesTask(vm);

    // when
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals("01 - test.mp3", tagFile.getName());
  }
}
