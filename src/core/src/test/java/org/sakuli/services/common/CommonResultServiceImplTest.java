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

import org.sakuli.LoggerTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class CommonResultServiceImplTest extends LoggerTest {

    private CommonResultServiceImpl testling = spy(new CommonResultServiceImpl());
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
        doNothing().when(testling).cleanClipboard();
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
        List<String> regExes = Arrays.asList(
                "INFO.*",
                "=========== RESULT of SAKULI Testsuite \"LOG_TEST_SUITE\" - " + testSuiteState + " =================",
                "test suite id: LOG_TEST_SUITE",
                "guid: LOG_TEST_SUITE.*",
                "name: Unit Test Sample Test Suite",
                "RESULT STATE: " + testSuiteState,
                "result code: " + testSuiteState.getErrorCode(),
                testSuiteState.isError() ? "ERRORS:TEST" : null,
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
                "\t\t======== test case step \"step_for_unit_test\" ended with " + stepState + " =================",
                "\t\tname: step_for_unit_test",
                "\t\tRESULT STATE: " + stepState,
                "\t\tresult code: " + stepState.getErrorCode(),
                "\t\tdb primary key: -1*",
                "\t\tduration: 1.0 sec.",
                "\t\twarning time: 2 sec.",
                "\t\tstart time: .*",
                "\t\tend time: .*",
                "===========  SAKULI Testsuite \"LOG_TEST_SUITE\" execution FINISHED - " + testSuiteState + " ======================",
                "",
                stateOutputRegex,
                testSuiteState.isError() ? "TEST.*" : null
        );

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

}