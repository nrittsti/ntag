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
import ntag.io.NTagProperties;
import ntag.io.Resources;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * {@link StringConverter} and simple validation support for date fields.
 */
public class ValidatingDateFieldConverter extends AbstractValidatingConverter<LocalDate> {
  private final DateTimeFormatter dateTimeFormatter;

  public ValidatingDateFieldConverter(TextField textField, int maxLength) {
    super(textField, maxLength);
    NTagProperties appProps = NTagProperties.instance();
    this.dateTimeFormatter = DateTimeFormatter.ofPattern(appProps.getDateFormat());
  }

  @Override
  protected String getValidationMessage() {
    return Resources.format("ntag", "msg_invalid_date_field", getMaxLength());
  }

  @Override
  protected LocalDate parse(String value) {
    return LocalDate.parse(value, dateTimeFormatter);
  }

  @Override
  public String toString(LocalDate value) {
    this.lastValidValue = value == null ? null : value.format(dateTimeFormatter);
    return this.lastValidValue;
  }
}
