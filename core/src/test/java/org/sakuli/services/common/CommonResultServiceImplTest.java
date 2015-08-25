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

package org.sakuli.services.common;

import org.apache.commons.io.FileUtils;
import org.sakuli.LoggerTest;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.helper.TestCaseStepHelper;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class CommonResultServiceImplTest extends LoggerTest {

    private CommonResultServiceImpl testling = spy(new CommonResultServiceImpl());
    private TestSuite testSuite;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @BeforeMethod
    public void init() {
        super.init();
        doNothing().when(testling).cleanClipboard();
        testSuite = new TestSuiteExampleBuilder()
                .withId("LOG_TEST_SUITE").withState(TestSuiteState.ERRORS).withException(new SakuliException("TEST")).buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
    }

    @Test
    public void testSaveAllResults() throws Exception {
        Path logfile = Paths.get(properties.getLogFile());
        testling.saveAllResults();
        String lastLineOfLogFile = getLastLineOfLogFile(logfile, 42);
        List<String> regExes = Arrays.asList(
                "INFO.*",
                "=========== RESULT of SAKULI Testsuite \"LOG_TEST_SUITE\" - ERRORS =================",
                "test suite id: LOG_TEST_SUITE",
                "guid: LOG_TEST_SUITE.*",
                "name: Unit Test Sample Test Suite",
                "RESULT STATE: ERRORS",
                "result code: 6",
                "ERRORS:TEST",
                "db primary key: -1",
                "duration: 120.0 sec.",
                "warning time: 0 sec.",
                "critical time: 0 sec.",
                "start time: 17-08-2014 14:00:00",
                "end time: 17-08-2014 14:02:00",
                "db primary key of job table: -1",
                "browser: firefox",
                "\t======== test case \"UNIT_TEST.*\" ended with OK =================",
                "\ttest case id: UNIT_TEST_CASE.*",
                "\tname: Unit Test Case",
                "\tRESULT STATE: OK",
                "\tresult code: 0",
                "\tdb primary key: -1",
                "\tduration: 3.0 sec.",
                "\twarning time: 4 sec.",
                "\tcritical time: 5 sec.",
                "\tstart time: .*",
                "\tend time: .*",
                "\tstart URL: http://www.start-url.com",
                "\tlast URL: http://www.last-url.com",
                "\t\t======== test case step \"step_for_unit_test\" ended with OK =================",
                "\t\tname: step_for_unit_test",
                "\t\tRESULT STATE: OK",
                "\t\tresult code: 0",
                "\t\tdb primary key: -1*",
                "\t\tduration: 1.0 sec.",
                "\t\twarning time: 2 sec.",
                "\t\tstart time: .*",
                "\t\tend time: .*",
                "===========  SAKULI Testsuite \"LOG_TEST_SUITE\" execution FINISHED - ERRORS ======================",
                "",
                "ERROR .* ERROR-Summary:",
                "TEST.*");
        List<String> strings = Arrays.asList(lastLineOfLogFile.split("\n"));
        Iterator<String> regExIterator = regExes.iterator();
        for (String string : strings) {
            String regex = regExIterator.next();
            logger.debug("\nREGEX: {}\n |\n - STR: {}", regex, string);
            Assert.assertTrue(string.matches(regex));
        }
    }

    @Test
    public void testWriteCachedStepDefinitions() throws Exception {
        final String cacheFilePath = "valid/validTestCase/" + TestCaseStepHelper.SAKULI_STEPS_CACHE_FILE;
        if (this.getClass().getResource(cacheFilePath) != null) {
            Files.deleteIfExists(getResource(cacheFilePath, true));
        }

        Path tcFile = getResource("valid/validTestCase/_tc.js", true);
        testSuite.setState(TestSuiteState.ERRORS);
        TestCase tc = mock(TestCase.class);
        when(tc.getTcFile()).thenReturn(tcFile);
        when(tc.getSteps()).thenReturn(Arrays.asList(
                new TestCaseStepBuilder("step_ok").withState(TestCaseStepState.OK).build(),
                new TestCaseStepBuilder("step_not_started").build()
        ));
        testSuite.setTestCases(Collections.singletonMap("1", tc));

        //on error no cache file should be written
        testling.writeCachedStepDefinitions();
        assertNull(getResource(cacheFilePath, false));

        //on != error cache file should be written
        testSuite.setState(TestSuiteState.CRITICAL_IN_SUITE);
        testling.writeCachedStepDefinitions();
        Path cacheFile = getResource(cacheFilePath, true);
        assertTrue(Files.exists(cacheFile));
        assertEquals(FileUtils.readFileToString(cacheFile.toFile(), Charset.forName("UTF-8")),
                "step_ok\nstep_not_started\n");
    }

    protected Path getResource(String fileName, boolean exists) throws URISyntaxException {
        URL filePath = this.getClass().getResource(fileName);
        if (exists) {
            assertNotNull(filePath);
            return Paths.get(filePath.toURI());
        }
        assertNull(filePath);
        return null;
    }
}