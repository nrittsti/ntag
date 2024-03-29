/*
 *   This file is part of NTag (audio file tag editor).
 *
 *   NTag is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   NTag is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Copyright 2023, Nico Rittstieg
 *
 */
package ntag.model;

import javafx.beans.property.*;
import ntag.NTagException;
import ntag.io.JAudiotaggerUtil;
import ntag.io.NTagProperties;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagFile {

  public static final Logger LOGGER = Logger.getLogger(TagFile.class.getName());

  // *** jaudiotagger AudioFile representation

  private AudioFile audioFile;

  public AudioFile getAudioFile() {
    return audioFile;
  }

  public void setAudioFile(AudioFile audioFile) {
    this.audioFile = audioFile;
  }

  // ***
  //
  // Status Properties
  //
  // ***

  // *** human readable file informations

  private String infos;

  public String getInfos() {
    return infos;
  }

  public void setInfos(String infos) {
    this.infos = infos;
  }

  // *** dirty flag for unsafed changes

  private boolean dirty;

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
    updateStatus();
  }

  // *** status (contains char codes)

  private final StringProperty status = new SimpleStringProperty(this, "status", "");

  public final StringProperty statusProperty() {
    return this.status;
  }

  public final java.lang.String getStatus() {
    return this.statusProperty().get();
  }

  public final void setStatus(final java.lang.String status) {
    this.statusProperty().set(status);
  }

  // ***
  //
  // File Properties
  //
  // ***

  // *** Origin Path

  private String path;

  /**
   * Returns the Path of this audiofile
   *
   * @return the Path of this AudioFile
   */
  public Path getPath() {
    if (path != null) {
      return Paths.get(path);
    } else {
      return null;
    }
  }

  public void setPath(Path path) {
    if (path == null) {
      this.path = null;
    } else {
      this.path = path.toString();
      if (this.audioFile != null) {
        this.audioFile.setFile(new File(this.path));
      }
    }
  }

  // *** Filename

  private final StringProperty name = new SimpleStringProperty(this, "name", "");

  public final StringProperty nameProperty() {
    return this.name;
  }

  public final java.lang.String getName() {
    return this.nameProperty().get();
  }

  public final void setName(final java.lang.String name) {
    this.nameProperty().set(name);
  }

  // *** Directory

  private String directory;

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  // *** File Size in Bytes

  private long size;

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  // *** file extension

  private String extension;

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  // *** file creation timestamp

  private LocalDateTime created;

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  // *** file modified timestamp

  private LocalDateTime modified;

  @SuppressWarnings("unused")
  public LocalDateTime getModified() {
    return modified;
  }

  public void setModified(LocalDateTime modified) {
    this.modified = modified;
  }

  // *** file read-only flag

  private boolean readOnly;

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  // ***
  //
  // Header Information Properties
  //
  // ***

  // *** audioformat like AAC, MP3, OGG or MP4

  private AudioFormat audioFormat;

  public AudioFormat getAudioFormat() {
    return audioFormat;
  }

  public void setAudioFormat(AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
  }

  // *** audio codec

  private String codec;

  public String getCodec() {
    return codec;
  }

  public void setCodec(String codec) {
    this.codec = codec;
  }

  // *** audio channels like mono, stereo amd joint stereo

  private String channels;

  public String getChannels() {
    return channels;
  }

  public void setChannels(String channels) {
    this.channels = channels;
  }

  // *** Tagging System like AiffTag, AsfTag, FlacTag, GenericTag, ID3vXXTag,
  // Mp4Tag, RealTag, VorbisCommentTag, WavTag

  private String taggingSystem;

  public String getTaggingSystem() {
    return taggingSystem;
  }

  public void setTaggingSystem(String taggingSystem) {
    this.taggingSystem = taggingSystem;
  }

  // *** software encoder

  private String encoder;

  public String getEncoder() {
    return encoder;
  }

  public void setEncoder(String encoder) {
    this.encoder = encoder;
  }

  // *** VBR flag

  private boolean vbr;

  @SuppressWarnings("unused")
  public boolean isVbr() {
    return vbr;
  }

  public void setVbr(boolean vbr) {
    this.vbr = vbr;
  }

  // *** lossless flag

  private boolean lossless;

  public boolean isLossless() {
    return lossless;
  }

  public void setLossless(boolean lossless) {
    this.lossless = lossless;
  }

  // *** Bitrate (kBit/s)

  private long bitrate;

  public long getBitrate() {
    return bitrate;
  }

  public void setBitrate(long bitrate) {
    this.bitrate = bitrate;
  }

  // *** sampling rate in Hz

  private long samplingRate;

  public long getSamplingRate() {
    return samplingRate;
  }

  public void setSamplingRate(long samplingRate) {
    this.samplingRate = samplingRate;
  }

  // *** playtime in seconds

  private long playtime;

  public long getPlaytime() {
    return playtime;
  }

  public void setPlaytime(long playtime) {
    this.playtime = playtime;
  }

  // ***
  //
  // Metadata Properties
  //
  // ***

  // *** Title

  private final StringProperty title = new SimpleStringProperty(this, "title", "");

  public final StringProperty titleProperty() {
    return this.title;
  }

  public final String getTitle() {
    return this.titleProperty().get();
  }

  public final void setTitle(final String title) {
    this.titleProperty().set(title);
    updateStatus();
  }

  // *** Artist

  private final StringProperty artist = new SimpleStringProperty(this, "artist", "");

  public final StringProperty artistProperty() {
    return this.artist;
  }

  public final java.lang.String getArtist() {
    return this.artistProperty().get();
  }

  public final void setArtist(final java.lang.String artist) {
    this.artistProperty().set(artist);
    updateStatus();
  }

  // *** Album

  private final StringProperty album = new SimpleStringProperty(this, "album", "");

  public final StringProperty albumProperty() {
    return this.album;
  }

  public final java.lang.String getAlbum() {
    return this.albumProperty().get();
  }

  public final void setAlbum(final java.lang.String album) {
    this.albumProperty().set(album);
    updateStatus();
  }

  // *** AlbumArtist

  private final StringProperty albumArtist = new SimpleStringProperty(this, "albumArtist", "");

  public final StringProperty albumArtistProperty() {
    return this.albumArtist;
  }

  public final java.lang.String getAlbumArtist() {
    return this.albumArtistProperty().get();
  }

  public final void setAlbumArtist(final java.lang.String albumArtist) {
    this.albumArtistProperty().set(albumArtist);
  }

  // *** Composer

  private final StringProperty composer = new SimpleStringProperty(this, "composer", "");

  public final StringProperty composerProperty() {
    return this.composer;
  }

  public final java.lang.String getComposer() {
    return this.composerProperty().get();
  }

  public final void setComposer(final java.lang.String composer) {
    this.composerProperty().set(composer);
  }

  // *** Year

  private final ObjectProperty<Integer> year = new SimpleObjectProperty<>(this, "year", null);

  public final ObjectProperty<Integer> yearProperty() {
    return this.year;
  }

  public final java.lang.Integer getYear() {
    return this.yearProperty().get();
  }

  public final void setYear(final java.lang.Integer year) {
    this.yearProperty().set(year);
    updateStatus();
  }

  // *** date

  private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(this, "date", null);

  public final ObjectProperty<LocalDate> dateProperty() {
    return this.date;
  }

  public final java.time.LocalDate getDate() {
    return this.dateProperty().get();
  }

  public final void setDate(final java.time.LocalDate date) {
    this.dateProperty().set(date);
  }

  // *** Genre

  private final StringProperty genre = new SimpleStringProperty(this, "genre", "");

  public final StringProperty genreProperty() {
    return this.genre;
  }

  public final java.lang.String getGenre() {
    return this.genreProperty().get();
  }

  public final void setGenre(final java.lang.String genre) {
    this.genreProperty().set(genre);
  }

  // *** Track

  private final ObjectProperty<Integer> track = new SimpleObjectProperty<>(this, "track", null);

  public final ObjectProperty<Integer> trackProperty() {
    return this.track;
  }

  public final java.lang.Integer getTrack() {
    return this.trackProperty().get();
  }

  public final void setTrack(final java.lang.Integer track) {
    this.trackProperty().set(track);
  }

  // *** TrackTotal

  private final ObjectProperty<Integer> trackTotal = new SimpleObjectProperty<>(this, "trackTotal", null);

  public final ObjectProperty<Integer> trackTotalProperty() {
    return this.trackTotal;
  }

  public final java.lang.Integer getTrackTotal() {
    return this.trackTotalProperty().get();
  }

  public final void setTrackTotal(final java.lang.Integer trackTotal) {
    this.trackTotalProperty().set(trackTotal);
  }

  // *** Disc

  private final ObjectProperty<Integer> disc = new SimpleObjectProperty<>(this, "disc", null);

  public final ObjectProperty<Integer> discProperty() {
    return this.disc;
  }

  public final java.lang.Integer getDisc() {
    return this.discProperty().get();
  }

  public final void setDisc(final java.lang.Integer disc) {
    this.discProperty().set(disc);
  }

  // *** DiscTotal

  private final ObjectProperty<Integer> discTotal = new SimpleObjectProperty<>(this, "discTotal", null);

  public final ObjectProperty<Integer> discTotalProperty() {
    return this.discTotal;
  }

  public final java.lang.Integer getDiscTotal() {
    return this.discTotalProperty().get();
  }

  public final void setDiscTotal(final java.lang.Integer discTotal) {
    this.discTotalProperty().set(discTotal);
  }

  // *** Comment

  private final StringProperty comment = new SimpleStringProperty(this, "comment", "");

  public final StringProperty commentProperty() {
    return this.comment;
  }

  public final java.lang.String getComment() {
    return this.commentProperty().get();
  }

  public final void setComment(final java.lang.String comment) {
    this.commentProperty().set(comment);
  }

  // Language

  private final StringProperty language = new SimpleStringProperty(this, "language", "");

  public final StringProperty languageProperty() {
    return this.language;
  }

  public final java.lang.String getLanguage() {
    return this.languageProperty().get();
  }

  public final void setLanguage(final java.lang.String language) {
    this.languageProperty().set(language);
  }

  // *** Rating from 0 (unrated) to 10

  private final ObjectProperty<Integer> rating = new SimpleObjectProperty<>(this, "rating", null);

  public final ObjectProperty<Integer> ratingProperty() {
    return this.rating;
  }

  public final java.lang.Integer getRating() {
    return this.ratingProperty().get();
  }

  public final void setRating(final java.lang.Integer rating) {
    this.ratingProperty().set(rating);
    updateStatus();
  }

  // *** Lyrics

  private final StringProperty lyrics = new SimpleStringProperty(this, "lyrics", "");

  public final StringProperty lyricsProperty() {
    return this.lyrics;
  }

  public final String getLyrics() {
    return this.lyricsProperty().get();
  }

  public final void setLyrics(final java.lang.String lyrics) {
    this.lyricsProperty().set(lyrics);
    updateStatus();
  }

  // *** compilation

  private final BooleanProperty compilation = new SimpleBooleanProperty(this, "compilation", false);

  public final BooleanProperty compilationProperty() {
    return this.compilation;
  }

  public final boolean isCompilation() {
    return this.compilationProperty().get();
  }

  public final void setCompilation(final boolean compilation) {
    this.compilationProperty().set(compilation);
  }

  // *** incomplete

  private final BooleanProperty incomplete = new SimpleBooleanProperty(this, "incomplete", false);

  public final BooleanProperty incompleteProperty() {
    return this.incomplete;
  }

  public final boolean isIncomplete() {
    return this.incompleteProperty().get();
  }

  public final void setIncomplete(final boolean incomplete) {
    this.incompleteProperty().set(incomplete);
  }

  // *** SingleArtwork

  private final BooleanProperty singleArtwork = new SimpleBooleanProperty(this, "singleArtwork", false);

  public final BooleanProperty singleArtworkProperty() {
    return this.singleArtwork;
  }

  @SuppressWarnings("unused")
  public final boolean isSingleArtwork() {
    return this.singleArtworkProperty().get();
  }

  public final void setSingleArtwork(final boolean singleArtwork) {
    this.singleArtworkProperty().set(singleArtwork);
  }

  // *** Artwork

  private final ObjectProperty<ArtworkTag> artwork = new SimpleObjectProperty<>(this, "artwork", null);

  public final ObjectProperty<ArtworkTag> artworkProperty() {
    return this.artwork;
  }

  public final ntag.model.ArtworkTag getArtwork() {
    return this.artworkProperty().get();
  }

  public final void setArtwork(final ntag.model.ArtworkTag artwork) {
    this.artworkProperty().set(artwork);
    updateStatus();
  }

  // ***
  //
  // Construction
  //
  // ***

  public TagFile() {
    super();
    this.infos = "";
    this.setArtist("");
    this.setAlbum("");
    this.setAlbumArtist("");
    this.setGenre("");
    this.setComposer("");
    this.setYear(0);
    this.setTrack(0);
    this.setTrackTotal(0);
    this.setDisc(0);
    this.setDiscTotal(0);
    this.setComment("");
    this.setLyrics("");
    this.setCompilation(false);
    this.setLanguage(null);
    this.setRating(-1);
    this.setDate(null);
    this.setArtwork(null);
  }

  // ***
  //
  // public API
  //
  // ***

  /**
   * Returns true, if artwork data is missing.
   *
   * @return true, if artwork data is missing.
   */
  public boolean isArtworkMissing() {
    return getArtwork() == null;
  }

  /**
   * Returns a short status string.<br>
   * <br>
   * ro - File is ReadOnly C - Metadata has changed<br>
   * M - Common metadata is incomplete<br>
   * A - Artwork is missing<br>
   * L - Lyrics is missing<br>
   * R - Rating is missing<br>
   */
  public void updateStatus() {
    NTagProperties settings = NTagProperties.instance();
    StringBuilder buffer = new StringBuilder(4);
    this.setIncomplete(getGenre() == null || getGenre().isEmpty() || getTitle() == null || getTitle().isEmpty() || getAlbum() == null || getAlbum().isEmpty() || getArtist() == null
        || getArtist().isEmpty() || getYear() == null || getYear() < 1800);
    if (isReadOnly()) {
      buffer.append("ro ");
    }
    if (isDirty()) {
      buffer.append('C');
    }
    if (settings.isWarnIfMetadataIsIncomplete() && isIncomplete()) {
      buffer.append('M');
    }
    if (settings.isWarnIfArtworkIsMissing() && isArtworkMissing()) {
      buffer.append('A');
    }
    if (settings.isWarnIfLyricsIsMissing() && getLyrics().isEmpty()) {
      buffer.append('L');
    }
    if (settings.isWarnIfRatingIsMissing() && getRating() == null || getRating() <= 0) {
      buffer.append('R');
    }
    setStatus(buffer.toString());
  }

  /**
   * Returns all native Tags
   *
   * @return List<TagField>
   */
  public List<TagField> getTags() {
    final AudioFile audioFile = getAudioFile();
    Iterator<TagField> iterator = null;
    if (audioFile instanceof MP3File) {
      MP3File mp3File = (MP3File) audioFile;
      if (mp3File.hasID3v2Tag()) {
        AbstractID3v2Tag tag = mp3File.getID3v2Tag();
        if (tag != null) {
          iterator = tag.getFields();
        }
      }
    }
    if (iterator == null) {
      Tag tag = audioFile.getTag();
      if (tag != null) {
        iterator = tag.getFields();
      }
    }
    List<TagField> result = new ArrayList<>();
    if (iterator != null) {
      while (iterator.hasNext()) {
        result.add(iterator.next());
      }
    }
    return result;
  }

  /**
   * Removes the given Tag from AudioFile and saves changes to disk.
   *
   * @param tagField Tag to remove
   * @throws NTagException on IO Problems
   */
  public void removeTag(final TagField tagField) throws NTagException {
    final AudioFile audioFile = getAudioFile();
    final Tag tag = audioFile.getTag();
    JAudiotaggerUtil.removeTagField(tag, tagField);
    try {
      audioFile.commit();
    } catch (CannotWriteException e) {
      LOGGER.log(Level.SEVERE, "cannot write to audiofile " + path, e);
      throw new NTagException("cannot write to audiofile", e);
    }
    if (LOGGER.isLoggable(Level.INFO)) {
      LOGGER.info(String.format("Deleted %s Tag from file '%s'", tagField.getId(), name));
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getDirectory() == null) ? 0 : getDirectory().hashCode());
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
    TagFile other = (TagFile) obj;
    if (getDirectory() == null) {
      if (other.getDirectory() != null)
        return false;
    } else if (!getDirectory().equals(other.getDirectory()))
      return false;
    if (getName() == null) {
      return other.getName() == null;
    } else return getName().equals(other.getName());
  }

  @Override
  public String toString() {
    return getName();
  }
}
