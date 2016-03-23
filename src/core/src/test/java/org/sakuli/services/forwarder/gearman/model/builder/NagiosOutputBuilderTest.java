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

import org.apache.commons.lang.time.DateUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.sakuli.services.forwarder.AbstractOutputBuilder;
import org.sakuli.services.forwarder.MonitoringPropertiesTestHelper;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class NagiosOutputBuilderTest {
    @Mock
    private TestSuite testSuite;
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private NagiosPerformanceDataBuilder nagiosPerformanceDataBuilder;
    @Mock
    private SakuliProperties sakuliProperties;
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
        MonitoringPropertiesTestHelper.initMock(gearmanProperties);
    }

    @Test
    public void testBuild() throws Exception {
        doReturn("STATUS").when(testling).getStatusSummary(testSuite, gearmanProperties);
        doReturn("PERFORMANCE").when(nagiosPerformanceDataBuilder).build();

        NagiosOutput result = testling.build();
        assertEquals(result.getStatusSummary(), "STATUS");
        assertEquals(result.getPerformanceData(), "PERFORMANCE");
    }

    @Test
    public void testFormatTestSuiteTableExceptionWithScreenshot() throws Exception {
        Path screenshotPath = Paths.get(NagiosOutputBuilder.class.getResource("computer.png").toURI());
        Date startDate = new Date();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.ERRORS)
                .withStartDate(startDate)
                .withException(new SakuliExceptionWithScreenshot("TEST-ERROR", screenshotPath))
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .buildExample();

        String result = testling.formatTestSuiteTableStateMessage(testSuiteExample, gearmanProperties.getTemplateSuiteTable());
        String lastRun = AbstractOutputBuilder.dateFormat.format(testSuiteExample.getStopDate());

        final String separator = "<div";
        assertEquals(result.substring(0, result.indexOf(separator)), "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] Sakuli suite \"sakuli-123\"" +
                " (120.00s) EXCEPTION: 'TEST-ERROR'. (Last suite run: " + lastRun + ")");

        String start_1 = "<div id=\"sakuli_screenshot\">" +
                "<div id=\"openModal\" class=\"modalDialog\">" +
                    "<a href=\"#close\" title=\"Close\" class=\"close\">Close X</a>" +
                    "<a href=\"#openModal\"><img class=\"screenshot\" src=\"";
        String start = start_1 + "data:image/png;base64,";
        String end = "</a></div></div></td></tr>";
        String substring = result.substring(result.indexOf(separator));
        assertEquals(substring.substring(0, start.length()), start);
        assertEquals(substring.substring(substring.length() - end.length()), end);

        //now check the remove function
        String resultWithOutBase64Data = ScreenshotDivConverter.removeBase64ImageDataString(substring);
        assertEquals(resultWithOutBase64Data, start_1 + "\" >" + end);
    }

    @Test
    public void testFormatTestSuiteTableException() throws Exception {
        Date startDate = new Date();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.ERRORS)
                .withStartDate(startDate)
                .withException(new SakuliException("TEST-ERROR"))
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .buildExample();

        String result = testling.formatTestSuiteTableStateMessage(testSuiteExample, gearmanProperties.getTemplateSuiteTable());
        String lastRun = AbstractOutputBuilder.dateFormat.format(testSuiteExample.getStopDate());
        assertEquals(result, "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] Sakuli suite \"sakuli-123\"" +
                " (120.00s) EXCEPTION: 'TEST-ERROR'. (Last suite run: " + lastRun + ")</td></tr>");
    }

    @Test
    public void testFormatTestCaseTableStateMessage() throws Exception {
        String htmlTemplate = "<tr valign=\"top\"><td class=\"%s\">%s</td></tr>";
        GearmanProperties properties = MonitoringPropertiesTestHelper.initMock(mock(GearmanProperties.class));

        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.OK)
                .withId("case-ok")
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())),
                String.format(htmlTemplate, "serviceOK", "[OK] case \"case-ok\" ran in 3.00s - ok"));

        Date startDate = new Date();
        testCase = new TestCaseExampleBuilder()
                .withId("case-warning")
                .withState(TestCaseState.WARNING)
                .withStartDate(startDate)
                .withStopDate(DateUtils.addMilliseconds(startDate, 5500))
                .withWarningTime(5)
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())),
                String.format(htmlTemplate, "serviceWARNING", "[WARN] case \"case-warning\" over runtime (5.50s/warn at 5s)"));

        testCase = new TestCaseExampleBuilder().withState(TestCaseState.WARNING_IN_STEP)
                .withId("case-warning")
                .withTestCaseSteps(Arrays.asList(new TestCaseStepExampleBuilder()
                                .withName("step-name")
                                .withState(TestCaseStepState.WARNING)
                                .withStartDate(startDate)
                                .withStopDate(DateUtils.addMilliseconds(startDate, 3154))
                                .withWarningTime(3)
                                .buildExample(),
                        new TestCaseStepExampleBuilder()
                                .withName("step-name2")
                                .withState(TestCaseStepState.WARNING)
                                .withStartDate(DateUtils.addMilliseconds(startDate, 4000))
                                .withStopDate(DateUtils.addMilliseconds(startDate, 4154))
                                .withWarningTime(1)
                                .buildExample()))
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())),
                String.format(htmlTemplate, "serviceWARNING",
                        "[WARN] case \"case-warning\" (3.00s) ok, step \"step-name\" over runtime (3.15s/warn at 3s), " +
                                "step \"step-name2\" over runtime (0.15s/warn at 1s)"));

        testCase = new TestCaseExampleBuilder().withState(TestCaseState.CRITICAL)
                .withId("case-critical")
                .withStartDate(startDate)
                .withStopDate(DateUtils.addMilliseconds(startDate, 8888))
                .withCriticalTime(7)
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())),
                String.format(htmlTemplate, "serviceCRITICAL", "[CRIT] case \"case-critical\" over runtime (8.89s/crit at 7s)"));

        testCase = new TestCaseExampleBuilder().withState(TestCaseState.ERRORS)
                .withId("case-error")
                .withException(new SakuliException("EXCEPTION-MESSAGE"))
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())),
                String.format(htmlTemplate, "serviceCRITICAL", "[CRIT] case \"case-error\" EXCEPTION: EXCEPTION-MESSAGE"));
    }

    @Test
    public void testFormatTestCaseTableStateMessageWithScreenshot() throws Exception {
        Path screenshotPath = Paths.get(NagiosOutputBuilder.class.getResource("computer.png").toURI());
        String htmlTemplate = "<tr valign=\"top\"><td class=\"%s\">%s<\\/td><\\/tr>";
        GearmanProperties properties = MonitoringPropertiesTestHelper.initMock(mock(GearmanProperties.class));

        TestCase testCase = new TestCaseExampleBuilder().withState(TestCaseState.ERRORS)
                .withId("case-error")
                .withException(new SakuliExceptionWithScreenshot("EXCEPTION-MESSAGE", screenshotPath))
                .buildExample();
        String regex = String.format(htmlTemplate, "serviceCRITICAL", "\\[CRIT\\] case \"case-error\" EXCEPTION: EXCEPTION-MESSAGE" +
                "<div.* src=\"data:image\\/png;base64,.*><\\/div>");
        BaseTest.assertRegExMatch(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())),
                regex);
    }

    @Test
    public void testFormatTestCaseTableStateMessageWithScreenshotTestCase() throws Exception {
        Path screenshotPath = Paths.get(NagiosOutputBuilder.class.getResource("computer.png").toURI());
        String htmlTemplate = "<tr valign=\"top\"><td class=\"%s\">%s<\\/td><\\/tr>";
        GearmanProperties properties = MonitoringPropertiesTestHelper.initMock(mock(GearmanProperties.class));

        TestCase testCase = new TestCaseExampleBuilder().withState(TestCaseState.ERRORS)
                .withId("case-error")
                .withException(new SakuliExceptionWithScreenshot("EXCEPTION-MESSAGE", screenshotPath))
                .withTestCaseSteps(Collections.singletonList(
                        new TestCaseStepExampleBuilder()
                                .withState(TestCaseStepState.ERRORS)
                                .withException(new SakuliExceptionWithScreenshot("STEP-EXCEPTION-MESSAGE", screenshotPath))
                                .buildExample()
                ))
                .buildExample();
        String regex = String.format(htmlTemplate,
                "serviceCRITICAL", "\\[CRIT\\] case \"case-error\" EXCEPTION: EXCEPTION-MESSAGE" +
                        " - STEP \"step_for_unit_test\": STEP-EXCEPTION-MESSAGE" +
                        "<div.* src=\"data:image\\/png;base64,.*><\\/div>" +
                        "<div.* src=\"data:image\\/png;base64,.*><\\/div>");
        BaseTest.assertRegExMatch(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())), regex);
    }

    @Test
    public void testFormatTestCaseTableStateMessageWithScreenshotOnylInTestCase() throws Exception {
        Path screenshotPath = Paths.get(NagiosOutputBuilder.class.getResource("computer.png").toURI());
        String htmlTemplate = "<tr valign=\"top\"><td class=\"%s\">%s<\\/td><\\/tr>";
        GearmanProperties properties = MonitoringPropertiesTestHelper.initMock(mock(GearmanProperties.class));

        TestCase testCase = new TestCaseExampleBuilder().withState(TestCaseState.ERRORS)
                .withId("case-error")
                .withTestCaseSteps(Collections.singletonList(
                        new TestCaseStepExampleBuilder()
                                .withState(TestCaseStepState.ERRORS)
                                .withException(new SakuliExceptionWithScreenshot("STEP-EXCEPTION-MESSAGE", screenshotPath))
                                .buildExample()
                ))
                .buildExample();
        String regex = String.format(htmlTemplate,
                "serviceCRITICAL", "\\[CRIT\\] case \"case-error\" EXCEPTION: " +
                        "STEP \"step_for_unit_test\": STEP-EXCEPTION-MESSAGE" +
                        "<div.* src=\"data:image\\/png;base64,.*><\\/div>");
        BaseTest.assertRegExMatch(testling.formatTestCaseTableStateMessage(testCase, properties.lookUpTemplate(testCase.getState())), regex);
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

        String lastRun = AbstractOutputBuilder.dateFormat.format(testSuite.getStopDate());
        String expectedHTML =
                "[OK] Sakuli suite \"TEST-SUITE-ID\" ok (120.00s). (Last suite run: " + lastRun + ")\\\\n" +
                        String.format(ScreenshotDiv.STYLE_TEMPLATE, testling.getOutputScreenshotDivWidth()) +
                        "<table style=\"border-collapse: collapse;\">" +
                        "<tr valign=\"top\">" +
                        "<td class=\"serviceOK\">[OK] Sakuli suite \"TEST-SUITE-ID\" ok (120.00s). (Last suite run: " + lastRun + ")" +
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