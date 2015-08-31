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
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.GearmanPropertiesTestHelper;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.sakuli.services.forwarder.gearman.model.PlaceholderMap;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.sakuli.services.forwarder.gearman.TextPlaceholder.*;
import static org.testng.Assert.assertEquals;

public class OutputBuilderTest {
    @Mock
    private TestSuite testSuite;
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private PerformanceDataBuilder performanceDataBuilder;
    @Spy
    @InjectMocks
    private ScreenshotDivConverter screenshotDivConverter;
    @Spy
    @InjectMocks
    private OutputBuilder testling;

    @DataProvider(name = "placeholder")
    public static Object[][] placeholder() {
        return new Object[][]{
                {null, null},
                {"empty {{}} test", "empty {{}} test"},
                {"test message " + STATE.getPattern() + " test", "test message OK test"},
                {STATE.getPattern() + " test", "OK test"},
                {STATE.getPattern() + " test-" + STATE.getPattern(), "OK test-OK"},
                {"test message " + STATE.getPattern(), "test message OK"},
                {"test message " + STATE.getPattern() + " with id \"" + ID.getPattern() + "\"", "test message OK with id \"sakuli-123\""},
        };
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(testling, "screenshotDivConverter", screenshotDivConverter);
    }

    @Test(dataProvider = "placeholder")
    public void testReplacePlaceHolder(String testMessage, String expectedMesaage) throws Exception {
        PlaceholderMap map = new PlaceholderMap();
        map.put(STATE, "OK");
        map.put(ID, "sakuli-123");
        assertEquals(OutputBuilder.replacePlaceHolder(testMessage, map), expectedMesaage);

        //test recusiv
        map.put(STATE, STATE_DESC.getPattern());
        map.put(STATE_DESC, "OK");
        assertEquals(OutputBuilder.replacePlaceHolder(testMessage, map), expectedMesaage);
    }


    @Test
    public void testBuild() throws Exception {
        GearmanPropertiesTestHelper.initMock(gearmanProperties);
        doReturn("STATUS").when(testling).getStatusSummary(testSuite, gearmanProperties);
        doReturn("PERFORMANCE").when(performanceDataBuilder).build();

        NagiosOutput result = testling.build();
        assertEquals(result.getStatusSummary(), "STATUS");
        assertEquals(result.getPerformanceData(), "PERFORMANCE");
    }

    @Test
    public void testFormatTestSuiteStateMessageUnknown() throws Exception {
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.RUNNING)
                .withStopDate(null)
                .buildExample();
        GearmanProperties propertiesExample = new GearmanPropertiesExampleBuilder().buildExample();

        String result = testling.formatTestSuiteSummaryStateMessage(testSuiteExample, propertiesExample);
        assertEquals(result, "UNKNOWN - [UNKN] Sakuli suite \"sakuli-123\" ran in -1.00 seconds - suite still running. (Last suite run: xx)");
    }

    @Test
    public void testFormatTestSuiteStateMessageOK() throws Exception {
        Date startDate = new Date();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.OK)
                .withStartDate(startDate)
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .buildExample();
        GearmanProperties propertiesExample = new GearmanPropertiesExampleBuilder().buildExample();

        String result = testling.formatTestSuiteSummaryStateMessage(testSuiteExample, propertiesExample);
        String lastRun = OutputBuilder.dateFormat.format(testSuiteExample.getStopDate());
        assertEquals(result, "OK - [OK] Sakuli suite \"sakuli-123\" ran in 120.00 seconds - ok. (Last suite run: " + lastRun + ")");
    }

    @Test
    public void testFormatTestSuiteStateMessageException() throws Exception {
        Date startDate = new Date();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.ERRORS)
                .withStartDate(startDate)
                .withException(new SakuliException("TEST-ERROR"))
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .buildExample();
        GearmanProperties propertiesExample = new GearmanPropertiesExampleBuilder().buildExample();

        String result = testling.formatTestSuiteSummaryStateMessage(testSuiteExample, propertiesExample);
        String lastRun = OutputBuilder.dateFormat.format(testSuiteExample.getStopDate());
        assertEquals(result, "CRITICAL - [CRIT] Sakuli suite \"sakuli-123\" ran in 120.00 seconds - EXCEPTION: \"TEST-ERROR\". (Last suite run: " + lastRun + ")");
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
        GearmanProperties propertiesExample = new GearmanPropertiesExampleBuilder().buildExample();

        String result = testling.formatTestSuiteTableStateMessage(testSuiteExample, propertiesExample);
        String lastRun = OutputBuilder.dateFormat.format(testSuiteExample.getStopDate());
        assertEquals(result, "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] Sakuli suite \"sakuli-123\" ran" +
                " in 120.00 seconds - EXCEPTION: \"TEST-ERROR\". (Last suite run: " + lastRun + ")</td></tr>");
    }

    @Test
    public void testFormatTestSuiteTableExceptionWithScreenshot() throws Exception {
        GearmanPropertiesTestHelper.initMock(gearmanProperties);
        Path screenshotPath = Paths.get(OutputBuilder.class.getResource("computer.png").toURI());
        Date startDate = new Date();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.ERRORS)
                .withStartDate(startDate)
                .withException(new SakuliExceptionWithScreenshot("TEST-ERROR", screenshotPath))
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .buildExample();

        String result = testling.formatTestSuiteTableStateMessage(testSuiteExample, gearmanProperties);
        String lastRun = OutputBuilder.dateFormat.format(testSuiteExample.getStopDate());

        final String separator = "<div";
        assertEquals(result.substring(0, result.indexOf(separator)), "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] Sakuli suite \"sakuli-123\" ran" +
                " in 120.00 seconds - EXCEPTION: \"TEST-ERROR\". (Last suite run: " + lastRun + ")");

        String start_1 = "<div style=\"width:640px\" id=\"sakuli_screenshot\">" +
                "<img style=\"width:98%;border:2px solid gray;display: block;margin-left:auto;margin-right:auto;margin-bottom:4px\" " +
                "src=\"";
        String start = start_1 + "data:image/png;base64,";
        String end = "></div></td></tr>";
        String substring = result.substring(result.indexOf(separator));
        assertEquals(substring.substring(0, start.length()), start);
        assertEquals(substring.substring(substring.length() - end.length()), end);

        //now check the remove function
        String resultWithOutBase64Data = ScreenshotDivConverter.removeBase64ImageDataString(substring);
        assertEquals(resultWithOutBase64Data, start_1 + "\" " + end);
    }

    @Test
    public void testFormatTestCaseTableStateMessage() throws Exception {
        String htmlTemplate = "<tr valign=\"top\"><td class=\"%s\">%s</td></tr>";
        GearmanProperties properties = GearmanPropertiesTestHelper.initMock(mock(GearmanProperties.class));

        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.OK)
                .withId("case-ok")
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties),
                String.format(htmlTemplate, "serviceOK", "[OK] case \"case-ok\" ran in 3.00s - ok"));

        Date startDate = new Date();
        testCase = new TestCaseExampleBuilder()
                .withId("case-warning")
                .withState(TestCaseState.WARNING)
                .withStartDate(startDate)
                .withStopDate(DateUtils.addMilliseconds(startDate, 5500))
                .withWarningTime(5)
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties),
                String.format(htmlTemplate, "serviceWARNING", "[WARN] case \"case-warning\" over runtime (5.50s /warning at 5s) "));

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
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties),
                String.format(htmlTemplate, "serviceWARNING", "[WARN] case \"case-warning\" over runtime (3.00s /warning in step at 4s) step \"step-name\" (3.15s /warn at 3s), step \"step-name2\" (0.15s /warn at 1s)"));

        testCase = new TestCaseExampleBuilder().withState(TestCaseState.CRITICAL)
                .withId("case-critical")
                .withStartDate(startDate)
                .withStopDate(DateUtils.addMilliseconds(startDate, 8888))
                .withCriticalTime(7)
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties),
                String.format(htmlTemplate, "serviceCRITICAL", "[CRIT] case \"case-critical\" over runtime (8.89s /critical at 7s) "));

        testCase = new TestCaseExampleBuilder().withState(TestCaseState.ERRORS)
                .withId("case-error")
                .withException(new SakuliException("EXCEPTION-MESSAGE"))
                .buildExample();
        assertEquals(testling.formatTestCaseTableStateMessage(testCase, properties),
                String.format(htmlTemplate, "serviceCRITICAL", "[CRIT] case \"case-error\" EXCEPTION: EXCEPTION-MESSAGE"));
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

        String lastRun = OutputBuilder.dateFormat.format(testSuite.getStopDate());
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

    @Test
    public void testGetTextPlaceHolderTestSuite() throws Exception {
        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withState(TestSuiteState.OK)
                .withTestSuiteFolder(Paths.get("folder"))
                .buildExample();
        final PlaceholderMap textPlaceholder = testling.getTextPlaceholder(testSuite);

        assertEquals(textPlaceholder.get(STATE), "OK");
        assertEquals(textPlaceholder.get(STATE_SHORT), "[OK]");
        assertEquals(textPlaceholder.get(STATE_DESC), "ok");
        assertEquals(textPlaceholder.get(SUITE_SUMMARY), "{{state_description}}");
        assertEquals(textPlaceholder.get(NAME), testSuite.getName());
        assertEquals(textPlaceholder.get(ID), testSuite.getId());
        assertEquals(textPlaceholder.get(DURATION), "120.00");
        assertEquals(textPlaceholder.get(START_DATE), OutputBuilder.dateFormat.format(testSuite.getStartDate()));
        assertEquals(textPlaceholder.get(STOP_DATE), OutputBuilder.dateFormat.format(testSuite.getStopDate()));
        assertEquals(textPlaceholder.get(WARN_THRESHOLD), "0");
        assertEquals(textPlaceholder.get(CRITICAL_THRESHOLD), "0");
        assertEquals(textPlaceholder.get(ERROR_MESSAGE), "");
        assertEquals(textPlaceholder.get(SUITE_FOLDER), "folder");
        assertEquals(textPlaceholder.get(HOST), "localhost");
        assertEquals(textPlaceholder.get(BROWSER_INFO), "firefox");
        assertEquals(textPlaceholder.get(TD_CSS_CLASS), "serviceOK");
        //assert empty fields
        assertEquals(textPlaceholder.get(CASE_FILE), "");
        assertEquals(textPlaceholder.get(CASE_LAST_URL), "");
        assertEquals(textPlaceholder.get(CASE_START_URL), "");
        assertEquals(textPlaceholder.get(STEP_INFORMATION), "");
    }

    @Test
    public void testGetTextPlaceHolderTestCase() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.OK)
                .withTestCaseFile(Paths.get("folder/_tc.js"))
                .buildExample();
        final PlaceholderMap textPlaceholder = testling.getTextPlaceholder(testCase);

        assertEquals(textPlaceholder.get(STATE), "OK");
        assertEquals(textPlaceholder.get(STATE_SHORT), "[OK]");
        assertEquals(textPlaceholder.get(STATE_DESC), "ok");
        assertEquals(textPlaceholder.get(NAME), testCase.getName());
        assertEquals(textPlaceholder.get(ID), testCase.getId());
        assertEquals(textPlaceholder.get(DURATION), "3.00");
        assertEquals(textPlaceholder.get(STOP_DATE), OutputBuilder.dateFormat.format(testCase.getStopDate()));
        assertEquals(textPlaceholder.get(START_DATE), OutputBuilder.dateFormat.format(testCase.getStartDate()));
        assertEquals(textPlaceholder.get(WARN_THRESHOLD), "4");
        assertEquals(textPlaceholder.get(CRITICAL_THRESHOLD), "5");
        assertEquals(textPlaceholder.get(ERROR_MESSAGE), "");
        assertEquals(textPlaceholder.get(CASE_FILE), String.format("folder%s_tc.js", File.separator));
        assertEquals(textPlaceholder.get(CASE_LAST_URL), "http://www.last-url.com");
        assertEquals(textPlaceholder.get(CASE_START_URL), "http://www.start-url.com");
        assertEquals(textPlaceholder.get(STEP_INFORMATION), "");
        assertEquals(textPlaceholder.get(TD_CSS_CLASS), "serviceOK");
        //assert empty fields
        assertEquals(textPlaceholder.get(SUITE_SUMMARY), "");
        assertEquals(textPlaceholder.get(SUITE_FOLDER), "");
        assertEquals(textPlaceholder.get(HOST), "");
        assertEquals(textPlaceholder.get(BROWSER_INFO), "");
    }
}