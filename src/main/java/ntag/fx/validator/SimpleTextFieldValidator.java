/*
  This file is part of NTag (audio file tag editor).
  <p>
  NTag is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  NTag is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with NTag.  If not, see <http://www.gnu.org/licenses/>.
  <p>
  Copyright 2016, Nico Rittstieg
 */
package ntag.fx.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.converter.LocalDateStringConverter;
import ntag.fx.util.FxUtil;
import ntag.io.NTagProperties;

import javax.xml.bind.ValidationException;
import java.lang.ref.WeakReference;
import java.time.format.DateTimeFormatter;

public class SimpleTextFieldValidator implements ChangeListener<Boolean> {

  private static final String ERROR_STYLE = "-fx-effect: dropshadow( three-pass-box ,red , 8, 0.0 , 0 , 0 );";

  private final WeakReference<TextField> weakTextFieldReference;
  private ValidationMode validationMode;
  private String lastValue;
  private int maxLength;
  private NTagProperties appProps = null;

  public SimpleTextFieldValidator(TextField textField, ValidationMode validationMode, int maxLength) {
    appProps = new NTagProperties();
    if (textField == null) {
      throw new IllegalArgumentException("textField cannot be null");
    }
    if (validationMode == null) {
      throw new IllegalArgumentException("validationMode cannot be null");
    }
    if (maxLength < 1) {
      throw new IllegalArgumentException("maxLength nust be greater than 0");
    }
    textField.focusedProperty().addListener(this);
    this.weakTextFieldReference = new WeakReference<>(textField);
    this.validationMode = validationMode;
    this.maxLength = maxLength;
  }

  @Override
  public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    TextField textField = weakTextFieldReference.get();
    if (textField == null) {
      return;
    }
    if (newValue) {
      // focus gain
      this.lastValue = textField.getText();
    } else {
      // focus lost
      String text = textField.getText();
      try {
        if (text.length() > maxLength) {
          throw new ValidationException(String.format("TextField length limit %d is exceeded", maxLength));
        }
        switch (validationMode) {
          case UInteger:
            Integer.parseUnsignedInt(text);
            break;
          case LocalDate:
            new LocalDateStringConverter(DateTimeFormatter.ofPattern(appProps.getDateFormat()), null).fromString(text);
            break;
          default:
            break;
        }
      } catch (Exception e) {
        textField.setText(this.lastValue);
        textField.setStyle(ERROR_STYLE);
        FxUtil.showNotification(e.getClass().getSimpleName() + ": " + e.getMessage(), null, 3000);
        return;
      }
      textField.setStyle("");
    }
  }

  public ValidationMode getValidationMode() {
    return validationMode;
  }

  public void setValidationMode(ValidationMode validationMode) {
    if (validationMode == null) {
      throw new IllegalArgumentException("validationMode cannot be null");
    }
    this.validationMode = validationMode;
  }

  public int getMaxLength() {
    if (maxLength < 1) {
      throw new IllegalArgumentException("maxLength nust be greater than 0");
    }
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public enum ValidationMode {
    Text, UInteger, LocalDate;
  }
}
