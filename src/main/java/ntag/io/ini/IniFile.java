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
package ntag.io.ini;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Map.Entry;

public class IniFile {

  private final Map<String, Map<String, List<String>>> sections = new LinkedHashMap<>();

  public IniFile() {

  }

  public void read(Path path) throws IOException {
    if (path == null) {
      throw new IllegalArgumentException("path cannot be null");
    }
    sections.clear();
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      int currentLine = 0;
      String currentSection = null;
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        currentLine++;
        line = line.trim();
        if (line.length() == 0) {
          continue;
        } else if (line.charAt(0) == '#' || line.charAt(0) == ';') {
          continue;
        } else if (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']') {
          currentSection = line.substring(1, line.length() - 1);
          continue;
        }
        int index = line.indexOf('=');
        if (index != -1) {
          setValue(currentSection, line.substring(0, index), line.substring(index + 1).trim(), true);
        } else {
          throw new IOException(String.format("Line %d in INI-File '%s' contains no valid content!", currentLine, path.toString()));
        }
      }
    }
  }

  public void write(Path path) throws IOException {
    if (path == null) {
      throw new IllegalArgumentException("path cannot be null");
    }
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
      for (Entry<String, Map<String, List<String>>> section : sections.entrySet()) {
        writer.write('[');
        writer.write(section.getKey());
        writer.write(']');
        writer.newLine();
        writer.newLine();

        List<String> keyList = new ArrayList<>(section.getValue().keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
          for (String value : section.getValue().get(key)) {
            writer.write(key);
            writer.write(" = ");
            writer.write(value);
            writer.newLine();
          }
        }
        writer.newLine();
      }
    }
  }

  public void setValue(String section, String key, Double value) {
    setValue(section, key, value != null ? value.toString() : null, false);
  }

  public void setValue(String section, String key, Float value) {
    setValue(section, key, value != null ? value.toString() : null, false);
  }

  public void setValue(String section, String key, Integer value) {
    setValue(section, key, value != null ? value.toString() : null, false);
  }

  public void setValue(String section, String key, Boolean value) {
    setValue(section, key, value ? "1" : "0", false);
  }

  public void setValue(String section, String key, String value) {
    setValue(section, key, value, false);
  }

  public void setValue(String section, String key, String value, boolean append) {
    if (section == null) {
      throw new IllegalArgumentException("section cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    List<String> valueList = getValues(section, key);
    if (!append || value == null) {
      valueList.clear();
    }
    if (value != null) {
      valueList.add(value);
    }
  }

  public void setValues(String section, String key, List<String> values, boolean append) {
    if (section == null) {
      throw new IllegalArgumentException("section cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    if (values == null) {
      throw new IllegalArgumentException("values cannot be null");
    }
    List<String> valueList = getValues(section, key);
    if (!append) {
      valueList.clear();
    }
    valueList.addAll(values);
  }

  public void setDoubleValues(String section, String key, List<Double> values, boolean append) {
    if (section == null) {
      throw new IllegalArgumentException("section cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    if (values == null) {
      throw new IllegalArgumentException("values cannot be null");
    }
    List<String> valueList = getValues(section, key);
    if (!append) {
      valueList.clear();
    }
    for (Double d : values) {
      valueList.add(d.toString());
    }
  }

  public void setIntegerValues(String section, String key, List<Integer> values, boolean append) {
    if (section == null) {
      throw new IllegalArgumentException("section cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    if (values == null) {
      throw new IllegalArgumentException("values cannot be null");
    }
    List<String> valueList = getValues(section, key);
    if (!append) {
      valueList.clear();
    }
    for (Integer i : values) {
      valueList.add(i.toString());
    }
  }

  public Map<String, List<String>> getSection(String name) {
    if (name == null) {
      throw new IllegalArgumentException("section cannot be null");
    }
    name = name.trim().toLowerCase();
    return sections.computeIfAbsent(name, k -> new LinkedHashMap<>());
  }

  public List<String> getValues(String section, String key, String... defaultValues) {
    if (section == null) {
      throw new IllegalArgumentException("section cannot be null");
    }
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    Map<String, List<String>> sectionMap = getSection(section);
    key = key.trim().toLowerCase();
    List<String> values = sectionMap.computeIfAbsent(key, k -> new ArrayList<>());
    if (values.isEmpty() && defaultValues != null) {
      Collections.addAll(values, defaultValues);
    }
    return values;
  }

  public List<Double> getDoubleValues(String name, String key) {
    List<String> values = getValues(name, key);
    List<Double> doubleValues = new ArrayList<>();
    for (String value : values) {
      doubleValues.add(Double.valueOf(value));
    }
    return doubleValues;
  }

  public List<Integer> getIntegerValues(String name, String key, Integer... defaultValues) {
    List<String> values = getValues(name, key);
    List<Integer> integerValues = new ArrayList<>();
    for (String value : values) {
      integerValues.add(Integer.valueOf(value));
    }
    if (integerValues.isEmpty() && defaultValues != null) {
      Collections.addAll(integerValues, defaultValues);
    }
    return integerValues;
  }

  public String getValue(String name, String key, String defaultValue) {
    List<String> values = getValues(name, key);
    if (values.isEmpty()) {
      if (defaultValue != null) {
        values.add(defaultValue);
      }
      return defaultValue;
    } else {
      return values.get(0);
    }
  }

  public Integer getInteger(String name, String key, Integer defaultValue) {
    List<String> values = getValues(name, key);
    if (values.isEmpty()) {
      if (defaultValue != null) {
        values.add(defaultValue.toString());
      }
      return defaultValue;
    } else {
      return Integer.parseInt(values.get(0));
    }
  }

  public Float getFloat(String name, String key, Float defaultValue) {
    List<String> values = getValues(name, key);
    if (values.isEmpty()) {
      if (defaultValue != null) {
        values.add(defaultValue.toString());
      }
      return defaultValue;
    } else {
      return Float.parseFloat(values.get(0));
    }
  }

  public Double getDouble(String name, String key, Double defaultValue) {
    List<String> values = getValues(name, key);
    if (values.isEmpty()) {
      if (defaultValue != null) {
        values.add(defaultValue.toString());
      }
      return defaultValue;
    } else {
      return Double.valueOf(values.get(0));
    }
  }

  public Boolean getBoolean(String name, String key, Boolean defaultValue) {
    List<String> values = getValues(name, key);
    if (values.isEmpty()) {
      if (defaultValue != null) {
        values.add(defaultValue ? "1" : "0");
      }
      return defaultValue;
    } else {
      return "1".equals(values.get(0)) ? Boolean.TRUE : Boolean.FALSE;
    }
  }
}
