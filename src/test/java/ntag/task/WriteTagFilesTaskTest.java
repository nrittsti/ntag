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
import ntag.NTagException;
import ntag.io.TagFileReader;
import ntag.model.TagFile;
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
    WriteTagFilesTask task = new WriteTagFilesTaskTestWithoutRunLater(tagFileList);
    // when
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals(tagFileList, task.getUpdatedFiles());
  }

  // avoid java.lang.IllegalStateException: Toolkit not initialized from
  // com.sun.javafx.application.PlatformImpl.runLater
  private static class WriteTagFilesTaskTestWithoutRunLater extends WriteTagFilesTask {
    public WriteTagFilesTaskTestWithoutRunLater(List<TagFile> viewModel) {
      super(viewModel);
    }

    @Override
    protected void updateProgress(double workDone, double max) {

    }

    @Override
    protected void updateMessage(String message) {

    }
  }
}
