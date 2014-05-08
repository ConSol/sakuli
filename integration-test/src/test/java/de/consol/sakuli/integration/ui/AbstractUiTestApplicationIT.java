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

import de.consol.sakuli.integration.IntegrationTest;
import de.consol.sakuli.integration.ui.app.UiTestApplication;
import de.consol.sakuli.integration.ui.app.UiTestEvent;
import javafx.application.Platform;
import org.apache.log4j.Logger;
import org.testng.annotations.*;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Represents an abstract representation of UI (user interface) integration test for Sakuli.
 * There should be tested, if all graphical actions will work correctly.
 *
 * @author tschneck
 *         Date: 08.05.2014
 */
@Test(groups = IntegrationTest.GROUP)
public abstract class AbstractUiTestApplicationIT {

    private static final Logger logger = Logger.getLogger(AbstractUiTestApplicationIT.class);
    protected static Long DEFAULT_TIME_OUT_SEC = 30L;

    protected static Map<UiTestEvent, Integer> eventCounter;
    protected ExecutorService executorService;
    protected UiTestApplication uiTestApplication;

    @BeforeSuite
    public void setUp() throws Exception {
        executorService = Executors.newCachedThreadPool();
    }

    @AfterSuite
    public void tearDown() throws Exception {
        executorService.shutdownNow();
    }

    @BeforeMethod
    public void init() throws Exception {
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

    protected Future<Long> startUiTestApplication() {
        logger.info("............................START");
        return executorService.submit(uiTestApplication);
    }

    protected void countEvent(UiTestEvent testEvent) {
        Integer count = 1;
        if (eventCounter.containsKey(testEvent)) {
            count = eventCounter.get(testEvent) + 1;
        }
        eventCounter.put(testEvent, count);
    }

    protected int getEventCount(UiTestEvent testEvent) {
        if (eventCounter.containsKey(testEvent)) {
            return eventCounter.get(testEvent);
        }
        return 0;
    }

    @AfterMethod
    public void initStop() throws Exception {
        executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        logger.info("............................STOP");
    }
}
