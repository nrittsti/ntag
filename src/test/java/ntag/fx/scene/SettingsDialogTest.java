package ntag.fx.scene;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ntag.AbstractFxTest;
import ntag.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(Category.Selenium)
public class SettingsDialogTest extends AbstractFxTest {

    @Start
    protected void start(Stage stage) throws IOException {
        super.start(stage);
    }

    @Stop
    protected void stop() {
        super.stop();
    }

    @Test
    @DisplayName("show settings dialog and change language")
    void show_settings_dialog(FxRobot robot) throws Exception {
        // given
        Locale expected = Locale.GERMAN;
        // when
        robot.clickOn("#settings-button");
        // then
        Assertions.assertThat(robot.lookup(".dialog")).isNotNull();
        // when
        robot.clickOn("#languageComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#okButton");
        waitForQuery(robot, "#settings-button", 1);
        // when
        Locale actual = appProps.getLanguage();
        assertEquals(expected, actual);
    }
}
