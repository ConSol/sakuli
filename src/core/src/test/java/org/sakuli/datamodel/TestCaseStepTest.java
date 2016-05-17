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


import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.exceptions.SakuliException;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 19.07.13
 */
public class TestCaseStepTest {
    @Test
    public void testRefreshState() throws Exception {
        TestCaseStep testling = new TestCaseStep();
        testling.refreshState();
        assertEquals(TestCaseStepState.INIT, testling.getState());

        Date currentDate = new Date();
        testling.setStartDate(new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;

        testling.refreshState();
        assertEquals(TestCaseStepState.OK, testling.getState());

        testling.setWarningTime(4);
        testling.refreshState();
        assertEquals(TestCaseStepState.WARNING, testling.getState());
    }

    @Test
    public void testRefreshStateZeroWarningTime() throws Exception {
        TestCaseStep testling = new TestCaseStep();
        testling.refreshState();
        assertEquals(TestCaseStepState.INIT, testling.getState());

        testling.setWarningTime(-1);
        Date currentDate = new Date();
        testling.setStartDate(new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;

        testling.refreshState();
        assertEquals(TestCaseStepState.OK, testling.getState());
    }

    @Test
    public void testSetName() throws Exception {
        TestCaseStep testCaseStep = new TestCaseStep();
        testCaseStep.setName("example step with spaces");
        assertEquals(testCaseStep.getName(), "example_step_with_spaces");
        assertEquals(testCaseStep.getId(), "example_step_with_spaces");
    }

    @Test
    public void testSetId() throws Exception {
        TestCaseStep testCaseStep = new TestCaseStep();
        testCaseStep.setId("example step with spaces");
        assertEquals(testCaseStep.getName(), "example_step_with_spaces");
        assertEquals(testCaseStep.getId(), "example_step_with_spaces");
    }

    @Test
    public void testGetExceptionMessage() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder().withException(new SakuliException("CASE-EXCEPTION")).buildExample();
        assertEquals(testCase.getExceptionMessages(true, Collections.emptyMap()), "CASE-EXCEPTION");
        assertEquals(testCase.getExceptionMessages(false, Collections.emptyMap()), "CASE-EXCEPTION");

        testCase.setSteps(Collections.singletonList(
                new TestCaseStepExampleBuilder()
                        .withException(new SakuliException("STEP-EXCEPTION"))
                        .buildExample()));
        assertEquals(testCase.getExceptionMessages(true, Collections.emptyMap()), "CASE-EXCEPTION - STEP \"step_for_unit_test\": STEP-EXCEPTION");
        assertEquals(testCase.getExceptionMessages(false, Collections.emptyMap()), "CASE-EXCEPTION\n\tSTEP \"step_for_unit_test\": STEP-EXCEPTION");

    }
}
