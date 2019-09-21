/*
  This file is part of NTag (audio file tag editor).
  <p>
  NTag is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  NTag is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with NTag.  If not, see <http://www.gnu.org/licenses/>.
  <p>
  Copyright 2016, Nico Rittstieg
 */
package ntag.task;

import javafx.concurrent.Task;
import ntag.commons.StringBuilderUtil;
import ntag.fx.scene.RenameFilesViewModel;
import ntag.io.Resources;
import ntag.io.util.FileUtil;
import ntag.model.TagFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RenameFilesTask extends Task<List<TagFile>> {

    private List<String> errors = new ArrayList<>();

    private final RenameFilesViewModel viewModel;
    private int maxDisc = 9;
    private int maxTrack = 99;

    public RenameFilesTask(RenameFilesViewModel viewModel) {
        super();
        if (viewModel == null) {
            throw new IllegalArgumentException("viewModel cannot be null");
        }
        this.viewModel = viewModel;
        for (TagFile tagFile : viewModel.getFiles()) {
            if (tagFile.getTrack() > maxTrack) {
                maxTrack = tagFile.getTrack();
            }
            if (tagFile.getDisc() > maxDisc) {
                maxDisc = tagFile.getDisc();
            }
        }
    }

    @Override
    protected List<TagFile> call() {
        for (int i = 0; i < viewModel.getFiles().size(); i++) {
            if (isCancelled()) {
                updateMessage("Cancelled");
                break;
            }
            try {
              updateMessage(Resources.format("ntag", "msg_rename_file", i, viewModel.getFiles().size()));
                rename(viewModel.getFiles().get(i), viewModel.getFormat());
            } catch (Exception e) {
                errors.add(String.format("%s\n%s", viewModel.getFiles().get(i).getPath(), e.getMessage()));
            }
            updateProgress(i + 1, viewModel.getFiles().size());
        }
        return viewModel.getFiles();
    }

    private void rename(TagFile tagFile, String format) throws IOException {
        StringBuilder sb = new StringBuilder(50);
        sb.append(format);
        sb.append(tagFile.getExtension());
        if (maxDisc == 0) {
            StringBuilderUtil.replace(sb, "%disc", "");
        } else {
            StringBuilderUtil.replace(sb, "%disc", String.format("%0" + String.valueOf(maxDisc).length() + "d", tagFile.getDisc()));
        }
        if (maxTrack == 0) {
            StringBuilderUtil.replace(sb, "%track", "");
        } else {
            StringBuilderUtil.replace(sb, "%track", String.format("%0" + String.valueOf(maxTrack).length() + "d", tagFile.getTrack()));
        }
        StringBuilderUtil.replace(sb, "%title", tagFile.getTitle());
        StringBuilderUtil.replace(sb, "%album", tagFile.getAlbum());
        StringBuilderUtil.replace(sb, "%artist", tagFile.getArtist());
        StringBuilderUtil.replace(sb, "%year", tagFile.getYear() > 0 ? "" + tagFile.getYear() : "");
        String fileName;
        if (viewModel.isStripUnsafeChars()) {
          fileName = FileUtil.sanitizeFilename(sb.toString());
        } else {
            fileName = sb.toString();
        }
        Path path = tagFile.getPath();
        Path newPath = path.resolveSibling(fileName);
        Files.move(path, newPath);
        tagFile.setName(fileName);
        tagFile.setPath(newPath);
        viewModel.getUpdatedFiles().add(tagFile);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }
}
