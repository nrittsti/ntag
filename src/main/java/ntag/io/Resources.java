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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Resources {

  private Resources() {

  }

  private final static Map<String, ResourceBundle> bundles = new HashMap<>();
  private static Locale locale = Locale.ENGLISH;

  public static ResourceBundle getResourceBundle(String name) {
    assert name != null : "name is null";
    final String key = name + locale.getLanguage();
    if (!bundles.containsKey(key)) {
      bundles.put(key, ResourceBundle.getBundle("bundles." + name, locale));
    }
    return bundles.get(key);
  }

  public static String get(String bundle, String key) {
    assert bundle != null : "bundle is null";
    assert key != null : "key is null";
    String res = getResourceBundle(bundle).getString(key);
    assert res.length() > 0 : "Undefinied resource: " + key;
    return res;
  }

  public static String format(String bundle, String key, Object... args) {
    return String.format(get(bundle, key), args);
  }

  public static Locale getLocale() {
    return locale;
  }

  public static void setLocale(Locale locale) {
    assert locale != null : "locale is null";
    Resources.locale = locale;
  }
}
