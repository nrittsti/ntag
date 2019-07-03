package ntag;

import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;
import org.testfx.util.WaitForAsyncUtils;
import toolbox.fx.FxUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
public class AbstractFxTest extends AbstractAudioFileTest {

    protected NTag ntag;

    @Start
    protected void start(Stage stage) throws IOException {
        this.ntag = new NTag();
        this.ntag.initPrimaryStage(stage);
    }

    @Stop
    protected void stop() {
        FxUtil.getPrimaryStage().close();
    }

    protected void waitForQuery(FxRobot robot, String query, int seconds) throws TimeoutException {
        WaitForAsyncUtils.waitFor(seconds, TimeUnit.SECONDS, () -> robot.lookup(query).tryQuery().isPresent());
    }
}
