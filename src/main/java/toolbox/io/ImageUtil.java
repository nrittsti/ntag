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
package toolbox.io;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public final class ImageUtil {

	private ImageUtil() {

	}

	public enum ImageType {
		JPG(new String[] { "image/jpeg", "image/jpg" }, "jpeg", new String[] { ".jpg", ".jpeg" }), //
		PNG(new String[] { "image/png" }, "png", new String[] { ".png" });

		private final String[] mimeTypes;
		private final String format;
		private final String[] extensions;

		private ImageType(String[] mimeTypes, String format, String[] extensions) {
			this.mimeTypes = mimeTypes;
			this.format = format;
			this.extensions = extensions;
		}

		public String[] getMimeTypes() {
			return mimeTypes;
		}

		public String getFormat() {
			return format;
		}

		public String[] getExtensions() {
			return extensions.clone();
		}

		public static ImageType getByMimeType(String value) {
			if (value == null) {
				return null;
			}
			value = value.toLowerCase().trim();
			for (ImageType type : ImageType.values()) {
				for (String mimeType : type.mimeTypes) {
					if (mimeType.equals(value)) {
						return type;
					}
				}
			}
			return null;
		}

		public static ImageType getByFormat(String format) {
			if (format == null) {
				return null;
			}
			format = format.toLowerCase().trim();
			for (ImageType type : ImageType.values()) {
				if (type.format.equals(format)) {
					return type;
				}
			}
			return null;
		}

		public static ImageType getByExtension(String extension) {
			if (extension == null) {
				return null;
			}
			extension = extension.toLowerCase().trim();
			for (ImageType type : ImageType.values()) {
				for (String ex : type.extensions) {
					if (ex.equals(extension)) {
						return type;
					}
				}
			}
			return null;
		}
	}

	/**
	 * If you've been using the imageIO.write method to save JPEG images, you
	 * may notice that some image lose quality. This is because you can't
	 * instruct the imagIO.write method to apply a certain compression quality
	 * to the images.
	 *
	 * @param image
	 * @param os
	 * @throws IOException
	 */
	public static void write(BufferedImage image, OutputStream os, ImageType imageType, float quality) throws IOException {
		ImageWriter writer = ImageIO.getImageWritersByFormatName(imageType.getFormat()).next();
		// instantiate an ImageWriteParam object with default compression
		// options
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		if (imageType == ImageType.JPG) {
			// Now, we can set the compression quality:
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(quality); // an integer between 0 and 1
		}
		// 1 specifies minimum compression and maximum quality
		MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(os);
		writer.setOutput(output);
		IIOImage ioImage = new IIOImage(image, null, null);
		writer.write(null, ioImage, iwp);
		writer.dispose();
	}

	public static BufferedImage scale(BufferedImage image, int width, int height) {
		final Image scaledImage = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		final BufferedImage bufferedScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedScaledImage.getGraphics().drawImage(scaledImage, 0, 0, null);
		return bufferedScaledImage;
	}
}
