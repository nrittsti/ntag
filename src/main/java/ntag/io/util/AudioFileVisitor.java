/**
 * This file is part of NTag (audio file tag editor).
 * <p>
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2016, Nico Rittstieg
 */
package ntag.io.util;

import ntag.model.AudioFormat;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AudioFileVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> audioFiles = new ArrayList<>();
    private final Set<String> extensions = AudioFormat.getFileExtensions();
    private final int maxFiles;

    public AudioFileVisitor(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isRegularFile() && matches(file) && !audioFiles.contains(file)) {
            audioFiles.add(file);
        }
        if (audioFiles.size() < maxFiles) {
            return FileVisitResult.CONTINUE;
        } else {
            return FileVisitResult.TERMINATE;
        }
    }

    public List<Path> getAudioFiles() {
        return audioFiles;
    }

    private boolean matches(Path file) {
        String name = file.toString().toLowerCase();
        for (String extension : extensions) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
