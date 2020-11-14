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
package ntag.io.util;

import java.util.ArrayList;
import java.util.List;

public class CommandLineParser {

  private static final String OPTION_PATTERN = "([-][a-zA-Z])|([-]{2}[a-zA-Z0-9]*)";

  private final List<CommandLineOption> options = new ArrayList<>();

  public void parse(String[] args) {
    if (args == null) {
      throw new IllegalArgumentException("args cannot be null");
    }
    CommandLineOption option = null;
    for (String arg : args) {
      if (arg.startsWith("-")) {
        if (option != null && option.isValueAttachment() && option.getValue() == null) {
          throw new IllegalArgumentException(String.format("Missing value for option [%s]", option));
        }
        if (!arg.matches(OPTION_PATTERN)) {
          throw new IllegalArgumentException(String.format("Invalid option argument [%s]", arg));
        }
        boolean foundFlag = false;
        final boolean longOptionFlag = arg.charAt(1) == '-';
        for (CommandLineOption o : options) {
          if ((longOptionFlag && o.getLongName().equals(arg.substring(2))) || (o.getShortName() == arg.charAt(1))) {
            option = o;
            if (option.isActivated()) {
              throw new IllegalArgumentException(String.format("Duplicate Option [%s]", option));
            }
            option.setActivated(true);
            foundFlag = true;
          }
        }
        if (!foundFlag) {
          throw new IllegalArgumentException(String.format("Unknown Option [%s]", option));
        }
      } else {
        if (option == null) {
          throw new IllegalArgumentException(String.format("Invalid Argument [%s]", arg));
        } else if (!option.isValueAttachment()) {
          throw new IllegalArgumentException(String.format("Option [%s] expects no value [%s]", option.toString(), arg));
        } else if (option.getValue() != null) {
          throw new IllegalArgumentException(String.format("Option [%s] expects a single value [%s]", option.toString(), arg));
        } else {
          if (arg.length() > 2 && arg.startsWith("'") && arg.endsWith("'")) {
            arg = arg.substring(1, arg.length() - 1);
          }
          option.setValue(arg);
        }
      }
    }
  }

  public void addOption(char shortName, String longName, boolean hasValue) {
    CommandLineOption option = new CommandLineOption(shortName, longName, hasValue);
    if (options.contains(option)) {
      throw new IllegalArgumentException(String.format("Duplicate Option [%s]", option));
    }
    options.add(option);
  }

  public boolean hasOption(char shortName) {
    if (shortName == 0) {
      throw new IllegalArgumentException("shortName cannot be 0");
    }
    for (CommandLineOption option : options) {
      if (option.getShortName() == shortName) {
        return option.isActivated();
      }
    }
    return false;
  }

  public boolean hasOption(String longName) {
    if (longName == null || longName.length() == 0) {
      throw new IllegalArgumentException("longName cannot be null or empty");
    }
    for (CommandLineOption option : options) {
      if (longName.equalsIgnoreCase(option.getLongName())) {
        return option.isActivated();
      }
    }
    return false;
  }

  public String getOptionValue(char shortName) {
    if (shortName == 0) {
      throw new IllegalArgumentException("shortName cannot be 0");
    }
    for (CommandLineOption option : options) {
      if (option.getShortName() == shortName) {
        return option.getValue();
      }
    }
    return null;
  }

  public String getOptionValue(String longName) {
    if (longName == null || longName.length() == 0) {
      throw new IllegalArgumentException("longName cannot be null or empty");
    }
    for (CommandLineOption option : options) {
      if (longName.equalsIgnoreCase(option.getLongName())) {
        return option.getValue();
      }
    }
    return null;
  }
}
