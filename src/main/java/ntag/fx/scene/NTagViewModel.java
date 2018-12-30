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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ntag.fx.scene.control.tableview.TagFileTableColumn;
import ntag.fx.scene.control.tableview.TagFileTableColumn.ColumnType;
import ntag.io.NTagProperties;
import ntag.model.TagFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NTagViewModel {

	// ***
	//
	// Instance Attributes
	//
	// ***

	private final NTagProperties appProps;

	// ***
	//
	// Properties
	//
	// ***

	// *** Columns

	private ObservableList<TagFileTableColumn> columns = FXCollections.observableArrayList();

	public ObservableList<TagFileTableColumn> getColumns() {
		return columns;
	}

	// *** Files

	private ObservableList<TagFile> files = FXCollections.observableArrayList();

	public ObservableList<TagFile> getFiles() {
		return files;
	}

	// *** FilteredFiles

	private FilteredList<TagFile> filteredFiles = null;

	public ObservableList<TagFile> getFilteredFiles() {
		return filteredFiles;
	}

	// *** UpdatedFiles

	private Set<TagFile> updatedFiles = new HashSet<>();

	public Set<TagFile> getUpdatedFiles() {
		return updatedFiles;
	}

	// *** FileCount

	private ObjectProperty<Integer> fileCount = new SimpleObjectProperty<Integer>(this, "fileCount", 0);

	public final ObjectProperty<Integer> fileCountProperty() {
		return this.fileCount;
	}

	public final Integer getFileCount() {
		return this.fileCountProperty().get();
	}

	public final void setFileCount(final Integer fileCount) {
		this.fileCountProperty().set(fileCount);
	}

	// *** playtime

	private ObjectProperty<Long> playtime = new SimpleObjectProperty<Long>(this, "playtime", 0l);

	public final ObjectProperty<Long> playtimeProperty() {
		return this.playtime;
	}

	public final java.lang.Long getPlaytime() {
		return this.playtimeProperty().get();
	}

	public final void setPlaytime(final java.lang.Long playtime) {
		this.playtimeProperty().set(playtime);
	}

	// *** fileSize

	private ObjectProperty<Long> fileSize = new SimpleObjectProperty<Long>(this, "fileSize", 0l);

	public final ObjectProperty<Long> fileSizeProperty() {
		return this.fileSize;
	}

	public final java.lang.Long getFileSize() {
		return this.fileSizeProperty().get();
	}

	public final void setFileSize(final java.lang.Long fileSize) {
		this.fileSizeProperty().set(fileSize);
	}

	// *** SelectedFiles

	private ObservableList<TagFile> selectedFiles = FXCollections.observableArrayList();

	public ObservableList<TagFile> getSelectedFiles() {
		return selectedFiles;
	}

	// *** SelectedFileCount

	private ObjectProperty<Integer> selectedFileCount = new SimpleObjectProperty<Integer>(this, "selectedFileCount", 0);

	public final ObjectProperty<Integer> selectedFileCountProperty() {
		return this.selectedFileCount;
	}

	public final java.lang.Integer getSelectedFileCount() {
		return this.selectedFileCountProperty().get();
	}

	public final void setSelectedFileCount(final java.lang.Integer selectedFileCount) {
		this.selectedFileCountProperty().set(selectedFileCount);
	}

	// *** SelectedPlaytime

	private ObjectProperty<Long> selectedPlaytime = new SimpleObjectProperty<Long>(this, "selectedPlaytime", 0l);

	public final ObjectProperty<Long> selectedPlaytimeProperty() {
		return this.selectedPlaytime;
	}

	public final java.lang.Long getSelectedPlaytime() {
		return this.selectedPlaytimeProperty().get();
	}

	public final void setSelectedPlaytime(final java.lang.Long selectedPlaytime) {
		this.selectedPlaytimeProperty().set(selectedPlaytime);
	}

	// *** SelectedFileSize

	private ObjectProperty<Long> selectedFileSize = new SimpleObjectProperty<Long>(this, "selectedFileSize", 0l);

	public final ObjectProperty<Long> selectedFileSizeProperty() {
		return this.selectedFileSize;
	}

	public final java.lang.Long getSelectedFileSize() {
		return this.selectedFileSizeProperty().get();
	}

	public final void setSelectedFileSize(final java.lang.Long selectedFileSize) {
		this.selectedFileSizeProperty().set(selectedFileSize);
	}

	// ***
	//
	// Construction
	//
	// ***

	public NTagViewModel(NTagProperties appProps) {
		this.appProps = appProps;
		for (ColumnType c : appProps.getVisibleColumns()) {
			columns.add(new TagFileTableColumn(c));
		}
		files.addListener((Change<? extends TagFile> change) -> {
			setFileCount(change.getList().size());
			calcFileProperties();
		});
		selectedFiles.addListener((Change<? extends TagFile> change) -> {
			setSelectedFileCount(change.getList().size());
			calcSelectedFileProperties();
		});
		filteredFiles = new FilteredList<>(files);
		columns.addListener((Change<? extends TagFileTableColumn> change) -> {
			List<ColumnType> columnList = new ArrayList<>();
			for (TagFileTableColumn tc : columns) {
				columnList.add(tc.getType());
			}
			appProps.setVisibleColumns(columnList);
		});
	}

	// ***
	//
	// public API
	//
	// ***

	public void setFilterMode(NTagFilterMode filterMode) {
		filteredFiles.setPredicate(tagFile -> {
			switch (filterMode) {
				case MissingArtwork:
					return !tagFile.isArtworkPresent();
				case ChangedFiles:
					return tagFile.isDirty();
				case MissingMetadata:
					return tagFile.isIncomplete();
				case MissingLyrics:
					return tagFile.getLyrics() == null || tagFile.getLyrics().length() == 0;
				case MissingRating:
					return tagFile.getRating() == null || tagFile.getRating() <= 0;
				case All:
					return true;
				default:
					return true;
			}
		});
	}

	// ***
	//
	// hidden implementation
	//
	// ***

	private void calcFileProperties() {
		long playtime = 0;
		long fileSize = 0;
		for(TagFile file : getFiles()) {
			playtime += file.getPlaytime();
			fileSize += file.getSize();
		}
		setPlaytime(playtime);
		setFileSize(fileSize);
	}

	private void calcSelectedFileProperties() {
		long playtime = 0;
		long fileSize = 0;
		for (TagFile file : getSelectedFiles()) {
			playtime += file.getPlaytime();
			fileSize += file.getSize();
		}
		setSelectedPlaytime(playtime);
		setSelectedFileSize(fileSize);
	}
}
