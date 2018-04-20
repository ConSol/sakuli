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
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
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

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;

/**
 * @author Georgi Todorov
 */
@Test(groups = IntegrationTest.GROUP)
public class GearmanTestStepTemplateOutputBuilderIntegrationTest extends BaseTest {

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
        doReturn(new TestCaseExampleBuilder().withId("case2").buildExample())
                .when(testling).getCurrentTestCase();
    }

    private String getTemplatePath() {
        // If not available execute test via `mvn test-compile` to extract the file dependencies
        return getResource("common/config/templates");
    }

    private String getOutputPath() {
        return getResource("output", this.getClass());
    }

    private String loadExpectedOutput(String testCaseName) throws IOException {
        return FileUtils.readFileToString(Paths.get(getOutputPath() + File.separator + "TestStep_" + testCaseName + ".txt").toFile());
    }

    @Test
    public void testOK() throws Exception {
        TestCaseStep testStep = new TestCaseStepExampleBuilder()
                .withState(TestCaseStepState.OK)
                .withName("Calculation")
                .withWarningTime(10)
                .withWarningTime(20)
                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 10).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 7, 290).toDate())
                .buildExample();
        String output = testling.createOutput(testStep);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseStepState.OK.name()));
    }

    @Test
    public void testWarnInStep() throws Exception {
        TestCaseStep testStep = new TestCaseStepExampleBuilder()
                .withState(TestCaseStepState.WARNING)
                .withName("Calculation")
                .withWarningTime(1)
                .withCriticalTime(10)
                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 20).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 20).toDate())
                .buildExample();
        String output = testling.createOutput(testStep);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseStepState.WARNING.name()));
    }

    @Test
    public void testCritInStep() throws Exception {
        TestCaseStep testStep = new TestCaseStepExampleBuilder()
                .withState(TestCaseStepState.WARNING)
                .withName("Calculation")
                .withWarningTime(1)
                .withCriticalTime(2)
                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 20).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 20).toDate())
                .buildExample();
        String output = testling.createOutput(testStep);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseStepState.CRITICAL.name()));
    }

    @Test
    public void testException() throws Exception {
        TestCaseStep testStep = new TestCaseStepExampleBuilder()
                .withState(TestCaseStepState.ERRORS)
                .withName("Test_Sahi_landing_page")
                .withWarningTime(5)
                .withWarningTime(10)
                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 50).toDate())
                .withException(new SakuliCheckedException("_highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a>"))
                .buildExample();
        ScreenshotDiv screenshotDiv = new ScreenshotDiv();
        screenshotDiv.setId("sakuli_screenshot243575009");
        screenshotDiv.setFormat("jpg");
        screenshotDiv.setBase64screenshot("/9j/4AAQSkZJRgABAgAAAQABAAD9k=");
        doReturn(screenshotDiv).when(screenshotDivConverter).convert(notNull(Throwable.class));
        String output = testling.createOutput(testStep);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseStepState.ERRORS.name()));
    }
}
