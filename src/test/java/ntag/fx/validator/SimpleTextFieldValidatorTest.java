package ntag.fx.validator;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import ntag.Category;
import ntag.io.NTagProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(Category.Unit)
class SimpleTextFieldValidatorTest {

  TextField textField;

  @BeforeAll
  static void initPlatform() {
    new JFXPanel();
  }

  @BeforeEach
  void setUp() {
    textField = new TextField();
  }

  @Test
  void validateText() {
    SimpleTextFieldValidator validator = new SimpleTextFieldValidator(textField, SimpleTextFieldValidator.ValidationMode.Text, 3);

    textField.setText("EUR");
    validator.changed(null, true, false);
    assertEquals("", textField.getStyle());

    textField.setText("abcd");
    validator.changed(null, true, false);
    assertTrue(textField.getStyle().length() > 0);
  }

  @Test
  void validateUInteger() {
    SimpleTextFieldValidator validator = new SimpleTextFieldValidator(textField, SimpleTextFieldValidator.ValidationMode.UInteger, 10);

    textField.setText("-10");
    validator.changed(null, true, false);
    assertTrue(textField.getStyle().length() > 0);

    textField.setText("10.0");
    validator.changed(null, true, false);
    assertTrue(textField.getStyle().length() > 0);

    textField.setText("10,0");
    validator.changed(null, true, false);
    assertTrue(textField.getStyle().length() > 0);

    textField.setText("EUR");
    validator.changed(null, true, false);
    assertTrue(textField.getStyle().length() > 0);

    textField.setText("10");
    validator.changed(null, true, false);
    assertEquals("", textField.getStyle());
  }

  @Test
  void validateLocalDate() {
    SimpleTextFieldValidator validator = new SimpleTextFieldValidator(textField, SimpleTextFieldValidator.ValidationMode.LocalDate, 10);

    NTagProperties props = new NTagProperties();
    props.setDateFormat("yyyy-MM-dd");

    textField.setText("abcd");
    validator.changed(null, true, false);
    assertTrue(textField.getStyle().length() > 0);

    textField.setText("2019-07-01");
    validator.changed(null, true, false);
    assertEquals("", textField.getStyle());
  }
}
