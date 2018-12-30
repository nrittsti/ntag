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
package toolbox.io;

import toolbox.util.StringBuilderUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtil {

	private FileUtil() {

	}

	public static void checkHomeDirectory(String homeDir) throws IOException {
		if (homeDir == null || homeDir.trim().length() == 0) {
			throw new IllegalArgumentException("Home directory cannot be null or empty!");
		}
		Path path = Paths.get(homeDir, "test.tmp");
		if (Files.isWritable(path)) {
			throw new IOException(Resources.get("msg_invalid_home_directory"));
		}
	}

	public static String removeInvalidChars(String filename) {
		StringBuilder builder = new StringBuilder(filename);
		StringBuilderUtil.replace(builder, "\\", "");
		StringBuilderUtil.replace(builder, "/", "");
		StringBuilderUtil.replace(builder, ":", "");
		StringBuilderUtil.replace(builder, "*", "");
		StringBuilderUtil.replace(builder, "?", "");
		StringBuilderUtil.replace(builder, "\"", "");
		StringBuilderUtil.replace(builder, "<", "");
		StringBuilderUtil.replace(builder, ">", "");
		StringBuilderUtil.replace(builder, "|", "");
		StringBuilderUtil.replace(builder, "À", "A");
		StringBuilderUtil.replace(builder, "Á", "A");
		StringBuilderUtil.replace(builder, "Â", "A");
		StringBuilderUtil.replace(builder, "Ã", "A");
		StringBuilderUtil.replace(builder, "Ä", "Ae");
		StringBuilderUtil.replace(builder, "Å", "A");
		StringBuilderUtil.replace(builder, "Æ", "Ae");
		StringBuilderUtil.replace(builder, "Ç", "C");
		StringBuilderUtil.replace(builder, "È", "E");
		StringBuilderUtil.replace(builder, "É", "E");
		StringBuilderUtil.replace(builder, "Ê", "E");
		StringBuilderUtil.replace(builder, "Ë", "E");
		StringBuilderUtil.replace(builder, "Ì", "I");
		StringBuilderUtil.replace(builder, "Í", "I");
		StringBuilderUtil.replace(builder, "Î", "I");
		StringBuilderUtil.replace(builder, "Ï", "I");
		StringBuilderUtil.replace(builder, "Ð", "D");
		StringBuilderUtil.replace(builder, "Ñ", "N");
		StringBuilderUtil.replace(builder, "Ò", "O");
		StringBuilderUtil.replace(builder, "Ó", "O");
		StringBuilderUtil.replace(builder, "Ô", "O");
		StringBuilderUtil.replace(builder, "Õ", "O");
		StringBuilderUtil.replace(builder, "Ö", "Oe");
		StringBuilderUtil.replace(builder, "Ø", "O");
		StringBuilderUtil.replace(builder, "Ù", "U");
		StringBuilderUtil.replace(builder, "Ú", "U");
		StringBuilderUtil.replace(builder, "Û", "U");
		StringBuilderUtil.replace(builder, "Ü", "Ue");
		StringBuilderUtil.replace(builder, "Ý", "y");
		StringBuilderUtil.replace(builder, "ß", "ss");
		StringBuilderUtil.replace(builder, "à", "a");
		StringBuilderUtil.replace(builder, "á", "a");
		StringBuilderUtil.replace(builder, "â", "a");
		StringBuilderUtil.replace(builder, "ã", "a");
		StringBuilderUtil.replace(builder, "ä", "ae");
		StringBuilderUtil.replace(builder, "å", "a");
		StringBuilderUtil.replace(builder, "æ", "ae");
		StringBuilderUtil.replace(builder, "ç", "c");
		StringBuilderUtil.replace(builder, "è", "e");
		StringBuilderUtil.replace(builder, "é", "e");
		StringBuilderUtil.replace(builder, "ê", "e");
		StringBuilderUtil.replace(builder, "ë", "e");
		StringBuilderUtil.replace(builder, "ì", "i");
		StringBuilderUtil.replace(builder, "í", "i");
		StringBuilderUtil.replace(builder, "î", "i");
		StringBuilderUtil.replace(builder, "ï", "i");
		StringBuilderUtil.replace(builder, "ð", "d");
		StringBuilderUtil.replace(builder, "ñ", "n");
		StringBuilderUtil.replace(builder, "ò", "o");
		StringBuilderUtil.replace(builder, "ó", "o");
		StringBuilderUtil.replace(builder, "ô", "o");
		StringBuilderUtil.replace(builder, "õ", "o");
		StringBuilderUtil.replace(builder, "ö", "oe");
		StringBuilderUtil.replace(builder, "ø", "oe");
		StringBuilderUtil.replace(builder, "ù", "u");
		StringBuilderUtil.replace(builder, "ú", "u");
		StringBuilderUtil.replace(builder, "û", "u");
		StringBuilderUtil.replace(builder, "ü", "ue");
		StringBuilderUtil.replace(builder, "ý", "y");
		StringBuilderUtil.replace(builder, "ÿ", "y");
		StringBuilderUtil.replace(builder, "Ÿ", "y");
		return builder.toString();
	}
}
