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

package de.consol.sakuli.services.receiver.gearman;

import de.consol.sakuli.builder.TestCaseExampleBuilder;
import de.consol.sakuli.builder.TestCaseStepExampleBuilder;
import de.consol.sakuli.builder.TestSuiteExampleBuilder;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliActionException;
import org.apache.commons.lang.time.DateUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import static de.consol.sakuli.datamodel.state.TestSuiteState.OK;
import static org.mockito.Mockito.when;

public class GearmanResultServiceImplTest {

    @InjectMocks
    private GearmanResultServiceImpl testling;
    @Mock
    private GearmanProperties properties;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        properties = GearmanPropertiesTestHelper.initMock(properties);
    }

    @Test(enabled = false)
    public void testSaveAllResults() throws Exception {
        when(properties.getServiceType()).thenReturn("passive");
        when(properties.getServerQueue()).thenReturn("check_results");
        when(properties.getServerHost()).thenReturn("99.99.99.20");
        when(properties.getServerPort()).thenReturn(4730);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");

        Date stopDate = new GregorianCalendar(2014, 14, 7, 13, 0).getTime();

        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withHost("win7sakuli")
                .withId("sakuli_demo22")
                .withState(OK)
                .withTestCases(Arrays.asList(
                        new TestCaseExampleBuilder()
                                .withId("ok_case")
                                .withStartDate(DateUtils.addSeconds(stopDate, -12))
                                .withStopDate(stopDate)
                                .withWarningTime(13)
                                .withCriticalTime(20)
                                .buildExample()))
                .buildExample();

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("warn_case")
                .withStartDate(DateUtils.addSeconds(stopDate, -30))
                .withStopDate(stopDate)
                .withWarningTime(20)
                .withCriticalTime(40)
                .buildExample());

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("warn_in_step")
                .withStartDate(DateUtils.addSeconds(stopDate, -30))
                .withStopDate(stopDate)
                .withWarningTime(40)
                .withCriticalTime(50)
                .withTestCaseSteps(Arrays.asList(new TestCaseStepExampleBuilder()
                                .withName("warn_step_1")
                                .withStartDate(DateUtils.addSeconds(stopDate, -29))
                                .withStopDate(DateUtils.addSeconds(stopDate, -20))
                                .withWarningTime(5)
                                .buildExample(),
                        new TestCaseStepExampleBuilder()
                                .withName("warn_step_2")
                                .withStartDate(DateUtils.addSeconds(stopDate, -19))
                                .withStopDate(DateUtils.addSeconds(stopDate, -10))
                                .withWarningTime(5)
                                .buildExample()))
                .buildExample());

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("crit_case")
                .withStartDate(DateUtils.addSeconds(stopDate, -14))
                .withStopDate(stopDate)
                .withWarningTime(10)
                .withCriticalTime(13)
                .buildExample());

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("error_case")
                .withStartDate(DateUtils.addSeconds(stopDate, -14))
                .withException(new SakuliActionException("exception test message"))
                .buildExample());

        ReflectionTestUtils.setField(testling, "testSuite", testSuite);

        testling.saveAllResults();
    }
}