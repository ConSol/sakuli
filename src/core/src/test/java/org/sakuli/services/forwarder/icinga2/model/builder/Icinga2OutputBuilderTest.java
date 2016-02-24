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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.icinga2.Icinga2Properties;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
public class Icinga2OutputBuilderTest {

    @Mock
    private Icinga2Properties properties;
    @Mock
    private ScreenshotDivConverter screenshotDivConverter;
    @InjectMocks
    private Icinga2OutputBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(properties.getTemplateSuiteSummary()).thenReturn(
                "{{state_short}} Sakuli suite \"{{id}}\" ran in {{duration}} seconds - {{suite_summary}}. " +
                        "(Last suite run: {{stop_date}})");
    }

    @Test
    public void testBuild() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.OK)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(), "[OK] Sakuli suite \"TEST-SUITE-ID\" ran in 120.00 seconds - ok. (Last suite run: 17.08 14:02:00)");
    }

    @Test
    public void testBuildError() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.ERRORS)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.ERRORS)
                        .withException(new SakuliException("MY-TEST-ERROR"))
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(), "[CRIT] Sakuli suite \"TEST-SUITE-ID\" ran in 120.00 seconds - EXCEPTION: 'CASE \"TEST-CASE-ID\": MY-TEST-ERROR'. (Last suite run: 17.08 14:02:00)");
    }

    @Test
    public void testBuildWarningInStep() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.WARNING_IN_STEP)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .withState(TestCaseState.WARNING_IN_STEP)
                        .buildExample()))
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        Assert.assertEquals(testling.build(), "[WARN] Sakuli suite \"TEST-SUITE-ID\" ran in 120.00 seconds - warning in step: threshold 0s. (Last suite run: 17.08 14:02:00)");
    }
}