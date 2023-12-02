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
 *   Copyright 2023, Nico Rittstieg
 *
 */
package ntag.fx.validator;

import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ntag.io.Resources;

/**
 * {@link StringConverter} and simple validation support for unsigned integer fields.
 */
public class ValidatingIntegerFieldConverter extends AbstractValidatingConverter<Integer> {

  public ValidatingIntegerFieldConverter(TextField textField, int maxLength) {
    super(textField, maxLength);
  }

  @Override
  protected String getValidationMessage() {
    return Resources.format("ntag", "msg_invalid_numeric_field", getMaxLength());
  }

  @Override
  protected Integer parse(String value) {
    return Integer.parseUnsignedInt(value);
  }

  @Override
  public String toString(Integer value) {
    this.lastValidValue = value == null ? null : value.toString();
    return this.lastValidValue;
  }
}
