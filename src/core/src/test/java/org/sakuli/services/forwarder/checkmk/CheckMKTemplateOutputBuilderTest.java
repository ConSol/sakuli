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

package org.sakuli.services.forwarder.checkmk;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author Georgi Todorov
 */
public class CheckMKTemplateOutputBuilderTest extends BaseTest {

    private static final String DEFAULT_SERVICE_DESCRIPTION = "service_description";

    @InjectMocks
    private CheckMKTemplateOutputBuilder testling;
    @Mock
    private ScreenshotDivConverter screenshotDivConverter;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private CheckMKProperties checkMKProperties;
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliProperties sakuliProperties;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(getTemplatePath()).when(sakuliProperties).getForwarderTemplateFolder();
        doReturn(DEFAULT_SERVICE_DESCRIPTION).when(checkMKProperties).getServiceDescription();
    }

    private String getTemplatePath() {
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
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.OK).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(44.99f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 30, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 30, 44, 99).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
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
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 7, 290).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 500).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 30, 10).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 30, 23, 580).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 7, 80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.OK.name()));
    }

    @Test
    public void testWarnInStep() throws Exception {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.WARNING_IN_STEP).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(44.75f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 31, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 31, 44, 750).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 13, 830).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 550).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
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
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 0, 930).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.WARNING)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(1)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 20).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 420).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.WARNING_IN_STEP.name()));
    }

    @Test
    public void testWarnInCase() throws Exception {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.WARNING_IN_CASE).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(42.84f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 34, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 34, 42, 840).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 34, 14, 30).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 7, 340).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 460).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
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
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 0, 940).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 7, 140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.WARNING_IN_CASE.name()));
    }

    @Test
    public void testCritInCase() throws Exception {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.CRITICAL_IN_CASE).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(46.96f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 35, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 35, 46, 960).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 35, 14, 130).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 280).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 7, 310).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 470).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
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
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 7, 120).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 440).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.CRITICAL_IN_CASE.name()));
    }

    @Test
    public void testWarnInSuite() throws Exception {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.WARNING_IN_SUITE).when(testSuite).getState();
        doReturn(3).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(46.94f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 32, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 32, 46, 940).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 32, 14, 130).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 210).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 7, 410).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 430).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 32, 10).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 32, 23, 580).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 60).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 7, 80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 360).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.WARNING_IN_SUITE.name()));
    }

    @Test
    public void testCritInSuite() throws Exception {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.CRITICAL_IN_SUITE).when(testSuite).getState();
        doReturn(30).when(testSuite).getWarningTime();
        doReturn(40).when(testSuite).getCriticalTime();
        doReturn(44.81f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 33, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 33, 44, 810).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 33, 13, 910).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 1, 160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 7, 250).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 1, 450).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 33, 10).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 33, 23, 550).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 00).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 1, 50).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 17, 30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 21, 390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.CRITICAL_IN_SUITE.name()));
    }

    @Test
    public void testException() throws Exception {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.ERRORS).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(44.80f).when(testSuite).getDuration();
        doReturn(new DateTime(1970, 1, 1, 10, 36, 0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970, 1, 1, 10, 36, 44, 800).toDate()).when(testSuite).getStopDate();
        when(testSuite.getExceptionMessages(anyBoolean(), any())).thenCallRealMethod();
        when(testSuite.getException()).thenCallRealMethod();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 36, 14, 200).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 7, 540).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 470).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
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
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 00).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 50).toDate())
                                                .withException(new SakuliException("_highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a>"))
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
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        ScreenshotDiv screenshotDiv = new ScreenshotDiv();
        screenshotDiv.setId("sakuli_screenshot243575009");
        screenshotDiv.setFormat("jpg");
        screenshotDiv.setBase64screenshot("/9j/4AAQSkZJRgABAgAAAQABAAD9k=");
        doReturn(screenshotDiv).when(screenshotDivConverter).convert(notNull(Throwable.class));
        String output = testling.createOutput();
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.ERRORS.name()));
    }

}
