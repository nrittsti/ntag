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
package ntag.fx.scene.control.converter;

import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class IntegerListConverter extends StringConverter<List<Integer>> {

  private final String delim;

  public IntegerListConverter(String delim) {
    if (delim == null || delim.isEmpty()) {
      throw new IllegalArgumentException("delim cannot be null or empty");
    }
    this.delim = delim;
  }

  @Override
  public String toString(List<Integer> list) {
    if (list == null || list.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (Integer value : list) {
      if (!sb.isEmpty()) {
        sb.append(delim).append(' ');
      }
      sb.append(value);
    }
    return sb.toString();
  }

  @Override
  public List<Integer> fromString(String value) {
    List<Integer> list = new ArrayList<>();
    if (value == null) {
      return list;
    }
    value = value.trim();
    if (value.isEmpty()) {
      return list;
    }
    StringTokenizer st = new StringTokenizer(value, delim);
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      if (!token.isEmpty()) {
        list.add(Integer.valueOf(token));
      }
    }
    return list;
  }
}