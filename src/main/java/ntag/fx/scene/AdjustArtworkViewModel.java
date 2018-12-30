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

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ntag.model.TagFile;
import toolbox.io.ImageUtil.ImageType;

public class AdjustArtworkViewModel {

	// ***
	//
	// Properties
	//
	// ***

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

	// *** Quality

	private FloatProperty qualityProp = new SimpleFloatProperty(this, "qualityProp", 0.9f);

	public FloatProperty qualityProperty() {
		return qualityProp;
	}

	public float getQuality() {
		return qualityProp.get();
	}

	public void setQuality(float value) {
		qualityProp.set(value);
	}

	// *** MaxResolution

	private IntegerProperty maxResolutionProp = new SimpleIntegerProperty(this, "maxResolution");

	public IntegerProperty maxResolutionProperty() {
		return maxResolutionProp;
	}

	public Integer getMaxResolution() {
		return maxResolutionProp.get();
	}

	public void setMaxResolution(Integer value) {
		maxResolutionProp.set(value);
	}

	// *** MaxKilobytes

	private IntegerProperty maxKilobytesProp = new SimpleIntegerProperty(this, "maxKilobytes");

	public IntegerProperty maxKilobytesProperty() {
		return maxKilobytesProp;
	}

	public Integer getMaxKilobytes() {
		return maxKilobytesProp.get();
	}

	public void setMaxKilobytes(Integer value) {
		maxKilobytesProp.set(value);
	}

	// *** TagFiles

	private ObservableList<TagFile> files = FXCollections.observableArrayList();

	public ObservableList<TagFile> getFiles() {
		return files;
	}

	// ImageType

	private ObjectProperty<ImageType> imageTypeProp = new SimpleObjectProperty<ImageType>(this, "imageType");

	public ObjectProperty<ImageType> imageTypeProp() {
		return imageTypeProp;
	}

	public ImageType getImageType() {
		return imageTypeProp.get();
	}

	public void setImageType(ImageType value) {
		imageTypeProp.set(value);
	}

	// Enforce Single Artwork Instance

	private BooleanProperty enforceSingleProp = new SimpleBooleanProperty(this, "enforceSingle");

	public BooleanProperty enforceSingleProperty() {
		return enforceSingleProp;
	}

	public boolean isEnforceSingle() {
		return enforceSingleProp.get();
	}

	public void setEnforceSingle(boolean value) {
		enforceSingleProp.set(value);
	}

	// Enforce ImageType

	private BooleanProperty enforceImageTypeProp = new SimpleBooleanProperty(this, "enforceImageType");

	public BooleanProperty enforceImageTypeProperty() {
		return enforceImageTypeProp;
	}

	public boolean isEnforceImageType() {
		return enforceImageTypeProp.get();
	}

	public void setEnforceImageType(boolean value) {
		enforceImageTypeProp.set(value);
	}

	// ***
	//
	// Construction
	//
	// ***

	public AdjustArtworkViewModel() {

	}
}
