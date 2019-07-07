package ntag.io.util;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import ntag.model.ArtworkTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(Category.Unit)
class ArtworkAdjusterTest extends AbstractAudioFileTest {

    private ArtworkAdjuster artworkAdjuster;
    private ArtworkTag artworkTag;

    @BeforeEach
    protected void setUp() throws IOException {
        super.setUp();
        this.artworkAdjuster = new ArtworkAdjuster();
        this.artworkTag = getArtworkTagSample();
    }

    @Test
    @DisplayName("artwork.jpg does not need to be changed")
    void adjust_nothing() throws IOException {
        // given
        int expected = artworkTag.getImageData().length;
        // when
        ArtworkTag actualArtworkTag = artworkAdjuster.adjust(artworkTag);
        int actual = actualArtworkTag.getImageData().length;
        // then
        assertEquals(expected, actual);
        assertEquals(artworkTag.getImageHash(), actualArtworkTag.getImageHash());
    }

    @Test
    @DisplayName("Shrink artwork.jpg to 60 KiB")
    void adjust_filesize() throws IOException {
        // given
        int expected = 60;
        // when
        artworkAdjuster.setMaxKilobytes(expected);
        ArtworkTag actualArtworkTag = artworkAdjuster.adjust(artworkTag);
        int actual = actualArtworkTag.getImageData().length / 1024;
        // then
        assertTrue(actual <= expected);
    }

    @Test
    @DisplayName("Shrink artwork.jpg to 200px")
    void adjust_size() throws IOException {
        // given
        int expected = 200;
        // when
        artworkAdjuster.setMaxResolution(expected);
        ArtworkTag actualArtworkTag = artworkAdjuster.adjust(artworkTag);
        // then
        assertEquals(expected, actualArtworkTag.getWidth());
        assertEquals(expected, actualArtworkTag.getHeight());
    }

    @Test
    @DisplayName("change artwork.jpg to .png")
    void adjust_image_type() throws IOException {
        // given
        ImageUtil.ImageType expected = ImageUtil.ImageType.JPG;
        // when
        artworkAdjuster.setImageType(expected);
        artworkAdjuster.setEnforceImageType(true);
        ArtworkTag actualArtworkTag = artworkAdjuster.adjust(artworkTag);
        // then
        assertEquals(expected, actualArtworkTag.getImageType());
    }
}