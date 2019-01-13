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
package ntag.io;

import ntag.NTagException;
import ntag.io.util.RatingConverter;
import ntag.model.ArtworkTag;
import ntag.model.AudioFormat;
import ntag.model.TagFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.id3.*;
import org.jaudiotagger.tag.id3.framebody.AbstractFrameBodyTextInfo;
import org.jaudiotagger.tag.id3.framebody.FrameBodyPOPM;
import org.jaudiotagger.tag.images.Artwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagFileReader {

	public static final Logger LOGGER = Logger.getLogger(TagFileReader.class.getName());

	// ***
	//
	// Instance Attributes
	//
	// ***

	private final List<DateTimeFormatter> TDRL_FRAME_FORMATTERS = new ArrayList<>();

	// ***
	//
	// Properties
	//
	// ***

	// *** Infos

	private StringBuilder infos = null;

	public StringBuilder getInfos() {
		return infos;
	}

	// *** TDRL: Release-Datum (ID3v24)

	private boolean useTDRL = false;

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

	// ***
	//
	// Construction
	//
	// ***

	public TagFileReader() {
		super();
		NTagProperties appProps = new NTagProperties();

		setRatingEMail(appProps.getRatingEMail());
		setUseTDOR(appProps.isID3ReleaseDateTDOR());
		setUseTDRL(appProps.isID3ReleaseDateTDRL());
		setUseTDRC(appProps.isID3ReleaseDateTDRC());

		// TDRC FORMATTER
		TDRL_FRAME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.UK));
		TDRL_FRAME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.UK));
		TDRL_FRAME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH", Locale.UK));
		TDRL_FRAME_FORMATTERS.add(TagFileConst.ISO_DATE_FORMAT);
	}

	// ***
	//
	// public API
	//
	// ***

	/**
	 * Erstellt ein vollständig gefülltest <code>TagFile</code> Objekt aus dem
	 * Metadaten einer Audio-Datei. Unterstützt werden die Dateiformate MP3,
	 * MP4, AAC, FLAC, WMA oder OGG. Die volle Bandbreite an Metadaten steht
	 * jedoch nur für das MP3 Format zur Verfügung.
	 *
	 * @param filePath
	 *            the absolut path of the media file
	 * @return TagFile
	 * @throws NTagException
	 * @throws IOException
	 */
	public TagFile createTagFile(Path filePath) throws NTagException, IOException {
		infos = new StringBuilder(1000);
		infos.append("Reading: '").append(filePath.getFileName()).append("' from ").append(filePath.getParent());
		// call jaudiotagger API
		final AudioFile audioFile = JAudiotaggerHelper.readAudioFile(filePath);
		final TagFile tagFile = new TagFile();
		tagFile.setPath(filePath);
		tagFile.setAudioFile(audioFile);

		updateTagFile(tagFile, false);

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(infos.toString());
		}
		return tagFile;
	}

	public void updateTagFile(final TagFile tagFile, boolean reload) throws IOException, NTagException {
		if (infos == null) {
			infos = new StringBuilder(1000);
		}
		final AudioFile audioFile;
		if (reload) {
			audioFile = JAudiotaggerHelper.readAudioFile(tagFile.getPath());
		} else {
			audioFile = tagFile.getAudioFile();
		}
		fillFileInformation(tagFile);
		if (audioFile instanceof MP3File) {
			MP3File mp3File = (MP3File) audioFile;
			// header
			fillHeaderInformation(tagFile, mp3File);
			// metadata
			if (mp3File.hasID3v2Tag()) {
				AbstractID3v2Tag tag = mp3File.getID3v2Tag();
				fillCommonMetaInformationen(tagFile, tag, false);
				fillAdvancedMetaInformationen(tagFile, tag);
			} else {
				fillCommonMetaInformationen(tagFile, audioFile.getTag(), true);
			}
			// create Info String
			tagFile.setInfos(createInfoString(mp3File).toString());
		} else {
			// header
			fillHeaderInformation(tagFile, audioFile);
			// metadata
			fillCommonMetaInformationen(tagFile, audioFile.getTag(), true);
			// create Info String
			tagFile.setInfos(createInfoString(audioFile).toString());
		}
	}

	// ***
	//
	// hidden implementation
	//
	// ***

	private void fillFileInformation(TagFile tagFile) throws IOException {
		tagFile.setName(tagFile.getPath().getFileName().toString());
		tagFile.setExtension(tagFile.getName().substring(tagFile.getName().lastIndexOf('.')));
		tagFile.setDirectory(tagFile.getPath().getParent().toString());
		BasicFileAttributes fileAttr = Files.readAttributes(tagFile.getPath(), BasicFileAttributes.class);
		tagFile.setSize(fileAttr.size());
		tagFile.setCreated(LocalDateTime.ofInstant(fileAttr.creationTime().toInstant(), ZoneId.systemDefault()));
		tagFile.setModified(LocalDateTime.ofInstant(fileAttr.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
		tagFile.setReadOnly(!Files.isWritable(tagFile.getPath()));
	}

	private void fillHeaderInformation(TagFile tagFile, MP3File audioFile) throws NTagException {
		MP3AudioHeader header = audioFile.getMP3AudioHeader();
		tagFile.setVbr(header.isVariableBitRate());
		tagFile.setLossless(header.isLossless());
		tagFile.setBitrate(header.getBitRateAsNumber());
		tagFile.setSamplingRate(header.getSampleRateAsNumber());
		tagFile.setChannels(trim(header.getChannels(), 30));
		tagFile.setAudioFormat(AudioFormat.MP3);
		tagFile.setCodec(trim(header.getFormat(), 30));
		tagFile.setEncoder(trim(header.getEncoder(), 30));
		tagFile.setPlaytime(header.getTrackLength());
	}

	private void fillHeaderInformation(TagFile tagFile, AudioFile audioFile) throws NTagException {
		AudioHeader header = audioFile.getAudioHeader();
		tagFile.setVbr(header.isVariableBitRate());
		tagFile.setLossless(header.isLossless());
		tagFile.setBitrate(header.getBitRateAsNumber());
		tagFile.setSamplingRate(header.getSampleRateAsNumber());
		tagFile.setChannels(trim(header.getChannels(), 30));
		tagFile.setAudioFormat(AudioFormat.getTypeByExtension(tagFile.getExtension()));
		tagFile.setCodec(trim(header.getFormat(), 30));
		tagFile.setEncoder("");
		tagFile.setPlaytime(header.getTrackLength());
	}

	private void fillCommonMetaInformationen(TagFile tagFile, Tag tag, boolean generic) {
		if (tag != null) {
			tagFile.setTaggingSystem(tag.getClass().getSimpleName());
			tagFile.setTitle(trim(tag.getFirst(FieldKey.TITLE), 150));
			tagFile.setArtist(trim(tag.getFirst(FieldKey.ARTIST), 150));
			tagFile.setAlbum(trim(tag.getFirst(FieldKey.ALBUM), 150));
			tagFile.setAlbumArtist(trim(tag.getFirst(FieldKey.ALBUM_ARTIST), 150));
			tagFile.setGenre(JAudiotaggerHelper.createGenre(tag.getFirstField(FieldKey.GENRE)));
			tagFile.setComposer(trim(tag.getFirst(FieldKey.COMPOSER), 150));
			tagFile.setTrack(JAudiotaggerHelper.parseInt(tag.getFirst(FieldKey.TRACK)));
			tagFile.setTrackTotal(JAudiotaggerHelper.parseInt(tag.getFirst(FieldKey.TRACK_TOTAL)));
			tagFile.setDisc(JAudiotaggerHelper.parseInt(tag.getFirst(FieldKey.DISC_NO)));
			tagFile.setDiscTotal(JAudiotaggerHelper.parseInt(tag.getFirst(FieldKey.DISC_TOTAL)));
			tagFile.setComment(trim(tag.getFirst(FieldKey.COMMENT), 150));
			tagFile.setLyrics(trim(tag.getFirst(FieldKey.LYRICS), 10000));
			tagFile.setCompilation(isBooleanStringTrue(tag.getFirst(FieldKey.IS_COMPILATION)));
			tagFile.setLanguage(tag.getFirst(FieldKey.LANGUAGE));
			createArtworkTag(tagFile, tag.getArtworkList());
			if (generic) {
				final String dateStr = tag.getFirst(FieldKey.YEAR);
				if (dateStr != null) {
					if (dateStr.length() > 4) {
						createDateFromISOString(tagFile, "DATE", dateStr);
					} else {
						createYear(tagFile, dateStr);
					}
				}
				createRating(tagFile, JAudiotaggerHelper.parseInt(tag.getFirst(FieldKey.RATING)));
			}
		} else {
			tagFile.setTaggingSystem("None");
		}
	}

	private void fillAdvancedMetaInformationen(TagFile tagFile, final AbstractID3v2Tag tag) {
		AbstractID3v2Frame frame = null;
		if (tag instanceof ID3v24Tag) {
			// TDRL: Release-Datum (ID3v24)
			// TDOR: Original release time (ID3v24)
			// TDRC: Recording time (ID3v24)
			// MediaMonkey erwartet das Datum im Feld TDOR
			// Die Frames TYER, TDAT,TIME,TRDA wurden in der Version ID3v24
			// durch den
			// TDRC
			// Frame ersetzt!

			if (useTDRL && tag.hasFrameAndBody("TDRL")) {
				frame = tag.getFirstField("TDRL");
			} else if (useTDOR && tag.hasFrameAndBody("TDOR")) {
				frame = tag.getFirstField("TDOR");
			} else if (useTDRC && tag.hasFrameAndBody("TDRC")) {
				frame = tag.getFirstField("TDRC");
			} else if (tag.hasFrameAndBody("TDRL")) {
				frame = tag.getFirstField("TDRL");
			} else if (tag.hasFrameAndBody("TDOR")) {
				frame = tag.getFirstField("TDOR");
			} else if (tag.hasFrameAndBody("TDRC")) {
				frame = tag.getFirstField("TDRC");
			}
			if (frame != null) {
				createDateFromISOString(tagFile,//
						frame.getIdentifier(), //
						"" + ((AbstractFrameBodyTextInfo) frame.getBody()).getObjectValue(DataTypes.OBJ_TEXT));
			}
		} else {
			if (tag.frameMap.containsKey("TYERTDAT")) {
				TyerTdatAggregatedFrame tyerTdat = (TyerTdatAggregatedFrame) tag.frameMap.get("TYERTDAT");
				String tyer = null;
				String tdat = null;
				for (AbstractID3v2Frame f : tyerTdat.getFrames()) {
					if ("TYER".equals(f.getIdentifier())) {
						tyer = "" + f.getBody().getObjectValue(DataTypes.OBJ_TEXT);
					} else if ("TDAT".equals(f.getIdentifier())) {
						tdat = "" + f.getBody().getObjectValue(DataTypes.OBJ_TEXT);
					}
				}
				// ID3V23 : TYER
				createYear(tagFile, tyer);
				// ID3V23 : TDAT
				// The 'Date' frame is a numeric string in the DDMM format
				// containing the
				// date for the recording.
				// This field is always four characters long.
				createID3v23ReleaseDate(tagFile, tdat);
			} else {
				frame = tag.getFirstField("TYER");
				if (frame != null) {
					createYear(tagFile, //
							"" + ((AbstractFrameBodyTextInfo) frame.getBody()).getObjectValue(DataTypes.OBJ_TEXT));
				}
				frame = tag.getFirstField("TDAT");
				if (frame != null) {
					createID3v23ReleaseDate(tagFile, //
							"" + ((AbstractFrameBodyTextInfo) frame.getBody()).getObjectValue(DataTypes.OBJ_TEXT));
				}
			}
		}
		// Rating: POPM
		// Email to user <text string> $00
		// Rating $xx
		// Counter $xx xx xx xx (xx ...)
		calculateRating(tagFile, tag);
	}

	private static boolean isBooleanStringTrue(String pvValue) {
		if ("Y".equalsIgnoreCase(pvValue) || "1".equals(pvValue) || "TRUE".equalsIgnoreCase(pvValue)
				|| "YES".equalsIgnoreCase(pvValue)) {
			return true;
		} else {
			return false;
		}
	}

	private static String trim(String value, int maxLength) {
		if (value == null) {
			return "";
		}
		value = value.trim();
		if ("null".equals(value)) {
			return "";
		}
		if (maxLength > 0 && value.length() > maxLength) {
			return value.substring(0, maxLength);
		} else {
			return value;
		}
	}

	private void createDateFromISOString(TagFile tagFile, String frame, String value) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("read " + frame + " from file: " + tagFile.getPath());
		}
		if (value != null && value.length() > 0) {
			try {
				for (DateTimeFormatter format : TDRL_FRAME_FORMATTERS) {
					try {
						tagFile.setDate(LocalDate.parse(value, format));
						tagFile.setYear(tagFile.getDate().getYear());
						if (LOGGER.isLoggable(Level.FINE)) {
							LOGGER.fine(tagFile.getPath() + "\nFound " + frame + " Frame with date: " + tagFile.getDate());
						}
						return;
					} catch (DateTimeParseException e) {
						// Do nothing;
					}
				}
			} catch (Exception e) {
				infos.append("\nCannot parse ").append(frame).append(" Frame with Value '").append(value).append("' : ").append(e.getMessage());
			}
		} else {
			infos.append("\nInvalid ").append(frame).append(" Frame with Value '").append(value).append("'");
		}
	}

	private void createID3v23ReleaseDate(TagFile tagFile, String tdat) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("read TDAT from file: " + tagFile.getPath());
		}
		if (tdat != null && tdat.length() == 4) {
			try {
				if (tagFile.getYear() > 0) {
					tdat = tdat + tagFile.getYear();
				} else {
					tdat = tdat + "1900";
				}
				DateTimeFormatter format = DateTimeFormatter.ofPattern("ddMMyyyy", Locale.UK);
				tagFile.setDate(LocalDate.parse(tdat, format));
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(tagFile.getPath() + "\nFound TDAT Frame with date: " + tagFile.getDate());
				}
			} catch (Exception e) {
				infos.append("\nCannot parse TDAT Frame with Value '").append(tdat).append("' : ").append(e.getMessage());
			}
		} else {
			infos.append("\nInvalid TDAT Frame with Value '").append(tdat).append("'");
		}
	}

	/**
	 * TYER
     * The 'Year' frame is a numeric string with a year of the recording.
     * This frames is always four characters long (until the year 10000).
	 */
	private void createYear(TagFile tagFile, String year) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine(tagFile.getPath() + "\nReading generic YEAR");
		}
		if (year == null || year.length() == 0) {
			infos.append("\nMissing Year");
			return;
		}
		if (year.length() == 4) {
			try {
				int intValue = Integer.parseInt(year);
				if (intValue < 999 || intValue > 2999) {
					infos.append("\nInvalid Year with Value '").append(year).append("'");
				} else {
					tagFile.setYear(intValue);
				}
			} catch (Exception e) {
				infos.append("\nInvalid Year with Value '").append(year).append("' : ").append(e.getMessage());
			}
		} else {
			infos.append("\nInvalid Year with Value '").append(year).append("'");
		}
	}

	private void createRating(final TagFile tagFile, final int rating) {
		if (rating > -1) {
			tagFile.setRating(RatingConverter.in(tagFile.getAudioFormat(), rating));
		} else {
			tagFile.setRating(-1);
		}
	}

	private void calculateRating(TagFile tagFile, final AbstractID3v2Tag tag) {
		final String email = getRatingEMail();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("read rating from file: " + tagFile.getPath());
		}
		List<TagField> list = null;
		try {
			list = tag.getFields(FieldKey.RATING);
		} catch (KeyNotFoundException e) {
			return;
		}
		if (list != null && !list.isEmpty()) {
			long ratingSum = 0;
			for (TagField tagField : list) {
				if (tagField instanceof AbstractID3v2Frame) {
					FrameBodyPOPM framePOPM = (FrameBodyPOPM) ((AbstractID3v2Frame) tagField).getBody();
					infos.append("\nFound Rating from '").append(framePOPM.getEmailToUser()).append("' with Rating Score '").append(framePOPM.getRating()).append("'");
					if (email.equalsIgnoreCase(framePOPM.getEmailToUser())) {
						createRating(tagFile, (int) framePOPM.getRating());
						return;
					} else {
						ratingSum += framePOPM.getRating();
					}
				}
			}
			createRating(tagFile, (int) ratingSum / list.size());
			infos.append("\nCalculated Rating Score is: ").append(tagFile.getRating());
		}
	}

	/**
	 * <b>MP3 ID3V2 Frame APIC:</B><br>
	 * Text encoding, MIME type, Picture type, Description and Picture data.<br>
	 * <b>All other audio formats:</B><br>
	 * MIME type, Picture data and default Picture type 0 'Other'
	 */
	private void createArtworkTag(final TagFile tagFile, final List<Artwork> tagArtList) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("read artwork from file: " + tagFile.getPath());
		}
		if (tagArtList != null && !tagArtList.isEmpty()) {
			Artwork frontCover = null;
			for (Artwork artwork : tagArtList) {
				if (artwork.getPictureType() == 3 && artwork.getBinaryData() != null && artwork.getBinaryData().length > 0) {
					frontCover = artwork;
					break;
				}
			}
			// Fallback
			for (Artwork artwork : tagArtList) {
				if (artwork.getBinaryData() != null && artwork.getBinaryData().length > 0) {
					frontCover = artwork;
					break;
				}
			}
			if (frontCover == null) {
				infos.append("\nFound invalid embedded artwork data!");
			} else {
				// Create ArtworkTag
				try {
					tagFile.setArtwork(new ArtworkTag(frontCover));
					tagFile.setSingleArtwork(tagArtList.size() == 1);
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, String.format("Error on processing artwork from file: " + tagFile.getPath()), e);
				}
			}
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("finished artwork from file: " + tagFile.getPath());
		}
	}

	private StringBuilder createInfoString(AudioFile audioFile) {
		Tag tag = audioFile.getTag();
		AudioHeader header = audioFile.getAudioHeader();
		if (tag == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s\n-----------------------------------\n", header.getClass().getSimpleName()));
		sb.append(String.format("\n%-14s%s", "Encoding", header.getEncodingType()));
		sb.append(String.format("\n%-14s%6s Hz", "Samplerate", header.getSampleRate().trim()));
		sb.append(String.format("\n%-14s%6s kbps", "Bitrate", header.getBitRate().trim()));
		if (header.isVariableBitRate()) {
			sb.append(" (VBR)");
		}
		if (header.isLossless()) {
			sb.append(" (Lossless)");
		}
		sb.append(String.format("\n%-14s%6s", "Channels", header.getChannels().trim()));
		sb.append(String.format("\n%-14s%6s seconds", "Lenghts", header.getTrackLength()));
		sb.append(String.format("\n\n%s\n-----------------------------------\n", tag.getClass().getSimpleName()));
		return sb;
	}

	private StringBuilder createInfoString(MP3File audioFile) {
		MP3AudioHeader header = audioFile.getMP3AudioHeader();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("%s\n-----------------------------------\n", header.getClass().getSimpleName()));
		sb.append(String.format("\n%-14s%s", "Encoding", header.getEncodingType()));
		sb.append(String.format("\n%-14s%6s Hz", "Samplerate", header.getSampleRate()));
		sb.append(String.format("\n%-14s%6s kbps", "Bitrate", header.getBitRate()));
		if (header.isVariableBitRate()) {
			sb.append(" (VBR)");
		}
		sb.append(String.format("\n%-14s%6s", "Channels", header.getChannels()));
		sb.append(String.format("\n%-14s%6s seconds", "Lenghts", header.getTrackLength()));
		sb.append(String.format("\n%-14s%s", "Encoder", header.getEncoder()));
		sb.append(String.format("\n%-14s%s %s", "MPEG Version", header.getMpegVersion(), header.getMpegLayer()));

		if (audioFile.getID3v1Tag() == null) {
			sb.append(String.format("\n\n%s\n-----------------------------------\n", ID3v1Tag.class.getSimpleName()));
			sb.append("not provided");
		} else {
			ID3v1Tag v1Tag = audioFile.getID3v1Tag();
			sb.append(String.format("\n\n%s\n-----------------------------------\n", audioFile.getID3v1Tag().getClass().getSimpleName()));
			sb.append(String.format("\n%-10s%s", "Title", v1Tag.getFirstTitle()));
			sb.append(String.format("\n%-10s%s", "Interpret", v1Tag.getFirstArtist()));
			sb.append(String.format("\n%-10s%s", "Album", v1Tag.getFirstAlbum()));
			sb.append(String.format("\n%-10s%s", "Genre", v1Tag.getFirstGenre()));
			sb.append(String.format("\n%-10s%s", "Year", v1Tag.getFirstYear()));
			sb.append(String.format("\n%-10s%s", "Comment", v1Tag.getFirstComment()));
			try {
				sb.append(String.format("\n%-10s%s", "Track", v1Tag.getFirstTrack()));
			} catch (Exception e) {
			}
		}
		if (audioFile.getID3v2Tag() == null) {
			sb.append(String.format("\n\n%s\n-----------------------------------\n", "ID3v2Tag"));
			sb.append("not provided");
		} else {
			sb.append(String.format("\n\n%s%d%s\n-----------------------------------\n", "ID3v", audioFile.getID3v2Tag().getMajorVersion(), "Tag"));
			sb.append("provided");
		}
		return sb;
	}
}
