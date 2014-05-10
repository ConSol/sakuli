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

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.loader.BeanLoader;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


/**
 * use the default log4j.properties in src/test/resources
 *
 * @author tschneck
 *         Date: 24.07.13
 */
public class LoggerTest extends BaseTest {
    protected static Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Mock
    private TestSuite testSuite;
    private PropertyHolder properties;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(testSuite.getId()).thenReturn("test_id_for_logger_test");
        properties = BeanLoader.loadBean(PropertyHolder.class);
    }

    @AfterClass
    public void clean() throws IOException {
        Path logFile = Paths.get(properties.getLogFile());
        Path logFileAll = Paths.get(properties.getLogFileAll());
        deleteFile(logFile);
        deleteFile(logFileAll);
        deleteFile(logFileAll.getParent());
    }

    private void deleteFile(Path logFile) {
        FileSystemProvider provider = logFile.getFileSystem().provider();
        try {
            provider.deleteIfExists(logFile);
        } catch (IOException e) {
            //do nothing
        }
    }

    @Test
    public void testLoggerSakuli() throws Exception {
        Path sakuliLog = Paths.get(properties.getLogFile());
        System.out.println("SAKULI_LOG_PATH: " + sakuliLog.toFile().getAbsolutePath());

        assertLogging(sakuliLog);
    }

    @Test
    public void testLoggerAll() throws Exception {
        Path all = Paths.get(properties.getLogFileAll());
        System.out.println("ALL_LOG_PATH: " + all.toFile().getAbsolutePath());

        assertLogging(all);
    }

    private void assertLogging(Path logFile) throws IOException {
        String logMessage = "INFO-LOG-MESSAGE FOR " + testSuite.getId();
        logger.info(logMessage);

        String lastLine = getLastLineOfLogFile(logFile);
        assertEquals("INFO ", lastLine.substring(0, 5));
        assertTrue("Test for info log", lastLine.contains(logMessage));

        //Test-Warning-Log
        logMessage = "WARNING-LOG-MESSAGE FOR " + testSuite.getId();
        logger.warn(logMessage);
        lastLine = getLastLineOfLogFile(logFile);
        assertEquals("WARN ", lastLine.substring(0, 5));
        assertTrue("Test for warning log", lastLine.contains(logMessage));

        //Test-Debug-Log
        logMessage = "DEBUG-LOG-MESSAGE FOR " + testSuite.getId();
        logger.debug(logMessage);
        lastLine = getLastLineOfLogFile(logFile);
        assertEquals("DEBUG", lastLine.substring(0, 5));
        assertTrue("Test for warning log", lastLine.contains(logMessage));

        //Test-Error-Log
        logMessage = "ERROR-LOG-MESSAGE FOR " + testSuite.getId();
        logger.error(logMessage);
        lastLine = getLastLineOfLogFile(logFile);
        assertEquals("ERROR", lastLine.substring(0, 5));
        assertTrue(lastLine.contains(logMessage));
    }

}
