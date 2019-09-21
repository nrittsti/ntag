/*
  This file is part of NTag (audio file tag editor).
  <p>
  NTag is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  NTag is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with NTag.  If not, see <http://www.gnu.org/licenses/>.
  <p>
  Copyright 2016, Nico Rittstieg
 */
package ntag.io;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public final class HashUtil {

    private static final Logger LOGGER = Logger.getLogger(HashUtil.class.getName());

    private HashUtil() {

    }

    public static String createFromByteArray(String algorithm, byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
            digest.update(data);
            return DatatypeConverter.printHexBinary(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe(e.getMessage());
            return "";
        }
    }
}
