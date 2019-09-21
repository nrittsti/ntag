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
package ntag.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import ntag.io.HashUtil;
import ntag.io.util.ImageUtil;
import ntag.io.util.ImageUtil.ImageType;
import org.jaudiotagger.tag.images.Artwork;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class ArtworkTag implements Comparable<ArtworkTag> {

    public static ImageType defaultImageType;
    public static int defaultMaxResolution;
    public static float defaultQuality;

    // ***
    //
    // instance attributes
    //
    // ***

    private static final Logger LOGGER = Logger.getLogger(ArtworkTag.class.getName());

    private byte[] imageData = null;
    private ImageType imageType = null;
    private String imageHash = null;
    private int width;
    private int height;
    private boolean enforceSingleArtwork;

    // ***
    //
    // Construction
    //
    // ***

    public ArtworkTag() {

    }

    public ArtworkTag(byte[] data, int width, int height, ImageType imageType) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (imageType == null) {
            throw new IllegalArgumentException("imageType cannot be null");
        }
        setImageData(data);
        setWidth(width);
        setHeight(height);
        setImageType(imageType);
    }

    public ArtworkTag(Artwork artwork) throws IOException {
        setImageData(artwork.getBinaryData());
        setImageType(ImageType.getByMimeType(artwork.getMimeType()));
        setWidth(artwork.getWidth());
        setHeight(artwork.getHeight());
        if (getImageType() == null) {
            throw new IOException(String.format("MIME '%s' is not supported", artwork.getMimeType()));
        }
    }

    public ArtworkTag(Image image) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null");
        }
        initFromBufferedImage(SwingFXUtils.fromFXImage(image, null), null);
    }

    public ArtworkTag(java.awt.Image image) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null");
        }
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bi.createGraphics().drawImage(image, 0, 0, null);
        initFromBufferedImage(bi, null);
    }

    public ArtworkTag(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        final int maxSize = defaultMaxResolution;
        BufferedImage image = ImageIO.read(file);
        if (image.getWidth() > maxSize || image.getHeight() > maxSize) {
            LOGGER.info(String.format("Image '%s' must be scalled to %d x %d Pixel", file.getAbsolutePath(), maxSize, maxSize));
            initFromBufferedImage(image, null);
        } else {
            final String extension = file.getName().substring(file.getName().lastIndexOf('.'));
            ImageType imageType = ImageType.getByExtension(extension);
            if (imageType == null) {
                throw new IOException("Image format not supported");
            }
            setImageType(imageType);
            setImageData(Files.readAllBytes(file.toPath()));
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }

    public ArtworkTag(BufferedImage image, ImageType imageType) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null");
        }
        initFromBufferedImage(image, imageType);
    }

    public ArtworkTag(BufferedImage image) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null");
        }
        initFromBufferedImage(image, null);
    }

    // ***
    //
    // public API
    //
    // ***

    public byte[] getImageData() {
        return imageData;
    }

    protected void setImageData(byte[] imageData) {
        this.imageData = imageData;
        if (imageData == null) {
            imageHash = null;
        } else {
            imageHash = HashUtil.createFromByteArray("MD5", imageData);
        }
    }

    public String getImageHash() {
        return imageHash;
    }

    public ImageType getImageType() {
        return imageType;
    }

    protected void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public int getWidth() throws IOException {
        if (width <= 0 && imageData.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bis);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }
        }
        return width;
    }

    public int getHeight() throws IOException {
        if (height <= 0 && imageData.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bis);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }
        }
        return height;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    public boolean isEnforceSingleArtwork() {
        return enforceSingleArtwork;
    }

    public void setEnforceSingleArtwork(boolean enforceSingleArtwork) {
        this.enforceSingleArtwork = enforceSingleArtwork;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageHash == null) ? 0 : imageHash.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArtworkTag other = (ArtworkTag) obj;
        if (imageHash == null) {
            if (other.imageHash != null)
                return false;
        } else if (!imageHash.equals(other.imageHash))
            return false;
        return true;
    }

    @Override
    public int compareTo(ArtworkTag other) {
        if (imageData == null) {
            return -1;
        } else if (imageData.length == other.getImageData().length) {
            return 0;
        } else if (imageData.length > other.getImageData().length) {
            return 1;
        } else {
            return -1;
        }
    }

    // ***
    //
    // hidden implementation
    //
    // ***

    private void initFromBufferedImage(BufferedImage bi, ImageType imageType) throws IOException {
        final int maxSize = defaultMaxResolution;
        if (bi.getWidth() > maxSize || bi.getHeight() > maxSize) {
            bi = ImageUtil.scale(bi, maxSize, maxSize);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (imageType == null) {
            imageType = defaultImageType;
        }
        ImageUtil.write(bi, bos, imageType, defaultQuality);
        setImageData(bos.toByteArray());
        setImageType(imageType);
        this.width = bi.getWidth();
        this.height = bi.getHeight();
    }
}
