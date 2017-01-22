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
package ntag.fx.util;

import java.util.Date;
import java.util.TimeZone;

public final class NTagFormat {

	private NTagFormat() {

	}

	public static String samplerate(final long value) {
		return String.format("%d kHz", value > 0 ? value / 1000 : value);
	}

	public static String bitrate(final long bitrate) {
		return String.format("%d kbit/s", bitrate);
	}

	/**
	 * Formats the given playtime as HH:mm:ss
	 *
	 * @param playtime
	 *            in seconds
	 * @return the formatted string
	 */
	public static String playtime(final long playtime) {
		Date date = new Date((playtime * 1000) - TimeZone.getDefault().getRawOffset());
		if (playtime < 3600) {
			return String.format("%1$TM:%1$TS", date);
		} else if (playtime < 86400) {
			return String.format("%1$TH:%1$TM:%1$TS", date);
		} else {
			return String.format("%1$td:%1$TH:%1$TM:%1$TS", date);
		}
	}

	/**
	 * Formats the given file size in Mebibyte<br>
	 * (IEC Standard)<br>
	 * 1 MiB = 1.048.576 Byte
	 *
	 * @param size
	 *            in Bytes
	 * @return the formatted string
	 */
	public static String fileSize(final long size, boolean binary) {
		if(binary) {
			if (size < 1024) {
				return size + " Bytes";
			} else if (size < 1048576) {
				return round(size / 1024d, 1) + " KiB";
			} else if (size < 1073741824d) {
				return round(size / 1048576d, 1) + " MiB";
			} else {
				return round(size / 1073741824d, 1) + "GiB";
			}
		} else {
			// decimal
			if (size < 1024) {
				return size + " Bytes";
			} else if (size < 1000000) {
				return round(size / 1000d, 1) + " KB";
			} else if (size < 1000000000d) {
				return round(size / 1000000d, 1) + " MB";
			} else {
				return round(size / 1000000000d, 1) + "GB";
			}
		}
	}

	/**
	 * runded einen double Wert auf beliebige Stellen
	 *
	 * @param value
	 *            der double Wert
	 * @param digits
	 *            die Genauigkeit (Nachkommastellen)
	 * @return the rounded double value
	 */
	public static double round(final double value, final int digits) {
		double result = Math.round(value * Math.pow(10d, digits));
		return result / Math.pow(10d, digits);
	}

}
