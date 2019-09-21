package ntag;

import ntag.io.NTagProperties;
import ntag.io.util.ImageUtil;
import ntag.model.ArtworkTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class AbstractAudioFileTest {

    protected static final String SAMPLE_FLAC = "sample.flac";
    protected static final String SAMPLE_WMA = "sample.wma";
    protected static final String SAMPLE_M4A = "sample.m4a";
    protected static final String SAMPLE_ID3V23_MP3 = "sample_id3v23.mp3";
    protected static final String SAMPLE_ID3V24_MP3 = "sample_id3v24.mp3";
  protected static final String SAMPLE_INI = "sample.ini";

  protected static final String[] ALL_AUDIO_FILES = {
          SAMPLE_FLAC,
          SAMPLE_WMA,
          SAMPLE_M4A,
          SAMPLE_ID3V23_MP3,
          SAMPLE_ID3V24_MP3
  };

    protected NTagProperties appProps;

    @TempDir
    protected Path tempDirPath;

    @BeforeEach
    protected void setUp() throws IOException {
        NTagProperties.setHomeDir(tempDirPath);
        this.appProps = new NTagProperties();
        this.appProps.distribute();
    }

    protected Path getTempDir() {
      return tempDirPath;
    }

    protected long getFileCount() throws IOException {
      try (Stream<Path> files = Files.list(tempDirPath)) {
            return files.count();
        }
    }

    protected void copyFilesToTempDir() throws IOException {
        Files.copy(getFlacSample(), tempDirPath.resolve(SAMPLE_FLAC));
        Files.copy(getWmaSample(), tempDirPath.resolve(SAMPLE_WMA));
        Files.copy(getM4aSample(), tempDirPath.resolve(SAMPLE_M4A));
        Files.copy(getMp3Id23Sample(), tempDirPath.resolve(SAMPLE_ID3V23_MP3));
        Files.copy(getMp3Id24Sample(), tempDirPath.resolve(SAMPLE_ID3V24_MP3));
    }

    protected static ArtworkTag getArtworkTagSample() throws IOException {
        return new ArtworkTag(Files.readAllBytes(getArtwork()), 500, 500, ImageUtil.ImageType.JPG);
    }

  protected static Path getIniSample() {
    return getPathFromResources(SAMPLE_INI);
  }

    protected static Path getArtwork() {
        return getPathFromResources("artwork.jpg");
    }

    protected static Path getFlacSample() {
        return getPathFromResources(SAMPLE_FLAC);
    }

    protected static Path getWmaSample() {
        return getPathFromResources(SAMPLE_WMA);
    }

    protected static Path getM4aSample() {
        return getPathFromResources(SAMPLE_M4A);
    }

    protected static Path getMp3Id23Sample() {
        return getPathFromResources(SAMPLE_ID3V23_MP3);
    }

    protected static Path getMp3Id24Sample() {
        return getPathFromResources(SAMPLE_ID3V24_MP3);
    }

    protected static Path getPathFromResources(String fileName) {
        ClassLoader classLoader = AbstractAudioFileTest.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            try {
                return Path.of(resource.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(String.format("Cannot resolve '%s'", fileName), e);
            }
        }
    }
}
