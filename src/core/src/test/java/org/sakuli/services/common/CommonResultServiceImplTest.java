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

import org.mockito.Mock;
import org.sakuli.LoggerTest;
import org.sakuli.builder.*;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.*;
import org.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Mockito.*;

public class CommonResultServiceImplTest extends LoggerTest {

    @Mock
    private SakuliProperties sakuliProperties;

    private CommonResultServiceImpl testling;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @DataProvider(name = "states")
    public static Object[][] states() {
        return new Object[][]{
                {TestSuiteState.ERRORS, TestCaseState.WARNING, "ERROR .* ERROR:"},
                {TestSuiteState.WARNING_IN_CASE, TestCaseState.WARNING, "WARN .* WARNING_IN_CASE: Unit Test Case"},
                {TestSuiteState.CRITICAL_IN_CASE, TestCaseState.CRITICAL, "WARN .* CRITICAL_IN_CASE: Unit Test Case"},
                {TestSuiteState.WARNING_IN_STEP, TestCaseState.WARNING_IN_STEP, "WARN .* WARNING_IN_STEP: Unit Test Case -\\> step_for_unit_test"}
        };
    }

    @Override
    @BeforeMethod
    public void init() {
        super.init();
        testling = spy(new CommonResultServiceImpl(sakuliProperties));
        doNothing().when(testling).cleanUp();
    }

    @Test(dataProvider = "states")
    public void testSaveAllResults(TestSuiteState testSuiteState, TestCaseState testCaseState, String stateOutputRegex) throws Exception {
        TestCaseStepState stepState = TestCaseStepState.WARNING;
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withId("LOG_TEST_SUITE").withState(testSuiteState)
                .withException(testSuiteState.isError() ? new SakuliException("TEST") : null)
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                                .withTestCaseSteps(Collections.singletonList(new TestCaseStepExampleBuilder().withState(stepState).buildExample()))
                                .withState(testCaseState)
                                .buildExample()
                ))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Path logfile = Paths.get(properties.getLogFile());
        testling.saveAllResults();
        String lastLineOfLogFile = getLastLineOfLogFile(logfile, testSuiteState.isError() ? 42 : 39);
        List<String> regExes = getValidationExpressions(testSuiteState, testCaseState, stepState, stateOutputRegex, "TEST");

        List<String> strings = Arrays.asList(lastLineOfLogFile.split("\n"));
        Iterator<String> regExIterator = regExes.iterator();
        for (String string : strings) {
            String regex = regExIterator.next();
            while (regex == null && regExIterator.hasNext()) {
                regex = regExIterator.next();
            }
            logger.debug("\nREGEX: {}\n |\n - STR: {}", regex, string);
            assertRegExMatch(string, regex);
        }
    }

    @Test
    public void testSaveAllResultsWithErrorFormat() throws Exception {
        reset(sakuliProperties);
        when(sakuliProperties.getLogExceptionFormat()).thenReturn("TypeError(.*)");

        TestCaseStepState stepState = TestCaseStepState.WARNING;
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withId("LOG_TEST_SUITE").withState(TestSuiteState.ERRORS)
                .withException(new SakuliException("Something went wrong!\n"+
                        "Now some important information:\n" +
                        "TypeError el is undefined\n" +
                        "Some details are here ..."))
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withTestCaseSteps(Collections.singletonList(new TestCaseStepExampleBuilder().withState(stepState).buildExample()))
                        .withState(TestCaseState.WARNING)
                        .buildExample()
                ))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Path logfile = Paths.get(properties.getLogFile());
        testling.saveAllResults();
        String lastLineOfLogFile = getLastLineOfLogFile(logfile, 42);
        List<String> regExes = getValidationExpressions(TestSuiteState.ERRORS, TestCaseState.WARNING, stepState, "ERROR .* ERROR:", "el is undefined");

        List<String> strings = Arrays.asList(lastLineOfLogFile.split("\n"));
        Iterator<String> regExIterator = regExes.iterator();
        for (String string : strings) {
            String regex = regExIterator.next();
            while (regex == null && regExIterator.hasNext()) {
                regex = regExIterator.next();
            }
            logger.debug("\nREGEX: {}\n |\n - STR: {}", regex, string);
            assertRegExMatch(string, regex);
        }
    }

    @Test
    public void testSaveAllResultsWithErrorFormatNotMatching() throws Exception {
        reset(sakuliProperties);
        when(sakuliProperties.getLogExceptionFormat()).thenReturn("TypeError(.*)");

        TestCaseStepState stepState = TestCaseStepState.WARNING;
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withId("LOG_TEST_SUITE").withState(TestSuiteState.ERRORS)
                .withException(new SakuliException("Something went wrong!"))
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withTestCaseSteps(Collections.singletonList(new TestCaseStepExampleBuilder().withState(stepState).buildExample()))
                        .withState(TestCaseState.WARNING)
                        .buildExample()
                ))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Path logfile = Paths.get(properties.getLogFile());
        testling.saveAllResults();
        String lastLineOfLogFile = getLastLineOfLogFile(logfile, 42);
        List<String> regExes = getValidationExpressions(TestSuiteState.ERRORS, TestCaseState.WARNING, stepState, "ERROR .* ERROR:", "Something went wrong!");

        List<String> strings = Arrays.asList(lastLineOfLogFile.split("\n"));
        Iterator<String> regExIterator = regExes.iterator();
        for (String string : strings) {
            String regex = regExIterator.next();
            while (regex == null && regExIterator.hasNext()) {
                regex = regExIterator.next();
            }
            logger.debug("\nREGEX: {}\n |\n - STR: {}", regex, string);
            assertRegExMatch(string, regex);
        }
    }

    private List<String> getValidationExpressions(TestSuiteState testSuiteState, TestCaseState testCaseState, TestCaseStepState testCaseStepState, String stateOutputRegex, String errorMessage) {
        return Arrays.asList(
                "INFO.*",
                "=========== RESULT of SAKULI Testsuite \"LOG_TEST_SUITE\" - " + testSuiteState + " =================",
                "test suite id: LOG_TEST_SUITE",
                "guid: LOG_TEST_SUITE.*",
                "name: Unit Test Sample Test Suite",
                "RESULT STATE: " + testSuiteState,
                "result code: " + testSuiteState.getErrorCode(),
                testSuiteState.isError() ? "ERRORS:" + errorMessage : null,
                "db primary key: -1",
                "duration: 120.0 sec.",
                "warning time: 0 sec.",
                "critical time: 0 sec.",
                "start time: 17-08-2014 14:00:00",
                "end time: 17-08-2014 14:02:00",
                "db primary key of job table: -1",
                "browser: firefox",
                "\t======== test case \"UNIT_TEST.*\" ended with " + testCaseState + " =================",
                "\ttest case id: UNIT_TEST_CASE.*",
                "\tname: Unit Test Case",
                "\tRESULT STATE: " + testCaseState,
                "\tresult code: " + testCaseState.getErrorCode(),
                "\tdb primary key: -1",
                "\tduration: 3.0 sec.",
                "\twarning time: 4 sec.",
                "\tcritical time: 5 sec.",
                "\tstart time: .*",
                "\tend time: .*",
                "\tstart URL: http://www.start-url.com",
                "\tlast URL: http://www.last-url.com",
                "\t\t======== test case step \"step_for_unit_test\" ended with " + testCaseStepState + " =================",
                "\t\tname: step_for_unit_test",
                "\t\tRESULT STATE: " + testCaseStepState,
                "\t\tresult code: " + testCaseStepState.getErrorCode(),
                "\t\tdb primary key: -1*",
                "\t\tduration: 3.0 sec.",
                "\t\twarning time: 4 sec.",
                "\t\tstart time: .*",
                "\t\tend time: .*",
                "===========  SAKULI Testsuite \"LOG_TEST_SUITE\" execution FINISHED - " + testSuiteState + " ======================",
                "",
                stateOutputRegex,
                testSuiteState.isError() ? errorMessage + ".*" : null
        );
    }

}