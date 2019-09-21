package ntag.task;

import javafx.embed.swing.JFXPanel;
import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.NTagException;
import ntag.io.TagFileReader;
import ntag.model.TagFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag(Category.Unit)
class WriteTagFilesTaskTest extends AbstractAudioFileTest {

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
    tagFile.setTitle("abcde");
    List<TagFile> tagFileList = Collections.singletonList(tagFile);
    WriteTagFilesTask task = new WriteTagFilesTask(tagFileList);
    // when
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(tagFileList, task.getUpdatedFiles());
  }
}
