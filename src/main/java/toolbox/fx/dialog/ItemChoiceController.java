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
package toolbox.fx.dialog;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import toolbox.fx.FxUtil;
import toolbox.io.Resources;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemChoiceController<T> extends AbstractDialogController<ItemChoiceViewModel<T>> {

    private static final Logger LOGGER = Logger.getLogger(ItemChoiceController.class.getName());

    private ListChangeListener<? super T> selectionListChangeListener;

    // ***
    //
    // FXML
    //
    // ***

    @FXML
    private ListView<T> itemsListView;
    @FXML
    private ListView<T> selectionListView;
    @FXML
    private ListView<T> favListView;

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField createItemTextField;

    @FXML
    private Button addItemButton;
    @FXML
    private Button removeItemButton;
    @FXML
    private Button moveUpItemButton;
    @FXML
    private Button moveDownItemButton;
    @FXML
    private Button createItemButton;

    // ***
    //
    // Constructor
    //
    // ***

    public ItemChoiceController() {
        selectionListChangeListener = (Change<? extends T> event) -> {
            updateButtonState();
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTextField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                handleFilterAction(null);
            }
        });
        createItemTextField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                handleCreateItemAction(null);
            }
        });
        addItemButton.disableProperty().bind(itemsListView.getSelectionModel().selectedItemProperty().isNull());
        removeItemButton.disableProperty().bind(selectionListView.getSelectionModel().selectedItemProperty().isNull());
        selectionListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends T> observable, //
                                                                                  T oldValue, T newValue) -> {
            int index = selectionListView.getSelectionModel().getSelectedIndex();
            if (index < 0) {
                moveUpItemButton.setDisable(true);
                moveDownItemButton.setDisable(true);
            } else if (index == 0) {
                moveUpItemButton.setDisable(true);
                moveDownItemButton.setDisable(selectionListView.getItems().size() == 1);
            } else if (index == selectionListView.getItems().size() - 1) {
                moveUpItemButton.setDisable(false);
                moveDownItemButton.setDisable(true);
            } else {
                moveUpItemButton.setDisable(false);
                moveDownItemButton.setDisable(false);
            }
        });
        itemsListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                handleAddItemAction(null);
            }
        });
        selectionListView.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                handleRemoveItemAction(null);
            }
        });
        favListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                handleAddFavAction(null);
            }
        });


    }

    private void updateButtonState() {
        if (getViewModel().isSingleSelection() && selectionListView.getItems().size() > 0) {
            createItemButton.setDisable(true);
        } else {
            createItemButton.setDisable(false);
        }
    }

    // ***
    //
    // public API
    //
    // ***

    @Override
    public void setViewModel(ItemChoiceViewModel<T> viewModel) {
        super.setViewModel(viewModel);
        itemsListView.setItems(viewModel.getFilteredItems());
        selectionListView.setItems(viewModel.getSelection());
        favListView.setItems(viewModel.getFavorites());
        if (viewModel.getSelection().size() > 0) {
            selectionListView.getSelectionModel().select(0);
        }
        createItemButton.setDisable(viewModel.getStringConverter() == null);
        selectionListView.getItems().addListener(selectionListChangeListener);
        updateButtonState();
    }

    // ***
    //
    // Events
    //
    // ***

    @Override
    protected void unbindViewModel() {
        addItemButton.disableProperty().unbind();
        removeItemButton.disableProperty().unbind();
        selectionListView.getItems().removeListener(selectionListChangeListener);
    }

    @FXML
    private void handleOKAction(final ActionEvent event) {
        this.dialogResponse = DialogResponse.OK;
        this.getStage().close();
        unbindViewModel();
    }

    @FXML
    private void handleFilterAction(final ActionEvent event) {
        getViewModel().getFilteredItems().setPredicate(item -> {
            return item.toString().toLowerCase().contains(filterTextField.getText().toLowerCase());
        });
    }

    @FXML
    private void handleAddItemAction(final ActionEvent event) {
        if (getViewModel().isSingleSelection() && selectionListView.getItems().size() > 0) {
            selectionListView.getItems().clear();
        }
        T item = itemsListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (!selectionListView.getItems().contains(item)) {
                selectionListView.getItems().add(item);
            }
        }
    }

    @FXML
    private void handleAddFavAction(final ActionEvent event) {
        if (getViewModel().isSingleSelection() && selectionListView.getItems().size() > 0) {
            selectionListView.getItems().clear();
        }
        T item = favListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (!selectionListView.getItems().contains(item)) {
                selectionListView.getItems().add(item);
            }
        }
    }

    @FXML
    private void handleCreateItemAction(final ActionEvent event) {
        if (getViewModel().isSingleSelection() && selectionListView.getItems().size() > 0) {
            return;
        }
        String textValue = createItemTextField.getText().trim();
        if (textValue.length() > 0) {
            T item = null;
            try {
                item = getViewModel().getStringConverter().fromString(textValue);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, Resources.get("toolbox", "msg_string_conversion_error"), e);
                FxUtil.showException(Resources.get("toolbox", "msg_string_conversion_error"), e);
                return;
            }
            if (item == null) {
                Alert alert = new Alert(AlertType.ERROR, Resources.get("toolbox", "msg_string_conversion_error"), ButtonType.OK);
                alert.showAndWait();
                return;
            }
            if (!selectionListView.getItems().contains(item)) {
                selectionListView.getItems().add(item);
            }
            createItemTextField.setText("");
        }
    }

    @FXML
    private void handleRemoveItemAction(final ActionEvent event) {
        T item = selectionListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            selectionListView.getItems().remove(item);
        }
    }

    @FXML
    private void handleMoveUpItemAction(final ActionEvent event) {
        T item = selectionListView.getSelectionModel().getSelectedItem();
        int index = selectionListView.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            selectionListView.getItems().remove(index);
            selectionListView.getItems().add(index - 1, item);
            selectionListView.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void handleMoveDownItemAction(final ActionEvent event) {
        T item = selectionListView.getSelectionModel().getSelectedItem();
        int index = selectionListView.getSelectionModel().getSelectedIndex();
        if (index < (selectionListView.getItems().size() - 1)) {
            selectionListView.getItems().remove(index);
            selectionListView.getItems().add(index + 1, item);
            selectionListView.getSelectionModel().select(index + 1);
        }
    }
}