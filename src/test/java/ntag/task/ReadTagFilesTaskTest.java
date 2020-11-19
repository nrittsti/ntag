/*
 *   This file is part of NTag (audio file tag editor).
 *
 *   NTag is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   NTag is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Copyright 2020, Nico Rittstieg
 *
 */

package ntag.task;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.model.TagFile;
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

  @BeforeEach
  public void setUp() throws IOException {
    super.setUp();
    copyFilesToTempDir();
  }

  @Test
  void call() throws Exception {
    // given
    List<Path> pathList = Collections.singletonList(tempDirPath);
    ReadTagFilesTask task = new ReadTagFilesTaskWithoutRunLater(pathList, 10, 100);
    // when
    List<TagFile> files = task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(ALL_AUDIO_FILES.length, files.size());
  }

  // avoid java.lang.IllegalStateException: Toolkit not initialized from
  // com.sun.javafx.application.PlatformImpl.runLater
  private static class ReadTagFilesTaskWithoutRunLater extends ReadTagFilesTask {
    public ReadTagFilesTaskWithoutRunLater(List<Path> pathList, int maxFiles, int maxDepth) {
      super(pathList, maxFiles, maxDepth);
    }

    @Override
    protected void updateProgress(double workDone, double max) {

    }

    @Override
    protected void updateMessage(String message) {

    }
  }
}
