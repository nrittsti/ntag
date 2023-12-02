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
import ntag.fx.util.FxUtil;

import java.lang.ref.WeakReference;

/**
 * {@link StringConverter} and simple validation support for FX text editors.
 */
public abstract class AbstractValidatingConverter<T> extends StringConverter<T> {
  protected static final String ERROR_STYLE = "-fx-effect: dropshadow( three-pass-box ,red , 8, 0.0 , 0 , 0 );";

  protected final WeakReference<TextField> weakTextFieldReference;
  protected int maxLength;
  protected String lastValidValue;

  public AbstractValidatingConverter(TextField textField, int maxLength) {
    if (textField == null) {
      throw new IllegalArgumentException("textField cannot be null");
    }
    if (maxLength < 1) {
      throw new IllegalArgumentException("maxLength nust be greater than 0");
    }
    this.weakTextFieldReference = new WeakReference<>(textField);
    this.maxLength = maxLength;
  }

  @SuppressWarnings("unused")
  public int getMaxLength() {
    if (maxLength < 1) {
      throw new IllegalArgumentException("maxLength nust be greater than 0");
    }
    return maxLength;
  }

  @SuppressWarnings("unused")
  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  protected void validateTextLength(String value) {
    if (value.length() > maxLength) {
      throw new StringIndexOutOfBoundsException(String.format("TextField length limit %d is exceeded", maxLength));
    }
  }

  protected abstract String getValidationMessage();

  protected abstract T parse(String value);

  @Override
  public T fromString(String value) {
    TextField textField = weakTextFieldReference.get();
    if (textField == null) {
      return parse(value);
    }
    if (value == null) {
      textField.setStyle("");
      return null;
    }
    value = value.trim();
    if (value.isEmpty()) {
      textField.setStyle("");
      return null;
    }
    try {
      validateTextLength(value);
      T result = parse(value);
      this.lastValidValue = value;
      textField.setStyle("");
      return result;
    } catch (Exception e) {
      textField.setStyle(ERROR_STYLE);
      String msg = getValidationMessage();
      FxUtil.showNotification(msg, null, 3000);
      return lastValidValue == null ? null : parse(lastValidValue);
    }
  }
}
