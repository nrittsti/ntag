package ntag.io.util;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag(Category.Unit)
class AudioFileVisitorMaxTest extends AbstractAudioFileTest {

    private final static int MAX_FILES = 2;

    private AudioFileVisitor audioFileVisitor;

    @BeforeEach
    void setUp() throws IOException {
        this.copyFilesToTempDir();
        this.audioFileVisitor = new AudioFileVisitor(MAX_FILES);
    }

    @Test
    @DisplayName("keep MAX_FILES parameter")
    void visitFile() throws IOException {
        // given
        long expected = MAX_FILES;
        // when
        Files.walkFileTree(getTempDir(), new HashSet<>(), 100, audioFileVisitor);
        List<Path> actual = audioFileVisitor.getAudioFiles();
        // then
        assertNotNull(actual);
        assertEquals(expected, actual.size());
    }
}