/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakuli.integration.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.sakuli.integration.IntegrationTest;
import org.sakuli.integration.ui.app.UiTestApplication;
import org.sakuli.integration.ui.app.UiTestEvent;
import org.sakuli.javaDSL.AbstractSakuliTest;
import org.sakuli.javaDSL.actions.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Paths;
import java.util.Map;

/**
 * Represents an abstract representation of UI (user interface) integration test for Sakuli.
 * There should be tested, if all graphical actions will work correctly.
 *
 * @author tschneck
 *         Date: 08.05.2014
 */
public abstract class AbstractUiTestApplicationIT extends AbstractSakuliTest implements IntegrationTest {

    public static final String IMAGE_LIB_FOLDER_NAME = "image_lib";
    protected static Map<UiTestEvent, Integer> eventCounter;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Environment env;

    @Override
    protected String getSakuliMainFolder() {
        return SAKULI_MAIN_FOLDER_PATH;
    }

    @Override
    protected String getTestSuiteFolder() {
        return getTestSuiteRootFolder() + TEST_FOLDER_PATH;
    }

    @Override
    protected String getSahiFolder() {
        return Paths.get("../../sahi").normalize().toAbsolutePath().toString();
    }

    @BeforeMethod(alwaysRun = true)
    @Override
    public void initTcStep() throws Exception {
        super.initTcStep();
        env = new Environment();
    }

    @AfterSuite(alwaysRun = true)
    @Override
    public void tearDown() throws Exception {
        Platform.exit();
        super.tearDown();
    }

    protected void stopUiApplication(final Stage stage) {
        logger.info("............................STOP UI-App");
        Platform.runLater(new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (Platform.isFxApplicationThread()) {
                    logger.info("fire WINDOW_CLOSE_REQUEST to FX-THREAD!");
                    fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                    return true;
                }
                logger.error("cloud not close - NO FX-THREAD!");
                return false;
            }
        });
    }

    /**
     * @return a unique ID from {@link #getTestCaseId()}.
     */
    protected String getUniqueTestCaseId() {
        return getTestCaseId() + System.nanoTime();
    }

    /**
     * @return a test case id - should be implemented in the explicit tests
     */
    protected abstract String getTestCaseId();

    /**
     * Starts the example {@link UiTestApplication}
     */
    protected Stage startUiApplication() {
        logger.info("............................START");
        final UiTestApplication uiTestApplication = new UiTestApplication();
        new JFXPanel();
        Platform.runLater(uiTestApplication);
        return UiTestApplication.stage;
    }

    /**
     * add 1 to the current count of the assigned {@link UiTestEvent}.
     *
     * @param testEvent test event to count
     */
    synchronized protected void countEvent(UiTestEvent testEvent) {
        Integer count = 1;
        if (eventCounter.containsKey(testEvent)) {
            count = eventCounter.get(testEvent) + 1;
        }
        logger.info("set CLICK-COUNT to {}", count);
        eventCounter.put(testEvent, count);
    }

    /**
     * @return the counter of the assigned {@link UiTestEvent}.
     */
    synchronized protected int getEventCount(UiTestEvent testEvent) {
        if (eventCounter.containsKey(testEvent)) {
            return eventCounter.get(testEvent);
        }
        return 0;
    }
}
