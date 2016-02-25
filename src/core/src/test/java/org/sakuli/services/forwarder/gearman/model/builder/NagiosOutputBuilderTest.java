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

package org.sakuli.services.forwarder.gearman.model.builder;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.services.forwarder.AbstractOutputBuilder;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.GearmanPropertiesTestHelper;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertEquals;

public class NagiosOutputBuilderTest {
    @Mock
    private TestSuite testSuite;
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private NagiosPerformanceDataBuilder nagiosPerformanceDataBuilder;
    @Spy
    @InjectMocks
    private ScreenshotDivConverter screenshotDivConverter;
    @Spy
    @InjectMocks
    private NagiosOutputBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(testling, "screenshotDivConverter", screenshotDivConverter);
    }

    @Test
    public void testBuild() throws Exception {
        GearmanPropertiesTestHelper.initMock(gearmanProperties);
        doReturn("STATUS").when(testling).getStatusSummary(testSuite, gearmanProperties);
        doReturn("PERFORMANCE").when(nagiosPerformanceDataBuilder).build();

        NagiosOutput result = testling.build();
        assertEquals(result.getStatusSummary(), "STATUS");
        assertEquals(result.getPerformanceData(), "PERFORMANCE");
    }

    @Test
    public void testGetStatusSummary() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.OK)
                .withId("TEST-SUITE-ID")
                .withTestCases(Collections.singletonList(new TestCaseExampleBuilder()
                        .withId("TEST-CASE-ID")
                        .buildExample()))
                .buildExample();
        GearmanProperties gearmanProperties = new GearmanPropertiesExampleBuilder().buildExample();

        String lastRun = AbstractOutputBuilder.dateFormat.format(testSuite.getStopDate());
        String expectedHTML =
                "OK - OK - [OK] Sakuli suite \"TEST-SUITE-ID\" ran in 120.00 seconds - ok. (Last suite run: " + lastRun + ")\\\\n" +
                        "<table style=\"border-collapse: collapse;\">" +
                        "<tr valign=\"top\">" +
                        "<td class=\"serviceOK\">[OK] Sakuli suite \"TEST-SUITE-ID\" ran in 120.00 seconds - ok. (Last suite run: " + lastRun + ")" +
                        "</td>" +
                        "</tr>" +
                        "<tr valign=\"top\">" +
                        "<td class=\"serviceOK\">[OK] case \"TEST-CASE-ID\" ran in 3.00s - ok</td>" +
                        "</tr>" +
                        "</table>";


        String statusSummary = testling.getStatusSummary(testSuite, gearmanProperties);
        assertEquals(statusSummary, expectedHTML);

        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        ReflectionTestUtils.setField(testling, "gearmanProperties", gearmanProperties);
        NagiosOutput output = testling.build();
        String substringStatusSummary = output.getOutputString().substring(0, output.getOutputString().indexOf("|"));
        assertEquals(substringStatusSummary, expectedHTML);
    }

}