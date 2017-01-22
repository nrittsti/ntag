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
package ntag.fx.scene.control.editor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;
import ntag.model.ArtworkTag;
import toolbox.io.Resources;

public class ArtworkEditorProperty extends EditorProperty<ArtworkTag> {

	private static final Logger LOGGER = Logger.getLogger(ArtworkEditorProperty.class.getName());

	private final static EmptyCheck<ArtworkTag> emptyArtworkCheck = (ArtworkTag value) -> {
		return value == null || value.getImageData() == null;
	};

	public static boolean binaryUnit;

	// ***
	//
	// Construction
	//
	// ***

	public ArtworkEditorProperty() throws NoSuchMethodException, SecurityException {
		super("artwork", emptyArtworkCheck);
		setTooltip(new Tooltip(""));
		valueProperty().addListener((ObservableValue<? extends ArtworkTag> observable, ArtworkTag oldValue, ArtworkTag newValue) -> {
			createTooltip(newValue);
		});
	}

	@Override
	protected void createTooltip() {
		if (getTooltip() == null) {
			setTooltip(new Tooltip());
		}
		if (getValues().size() == 0) {
			getTooltip().setText("");
		} else if (getValues().size() == 1) {
			ArtworkTag artwork = getValues().get(0);
			createTooltip(artwork);
		} else {
			getTooltip().setText(Resources.format("ntag", "differences", getValues().size()));
		}
	}

	protected void createTooltip(ArtworkTag artwork) {
		if (artwork == null) {
			getTooltip().setText("");
			return;
		}
		final long unit = binaryUnit ? 1024 : 1000;
		try {
			getTooltip().setText(String.format("%s\n%d x %d Pixel\n%d %s", artwork.getImageType().getFormat(), //
					artwork.getWidth(), //
					artwork.getHeight(), //
					artwork.getImageData().length / unit, //
					binaryUnit ? "KiB" : "KB"));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "ImageIO Error", e);
		}
	}

	// ***
	//
	// public API
	//
	// ***

}
