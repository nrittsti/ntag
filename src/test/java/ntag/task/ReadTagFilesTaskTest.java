package ntag.task;

import javafx.embed.swing.JFXPanel;
import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.model.TagFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag(Category.Unit)
class ReadTagFilesTaskTest extends AbstractAudioFileTest {

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
  void call() throws Exception {
    // given
    List<Path> pathList = Collections.singletonList(tempDirPath);
    ReadTagFilesTask task = new ReadTagFilesTask(pathList, 10, 100);
    // when
    List<TagFile> files = task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(ALL_AUDIO_FILES.length, files.size());
  }
}
