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

package org.sakuli.services.forwarder;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.sakuli.services.forwarder.gearman.TextPlaceholder.*;
import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
public class AbstractOutputBuilderTest {
    @Mock
    private AbstractMonitoringTemplateProperties properties = new GearmanProperties();
    @Spy
    @InjectMocks
    private AbstractOutputBuilder testling = new AbstractOutputBuilder() {

        @Override
        protected int getSummaryMaxLength() {
            return 150;
        }

        @Override
        protected String getOutputScreenshotDivWidth() {
            return "640px";
        }
    };

    @Spy
    @InjectMocks
    private ScreenshotDivConverter screenshotDivConverter;


    @DataProvider(name = "placeholder")
    public static Object[][] placeholder() {
        return new Object[][]{
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
        MonitoringPropertiesTestHelper.initMonitoringMock(properties);
    }

    @Test(dataProvider = "placeholder")
    public void testReplacePlaceHolder(String testMessage, String expectedMesaage) throws Exception {
        PlaceholderMap map = new PlaceholderMap();
        map.put(STATE, "OK");
        map.put(ID, "sakuli-123");
        assertEquals(AbstractOutputBuilder.replacePlaceHolder(testMessage, map), expectedMesaage);

        //test recusiv
        map.put(STATE, STATE_DESC.getPattern());
        map.put(STATE_DESC, "OK");
        assertEquals(AbstractOutputBuilder.replacePlaceHolder(testMessage, map), expectedMesaage);
    }

    @Test(expectedExceptions = SakuliRuntimeException.class)
    public void testPlaceholderNull() throws Exception {
        AbstractOutputBuilder.replacePlaceHolder(null, null);
    }

    @Test
    public void testCutTo() throws Exception {
        assertEquals(AbstractOutputBuilder.cutTo("xxxxxxxx", 50), "xxxxxxxx");
        assertEquals(AbstractOutputBuilder.cutTo("xxxxxxxx", 5), "xxxxx ...");
        assertEquals(AbstractOutputBuilder.cutTo(null, 5), null);
    }

    @Test
    public void testFormatTestSuiteStateMessageUnknown() throws Exception {
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withState(TestSuiteState.RUNNING)
                .withStopDate(null)
                .buildExample();

        String result = testling.formatTestSuiteSummaryStateMessage(testSuiteExample, properties.getTemplateSuiteSummary());
        assertEquals(result, "[UNKN] Sakuli suite \"sakuli-123\" suite still running. (Last suite run: xx)");
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

        String result = testling.formatTestSuiteSummaryStateMessage(testSuiteExample, properties.getTemplateSuiteSummary());
        String lastRun = AbstractOutputBuilder.dateFormat.format(testSuiteExample.getStopDate());
        assertEquals(result, "[OK] Sakuli suite \"sakuli-123\" ok (120.00s). (Last suite run: " + lastRun + ")");
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

        String result = testling.formatTestSuiteSummaryStateMessage(testSuiteExample, properties.getTemplateSuiteSummary());
        String lastRun = AbstractOutputBuilder.dateFormat.format(testSuiteExample.getStopDate());
        assertEquals(result, "[CRIT] Sakuli suite \"sakuli-123\" (120.00s) EXCEPTION: 'TEST-ERROR'. (Last suite run: " + lastRun + ")");
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
        assertEquals(textPlaceholder.get(SUITE_SUMMARY), "{{state_description}} ({{duration}}s)");
        assertEquals(textPlaceholder.get(NAME), testSuite.getName());
        assertEquals(textPlaceholder.get(ID), testSuite.getId());
        assertEquals(textPlaceholder.get(DURATION), "120.00");
        assertEquals(textPlaceholder.get(START_DATE), AbstractOutputBuilder.dateFormat.format(testSuite.getStartDate()));
        assertEquals(textPlaceholder.get(STOP_DATE), AbstractOutputBuilder.dateFormat.format(testSuite.getStopDate()));
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
        assertEquals(textPlaceholder.get(STOP_DATE), AbstractOutputBuilder.dateFormat.format(testCase.getStopDate()));
        assertEquals(textPlaceholder.get(START_DATE), AbstractOutputBuilder.dateFormat.format(testCase.getStartDate()));
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

    @DataProvider
    public Object[][] testGenerateTestCaseStepInformationDP() {
        return new Object[][] {
                {new TestCaseStep[]{}, ""},
                {new TestCaseStep[]{createTestCaseStep(null, new DateTime(), TestCaseStepState.WARNING)},
                        ", step \"step_for_unit_test\" over runtime (3.00s/warn at 4s)"},
                {new TestCaseStep[]{createTestCaseStep(null, new DateTime(), TestCaseStepState.WARNING),
                        createTestCaseStep("step_2", new DateTime().plusSeconds(1), TestCaseStepState.WARNING)},
                        ", step \"step_for_unit_test\" over runtime (3.00s/warn at 4s), step \"step_2\" over runtime (3.00s/warn at 4s)"},
                {new TestCaseStep[]{createTestCaseStep("ok1", new DateTime(), TestCaseStepState.OK),
                        createTestCaseStep("ok2", new DateTime().plusSeconds(1), TestCaseStepState.OK)},
                        ""},
        };
    }

    @Test(dataProvider = "testGenerateTestCaseStepInformationDP")
    public void testGenerateTestCaseStepInformation(TestCaseStep[] testCaseSteps, String expectedTestCaseStepInformation) throws Exception {
        SortedSet<TestCaseStep> input = new TreeSet<>();
        input.addAll(Arrays.asList(testCaseSteps));
        assertEquals(AbstractOutputBuilder.generateStepInformation(input), expectedTestCaseStepInformation);
    }

    @DataProvider
    public Object[][] testGenerateTestCaseInformationDP() {
        return new Object[][] {
                {new TestCase[]{}, ""},
                {new TestCase[]{createTestCase(null, new DateTime(), TestCaseState.WARNING)},
                        ", case \"Unit Test Case\" over runtime (3.00s/warn at 4s)"},
                {new TestCase[]{createTestCase(null, new DateTime(), TestCaseState.WARNING),
                        createTestCase("case_2", new DateTime().plusSeconds(1), TestCaseState.WARNING)},
                        ", case \"Unit Test Case\" over runtime (3.00s/warn at 4s), case \"case_2\" over runtime (3.00s/warn at 4s)"},
                {new TestCase[]{createTestCase("ok1", new DateTime(), TestCaseState.OK),
                        createTestCase("ok2", new DateTime().plusSeconds(1), TestCaseState.OK)},
                        ""},
        };
    }

    @Test(dataProvider = "testGenerateTestCaseInformationDP")
    public void testGenerateTestCaseInformation(TestCase[] testCases, String expectedTestCaseInformation) throws Exception {
        SortedSet<TestCase> input = new TreeSet<>();
        input.addAll(Arrays.asList(testCases));
        assertEquals(AbstractOutputBuilder.generateCaseInformation(input), expectedTestCaseInformation);
    }

    private TestCase createTestCase(String name, DateTime creationDate, TestCaseState state) {
        TestCase testCase = new TestCaseExampleBuilder().withCreationDate(creationDate).withState(state).buildExample();
        if (name != null) {
            testCase.setName(name);
        }
        return testCase;
    }

    private TestCaseStep createTestCaseStep(String name, DateTime creationDate, TestCaseStepState state) {
        TestCaseStep testCaseStep = new TestCaseStepExampleBuilder().withCreationDate(creationDate).withState(state).buildExample();
        if (name != null) {
            testCaseStep.setName(name);
        }
        return testCaseStep;
    }
}
