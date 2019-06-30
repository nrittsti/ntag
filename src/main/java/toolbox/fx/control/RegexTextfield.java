/**
 * This file is part of NTag (audio file tag editor).
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
package toolbox.fx.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


public class RegexTextfield extends TextField {

    private IntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", 10000);
    private StringProperty regex = new SimpleStringProperty(this, "regex", ".");

    @Override
    public void replaceText(int start, int end, String text) {
        int length = getLength();
        if (getSelection().getLength() != 0) {
            length -= getSelection().getLength();
        }
        if (text == "" || (length < maxLength.getValue() && (text.matches(regex.getValue())))) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        int length = getLength();
        if (getSelection().getLength() != 0) {
            length -= getSelection().getLength();
        }
        if (text == "" || (length < maxLength.getValue() && (text.matches(regex.getValue())))) {
            super.replaceSelection(text);
        }
    }

    public int getMaxLength() {
        return maxLength.getValue();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength.setValue(maxLength);
    }

    public IntegerProperty maxLengthProperty() {
        return maxLength;
    }

    public String getRegex() {
        return regex.getValue();
    }

    public void setRegex(String regex) {
        if (regex == null || regex.length() == 0) {
            this.regex.setValue(".");
        } else {
            this.regex.setValue(regex);
        }
    }

    public StringProperty regexProperty() {
        return regex;
    }
}
