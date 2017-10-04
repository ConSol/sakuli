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
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author tschneck
 *         Date: 19.07.13
 */
public class TestCaseTest {
    private TestCase testling;
    private TestCaseStep stepTestling;

    @BeforeMethod
    public void setUp() throws Exception {
        testling = new TestCase("testling", "123");
        stepTestling = new TestCaseStep();
    }

    @Test
    public void testAddStep() throws Exception {
        testling.addStep(stepTestling);
        Assert.assertNotNull(testling.getSteps().get(testling.getSteps().size() - 1));
    }

    @Test
    public void testRefreshState() throws Exception {
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.OK);

        stepTestling.setState(TestCaseStepState.OK);
        testling.addStep(stepTestling);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.OK);

        Date currentDate = new Date();
        testling.setStartDate(new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;
        testling.setWarningTime(6);
        testling.setCriticalTime(6);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.OK);

        stepTestling.setState(TestCaseStepState.WARNING);
        stepTestling.setStartDate(currentDate);
        stepTestling.setStopDate(DateUtils.addSeconds(currentDate, 5));
        stepTestling.setWarningTime(4);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.WARNING_IN_STEP);

        testling.setWarningTime(4);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.WARNING);

        testling.setCriticalTime(4);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.CRITICAL);

        testling.setCriticalTime(5);
        testling.setCriticalTime(5);
        testling.refreshState();
        //should be still critical, because only higher values will be set
        assertEquals(testling.getState(), TestCaseState.CRITICAL);

    }

    @Test
    public void testRefreshStateWithZeroWarningTime() throws Exception {
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.OK);

        stepTestling.setState(TestCaseStepState.OK);
        testling.addStep(stepTestling);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.OK);

        Date currentDate = new Date();
        testling.setStartDate(new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;
        testling.setWarningTime(0);
        testling.setCriticalTime(0);
        testling.refreshState();
        assertEquals(testling.getState(), TestCaseState.OK);
    }


    @Test
    public void testGetAndResetTestActions() throws Exception {
        testling.addAction(TestAction.createSakuliTestAction("TestCaseAction", "getIdFromPath", null, "convert the path of the test case file to a valid test case ID", "docu"));
        testling.addAction(TestAction.createSahiTestAction("_highlight(_link(\\\"SSL Manager\\\"));", "docu"));
        List<TestAction> testActions = testling.getTestActions();
        assertNotNull(testActions);
        assertEquals(testActions.size(), 2);
        testActions = testling.getAndResetTestActions();
        assertNotNull(testActions);
        assertEquals(testActions.size(), 2);
        assertEquals(testling.getTestActions().size(), 0);
    }

}
