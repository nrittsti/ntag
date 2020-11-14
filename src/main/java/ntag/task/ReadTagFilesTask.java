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

import javafx.concurrent.Task;
import ntag.io.Resources;
import ntag.io.TagFileReader;
import ntag.io.util.AudioFileVisitor;
import ntag.model.TagFile;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadTagFilesTask extends Task<List<TagFile>> {

  public static final Logger LOGGER = Logger.getLogger(ReadTagFilesTask.class.getName());

  private final List<Path> pathList;
  private final List<String> errors = new ArrayList<>();
  private final int maxFiles;
  private final int maxDepth;

  public ReadTagFilesTask(List<Path> pathList, int maxFiles, int maxDepth) {
    if (pathList == null || pathList.isEmpty()) {
      throw new IllegalArgumentException("pathList cannot be null or empty");
    }
    this.pathList = pathList;
    this.maxFiles = maxFiles;
    this.maxDepth = maxDepth;
  }

  @Override
  protected List<TagFile> call() throws Exception {
    errors.clear();

    List<TagFile> resultList = new ArrayList<>();

    updateMessage(Resources.get("ntag", "msg_creating_filelist"));

    AudioFileVisitor visitor = new AudioFileVisitor(maxFiles);

    HashSet<FileVisitOption> options = new HashSet<>();
    options.add(FileVisitOption.FOLLOW_LINKS);
    for (Path path : pathList) {
      Files.walkFileTree(path, options, maxDepth, visitor);
    }

    List<Path> files = visitor.getAudioFiles();

    TagFileReader reader = new TagFileReader();
    for (int i = 0; i < files.size(); i++) {
      if (isCancelled()) {
        updateMessage("Cancelled");
        break;
      }
      updateMessage(Resources.format("ntag", "msg_reading_file", i, files.size()));
      try {
        resultList.add(reader.createTagFile(files.get(i)));
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, String.format("%s%n%s", files.get(i).toString(), e.getClass().getName()), e);
        errors.add(String.format("%s%n%s: %s", files.get(i).toString(), e.getClass().getName(), e.getMessage()));
      }
      updateProgress(i + 1, files.size());
    }
    return resultList;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public List<String> getErrors() {
    return errors;
  }
}
