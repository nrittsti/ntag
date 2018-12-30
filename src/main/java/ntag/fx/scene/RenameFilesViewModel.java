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
package ntag.fx.scene;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ntag.model.TagFile;

import java.util.HashSet;
import java.util.Set;

public class RenameFilesViewModel {

	// ***
	//
	// Properties
	//
	// ***

	// *** Strip Unsafe Chars

	private BooleanProperty stripUnsafeChars = new SimpleBooleanProperty(this, "stripUnsafeChars", true);

	public final BooleanProperty stripUnsafeCharsProperty() {
		return this.stripUnsafeChars;
	}

	public final boolean isStripUnsafeChars() {
		return this.stripUnsafeCharsProperty().get();
	}

	public final void setStripUnsafeChars(final boolean stripUnsafeChars) {
		this.stripUnsafeCharsProperty().set(stripUnsafeChars);
	}

	// *** Format

	private StringProperty format = new SimpleStringProperty(this, "format");

	public final StringProperty formatProperty() {
		return this.format;
	}

	public final java.lang.String getFormat() {
		return this.formatProperty().get();
	}

	public final void setFormat(final java.lang.String format) {
		this.formatProperty().set(format);
	}

	// *** EmptySelection

	private BooleanProperty emptySelection = new SimpleBooleanProperty(this, "emptySelection");

	public final BooleanProperty emptySelectionProperty() {
		return this.emptySelection;
	}

	public final boolean isEmptySelection() {
		return this.emptySelectionProperty().get();
	}

	public final void setEmptySelection(final boolean emptySelection) {
		this.emptySelectionProperty().set(emptySelection);
	}

	// *** TagFiles

	private ObservableList<TagFile> files = FXCollections.observableArrayList();

	public ObservableList<TagFile> getFiles() {
		return files;
	}

	// *** UpdatedFiles

	private Set<TagFile> updatedFiles = new HashSet<>();

	public Set<TagFile> getUpdatedFiles() {
		return updatedFiles;
	}

	public void setUpdatedFiles(Set<TagFile> updatedFiles) {
		this.updatedFiles = updatedFiles;
	}
}
