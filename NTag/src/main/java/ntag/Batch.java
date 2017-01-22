/**
 * This file is part of NTag (audio file tag editor).
 *
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016, Nico Rittstieg
 */
package ntag;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import ntag.io.JAudiotaggerHelper;
import ntag.io.util.AudioFileVisitor;

public class Batch {

	public static void main(String[] args) {
		Path path = Paths.get("");
		AudioFileVisitor visitor = new AudioFileVisitor(10000);
		try {
			Files.walkFileTree(path, new HashSet<FileVisitOption>(), 100, visitor);
			List<Path> files = visitor.getAudioFiles();
			for (Path filePath : files) {
				final AudioFile audioFile = JAudiotaggerHelper.readAudioFile(filePath);
				if (audioFile instanceof MP3File) {
					MP3File mp3File = (MP3File) audioFile;
					if (mp3File.hasID3v2Tag()) {
						AbstractID3v2Tag tag = mp3File.getID3v2Tag();
						if (tag instanceof ID3v23Tag) {
							if (doSomething(filePath, tag)) {
								audioFile.commit();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean doSomething(Path path, AbstractID3v2Tag tag) {

		return false;
	}
}
