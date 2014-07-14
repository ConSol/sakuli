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

package de.consol.sakuli.datamodel;


import de.consol.sakuli.datamodel.state.TestCaseState;
import de.consol.sakuli.datamodel.state.TestCaseStepState;
import org.apache.commons.lang.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        Assert.assertEquals(TestCaseState.OK, testling.getState());

        stepTestling.setState(TestCaseStepState.OK);
        testling.addStep(stepTestling);
        testling.refreshState();
        Assert.assertEquals(TestCaseState.OK, testling.getState());

        Date currentDate = new Date();
        testling.setStartDate(new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;
        testling.setWarningTime(6);
        testling.setCriticalTime(6);
        testling.refreshState();
        Assert.assertEquals(TestCaseState.OK, testling.getState());

        stepTestling.setState(TestCaseStepState.WARNING);
        stepTestling.setStartDate(currentDate);
        stepTestling.setStopDate(DateUtils.addSeconds(currentDate, 5));
        stepTestling.setWarningTime(4);
        testling.refreshState();
        Assert.assertEquals(TestCaseState.WARNING_IN_STEP, testling.getState());

        testling.setWarningTime(4);
        testling.refreshState();
        Assert.assertEquals(TestCaseState.WARNING, testling.getState());

        testling.setCriticalTime(4);
        testling.refreshState();
        Assert.assertEquals(TestCaseState.CRITICAL, testling.getState());

        testling.setCriticalTime(5);
        testling.setCriticalTime(5);
        testling.refreshState();
        //should be still critical, because only higher values will be set
        Assert.assertEquals(TestCaseState.CRITICAL, testling.getState());


    }

}
