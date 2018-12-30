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
package ntag.task;

import javafx.concurrent.Task;
import ntag.io.TagFileWriter;
import ntag.model.TagFile;

import java.util.ArrayList;
import java.util.List;

public class WriteTagFilesTask extends Task<Integer> {

	private final List<TagFile> files;
	private final List<TagFile> updatedFiles = new ArrayList<>();
	private final List<String> errors = new ArrayList<String>();

	public WriteTagFilesTask(List<TagFile> files) {
		if (files == null || files.isEmpty()) {
			throw new IllegalArgumentException("files cannot be null or empty");
		}
		this.files = files;
	}

	@Override
	protected Integer call() throws Exception {
		errors.clear();
		TagFileWriter writer = new TagFileWriter();
		for (int i = 0; i < files.size(); i++) {
			if (isCancelled()) {
				updateMessage("Cancelled");
				break;
			}
			updateMessage(toolbox.io.Resources.format("ntag", "msg_writing_file", i, files.size()));
			try {
				writer.update(files.get(i));
				updatedFiles.add(files.get(i));
			} catch (Exception e) {
				errors.add(String.format("%s\n%s: %s", files.get(i).toString(), e.getClass().getName(), e.getMessage()));
			}
			updateProgress(i + 1, files.size());
		}
		return 0;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public List<String> getErrors() {
		return errors;
	}

	public List<TagFile> getUpdatedFiles() {
		return updatedFiles;
	}
}
