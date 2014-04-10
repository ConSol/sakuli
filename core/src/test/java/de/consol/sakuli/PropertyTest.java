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

package de.consol.sakuli;

import de.consol.sakuli.loader.BeanLoader;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.AssertJUnit.assertTrue;


/**
 * run tests with mvn
 *
 * @author tschneck
 *         Date: 10.06.13
 */

public class PropertyTest extends BaseTest {
    final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    private PropertyHolder properties;

    @BeforeSuite
    public void init() {
        properties = BeanLoader.loadBean(PropertyHolder.class);
    }

    @Test
    public void testTestSuiteFolder() throws IOException {
        Path tsFolder = Paths.get(properties.getTestSuiteFolder());

        assertTrue("find test suite folder over system arguments" +
                "if it cannot found run \"mvn test\"" +
                "or add to vm args \"" +
                "-Dtest.suite.folder=<...pathToproject...>" + TEST_FOLDER_PATH
                , Files.exists(tsFolder));
        System.out.println(tsFolder.toFile().getAbsolutePath());
        assertTrue(tsFolder.toFile().getAbsolutePath().contains(TEST_FOLDER_PATH));
    }

    @Test
    public void testLogFolders() throws IOException {

        logger.info("testlog");
        Path logFile = Paths.get(properties.getLogFile());
        assertTrue("log file '_sakuli.log'", Files.exists(logFile));
//        Files.delete(logFile);

        Path logFileAll = Paths.get(properties.getLogFileAll());
        assertTrue("log file 'all.log'", Files.exists(logFileAll));
//        Files.delete(logFileAll);
    }

    @Test
    public void testScreenShotFolders() throws IOException {

//        logger.info("testlog");
//        Path screenshotFolder = Paths.get(properties.getScreenShotFolder());
//        if (!Files.exists(screenshotFolder)) {
//            Files.createDirectory(screenshotFolder);
//        }
//        assertTrue("screenShotFolder", Files.exists(screenshotFolder));
//        Files.delete(screenshotFolder);
    }

}