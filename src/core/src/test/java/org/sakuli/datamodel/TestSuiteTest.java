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

package org.sakuli.datamodel;

import org.apache.commons.lang.time.DateUtils;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 17.07.13
 */
public class TestSuiteTest {

    @Test
    public void testRefreshState() throws Exception {
        TestSuite testling = new TestSuite();
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.RUNNING);

        testling = new TestSuite();
        testling.setState(TestSuiteState.RUNNING);
        testling.setCriticalTime(0);
        testling.setWarningTime(0);
        TestCase tcTestling = new TestCase("testRefreshStat", testling.getId());
        tcTestling.setState(TestCaseState.OK);
        Map<String, TestCase> testCaseMap = new HashMap<>();
        testCaseMap.put(tcTestling.getId(), tcTestling);
        ReflectionTestUtils.setField(testling, "testCases", testCaseMap);
        testling.refreshState();
        //as long as stop date is not set at every case the suite is still running
        assertEquals(testling.getState(), TestSuiteState.RUNNING);

        //no errors, stop date is set
        tcTestling.setStopDate(new Date());
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.OK);

        tcTestling.setState(TestCaseState.WARNING_IN_STEP);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.WARNING_IN_STEP);

        tcTestling.setState(TestCaseState.WARNING);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.WARNING_IN_CASE);

        tcTestling.setState(TestCaseState.CRITICAL);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.CRITICAL_IN_CASE);

        tcTestling.setState(TestCaseState.ERRORS);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.ERRORS);

        //prepare dates for warning and critical test
        testling.setState(TestSuiteState.RUNNING);
        tcTestling.setState(TestCaseState.OK);
        Date currentDate = new Date();
        ReflectionTestUtils.setField(testling, "startDate", new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;

        testling.setCriticalTime(10);
        testling.setWarningTime(8);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.OK);

        testling.setWarningTime(4);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.WARNING_IN_SUITE);

        testling.setCriticalTime(4);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.CRITICAL_IN_SUITE);
    }

    @Test
    public void testRefreshStateZeroWarning() throws Exception {
        TestSuite testling = new TestSuite();
        Date currentDate = new Date();
        Date currentDateMinus30 = new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(30));
        testling.setStartDate(currentDateMinus30);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.RUNNING);

        testling.stopDate = currentDate;
        testling.setCriticalTime(0);
        testling.setWarningTime(0);
        TestCase tcTestling = new TestCase("testRefreshStat", testling.getId());
        tcTestling.setState(TestCaseState.OK);
        tcTestling.setWarningTime(0);
        tcTestling.setCriticalTime(0);
        tcTestling.setStartDate(currentDateMinus30);
        tcTestling.setStopDate(currentDate);
        Map<String, TestCase> testCaseMap = new HashMap<>();
        testCaseMap.put(tcTestling.getId(), tcTestling);
        ReflectionTestUtils.setField(testling, "testCases", testCaseMap);
        testling.refreshState();
        //unexpectd form Time Sate have to be OK
        assertEquals(testling.getState(), TestSuiteState.OK);

        testling.setCriticalTime(15);
        testling.refreshState();
        assertEquals(testling.getState(), TestSuiteState.CRITICAL_IN_SUITE);
    }

    @Test
    public void testGetGuid() throws Exception {
        TestSuite ts = new TestSuite();
        ts.setId("001");
        ts.setStartDate(new Date());
        assertNotNull(ts.getGuid());

        TestSuite ts2 = new TestSuite();
        ts2.setId("001");
        ts2.setStartDate(DateUtils.addMilliseconds(new Date(), 1));
        assertNotNull(ts2.getGuid());

        assertNotEquals(ts.getGuid(), ts2.getGuid());


        ts2.setStartDate(new GregorianCalendar(2014, 9, 5, 13, 15, 10).getTime());
        ts.setStartDate(new GregorianCalendar(2014, 9, 5, 1, 15, 10).getTime());
        assertNotEquals(ts.getGuid(), ts2.getGuid());
        assertEquals(ts2.getGuid(), "001__2014_10_05_13_15_10_00");
        assertEquals(ts.getGuid(), "001__2014_10_05_01_15_10_00");
    }

    @Test
    public void testGetExceptionMessage() {
        TestSuite testSuite = new TestSuite();
        String message = "suite-exception";
        testSuite.addException(new SakuliException(message));
        assertEquals(testSuite.getExceptionMessages(), message);
        assertEquals(testSuite.getExceptionMessages(true), message);

        TestCase tc1 = new TestCase("case1", "case1");
        String messageCase = "case-exception";
        tc1.addException(new SakuliException(messageCase));
        testSuite.addTestCase(tc1.getId(), tc1);
        assertEquals(testSuite.getExceptionMessages(), message + "\n" + "CASE '" + tc1.getId() + "': " + messageCase);
        assertEquals(testSuite.getExceptionMessages(true), message + " -- CASE '" + tc1.getId() + "': " + messageCase);
    }

    @Test
    public void testGetExceptionMessageWithErrorInStep() {
        TestSuite testSuite = new TestSuite();
        String message = "suite-exception";
        testSuite.addException(new SakuliException(message));
        assertEquals(testSuite.getExceptionMessages(), message);
        assertEquals(testSuite.getExceptionMessages(true), message);

        TestCase tc1 = new TestCase("case1", "case1");
        String messageCase = "case-exception";
        tc1.addException(new SakuliException(messageCase));
        testSuite.addTestCase(tc1.getId(), tc1);

        TestCaseStep step1 = new TestCaseStepBuilder("step1").build();
        String messageStep = "step-exception";
        step1.addException(new SakuliException(messageStep));
        tc1.addStep(step1);

        assertEquals(testSuite.getExceptionMessages(),
                message + "\n" + "CASE '" + tc1.getId() + "': " + messageCase
                        + "\n\tSTEP '" + step1.getId() + "': " + messageStep
        );
        assertEquals(testSuite.getExceptionMessages(true),
                message + " -- CASE '" + tc1.getId() + "': " + messageCase
                        + " - STEP '" + step1.getId() + "': " + messageStep
        );

        tc1.exception = null;
        assertEquals(testSuite.getExceptionMessages(),
                message + "\n" + "CASE '" + tc1.getId() + "': "
                        + "\n\tSTEP '" + step1.getId() + "': " + messageStep
        );
        assertEquals(testSuite.getExceptionMessages(true),
                message + " -- CASE '" + tc1.getId() + "': "
                        + "STEP '" + step1.getId() + "': " + messageStep
        );

        testSuite.exception = null;
        assertEquals(testSuite.getExceptionMessages(),
                "CASE '" + tc1.getId() + "': "
                        + "\n\tSTEP '" + step1.getId() + "': " + messageStep
        );
        assertEquals(testSuite.getExceptionMessages(true),
                "CASE '" + tc1.getId() + "': "
                        + "STEP '" + step1.getId() + "': " + messageStep
        );
    }
}

