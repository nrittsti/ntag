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
 *   Copyright 2020, Nico Rittstieg
 *
 */

package ntag.io;

import ntag.AbstractAudioFileTest;
import ntag.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Tag(Category.Unit)
class HashUtilTest extends AbstractAudioFileTest {

  @Test
  void createFromByteArray() throws Exception {
    // given
    byte[] given = Files.readAllBytes(getArtwork());
    byte[] expected = new byte[]{-44, -22, -48, -36, -93, -41, -86, 77, 62, 3, 74, -32, -101, -60, 23, 64};
    // when
    byte[] actual = HashUtil.createFromByteArray("MD5", given);
    // then
    assertArrayEquals(expected, actual);
  }
}
