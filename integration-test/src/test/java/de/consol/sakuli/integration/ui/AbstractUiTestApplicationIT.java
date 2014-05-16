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

package de.consol.sakuli.integration.ui;

import de.consol.sakuli.actions.environment.Environment;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.integration.IntegrationTest;
import de.consol.sakuli.integration.builder.TestCaseBuilder;
import de.consol.sakuli.integration.ui.app.UiTestApplication;
import de.consol.sakuli.integration.ui.app.UiTestEvent;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.loader.ScreenActionLoader;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import javafx.application.Platform;
import net.sf.sahi.report.Report;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.*;

import java.io.File;
import java.util.Map;
import java.util.concurrent.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Represents an abstract representation of UI (user interface) integration test for Sakuli.
 * There should be tested, if all graphical actions will work correctly.
 *
 * @author tschneck
 *         Date: 08.05.2014
 */
@Test(groups = IntegrationTest.GROUP)
public abstract class AbstractUiTestApplicationIT implements IntegrationTest {

    private static final String TEST_CONTEXT_PATH = "ui-beanRefFactory.xml";
    protected static Long DEFAULT_TIME_OUT_SEC = 30L;
    protected static Map<UiTestEvent, Integer> eventCounter;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ExecutorService executorService;
    protected UiTestApplication uiTestApplication;
    protected Environment env;
    private ScreenActionLoader screenActionLoader;
    @Mock
    private RhinoScriptRunner rhinoScriptRunner;

    @BeforeSuite
    public void setUp() throws Exception {
        executorService = Executors.newCachedThreadPool();
        BeanLoader.CONTEXT_PATH = TEST_CONTEXT_PATH;
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = INCLUDE_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = SAHI_FOLDER_PATH;
    }

    @AfterSuite
    public void tearDown() throws Exception {
        executorService.shutdownNow();
    }

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        //generate a new empty test case for each IT
        TestCase emptyTestCase = TestCaseBuilder.createEmptyTestCase("Integration Test for " + getUniqueTestCaseId(), getUniqueTestCaseId());
        screenActionLoader = BeanLoader.loadScreenActionLoader();
        screenActionLoader.getTestSuite().addTestCase(emptyTestCase.getId(), emptyTestCase);
        screenActionLoader.init(emptyTestCase.getId(), getImagePaths());
        screenActionLoader = initMocks(screenActionLoader);

        //load environment variable
        env = new Environment(false, screenActionLoader);

        //prepare the ui test application
        UiTestApplication.cleanAllEvents();
        eventCounter = new ConcurrentHashMap<>();
        uiTestApplication = new UiTestApplication();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(DEFAULT_TIME_OUT_SEC));
                    logger.error("!!! DEFAULT TIMEOUT REACHED (" + DEFAULT_TIME_OUT_SEC + " sec) !!!");
                    Platform.exit();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @AfterMethod
    public void initStop() throws Throwable {
        executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        logger.info("............................STOP");
        Throwable e = screenActionLoader.getTestSuite().getException();
        if (e != null) {
            throw e;
        }
    }

    private ScreenActionLoader initMocks(ScreenActionLoader screenActionLoader) {
        ReflectionTestUtils.setField(screenActionLoader.getBaseLoader(), "rhinoScriptRunner", rhinoScriptRunner, RhinoScriptRunner.class);
        when(rhinoScriptRunner.getReport()).thenReturn(mock(Report.class));
        return screenActionLoader;
    }

    /**
     * @return the path as string to the pattern images.
     */
    protected String[] getImagePaths() {
        return new String[]{
                screenActionLoader.getTestSuite().getTestSuiteFolder().toString() + File.separator + "image_lib"
        };
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
     *
     * @return
     */
    protected Future<Long> startUiTestApplication() {
        logger.info("............................START");
        return executorService.submit(uiTestApplication);
    }

    /**
     * add 1 to the current count of the assigned {@link UiTestEvent}.
     *
     * @param testEvent test event to count
     */
    protected void countEvent(UiTestEvent testEvent) {
        Integer count = 1;
        if (eventCounter.containsKey(testEvent)) {
            count = eventCounter.get(testEvent) + 1;
        }
        eventCounter.put(testEvent, count);
    }

    /**
     * @return the counter of the assigned {@link UiTestEvent}.
     */
    protected int getEventCount(UiTestEvent testEvent) {
        if (eventCounter.containsKey(testEvent)) {
            return eventCounter.get(testEvent);
        }
        return 0;
    }

    protected ScreenActionLoader getScreenActionLoader() {
        return screenActionLoader;
    }
}
