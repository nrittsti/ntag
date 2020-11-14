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
import ntag.fx.scene.RenameFilesViewModel;
import ntag.io.TagFileReader;
import ntag.model.TagFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag(Category.Unit)
class RenameFilesTaskTest extends AbstractAudioFileTest {

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

    RenameFilesTask task = new RenameFilesTaskWithoutRunLater(vm);

    // when
    task.call();
    // then
    assertFalse(task.hasErrors());
    assertEquals("01 - test.mp3", tagFile.getName());
  }

  // avoid java.lang.IllegalStateException: Toolkit not initialized from
  // com.sun.javafx.application.PlatformImpl.runLater
  private static class RenameFilesTaskWithoutRunLater extends RenameFilesTask {
    public RenameFilesTaskWithoutRunLater(RenameFilesViewModel viewModel) {
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
