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
package ntag.fx.scene.control.tableview;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import toolbox.fx.control.BooleanTableCell;
import toolbox.fx.control.EnhancedTableCell;
import toolbox.fx.control.LocalDateTableCell;
import toolbox.fx.control.LocalDateTimeTableCell;
import toolbox.io.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagFileTableColumn extends TableColumn<Object, Object> {

	static final Logger LOGGER = Logger.getLogger(TagFileTableColumn.class.getName());

	public enum ColumnType {

		ROW(30, "row", null), //
		STATUS(80, "status", StatusTableCell.class), //
		FILENAME(300, "name", null), //
		EXTENSION(40, "extension", null), //
		DIRECTORY(300, "directory", null), //
		TITLE(200, "title", null), //
		ARTIST(150, "artist", null), //
		ALBUM(150, "album", null), //
		ALBUM_ARTIST(150, "albumArtist", null), //
		GENRE(100, "genre", null), //
		YEAR(50, "year", null), //
		DATE(100, "date", LocalDateTableCell.class), //
		DISC(50, "disc", null), //
		TRACK(50, "track", null), //
		COMPOSER(150, "composer", null), //
		COMMENT(200, "comment", null), //
		RATING(80, "rating", RatingTableCell.class), //
		PLAYTIME(80, "playtime", PlaytimeTableCell.class), //
		SIZE(80, "size", FileSizeTableCell.class), //
		FORMAT(100, "audioFormat", null), //
		BITRATE(80, "bitrate", BitrateTableCell.class), //
		VBR(40, "vbr", BooleanTableCell.class), //
		CREATED(140, "created", LocalDateTimeTableCell.class), //
		MODIFIED(140, "modified", LocalDateTimeTableCell.class);

		private final double width;
		private final String property;
		private String label;
		private final Class<? extends TableCell<Object, ? extends Object>> tableCellClass;

		private ColumnType(double width, String property, Class<? extends TableCell<Object, ? extends Object>> tableCellClass) {
			this.width = width;
			this.label = Resources.get("ntag", "lbl_" + this.name().toLowerCase());
			this.property = property;
			this.tableCellClass = tableCellClass;
		}

		public static List<ColumnType> getSortedValues() {
			ArrayList<ColumnType> values = new ArrayList<>(Arrays.asList(ColumnType.values()));
			values.sort((ColumnType o1, ColumnType o2) -> {
				return o1.label.compareTo(o2.label);
			});
			return values;
		}

		public String getLabel() {
			return label;
		}

		public double getWidth() {
			return width;
		}

		public String getProperty() {
			return property;
		}

		public Class<? extends TableCell<Object, ? extends Object>> getTableCellClass() {
			return tableCellClass;
		}
	}

	// ***
	//
	// Attributes
	//
	// ***

	private ColumnType type;
	private static TagFileTableHeaderMenu headerContextMenu = null;

	// ***
	//
	// Construction
	//
	// ***

	public TagFileTableColumn(ColumnType type) {
		this(type, type != null ? type.getWidth() : 0);
		this.setContextMenu(headerContextMenu);
	}

	public TagFileTableColumn(ColumnType type, double width) {
		super();
		if (type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}
		if (width < 0) {
			throw new IllegalArgumentException("width must be greater than 0");
		}
		this.type = type;
		this.setResizable(true);
		this.setSortable(type != ColumnType.ROW);
		this.setId(type.name());
		this.setText(type.getLabel());
		if(type == ColumnType.ROW) {
			this.setCellValueFactory(column -> {
				return new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue()) + 1);
			});
		} else {
			this.setCellValueFactory(new PropertyValueFactory<>(type.getProperty()));
		}
		Class<? extends TableCell<Object, ? extends Object>> tableCellClass = type.getTableCellClass();

		Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = new Callback<TableColumn<Object, Object>, TableCell<Object, Object>>() {
			@SuppressWarnings("unchecked")
			@Override
			public TableCell<Object, Object> call(TableColumn<Object, Object> p) {
				if (tableCellClass != null) {
					try {
						return (TableCell<Object, Object>) tableCellClass.newInstance();
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Error by ColumnType: " + type.name(), e);
					}
				}
				return new EnhancedTableCell<>();
			}
		};
		this.setCellFactory(cellFactory);

		this.setPrefWidth(width);
	}

	// ***
	//
	// public API
	//
	// ***

	public ColumnType getType() {
		return type;
	}

	public static TagFileTableHeaderMenu getHeaderContextMenu() {
		return headerContextMenu;
	}

	public static void setHeaderContextMenu(TagFileTableHeaderMenu headerContextMenu) {
		TagFileTableColumn.headerContextMenu = headerContextMenu;
	}
}
