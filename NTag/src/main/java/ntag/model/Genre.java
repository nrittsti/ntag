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
package ntag.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Enum GenreType<br>
 * The official listing of ID3 genre is published on
 * <url>http://www.id3.org/id3v2.3.0</url>
 */
public enum Genre {

	UNKNOWN(-1, "Unknown"),
	BLUES(0, "Blues"),
	CLASSIC_ROCK(1, "Classic Rock"),
	COUNTRY(2, "Country"),
	DANCE(3, "Dance", "Dance & DJ"),
	DISCO(4, "Disco"),
	FUNK(5, "Funk"),
	GRUNGE(6, "Grunge"),
	HIP_HOP(7, "Hip-Hop", "Rap & Hip-Hop"),
	JAZZ(8, "Jazz"),
	METAL(9, "Metal", "Hard Rock & Metal"),
	NEW_AGE(10, "New Age"),
	OLDIES(11, "Oldies"),
	OTHER(12, "Other"),
	POP(13, "Pop", "Rock/Pop"),
	R_AND_B(14, "R&B"),
	RAP(15, "Rap", "Rap & Hip-Hop"),
	REGGAE(16, "Reggae"),
	ROCK(17, "Rock", "Rock/Pop"),
	TECHNO(18, "Techno"),
	INDUSTRIAL(19, "Industrial"),
	ALTERNATIVE(20, "Alternative"),
	SKA(21, "Ska"),
	DEATH_METAL(22, "Death Metal"),
	PRANKS(23, "Pranks"),
	SOUNDTRACK(24, "Soundtrack"),
	EURO_TECHNO(25, "Euro-Techno"),
	AMBIENT(26, "Ambient"),
	TRIP_HOP(27, "Trip-Hop"),
	VOCAL(28, "Vocal"),
	JAZZ_FUNK(29, "Jazz&Funk"),
	FUSION(30, "Fusion"),
	TRANCE(31, "Trance"),
	CLASSICAL(32, "Classical"),
	INSTRUMENTAL(33, "Instrumental"),
	ACID(34, "Acid"),
	HOUSE(35, "House"),
	GAME(36, "Game"),
	SOUND_CLIP(37, "Sound Clip"),
	GOSPEL(38, "Gospel"),
	NOISE(39, "Noise"),
	ALTERNROCK(40, "Alternative Rock", "Alt. Rock"),
	BASS(41, "Bass"),
	SOUL(42, "Soul"),
	PUNK(43, "Punk"),
	SPACE(44, "Space"),
	MEDITATIVE(45, "Meditative"),
	INSTRUMENTAL_POP(46, "Instrumental Pop"),
	INSTRUMENTAL_ROCK(47, "Instrumental Rock"),
	ETHNIC(48, "Ethnic"),
	GOTHIC(49, "Gothic"),
	DARKWAVE(50, "Darkwave"),
	TECHNO_INDUSTRIAL(51, "Techno Industrial"),
	ELECTRONIC(52, "Electronic"),
	POP_FOLK(53, "Pop-Folk"),
	EURODANCE(54, "Eurodance"),
	DREAM(55, "Dream"),
	SOUTHERN_ROCK(56, "Southern Rock"),
	COMEDY(57, "Comedy"),
	CULT(58, "Cult"),
	GANGSTA(59, "Gangsta"),
	TOP_40(60, "Top 40"),
	CHRISTIAN_RAP(61, "Christian Rap"),
	POP_FUNK(62, "Pop/Funk"),
	JUNGLE(63, "Jungle"),
	NATIVE_AMERICAN(64, "Native American"),
	CABARET(65, "Cabaret"),
	NEW_WAVE(66, "New Wave"),
	PSYCHADELIC(67, "Psychedelic"),
	RAVE(68, "Rave"),
	SHOWTUNES(69, "Showtunes"),
	TRAILER(70, "Trailer"),
	LO_FI(71, "Lo-Fi"),
	TRIBAL(72, "Tribal"),
	ACID_PUNK(73, "Acid Punk"),
	ACID_JAZZ(74, "Acid Jazz"),
	POLKA(75, "Polka"),
	RETRO(76, "Retro"),
	MUSICAL(77, "Musical"),
	ROCK_ROLL(78, "Rock & Roll"),
	HARD_ROCK(79, "Hard Rock"),
	FOLK(80, "Folk"),
	FOLK_ROCK(81, "Folk-Rock"),
	NATIONAL_FOLK(82, "National Folk"),
	SWING(83, "Swing"),
	FAST_FUSION(84, "Fast Fusion"),
	BEBOB(85, "Bebob"),
	LATIN(86, "Latin", "Latin Music"),
	REVIVAL(87, "Revival"),
	CELTIC(88, "Celtic"),
	BLUEGRASS(89, "Bluegrass"),
	AVANTGARDE(90, "Avantgarde"),
	GOTHIC_ROCK(91, "Gothic Rock"),
	PROGRESSIVE_ROCK(92, "Progressive Rock"),
	PSYCHEDELIC_ROCK(93, "Psychedelic Rock"),
	SYMPHONIC_ROCK(94, "Symphonic Rock"),
	SLOW_ROCK(95, "Slow Rock"),
	BIG_BAND(96, "Big Band"),
	CHORUS(97, "Chorus"),
	EASY_LISTENING(98, "Easy Listening"),
	ACOUSTIC(99, "Acoustic"),
	HUMOUR(100, "Humour"),
	SPEECH(101, "Speech"),
	CHANSON(102, "Chanson"),
	OPERA(103, "Opera"),
	CHAMBER_MUSIC(104, "Chamber Music"),
	SONATA(105, "Sonata"),
	SYMPHONY(106, "Symphony"),
	BOOTY_BASS(107, "Booty Bass"),
	PRIMUS(108, "Primus"),
	PORN_GROOVE(109, "Porn Groove"),
	SATIRE(110, "Satire"),
	SLOW_JAM(111, "Slow Jam"),
	CLUB(112, "Club"),
	TANGO(113, "Tango"),
	SAMBA(114, "Samba"),
	FOLKLORE(115, "Folklore"),
	BALLAD(116, "Ballad"),
	POWER_BALLAD(117, "Power Ballad"),
	RHYTHMIC_SOUL(118, "Rhythmic Soul"),
	FREESTYLE(119, "Freestyle"),
	DUET(120, "Duet"),
	PUNK_ROCK(121, "Punk Rock"),
	DRUM_SOLO(122, "Drum Solo"),
	A_CAPELLA(123, "A Capella"),
	EURO_HOUSE(124, "Euro-House"),
	DANCE_HALL(125, "Dance Hall"),
	GOA(126, "Goa"),
	DRUM_AND_BASE(127, "Drum & Base"),
	CLUB_HOUSE(128, "Club-House"),
	HARDCORE(129, "Hardcore"),
	TERROR(130, "Terror"),
	INDIE(131, "Indie"),
	BRITPOP(132, "BritPop"),
	NEGERPUNK(133, "Negerpunk"),
	POLSK_PUNK(134, "Polsk Punk"),
	BEAT(135, "Beat"),
	CHRISTIAN_GANGSTA_RAP(136, "Christian Gangsta Rap"),
	HEAVY_METAL(137, "Heavy Metal"),
	BLACK_METAL(138, "Black Metal"),
	CROSSOVER(139, "Crossover"),
	CONTEMPORARY_CHRISTIAN(140, "Contemporary Christian"),
	CHRISTIAN_ROCK(141, "Christian Rock"),
	MERENGUE(142, "Merengue"),
	SALSA(143, "Salsa"),
	THRASH_METAL(144, "Thrash Metal"),
	ANIME(145, "Anime"),
	JPOP(146, "Jpop"),
	SYNTHPOP(147, "Synthpop");

	private int value;
	private String[] labels;

	private Genre(int value, String... label) {
		this.value = value;
		this.labels = label;
	}

	public int getIntValue() {
		return this.value;
	}

	public String getFirstLabel() {
		return this.labels[0];
	}

	public static final Genre getGenreTypeByIntValue(int value) {
		for (Genre mt : Genre.values()) {
			if (mt.getIntValue() == value) {
				return mt;
			}
		}
		// Fallback: return the unknown genre
		return UNKNOWN;
	}

	public static final Genre getFirstGenreByString(String value) {
		for (Genre gt : Genre.values()) {
			for (String label : gt.labels) {
				if (label.equalsIgnoreCase(value)) {
					return gt;
				}
			}
		}
		return UNKNOWN;
	}

	public static final List<Genre> getGenreByString(String value) {
		List<Genre> list = new ArrayList<Genre>();
		for (Genre gt : Genre.values()) {
			for (String label : gt.labels) {
				if (label.equalsIgnoreCase(value)) {
					list.add(gt);
					break;
				}
			}
		}
		return list;
	}

	public static List<Genre> getAllGenre() {
		List<Genre> list = new ArrayList<Genre>();
		for (Genre gt : Genre.values()) {
			if (gt != UNKNOWN) {
				list.add(gt);
			}
		}
		Collections.sort(list, new Comparator<Genre>() {
			@Override
			public int compare(Genre gt1, Genre gt2) {
				return gt1.getFirstLabel().compareTo(gt2.getFirstLabel());
			}
		});
		return list;
	}

	public static List<String> getAllGenreAsString() {
		List<Genre> genreList = getAllGenre();
		List<String> resultList = new ArrayList<String>();
		for (Genre genre : genreList) {
			resultList.add(genre.getFirstLabel());
		}
		return resultList;
	}
}
