/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.forwarder.icinga2.model.builder;

import org.apache.commons.lang.time.DateUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.services.forwarder.MonitoringPropertiesTestHelper;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.icinga2.Icinga2Properties;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Date;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
public class Icinga2OutputBuilderTest {

    @Mock
    private Icinga2Properties properties;
    @Mock
    private ScreenshotDivConverter screenshotDivConverter;
    @Mock
    private SakuliProperties sakuliProperties;
    @InjectMocks
    private Icinga2OutputBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        MonitoringPropertiesTestHelper.initMonitoringMock(properties);
    }

    @Test
    public void testBuildOk() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.OK)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[OK] Sakuli suite \"TEST-SUITE-ID\" ok (120.00s). (Last suite run: 17.08.14 14:02:00)\n" +
                        "[OK] case \"TEST-CASE-ID\" ran in 3.00s - ok");
    }

    @Test
    public void testBuildErrorInSuite() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.ERRORS)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.OK)
                        .buildExample()))
                .withException(new SakuliException("MY-TEST-ERROR-SUITE"))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[CRIT] Sakuli suite \"TEST-SUITE-ID\" (120.00s) EXCEPTION: 'MY-TEST-ERROR-SUITE'. (Last suite run: 17.08.14 14:02:00)\n" +
                        "[OK] case \"TEST-CASE-ID\" ran in 3.00s - ok");
    }

    @Test
    public void testBuildErrorInCase() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.ERRORS)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.ERRORS)
                        .withException(new SakuliException("MY-TEST-ERROR-CASE"))
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[CRIT] Sakuli suite \"TEST-SUITE-ID\" (120.00s) EXCEPTION: 'CASE \"TEST-CASE-ID\": MY-TEST-ERROR-CASE'. (Last suite run: 17.08.14 14:02:00)\n" +
                        "[CRIT] case \"TEST-CASE-ID\" EXCEPTION: MY-TEST-ERROR-CASE");
    }

    @Test
    public void testBuildErrorInCaseMaxLength() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.ERRORS)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.ERRORS)
                        .withException(new SakuliException("MY-TEST-ERROR-CASE xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"))
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[CRIT] Sakuli suite \"TEST-SUITE-ID\" (120.00s) EXCEPTION: 'CASE \"TEST-CASE-ID\": MY-TEST-ERROR-CASE " +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx ...\n" +
                        "[CRIT] case \"TEST-CASE-ID\" EXCEPTION: MY-TEST-ERROR-CASE " +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }

    @Test
    public void testBuildErrorInStep() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.ERRORS)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.ERRORS)
                        .withTestCaseSteps(Collections.singletonList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.ERRORS)
                                        .withException(new SakuliException("MY-TEST-ERROR-IN-STEP"))
                                        .buildExample()
                        ))
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[CRIT] Sakuli suite \"TEST-SUITE-ID\" (120.00s) EXCEPTION: 'CASE \"TEST-CASE-ID\": STEP \"step_for_unit_test\": MY-TEST-ERROR-IN-STEP'. (Last suite run: 17.08.14 14:02:00)\n" +
                        "[CRIT] case \"TEST-CASE-ID\" EXCEPTION: STEP \"step_for_unit_test\": MY-TEST-ERROR-IN-STEP");
    }

    @Test
    public void testBuildCritical() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.CRITICAL_IN_SUITE)
                .withCriticalTime(100)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.OK)
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[CRIT] Sakuli suite \"TEST-SUITE-ID\" critical (120.00s/crit at 100s). (Last suite run: 17.08.14 14:02:00)\n" +
                        "[OK] case \"TEST-CASE-ID\" ran in 3.00s - ok");
    }

    @Test
    public void testBuildCriticalInCase() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.CRITICAL_IN_CASE)
                .withCriticalTime(140)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.CRITICAL)
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[CRIT] Sakuli suite \"TEST-SUITE-ID\" critical in case, case \"Unit Test Case\" over runtime (3.00s/crit at 5s). (Last suite run: 17.08.14 14:02:00)\n" +
                        "[CRIT] case \"TEST-CASE-ID\" over runtime (3.00s/crit at 5s)");
    }

    @Test
    public void testBuildWarningInSuite() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.WARNING_IN_SUITE)
                .withId("TEST-SUITE-ID")
                .withWarningTime(100)
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.OK)
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[WARN] Sakuli suite \"TEST-SUITE-ID\" warning (120.00s/warn at 100s). (Last suite run: 17.08.14 14:02:00)\n" +
                        "[OK] case \"TEST-CASE-ID\" ran in 3.00s - ok");
    }

    @Test
    public void testBuildWarningInStep() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.WARNING_IN_STEP)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withStopDate(DateUtils.addSeconds(new Date(), 4))
                        .withState(TestCaseState.WARNING_IN_STEP)
                        .withWarningTime(5)
                        .withTestCaseSteps(Collections.singletonList(
                                new TestCaseStepExampleBuilder()
                                        .withName("TEST-STEP-ID")
                                        .withWarningTime(2)
                                        .withState(TestCaseStepState.WARNING)
                                        .buildExample()
                        ))
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[WARN] Sakuli suite \"TEST-SUITE-ID\" warning in step, step \"TEST-STEP-ID\" over runtime (3.00s/warn at 2s). (Last suite run: 17.08.14 14:02:00)\n" +
                        "[WARN] case \"TEST-CASE-ID\" (4.00s) ok, step \"TEST-STEP-ID\" over runtime (3.00s/warn at 2s)");
    }

    @Test
    public void testBuildWarningInCase() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.WARNING_IN_CASE)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.WARNING)
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(),
                "[WARN] Sakuli suite \"TEST-SUITE-ID\" warning in case, case \"Unit Test Case\" over runtime (3.00s/warn at 4s). (Last suite run: 17.08.14 14:02:00)\n" +
                        "[WARN] case \"TEST-CASE-ID\" over runtime (3.00s/warn at 4s)");
    }
}