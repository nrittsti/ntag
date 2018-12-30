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
package ntag.io.util;

import ntag.model.ArtworkTag;
import toolbox.io.ImageUtil;
import toolbox.io.ImageUtil.ImageType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArtworkAdjuster {

	public static ImageType defaultImageType;
	public static boolean defaultEnforceImageType;
	public static int defaultMaxResolution;
	public static int defaultMaxKilobytes;
	public static float defaultQuality;

	private ImageType imageType;
	private boolean enforceImageType;
	private int maxResolution;
	private int maxKilobytes;
	private float quality;

	private int fileSizeKB;

	public ArtworkAdjuster() {
		imageType = defaultImageType;
		enforceImageType = defaultEnforceImageType;
		maxResolution = defaultMaxResolution;
		maxKilobytes = defaultMaxKilobytes;
		quality = defaultQuality;
	}

	public ArtworkTag adjust(ArtworkTag artwork) throws IOException {
		// (1): adjust resolution if required
		if (artwork.getHeight() > getMaxResolution() || artwork.getWidth() > getMaxResolution()) {
			artwork = scale(getMaxResolution(), artwork.getImageData());
		}
		float quality = getQuality();
		int size = artwork.getWidth();
		fileSizeKB = artwork.getImageData().length / 1000;
		// (2): adjust fileSize if required
		if (fileSizeKB > getMaxKilobytes()) {
			while (true) {
				if (size > 300) {
					size = size - 100;
					artwork = scale(size, artwork.getImageData());
				} else if (quality > 0.5f) {
					quality = quality - 0.1f;
					ArtworkTag encodedArtwork = encode(quality, artwork);
					if (fileSizeKB <= getMaxKilobytes()) {
						artwork = encodedArtwork;
						break;
					}
				} else {
					break;
				}
			}
		}
		// (3): change Image Format if required
		if (isEnforceImageType() && getImageType() != artwork.getImageType()) {
			artwork = encode(quality, artwork);
		}
		// (4): check max fileSize
		if ((artwork.getImageData().length / 1000) > getMaxKilobytes()) {
			throw new IOException(String.format("Cannot shrink artwork to %d KB!", getMaxKilobytes()));
		}
		return artwork;
	}

	private ArtworkTag scale(int size, byte[] data) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
		image = ImageUtil.scale(image, size, size);
		ArtworkTag scalledArtwork = new ArtworkTag(image, getImageType());
		fileSizeKB = scalledArtwork.getImageData().length / 1000;
		return scalledArtwork;
	}

	private ArtworkTag encode(float quality, ArtworkTag artwork) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(artwork.getImageData()));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageUtil.write(image, bos, getImageType(), quality);
		ArtworkTag encodedArtwork = new ArtworkTag(bos.toByteArray(), artwork.getWidth(), artwork.getHeight(), getImageType());
		fileSizeKB = encodedArtwork.getImageData().length / 1000;
		return encodedArtwork;
	}

	public ImageType getImageType() {
		return imageType;
	}

	public void setImageType(ImageType imageType) {
		this.imageType = imageType;
	}

	public boolean isEnforceImageType() {
		return enforceImageType;
	}

	public void setEnforceImageType(boolean enforceImageType) {
		this.enforceImageType = enforceImageType;
	}

	public int getMaxResolution() {
		return maxResolution;
	}

	public void setMaxResolution(int maxImageSize) {
		this.maxResolution = maxImageSize;
	}

	public int getMaxKilobytes() {
		return maxKilobytes;
	}

	public void setMaxKilobytes(int maxKilobytes) {
		this.maxKilobytes = maxKilobytes;
	}

	public float getQuality() {
		return quality;
	}

	public void setQuality(float quality) {
		this.quality = quality;
	}
}
