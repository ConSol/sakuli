/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2015 the original author or authors.
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

package de.consol.sakuli.aop;

import de.consol.sakuli.BaseTest;
import de.consol.sakuli.PropertyHolder;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 23.09.14
 */
public abstract class AopBaseTest {
    private Path logFile;

    @BeforeClass(alwaysRun = true)
    public void setLogLevel() throws Exception {
        BaseTest.setSakuliLogLevel("DEBUG");
        BaseTest.setSikuliLogLevel("DEBUG");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = BaseTest.INCLUDE_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "aopTest-beanRefFactory.xml";
        logFile = Paths.get(BeanLoader.loadBean(PropertyHolder.class).getLogFile());
    }

    protected void assertLastLine(String filter, LogLevel logLevel, String expectedMessage) throws IOException {
        String preFix = null;
        switch (logLevel) {
            case ERROR:
                preFix = "ERROR";
                break;
            case INFO:
                preFix = "INFO ";
                break;
            case DEBUG:
                preFix = "DEBUG";
                break;
            case WARNING:
                preFix = "WARN ";
                break;
        }
        String lastLineOfLogFile = BaseTest.getLastLineWithContent(logFile, filter);
        assertEquals(lastLineOfLogFile.substring(0, 5), preFix);
        assertEquals(lastLineOfLogFile.substring(lastLineOfLogFile.indexOf("]") + 4), expectedMessage);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }

    @AfterClass(alwaysRun = true)
    public void removeLogLevel() throws Exception {
        BaseTest.setSakuliLogLevel(null);
        BaseTest.setSikuliLogLevel(null);
    }
}
