/**
 * This file is part of NTag (tag-based database for audio files).
 * <p>
 * NTag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * NTag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with NTag.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright 2016, Nico Rittstieg
 */
package toolbox.util;

class CommandLineOption {

    /*
     * Properties
     */

    private char shortName;

    public char getShortName() {
        return shortName;
    }

    public void setShortName(char shortName) {
        this.shortName = shortName;
    }

    private String longName;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    private boolean valueAttachment;

    public boolean isValueAttachment() {
        return valueAttachment;
    }

    public void setValueAttachment(boolean valueAttachment) {
        this.valueAttachment = valueAttachment;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private boolean activated;

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /*
     * CTor
     */

    public CommandLineOption(char shortName, String longName, boolean valueAttachment) {
        super();
        this.shortName = shortName;
        this.longName = longName;
        this.valueAttachment = valueAttachment;
    }

    /*
     * Public API
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (shortName != 0) {
            sb.append('-').append(shortName);
        }
        if (longName != null && longName.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("--").append(longName);
        }
        return sb.toString();
    }
}
