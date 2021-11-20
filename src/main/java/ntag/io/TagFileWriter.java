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
 *   Copyright 2021, Nico Rittstieg
 *
 */
package ntag.io;

import ntag.NTagException;
import ntag.io.util.RatingConverter;
import ntag.model.ArtworkTag;
import ntag.model.AudioFormat;
import ntag.model.Genre;
import ntag.model.TagFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.id3.*;
import org.jaudiotagger.tag.id3.framebody.*;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TagFileWriter {

  public static final Logger LOGGER = Logger.getLogger(TagFileWriter.class.getName());

  // ***
  //
  // Instance Attributes
  //
  // ***

  private String fullPath;

  private StringBuilder infos = null;

  // ***
  //
  // Properties
  //
  // ***

  // *** Tag Infos

  public StringBuilder getInfos() {
    return infos;
  }

  // *** Change Count

  private int changeCount;

  public int getChangeCount() {
    return changeCount;
  }

  // *** ID3v1.1

  private boolean ID3v11;

  public boolean isID3v11() {
    return ID3v11;
  }

  public void setID3v11(boolean iD3v11) {
    ID3v11 = iD3v11;
  }

  // *** ID3v2.4

  private boolean ID3v24;

  public boolean isID3v24() {
    return ID3v24;
  }

  public void setID3v24(boolean iD3v24) {
    ID3v24 = iD3v24;
  }

  // *** Unwanted ID3 Frames

  private List<String> unwantedID3Frames;

  public List<String> getUnwantedID3Frames() {
    return unwantedID3Frames;
  }

  public void setUnwantedID3Frames(List<String> unwantedID3Frames) {
    this.unwantedID3Frames = unwantedID3Frames;
  }

  // *** TDRL: Release-Datum (ID3v24)

  private boolean useTDRL = false;

  @SuppressWarnings("unused")
  public boolean isUseTDRL() {
    return useTDRL;
  }

  public void setUseTDRL(boolean useTDRL) {
    this.useTDRL = useTDRL;
  }

  // *** TDOR: Original release time (ID3v24)

  private boolean useTDOR = false;

  public boolean isUseTDOR() {
    return useTDOR;
  }

  public void setUseTDOR(boolean useTDOR) {
    this.useTDOR = useTDOR;
  }

  // *** TDRC: Recording time (ID3v24)

  private boolean useTDRC = false;

  @SuppressWarnings("unused")
  public boolean isUseTDRC() {
    return useTDRC;
  }

  public void setUseTDRC(boolean useTDRC) {
    this.useTDRC = useTDRC;
  }

  // *** ID3 Rating Mail

  private String ratingEMail;

  public String getRatingEMail() {
    return ratingEMail;
  }

  public void setRatingEMail(String value) {
    this.ratingEMail = value;
  }

  // *** ID3 Ratin Single Frame

  private boolean ratingEnforceSingleFrame;

  public boolean isRatingEnforceSingleFrame() {
    return ratingEnforceSingleFrame;
  }

  public void setRatingEnforceSingleFrame(boolean value) {
    this.ratingEnforceSingleFrame = value;
  }

  // *** ID3 Artwork Single Frame

  private boolean artworkEnforceSingleFrame;

  public boolean isArtworkEnforceSingleFrame() {
    return artworkEnforceSingleFrame;
  }

  public void setArtworkEnforceSingleFrame(boolean value) {
    this.artworkEnforceSingleFrame = value;
  }

  // ***
  //
  // Construction
  //
  // ***

  public TagFileWriter() {
    super();
    NTagProperties props = NTagProperties.instance();

    setID3v11(props.isID3v11());
    setID3v24(props.isID3v24());
    setRatingEMail(props.getRatingEMail());
    setUseTDOR(props.isID3ReleaseDateTDOR());
    setUseTDRL(props.isID3ReleaseDateTDRL());
    setUseTDRC(props.isID3ReleaseDateTDRC());
    setUnwantedID3Frames(props.getID3FrameBlackList());
    setRatingEnforceSingleFrame(props.isRatingEnforceSingleFrame());
    setArtworkEnforceSingleFrame(props.isArtworkEnforceSingle());
  }

  // ***
  //
  // Public API
  //
  // ***

  public void update(final TagFile tagFile) throws NTagException {
    this.infos = new StringBuilder(1000);
    this.changeCount = 0;
    Path path = tagFile.getPath();
    this.fullPath = tagFile.getPath().toString();

    infos.append("Writing: '").append(path.getFileName()).append("' from Directory: ").append(path.getParent());

    AudioFile audioFile = tagFile.getAudioFile();

    // UPDATE METADATA FRAMES
    try {
      if (audioFile instanceof MP3File) {
        updateWithMP3Tag((MP3File) audioFile, tagFile);
      } else {
        updateWithGenericTag(audioFile, tagFile);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Cannot change tags in file " + fullPath, e);
      throw new NTagException("Cannot change tags", e);
    }
    // CHECK CHANGE COUNT
    if (changeCount > 0) {
      // UPDATE TO FILE
      try {
        audioFile.commit();
      } catch (CannotWriteException e) {
        LOGGER.log(Level.SEVERE, "cannot write to audiofile " + fullPath, e);
        throw new NTagException("cannot write to audiofile", e);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "An API error occured while updating the audiofile " + fullPath, e);
        throw new NTagException("An API error occured while updating the audiofile.", e);
      }
    } else {
      infos.append("\nNo Changes Found!");
    }
    tagFile.setDirty(false);
    if (LOGGER.isLoggable(Level.INFO)) {
      LOGGER.info(infos.toString());
    }
  }

  // ################################################################
  // # Generic Audio File Handling
  // ################################################################

  private void updateWithGenericTag(final AudioFile file, final TagFile tagFile) {
    Tag tag = file.getTagOrCreateAndSetDefault();

    // TITLE
    updateTextField(tag, FieldKey.TITLE, tagFile.getTitle());
    // ARTIST
    updateTextField(tag, FieldKey.ARTIST, tagFile.getArtist());
    // ALBUM
    updateTextField(tag, FieldKey.ALBUM, tagFile.getAlbum());
    // ALBUM ARTIST
    updateTextField(tag, FieldKey.ALBUM_ARTIST, tagFile.getAlbumArtist());
    // YEAR / DATE
    if (tagFile.getDate() != null) {
      // the date is be saved in ISO 8601 format
      updateTextField(tag, FieldKey.YEAR, tagFile.getDate().format(TagFileConst.ISO_DATE_FORMAT));
    } else {
      updateTextField(tag, FieldKey.YEAR, (tagFile.getYear() != null && tagFile.getYear() > 0) ? "" + tagFile.getYear() : "");
    }
    // COMMENT
    updateTextField(tag, FieldKey.COMMENT, tagFile.getComment());
    // COMPOSER
    updateTextField(tag, FieldKey.COMPOSER, tagFile.getComposer());
    // TRACK
    updateTextField(tag, FieldKey.TRACK, trackToString(tagFile.getTrack()));

    // TRACK TOTAL
    updateTextField(tag, FieldKey.TRACK_TOTAL, trackToString(tagFile.getTrackTotal()));

    // DISC
    updateTextField(tag, FieldKey.DISC_NO, trackToString(tagFile.getDisc()));

    // DISC TOTAL
    updateTextField(tag, FieldKey.DISC_TOTAL, trackToString(tagFile.getDiscTotal()));

    // LYRICS
    updateTextField(tag, FieldKey.LYRICS, tagFile.getLyrics());

    // COMPILATION
    updateTextField(tag, FieldKey.IS_COMPILATION, tagFile.isCompilation() ? "1" : "0");

    // LANGUAGE
    updateTextField(tag, FieldKey.LANGUAGE, tagFile.getLanguage());

    // GENRE
    updateTextField(tag, FieldKey.GENRE, tagFile.getGenre());

    // RATING
    if (tagFile.getRating() > -1) {
      updateTextField(tag, FieldKey.RATING, "" + RatingConverter.halfStarsToInternal(tagFile.getAudioFormat(), tagFile.getRating()));
    }

    // ARTWORK
    updateArtworkWithGenericTag(tag, tagFile.getArtwork());
  }

  private void updateTextField(final Tag tag, final FieldKey key, final String value) {
    String oldValue = null;
    try {
      oldValue = tag.getFirst(key);
      if (oldValue != null) {
        oldValue = oldValue.trim();
        // ACHTUNG BUG
        // die API benutzt bei manchen Feldern in Tag.getFirst(key) die
        // Methode String.valueOf(Object)
        // ist ein Object = null hat der String den Rückgabewert "null"
        if ("null".equals(oldValue) || oldValue.length() == 0) {
          oldValue = null;
        }
      }

    } catch (Exception e) {
      // ignore
    }
    try {
      if (value == null || value.length() == 0) {
        if (oldValue != null) {
          tag.deleteField(key);
          addChange(key.toString(), value, oldValue);
        }
      } else {
        if (oldValue == null || !oldValue.equals(value)) {
          tag.setField(key, value);
          addChange(key.toString(), value, oldValue);
        }
      }
    } catch (Exception e) {
      addError(key.toString(), value, e.getMessage());
    }
  }

  private static String trackToString(final Integer track) {
    if (track == null) {
      return "";
    } else {
      return track > 0 ? track.toString() : "";
    }
  }

  // ################################################################
  // # MP3 ID3v2 Handling
  // ################################################################

  private void updateWithMP3Tag(final MP3File file, final TagFile tagFile) {

    final String genre = tagFile.getGenre() != null ? tagFile.getGenre() : "";

    // Create ID3v1 Tag
    if (!isID3v11()) {
      JAudiotaggerUtil.removeID3v1Tag(file);
    } else {
      final ID3v1Tag v1tag = JAudiotaggerUtil.getID3v1Tag(file);
      v1tag.setTitle(tagFile.getTitle());
      v1tag.setArtist(tagFile.getArtist());
      v1tag.setAlbum(tagFile.getAlbum());
      v1tag.setYear((tagFile.getYear() != null && tagFile.getYear() > 0) ? tagFile.getYear().toString() : "");
      v1tag.setComment(tagFile.getComment());
      if (tagFile.getTrack() == null || tagFile.getTrack() <= 0) {
        try {
          v1tag.setField(FieldKey.TRACK, "");
        } catch (Exception ignored) {
        }
      } else {
        try {
          v1tag.setField(FieldKey.TRACK, trackToString(tagFile.getTrack()));
        } catch (Exception ignored) {
        }
      }
      // GENRE : ID3v1
      if (genre.length() == 0) {
        v1tag.setGenre("255");
      } else {
        v1tag.setGenre(genre);
      }
    }

    // Create ID3v2? Tag
    final AbstractID3v2Tag v2tag = JAudiotaggerUtil.getID3v2Tag(file, isID3v24());

    // TITLE
    updateTextField(v2tag, FieldKey.TITLE, tagFile.getTitle());
    // ARTIST
    updateTextField(v2tag, FieldKey.ARTIST, tagFile.getArtist());
    // ALBUM
    updateTextField(v2tag, FieldKey.ALBUM, tagFile.getAlbum());
    // ALBUM ARTIST
    updateTextField(v2tag, FieldKey.ALBUM_ARTIST, tagFile.getAlbumArtist());
    // YEAR : TYER
    if (tagFile.getDate() == null) {
      updateTextField(v2tag, FieldKey.YEAR, ((tagFile.getYear() != null && tagFile.getYear() > 0) ? tagFile.getYear().toString() : ""));
    }
    // COMMENT : COMM
    updateTextField(v2tag, FieldKey.COMMENT, tagFile.getComment());
    // COMPOSER
    updateTextField(v2tag, FieldKey.COMPOSER, tagFile.getComposer());
    // TRACK
    if (tagFile.getTrack() == null && tagFile.getTrackTotal() == null) {
      v2tag.removeFrame("TRCK");
      addRemove("TRCK");
    } else {
      // TRACK
      updateTextField(v2tag, FieldKey.TRACK, trackToString(tagFile.getTrack()));
      // TRACK TOTAL
      updateTextField(v2tag, FieldKey.TRACK_TOTAL, trackToString(tagFile.getTrackTotal()));
    }
    // DISC
    if (tagFile.getDisc() == null && tagFile.getDiscTotal() == null) {
      addRemove("TPOS");
    } else {
      // DISC
      updateTextField(v2tag, FieldKey.DISC_NO, trackToString(tagFile.getDisc()));
      // DISC TOTAL
      updateTextField(v2tag, FieldKey.DISC_TOTAL, trackToString(tagFile.getDiscTotal()));
    }

    // LYRICS : USLT
    updateTextField(v2tag, FieldKey.LYRICS, tagFile.getLyrics());

    // COMPILATION
    updateTextField(v2tag, FieldKey.IS_COMPILATION, tagFile.isCompilation() ? "1" : "0");

    // LANGUAGE : TLAN
    updateTextField(v2tag, FieldKey.LANGUAGE, tagFile.getLanguage());

    // GENRE : TCON
    updateTCON(v2tag, genre);

    // DATE OF RELEASE
    if (v2tag instanceof ID3v23Tag) {
      // TDAT
      updateID3v23ReleaseDate(tagFile, (ID3v23Tag) v2tag);
    } else {
      if (useTDOR) {
        updateID3v24ReleaseDate("TDOR", tagFile, (ID3v24Tag) v2tag);
      }
      if (useTDRC) {
        updateID3v24ReleaseDate("TDRC", tagFile, (ID3v24Tag) v2tag);
      }
      if (useTDRL) {
        updateID3v24ReleaseDate("TDRL", tagFile, (ID3v24Tag) v2tag);
      }
    }
    // RATING : POPM
    if (tagFile.getRating() > -1) {
      updateID3v2Rating(tagFile.getAudioFormat(), tagFile.getRating(), v2tag);
    }

    // ARTWORK
    updateArtworkWithMP3Tag(v2tag, tagFile.getArtwork());

    // Delete unwanted frames
    for (String frameName : getUnwantedID3Frames()) {
      if (v2tag.getFrame(frameName) != null) {
        v2tag.removeFrame(frameName);
        addRemove(frameName);
      }
    }

    // Update Tagging System
    tagFile.setTaggingSystem(v2tag.getClass().getSimpleName());
  }

  private void updateID3v24ReleaseDate(final String frameName, final TagFile media, final ID3v24Tag tag) {
    ID3v24Frame frame = (ID3v24Frame) tag.getFirstField(frameName);
    if (media.getDate() != null) {
      final String text = media.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK));
      if (frame != null) {
        AbstractFrameBodyTextInfo body = ((AbstractFrameBodyTextInfo) frame.getBody());
        if (!text.equals(body.getObjectValue(DataTypes.OBJ_TEXT))) {
          addChange(frameName, text, "" + body.getObjectValue(DataTypes.OBJ_TEXT));
          body.setObjectValue(DataTypes.OBJ_TEXT, text);
        }
      } else {
        frame = new ID3v24Frame(frameName);
        AbstractFrameBodyTextInfo body = (AbstractFrameBodyTextInfo) frame.getBody();
        body.setObjectValue(DataTypes.OBJ_TEXT, text);
        try {
          tag.addField(frame);
          addChange(frameName, text, null);
        } catch (FieldDataInvalidException e) {
          addError(frameName, text, e.getMessage());
        }
      }
    } else {
      if (frame != null) {
        tag.removeFrame(frameName);
        addRemove(frameName);
      }
    }
  }

  private void updateID3v23ReleaseDate(final TagFile tagFile, final ID3v23Tag tag) {
    if (tagFile.getDate() != null) {
      final String text = tagFile.getDate().format(DateTimeFormatter.ofPattern("ddMM", Locale.UK));
      ID3v23Frame frame = null;

      if (tag.hasFrame("TYERTDAT")) {
        TyerTdatAggregatedFrame tyerTdat = (TyerTdatAggregatedFrame) tag.getFrame("TYERTDAT").get(0);
        for (AbstractID3v2Frame f : tyerTdat.getFrames()) {
          if ("TDAT".equals(f.getIdentifier())) {
            frame = (ID3v23Frame) f;
          }
        }
      } else {
        frame = (ID3v23Frame) tag.getFirstField("TDAT");
      }
      if (frame != null) {
        FrameBodyTDAT tdat = ((FrameBodyTDAT) frame.getBody());
        if (!text.equals(tdat.getObjectValue(DataTypes.OBJ_TEXT))) {
          addChange("TDAT", text, "" + tdat.getObjectValue(DataTypes.OBJ_TEXT));
          tdat.setObjectValue(DataTypes.OBJ_TEXT, text);
        }
      } else {
        frame = new ID3v23Frame("TDAT");
        FrameBodyTDAT tdat = (FrameBodyTDAT) frame.getBody();
        tdat.setObjectValue(DataTypes.OBJ_TEXT, text);
        try {
          tag.addField(frame);
          addChange("TDAT", text, null);
        } catch (FieldDataInvalidException e) {
          addError("TDAT", text, e.getMessage());
        }
      }
    } else {
      if (tag.hasFrame("TYERTDAT")) {
        tag.removeFrame("TYERTDAT");
        addRemove("TDAT");
        if (tagFile.getYear() != null && tagFile.getYear() > 0) {
          try {
            tag.setField(FieldKey.YEAR, tagFile.getYear().toString());
          } catch (Exception e) {
            addError("TYER", tagFile.getYear().toString(), e.getMessage());
          }
        }
      } else if (tag.hasFrame("TDAT")) {
        tag.removeFrame("TDAT");
        addRemove("TDAT");
      }
    }
  }

  private void updateID3v2Rating(final AudioFormat audioFormat, int rating, final AbstractID3v2Tag tag) {
    final String email = getRatingEMail();
    int convertedRating = RatingConverter.halfStarsToInternal(audioFormat, rating);

    List<TagField> list;
    try {
      list = tag.getFields(FieldKey.RATING);
      // POPM Frames entfernen
      if (isRatingEnforceSingleFrame()) {
        tag.deleteField(FieldKey.RATING);
      }
    } catch (KeyNotFoundException e) {
      return;
    }
    AbstractID3v2Frame framePOPM = null;
    boolean addFrameFlag = false;
    if (list != null && !list.isEmpty()) {
      // Zuerst schauen ob es schon einen rating frame gibt
      for (TagField tagField : list) {
        if (tagField instanceof AbstractID3v2Frame) {
          AbstractID3v2Frame tmpFrame = (AbstractID3v2Frame) tagField;
          FrameBodyPOPM frameBodyPOPM = (FrameBodyPOPM) tmpFrame.getBody();
          if (email.equalsIgnoreCase(frameBodyPOPM.getEmailToUser())) {
            framePOPM = tmpFrame;
            if (frameBodyPOPM.getRating() != convertedRating) {
              frameBodyPOPM.setRating(convertedRating);
              addChange("POPM", "" + convertedRating, "" + frameBodyPOPM.getRating());
              if (isRatingEnforceSingleFrame()) {
                addFrameFlag = true;
              }
            }
          }
        }
      }
    }
    if (framePOPM == null) {
      // neuen Rating Frame anlegen
      framePOPM = tag.createFrame("POPM");
      FrameBodyPOPM frameBodyPOPM = (FrameBodyPOPM) framePOPM.getBody();
      frameBodyPOPM.setRating(convertedRating);
      frameBodyPOPM.setEmailToUser(email);
      addFrameFlag = true;
      addChange("RATING", "" + convertedRating, null);
    }
    if (addFrameFlag) {
      try {
        tag.addField(framePOPM);
      } catch (FieldDataInvalidException e) {
        addError("RATING", "" + convertedRating, e.getMessage());
      }
    }
  }

  private void updateTCON(final AbstractID3v2Tag tag, final String genre) {
    List<TagField> list;
    try {
      list = tag.getFields("TCON");
    } catch (KeyNotFoundException e) {
      return;
    }
    if (list != null && !list.isEmpty()) {
      if (genre == null || genre.length() == 0) {
        tag.removeFrame("TCON");
        addRemove("TCON");
        return;
      }
      if (list.size() > 1) {
        tag.removeFrame("TCON");
      } else {
        FrameBodyTCON frameBodyTCON = (FrameBodyTCON) ((AbstractID3v2Frame) list.get(0)).getBody();
        if (frameBodyTCON.getValues().size() > 1) {
          tag.removeFrame("TCON");
        } else {
          String textValue = frameBodyTCON.getFirstTextValue();
          if (textValue == null || textValue.length() == 0) {
            tag.removeFrame("TCON");
          } else {
            textValue = textValue.trim();
            if (textValue.startsWith("(") && textValue.endsWith(")")) {
              int intGenre = Genre.UNKNOWN.getIntValue();
              try {
                intGenre = Integer.parseInt(textValue.substring(1, textValue.length() - 1));
              } catch (Exception ignored) {
              }
              textValue = Genre.getGenreTypeByIntValue(intGenre).getFirstLabel();
            }
            if (genre.equals(textValue)) {
              return;
            } else {
              String oldGenre = frameBodyTCON.getFirstTextValue();
              frameBodyTCON.setObjectValue(DataTypes.OBJ_TEXT, genre);
              addChange("TCON", genre, oldGenre);
              return;
            }
          }
        }
      }
    }
    AbstractID3v2Frame frame = tag.createFrame("TCON");
    FrameBodyTCON body = (FrameBodyTCON) frame.getBody();
    body.setObjectValue(DataTypes.OBJ_TEXT, genre);
    try {
      tag.addField(frame);
      addChange("TCON", genre, null);
    } catch (FieldDataInvalidException e) {
      addError("TCON", genre, e.getMessage());
    }
  }

  // #########################################################################
  // # MP3 ARTWORK HANDLING
  // #########################################################################

  private void updateArtworkWithMP3Tag(final AbstractID3v2Tag v2tag, final ArtworkTag artworkTag) {
    List<TagField> coverartList;
    try {
      coverartList = v2tag.getFields(FieldKey.COVER_ART);
    } catch (KeyNotFoundException e) {
      coverartList = new ArrayList<>();
    }

    // VORHANDENEN APIC FRAME LÖSCHEN
    if (artworkTag == null) {
      if (coverartList.size() > 0) {
        v2tag.deleteArtworkField();
        addChange("APIC", null, "yes");
      }
      return;
    }

    // APIC FRAME AUF EIN BILD BESCHRÄNKEN
    if (isArtworkEnforceSingleFrame() && coverartList.size() > 1) {
      v2tag.deleteArtworkField();
      if (createFrameBodyAPIC(v2tag, artworkTag)) {
        addChange("APIC", "yes", "yes");
      }
      return;
    }

    if (coverartList.isEmpty()) {
      // NEUEN APIC FRAME ERZEUGEN
      if (createFrameBodyAPIC(v2tag, artworkTag)) {
        addChange("APIC", "yes", null);
      }
    } else {
      // VORHANDENEN APIC FRAME UPDATEN
      // 1. PictureType : FRONT COVER = 03 suchen
      for (TagField next : coverartList) {
        FrameBodyAPIC apicBody = (FrameBodyAPIC) ((AbstractID3v2Frame) next).getBody();
        if (apicBody.getPictureType() == 3) {
          updateFrameBodyAPIC(artworkTag, apicBody);
          return;
        }
      }
      // 2. PictureType : SONSTIGES = 00 suchen
      for (TagField next : coverartList) {
        FrameBodyAPIC apicBody = (FrameBodyAPIC) ((AbstractID3v2Frame) next).getBody();
        if (apicBody.getPictureType() == 0) {
          updateFrameBodyAPIC(artworkTag, apicBody);
          return;
        }
      }
      // 3. Einfach den Ersten updaten
      updateFrameBodyAPIC(artworkTag, (FrameBodyAPIC) ((AbstractID3v2Frame) coverartList.get(0)).getBody());
    }
  }

  private void updateFrameBodyAPIC(final ArtworkTag artworkTag, final FrameBodyAPIC apicBody) {
    final byte[] hash = HashUtil.createFromByteArray("MD5", apicBody.getImageData());
    if (!Arrays.equals(hash, artworkTag.getImageHash())) {
      apicBody.setMimeType(artworkTag.getImageType().getMimeTypes()[0]);
      apicBody.setImageData(artworkTag.getImageData());
      addChange("APIC", "yes", "yes");
    }
  }

  private boolean createFrameBodyAPIC(final AbstractID3v2Tag v2tag, final ArtworkTag artworkTag) {
    TagField artworkField;
    if (v2tag instanceof ID3v24Tag) {
      artworkField = ((ID3v24Tag) v2tag).createArtworkField(artworkTag.getImageData(), artworkTag.getImageType().getMimeTypes()[0]);
    } else if (v2tag instanceof ID3v23Tag) {
      artworkField = ((ID3v23Tag) v2tag).createArtworkField(artworkTag.getImageData(), artworkTag.getImageType().getMimeTypes()[0]);
    } else {
      artworkField = ((ID3v22Tag) v2tag).createArtworkField(artworkTag.getImageData(), artworkTag.getImageType().getMimeTypes()[0]);
    }
    try {
      v2tag.addField(artworkField);
      return true;
    } catch (Exception e) {
      addError("APIC", null, e.getMessage());
      return false;
    }
  }

  // #########################################################################
  // # GENERIC ARTWORK HANDLING (OGG, AAC, WMA ...)
  // #########################################################################

  private void updateArtworkWithGenericTag(final Tag tag, final ArtworkTag artworkTag) {

    List<Artwork> coverartList = tag.getArtworkList();

    // VORHANDENE LÖSCHEN
    if (artworkTag == null) {
      if (coverartList.size() > 0) {
        tag.deleteArtworkField();
        addChange("Artwork", null, "yes");
      }
      return;
    }

    // ARTWORK AUF EIN BILD BESCHRÄNKEN
    if (isArtworkEnforceSingleFrame() && coverartList.size() > 1) {
      tag.deleteArtworkField();
      if (createGenericArtwork(tag, artworkTag, true)) {
        addChange("Artwork", "yes", "yes");
      }
      return;
    }

    if (coverartList.isEmpty()) {
      // NEUEN APIC FRAME ERZEUGEN
      if (createGenericArtwork(tag, artworkTag, true)) {
        addChange("Artwork", "yes", null);
      }
    } else {
      Artwork artwork = coverartList.get(0);
      final byte[] hash = HashUtil.createFromByteArray("MD5", artwork.getBinaryData());
      if (!Arrays.equals(hash, artworkTag.getImageHash())) {
        createGenericArtwork(tag, artworkTag, false);
        addChange("Artwork", "yes", "yes");
      }
    }
  }

  private boolean createGenericArtwork(final Tag tag, final ArtworkTag artworkTag, boolean create) {
    Artwork artwork = ArtworkFactory.getNew();
    artwork.setBinaryData(artworkTag.getImageData());
    artwork.setMimeType(artworkTag.getImageType().getMimeTypes()[0]);
    artwork.setPictureType(3);
    try {
      if (create) {
        tag.setField(artwork);
      } else {
        tag.createField(artwork);
      }
      return true;
    } catch (FieldDataInvalidException e) {
      String msg = "Can't add Artwork Frame to file '" + fullPath + "'";
      LOGGER.log(Level.SEVERE, msg, e);
      addError("Artwork", null, e.getMessage());
      return false;
    }
  }

  // #########################################################################
  // # Logging
  // #########################################################################

  private void addChange(String frame, String value, String oldValue) {
    infos.append("\nChanging Frame '").append(frame).append("' from '").append(oldValue).append("' to '").append(value).append("'");
    changeCount++;
  }

  private void addRemove(String frame) {
    infos.append("\nRemoving Frame '").append(frame).append("'");
    changeCount++;
  }

  private void addError(String frame, String value, String msg) {
    infos.append("\nError on Frame '").append(frame).append(" with value '").append(value).append("' : ").append(msg);
  }
}
