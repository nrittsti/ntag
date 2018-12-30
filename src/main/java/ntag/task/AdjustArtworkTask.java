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
import ntag.fx.scene.AdjustArtworkViewModel;
import ntag.io.TagFileWriter;
import ntag.io.util.ArtworkAdjuster;
import ntag.model.ArtworkTag;
import ntag.model.TagFile;

import java.util.ArrayList;
import java.util.List;

public class AdjustArtworkTask extends Task<List<TagFile>> {

	private List<String> errors = new ArrayList<String>();

	private final AdjustArtworkViewModel viewModel;
	private ArtworkTag artwork;
	private int fileSize;
	private final TagFileWriter writer = new TagFileWriter();
	private final ArtworkAdjuster adjuster = new ArtworkAdjuster();

	public AdjustArtworkTask(AdjustArtworkViewModel viewModel) {
		super();
		if (viewModel == null) {
			throw new IllegalArgumentException("viewModel cannot be null");
		}
		this.viewModel = viewModel;
		adjuster.setImageType(viewModel.getImageType());
		adjuster.setEnforceImageType(viewModel.isEnforceImageType());
		adjuster.setMaxResolution(viewModel.getMaxResolution());
		adjuster.setMaxKilobytes(viewModel.getMaxKilobytes());
		adjuster.setQuality(viewModel.getQuality());
	}

	@Override
	protected List<TagFile> call() throws Exception {
		for (int i = 0; i < viewModel.getFiles().size(); i++) {
			if (isCancelled()) {
				updateMessage("Cancelled");
				break;
			}
			updateMessage(toolbox.io.Resources.format("ntag", "msg_checking_artwork", i, viewModel.getFiles().size()));
			TagFile tagFile = viewModel.getFiles().get(i);
			artwork = tagFile.getArtwork();
			fileSize = artwork.getImageData().length / 1000;
			if (artwork == null || //
					(artwork.getHeight() <= viewModel.getMaxResolution() && artwork.getWidth() <= viewModel.getMaxResolution() && //
					fileSize <= viewModel.getMaxKilobytes()) && //
					(!viewModel.isEnforceImageType() || viewModel.getImageType() == artwork.getImageType())) {
					// nothing to do
					continue;
			}
			updateMessage(toolbox.io.Resources.format("ntag", "msg_resizing_artwork", i, viewModel.getFiles().size()));
			try {
				// adjust artwork
				artwork = adjuster.adjust(artwork);
				fileSize = artwork.getImageData().length / 1000;
				// write image data back to audiofile
				tagFile.setArtwork(artwork);
				updateMessage(toolbox.io.Resources.format("ntag", "msg_writing_file", i, viewModel.getFiles().size()));
				writer.update(tagFile);
			} catch (Exception e) {
				errors.add(String.format("%s\n%s", viewModel.getFiles().get(i).getPath(), e.getMessage()));
			}
			updateProgress(i + 1, viewModel.getFiles().size());
		}
		return viewModel.getFiles();
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public List<String> getErrors() {
		return errors;
	}
}
