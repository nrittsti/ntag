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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import ntag.NTagException;
import ntag.model.Genre;
import toolbox.fx.FxUtil;

public final class JAudiotaggerHelper {

	static final Logger LOGGER = Logger.getLogger(JAudiotaggerHelper.class.getName());

	/**
	 * private constructor
	 */
	private JAudiotaggerHelper() {

	}

	public static AudioFile readAudioFile(final Path filePath) throws NTagException {

		if (Files.exists(filePath) == false) {
			String msg = String.format("The file '%s' does not exists.", filePath.toString());
			TagFileReader.LOGGER.severe(msg);
			throw new NTagException(msg);
		}
		// call jaudiotagger API
		try {
			return AudioFileIO.read(filePath.toFile());
		} catch (CannotReadException e) {
			TagFileReader.LOGGER.log(Level.SEVERE, "Can't read the audiofile " + filePath.toString(), e);
			throw new NTagException("Can't read the audiofile, please check filesystem read permission.", e);
		} catch (IOException e) {
			TagFileReader.LOGGER.log(Level.SEVERE, "An IO error occured while reading the audiofile " + filePath.toString(), e);
			throw new NTagException("An IO error occured while reading the audiofile.", e);
		} catch (TagException e) {
			TagFileReader.LOGGER.log(Level.SEVERE, "An API error occured while reading the audiofile " + filePath.toString(), e);
			throw new NTagException("An API error occured while reading the audiofile.", e);
		} catch (ReadOnlyFileException e) {
			TagFileReader.LOGGER.log(Level.SEVERE, "The audiofile is read only " + filePath.toString(), e);
			throw new NTagException("The audiofile is read only, please check the filesystem write permission.", e);
		} catch (InvalidAudioFrameException e) {
			TagFileReader.LOGGER.log(Level.SEVERE, "Invalid audio frames detected while reading the audiofile " + filePath.toString(), e);
			throw new NTagException("Invalid audio frames detected while reading the audiofile.", e);
		} catch (Exception e) {
			TagFileReader.LOGGER.log(Level.SEVERE, "An API error occured while reading the audiofile " + filePath.toString(), e);
			throw new NTagException("An API error occured while reading the audiofile.", e);
		}
	}

	/**
	 * Creates or returns the AbstractID3v2Tag from the given MP3File
	 *
	 * @param file
	 *            MP3File
	 * @return AbstractID3v2Tag
	 */
	public static AbstractID3v2Tag getID3v2Tag(final MP3File file, boolean ID3v24) {
		final AbstractID3v2Tag v2tag;
		if (ID3v24) {
			// use or create a ID3v24 Tag
			if (file.hasID3v2Tag()) {
				v2tag = file.getID3v2TagAsv24();
			} else {
				v2tag = new ID3v24Tag();
			}
		} else {
			// use or create a ID3v23 Tag
			if (file.hasID3v2Tag()) {
				if (file.getID3v2Tag() instanceof ID3v23Tag) {
					v2tag = file.getID3v2Tag();
				} else {
					v2tag = new ID3v23Tag(file.getID3v2Tag());
				}
			} else {
				v2tag = new ID3v23Tag();
			}
		}
		file.setTag(v2tag);
		return v2tag;
	}

	/**
	 * Creates or returns the AbstractID3v1Tag from the given MP3File
	 *
	 * @param file
	 *            MP3File
	 * @return ID3v1Tag
	 */
	public static ID3v1Tag getID3v1Tag(final MP3File file) {
		final ID3v1Tag v1tag;

		if (file.hasID3v1Tag()) {
			v1tag = file.getID3v1Tag();
		} else {
			v1tag = new ID3v1Tag();
			file.setTag(v1tag);
		}
		return v1tag;
	}

	/**
	 * Removes the AbstractID3v1Tag from the given MP3File
	 *
	 * @param file
	 *            MP3File
	 * @return ID3v1Tag
	 */
	public static void removeID3v1Tag(final MP3File file) {
		if (file.hasID3v1Tag()) {
			file.setID3v1Tag(null);
		}
	}

	public static String createGenre(TagField field) {
		if (field != null) {
			String genreString = ((TagTextField) field).getContent();
			// prüfen ob das genre als zahl gespeichert ist
			List<Integer> idList = parseGenreIDs(genreString);
			if (idList.isEmpty()) {
				// wenn keine Genre ID3v1 IDs gefunden worden
				// liegt wahrscheinlich das Genre als Text vor
				if (genreString != null && genreString.length() > 0) {
					return genreString;
				}
			} else {
				// GenreType's (enum) aus den gefundenen Genre ID3v1 IDs
				// erzeugen
				for (Integer genreInt : idList) {
					genreString = Genre.getGenreTypeByIntValue(genreInt).getFirstLabel();
					if (genreString != null && genreString.length() > 0) {
						return genreString;
					}
				}
			}
		}
		return "";
	}

	private static List<Integer> parseGenreIDs(String value) {
		List<Integer> idList = new ArrayList<>();
		if (value.matches("(\\([0-9]{1,3}\\))+")) {
			// check if the value is a refer to the numeric id3 v1 genre list
			// from a v2 genre frame
			StringTokenizer tokenizer = new StringTokenizer(value, ")");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				int id = parseInt(token.substring(1), -1);
				if (id != -1) {
					idList.add(id);
				}
			}
		} else {
			// check if the value is a simple int
			int id = parseInt(value, -1);
			if (id != -1) {
				idList.add(id);
			}
		}
		return idList;
	}

	public static int parseInt(String value, int defaultInt) {
		if (value == null) {
			return defaultInt;
		}
		value = value.trim();
		if (value.length() == 0 || "null".equals(value)) {
			return defaultInt;
		}
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return defaultInt;
	}

	public static int parseInt(String value) {
		return parseInt(value, -1);
	}

	public static long parseLong(String value, long defaultLong) {
		if (value == null) {
			return defaultLong;
		}
		value = value.trim();
		if (value.length() == 0 || "null".equals(value)) {
			return defaultLong;
		}
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
		}
		return defaultLong;
	}

	public static long parseLong(String value) {
		return parseLong(value, -1);
	}

	public static void removeTagField(Tag tag, TagField tagField) {
		if (tag.getFields(tagField.getId()).size() == 1) {
			tag.deleteField(tagField.getId());
		} else {
			Iterator<TagField> iterator = tag.getFields();
			while (iterator.hasNext()) {
				if (iterator.next() == tagField) {
					iterator.remove();
					break;
				}
			}
		}
	}

	public static void replaceTagField(Tag tag, TagField tagField) {
		removeTagField(tag, tagField);
		try {
			tag.addField(tagField);
		} catch (FieldDataInvalidException e) {
			FxUtil.showException("Can't add TagField", e);
		}
	}
}
