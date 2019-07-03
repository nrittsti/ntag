package ntag.fx.scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

@Tag(Category.Selenium)
public class AboutDialogTest extends AbstractFxTest {

    @Start
    protected void start(Stage stage) throws IOException {
        super.start(stage);
    }

    @Stop
    protected void stop() {
        super.stop();
    }

    @Test
    @DisplayName("show about dialog and check version label")
    void show_about_dialog(FxRobot robot) {
        // given
        String expected = appProps.getVersion();
        // when
        Button infoButton = robot.lookup("#info-button").queryAs(Button.class);
        robot.clickOn(infoButton);
        // then
        Assertions.assertThat(robot.lookup(".dialog")).isNotNull();
        Assertions.assertThat(robot.lookup("#versionLabel").queryAs(Label.class)).hasText(expected);
        // when
        robot.type(KeyCode.ENTER);
        // then
        Assertions.assertThat(robot.lookup("#info-button")).isNotNull();
    }
}
