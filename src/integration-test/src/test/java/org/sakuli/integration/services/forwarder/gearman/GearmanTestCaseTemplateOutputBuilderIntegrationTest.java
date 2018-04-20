/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.integration.services.forwarder.gearman;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.integration.IntegrationTest;
import org.sakuli.services.forwarder.ScreenshotDiv;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.GearmanTemplateOutputBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doReturn;

/**
 * @author Georgi Todorov
 */
@Test(groups = IntegrationTest.GROUP)
public class GearmanTestCaseTemplateOutputBuilderIntegrationTest extends BaseTest {

    private static final String TESTSUITE_ID = "example_xfce";
    private static final String DEFAULT_SERVICE_TYPE = "passive";
    private static final String DEFAULT_NAGIOS_HOST = "my.nagios.host";
    private static final String DEFAULT_NAGIOS_CHECK_COMMMAND = "check_sakuli";

    @InjectMocks
    @Spy
    private GearmanTemplateOutputBuilder testling;
    @Mock
    private ScreenshotDivConverter screenshotDivConverter;
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private SakuliProperties sakuliProperties;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(getTemplatePath()).when(sakuliProperties).getForwarderTemplateFolder();
        doReturn(TESTSUITE_ID).when(gearmanProperties).getNagiosServiceDescription();
        doReturn(DEFAULT_SERVICE_TYPE).when(gearmanProperties).getServiceType();
        doReturn(DEFAULT_NAGIOS_HOST).when(gearmanProperties).getNagiosHost();
        doReturn(DEFAULT_NAGIOS_CHECK_COMMMAND).when(gearmanProperties).getNagiosCheckCommand();

        TestSuite testSuite = new TestSuite();
        testSuite.setId(TESTSUITE_ID);
        doReturn(testSuite).when(testling).getCurrentTestSuite();
        doReturn(null).when(testling).getCurrentTestCase();
    }

    private String getTemplatePath() {
        // If not available execute test via `mvn test-compile` to extract the file dependencies
        return getResource("common/config/templates");
    }

    private String getOutputPath() {
        return getResource("output", this.getClass());
    }

    private String loadExpectedOutput(String testCaseName) throws IOException {
        return FileUtils.readFileToString(Paths.get(getOutputPath() + File.separator + "TestCase_" + testCaseName + ".txt").toFile());
    }

    @Test
    public void testOK() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.OK)
                .withWarningTime(20)
                .withCriticalTime(30)
                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 14, 20).toDate())
                .withId("case1")
                .withTestCaseSteps(
                        Arrays.asList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Test_Sahi_landing_page")
                                        .withWarningTime(5)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 160).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Calculation")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 10).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 30, 7, 290).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Editor")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 20).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 500).toDate())
                                        .buildExample()
                        )
                )
                .buildExample();
        String output = testling.createOutput(testCase);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.OK.name()));
    }

    @Test
    public void testWarnInStep() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.WARNING_IN_STEP)
                .withWarningTime(20)
                .withCriticalTime(30)
                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 20).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 33, 430).toDate())
                .withId("case2")
                .withTestCaseSteps(
                        Arrays.asList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Test_Sahi_landing_page_(case2)")
                                        .withWarningTime(5)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 10).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 0, 930).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.WARNING)
                                        .withName("Calculation_(case2)")
                                        .withWarningTime(1)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 20).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 20).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Editor_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 30).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 420).toDate())
                                        .buildExample()
                        )
                )
                .buildExample();
        String output = testling.createOutput(testCase);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.WARNING_IN_STEP.name()));
    }

    @Test
    public void testCritInStep() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.CRITICAL_IN_STEP)
                .withWarningTime(20)
                .withCriticalTime(30)
                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 20).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 33, 430).toDate())
                .withId("case2")
                .withTestCaseSteps(
                        Arrays.asList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Test_Sahi_landing_page_(case2)")
                                        .withWarningTime(5)
                                        .withCriticalTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 10).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 0, 930).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.WARNING)
                                        .withName("Calculation_(case2)")
                                        .withWarningTime(1)
                                        .withCriticalTime(2)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 20).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 20).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Editor_(case2)")
                                        .withWarningTime(10)
                                        .withCriticalTime(20)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 30).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 420).toDate())
                                        .buildExample()
                        )
                )
                .buildExample();
        String output = testling.createOutput(testCase);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.CRITICAL_IN_STEP.name()));
    }

    @Test
    public void testWarnInCase() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.WARNING)
                .withWarningTime(2)
                .withCriticalTime(30)
                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 20).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 33, 540).toDate())
                .withId("case2")
                .withTestCaseSteps(
                        Arrays.asList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Test_Sahi_landing_page_(case2)")
                                        .withWarningTime(5)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 10).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 34, 0, 940).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Calculation_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 20).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 34, 7, 140).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Editor_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 30).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 390).toDate())
                                        .buildExample()
                        )
                )
                .buildExample();
        String output = testling.createOutput(testCase);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.WARNING.name()));
    }

    @Test
    public void testCritInCase() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.CRITICAL)
                .withWarningTime(2)
                .withCriticalTime(3)
                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 20).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 33, 700).toDate())
                .withId("case2")
                .withTestCaseSteps(
                        Arrays.asList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Test_Sahi_landing_page_(case2)")
                                        .withWarningTime(5)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 80).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Calculation_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0, 10).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 35, 7, 120).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Editor_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0, 20).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 440).toDate())
                                        .buildExample()
                        )
                )
                .buildExample();
        String output = testling.createOutput(testCase);
        String expected = loadExpectedOutput(TestCaseState.CRITICAL.name());
        Assert.assertEquals(output, expected);
        Assert.assertEquals(output.getBytes(), expected.getBytes());
    }

    @Test
    public void testException() throws Exception {
        TestCase testCase = new TestCaseExampleBuilder()
                .withState(TestCaseState.ERRORS)
                .withWarningTime(20)
                .withCriticalTime(30)
                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 10).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 23, 550).toDate())
                .withId("case2")
                .withTestCaseSteps(
                        Arrays.asList(
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.ERRORS)
                                        .withName("Test_Sahi_landing_page_(case2)")
                                        .withWarningTime(5)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 50).toDate())
                                        .withException(new SakuliCheckedException("_highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a>"))
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Calculation_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 36, 10).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 36, 17, 30).toDate())
                                        .buildExample(),
                                new TestCaseStepExampleBuilder()
                                        .withState(TestCaseStepState.OK)
                                        .withName("Editor_(case2)")
                                        .withWarningTime(10)
                                        .withStartDate(new DateTime(1970, 1, 1, 10, 36, 20).toDate())
                                        .withStopDate(new DateTime(1970, 1, 1, 10, 36, 21, 390).toDate())
                                        .buildExample()
                        )
                )
                .buildExample();
        ScreenshotDiv screenshotDiv = new ScreenshotDiv();
        screenshotDiv.setId("sakuli_screenshot243575009");
        screenshotDiv.setFormat("jpg");
        screenshotDiv.setBase64screenshot("/9j/4AAQSkZJRgABAgAAAQABAAD9k=");
        doReturn(screenshotDiv).when(screenshotDivConverter).convert(notNull(Exception.class));
        String output = testling.createOutput(testCase);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.ERRORS.name()));
    }

}
