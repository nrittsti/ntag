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
 *   Copyright 2021, Nico Rittstieg
 *
 */
package ntag.fx.scene.control.tableview;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import ntag.fx.scene.NTagViewModel;
import ntag.model.TagFile;

import java.net.URI;

import static ntag.fx.util.FxUtil.openURI;

public class TagFileTableView extends EnhancedTableView<TagFile> {

  // ***
  //
  // Attributes
  //
  // ***

  private final ListChangeListener<TagFile> selectionModelListener;
  private final ListChangeListener<TagFile> selectedFilesListener;

  // ***
  //
  // Properties
  //
  // ***

  // *** SelectedTagFiles

  private final ObservableList<TagFile> selectedFiles = FXCollections.observableArrayList();

  @SuppressWarnings("unused")
  public ObservableList<TagFile> getSelectedFiles() {
    return selectedFiles;
  }

  // ***
  //
  // Construction
  //
  // ***

  public TagFileTableView() {

    selectionModelListener = (Change<? extends TagFile> change) -> onSelectionModelChanged();
    this.getSelectionModel().getSelectedItems().addListener(selectionModelListener);

    selectedFilesListener = (Change<? extends TagFile> change) -> onSelectedFilesChanged();
    this.selectedFiles.addListener(selectedFilesListener);
    this.setOnMousePressed(mouseEvent -> {
      if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
        TagFile tagFile = getSelectionModel().getSelectedItem();
        if (tagFile != null) {
          URI uri = tagFile.getPath().toUri();
          openURI(uri);
        }
      }
    });
  }

  // ***
  //
  // public API
  //
  // ***

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void setViewModel(NTagViewModel viewModel) {
    if (viewModel == null) {
      throw new IllegalArgumentException("viewModel cannot be null");
    }

    // FilteredList is unmodifiable, so it cannot be sorted. The List must
    // be wraped in SortedList for this purpose.
    // http://stackoverflow.com/a/18227763
    this.setItems(viewModel.getSortedFiles());
    viewModel.getSortedFiles().comparatorProperty().bind(this.comparatorProperty());

    this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    ObservableList a = viewModel.getColumns();
    ObservableList b = this.getColumns();
    Bindings.bindContentBidirectional(b, a);

    // Configure Table Header Contextmenu
    TagFileTableHeaderMenu contextMenu = new TagFileTableHeaderMenu(viewModel.getColumns());
    TagFileTableColumn.setHeaderContextMenu(contextMenu);
    for (TableColumn c : this.getColumns()) {
      c.setContextMenu(contextMenu);
    }

    Bindings.bindContentBidirectional(selectedFiles, viewModel.getSelectedFiles());
  }

  // ***
  //
  // hidden implementation
  //
  // ***

  private void onSelectionModelChanged() {
    this.selectedFiles.removeListener(selectedFilesListener);
    this.selectedFiles.setAll(this.getSelectionModel().getSelectedItems());
    this.selectedFiles.addListener(selectedFilesListener);
  }

  private void onSelectedFilesChanged() {
    this.getSelectionModel().getSelectedItems().removeListener(selectionModelListener);
    this.getSelectionModel().clearSelection();
    int[] rows = new int[selectedFiles.size()];
    for (int i = 0; i < selectedFiles.size(); i++) {
      rows[i] = this.getItems().indexOf(selectedFiles.get(i));
    }
    this.getSelectionModel().selectIndices(-1, rows);
    this.getSelectionModel().getSelectedItems().addListener(selectionModelListener);
  }
}
