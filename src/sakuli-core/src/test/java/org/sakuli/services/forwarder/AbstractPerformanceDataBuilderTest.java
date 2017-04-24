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

package org.sakuli.services.forwarder;

import org.apache.commons.lang.time.DateUtils;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 8/26/15
 */
public class AbstractPerformanceDataBuilderTest {

    @DataProvider(name = "performanceDataRows")
    public static Object[][] performanceDataRows() {
        return new Object[][]{
                {null, "name with space", null, null, null, "name_with_space=;;;;"},
                {"name_with_space=;;;;", " second_name ", "0", "", "", "name_with_space=;;;; second_name=0;;;;"},
                {" ", " warnIng ", " 15s ", " 10.5s ", " 11.0s ", "warnIng=15s;10.5s;11.0s;;"},
                {"nam1=;;;; nam2=;;;;", "nam3", "0", "", "", "nam1=;;;; nam2=;;;; nam3=0;;;;"},
        };

    }

    @DataProvider(name = "performanceDataRowsNonOverview")
    public static Object[][] performanceDataRowsNonOverview() {
        return new Object[][]{
                {null, "name with space", 0f, 0, 0, "name_with_space=0.00s;;;;"},
                {"name_with_space=;;;;", " second_name ", 12.5f, 10, 14, "name_with_space=;;;; second_name=12.50s;10;14;;"},
        };

    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(dataProvider = "performanceDataRows")
    public void testAddPerformanceDataRow(String currentPerformanceData, String name, String value, String warning, String critical, String expectedData) throws Exception {
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow(currentPerformanceData, name, value, warning, critical), expectedData);
    }

    @Test(dataProvider = "performanceDataRowsNonOverview")
    public void testAddPerformanceDataRow(String currentPerformanceData, String name, float duration, int warning, int critical, String expectedData) throws Exception {
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow(currentPerformanceData, name, duration, warning, critical), expectedData);
    }

    @Test
    public void testAddUnknonwPerformanceData() throws Exception {
        assertEquals(AbstractPerformanceDataBuilder.addUnknownPerformanceDataRow("", "my_name_test"), "my_name_test=U;;;;");
    }

    @Test
    public void testAddThresholdPerformanceData() throws Exception {
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow4Threshold("", "suite", 0, 0), "");
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow4Threshold("", "suite", 0, -1), "");
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow4Threshold("", "suite", -1, -1), "");
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow4Threshold("", "suite", 10, -1),
                "suite__warning=10s;;;;");
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow4Threshold("", "suite", 10, 20),
                "suite__warning=10s;;;; suite__critical=20s;;;;");
        assertEquals(AbstractPerformanceDataBuilder.addPerformanceDataRow4Threshold("", "suite", 0, 20),
                "suite__critical=20s;;;;");
    }

    @Test
    public void testSuitePerformanceDataUnknownData() throws Exception {
        BaseTest.assertRegExMatch(
                AbstractPerformanceDataBuilder.getTestSuitePerformanceData(
                        new TestSuiteExampleBuilder().withState(TestSuiteState.ERRORS).buildExample()
                ),
                "suite__state=2;;;; suite_UnitTest_\\d*=U;;;;.*");
    }

    @Test
    public void testCasePerformanceDataUnknownData() throws Exception {
        BaseTest.assertRegExMatch(
                AbstractPerformanceDataBuilder.addTestCasePerformanceData(
                        "",
                        new TreeSet<>(Collections.singletonList(
                                new TestCaseExampleBuilder().withState(TestCaseState.ERRORS).buildExample()
                        ))
                ),
                "c_001__state=2;;;; c_001__warning=4s;;;; c_001__critical=5s;;;; c_001_UNIT_TEST_CASE_\\d*=U;;;;.*");
    }

    @Test
    public void testStepPerformanceDataUnknownData() throws Exception {
        assertEquals(
                AbstractPerformanceDataBuilder.addTestCaseStepPerformanceData(
                        "",
                        new TreeSet<>(Arrays.asList(
                                new TestCaseStepExampleBuilder().withName("step1")
                                        .withState(TestCaseStepState.OK).buildExample(),
                                new TestCaseStepExampleBuilder().withName("step2")
                                        .withState(TestCaseStepState.INIT).withStartDate(null).buildExample()
                        )),
                        5
                ),
                "s_005_001_step1=3.00s;4;;; s_005_002_step2=U;;;;");
    }

    @Test
    public void testStepPerformanceDataUnknownDataOnError() throws Exception {
        assertEquals(
                AbstractPerformanceDataBuilder.addTestCaseStepPerformanceData(
                        "",
                        new TreeSet<>(Arrays.asList(
                                new TestCaseStepExampleBuilder().withName("step1")
                                        .withState(TestCaseStepState.OK).buildExample(),
                                new TestCaseStepExampleBuilder().withName("step2")
                                        .withState(TestCaseStepState.ERRORS).withStartDate(null).buildExample()
                        )),
                        5
                ),
                "s_005_001_step1=3.00s;4;;; s_005_002_step2=U;;;;");
    }

    @Test
    public void testGetPerformanceData() throws Exception {
        Date startDate = new GregorianCalendar(2014, 14, 7, 13, 0).getTime();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withStartDate(startDate)
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .withWarningTime(100)
                .withCriticalTime(150)
                .withTestCases(Arrays.asList(
                        new TestCaseExampleBuilder().withState(TestCaseState.WARNING_IN_STEP)
                                .withId("case-warning")
                                .withStartDate(startDate)
                                .withStopDate(DateUtils.addSeconds(startDate, 20))
                                .withWarningTime(19)
                                .withCriticalTime(25)
                                .withTestCaseSteps(Arrays.asList(new TestCaseStepExampleBuilder()
                                                .withName("step1")
                                                .withState(TestCaseStepState.WARNING)
                                                .withStartDate(startDate)
                                                .withStopDate(DateUtils.addSeconds(startDate, 10))
                                                .withWarningTime(9)
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withName("step2")
                                                .withState(TestCaseStepState.OK)
                                                .withStartDate(DateUtils.addSeconds(startDate, 11))
                                                .withStopDate(DateUtils.addSeconds(startDate, 19))
                                                .withWarningTime(10)
                                                .buildExample()))
                                .buildExample(),
                        new TestCaseExampleBuilder().withState(TestCaseState.OK)
                                .withId("case with no steps")
                                .withStartDate(DateUtils.addSeconds(startDate, 25))
                                .withStopDate(DateUtils.addSeconds(startDate, 40))
                                .withWarningTime(0)
                                .withCriticalTime(0)
                                .withTestCaseSteps(null)
                                .buildExample()))
                .buildExample();
        testSuiteExample.refreshState();

        assertEquals(AbstractPerformanceDataBuilder.getTestSuitePerformanceData(testSuiteExample), "suite__state=1;;;; " +
                "suite__warning=100s;;;; " +
                "suite__critical=150s;;;; " +
                "suite_sakuli-123=120.00s;100;150;; " +
                "c_001__state=1;;;; " +
                "c_001__warning=19s;;;; " +
                "c_001__critical=25s;;;; " +
                "c_001_case-warning=20.00s;19;25;; " +
                "s_001_001_step1=10.00s;9;;; " +
                "s_001_002_step2=8.00s;10;;; " +
                "c_002__state=0;;;; " +
                "c_002_case_with_no_steps=15.00s;;;;");
    }


    @Test
    public void testGetPerformanceDataWithPreParsedSteps() throws Exception {
        Date startDate = new GregorianCalendar(2014, 14, 7, 13, 0).getTime();
        TestSuite testSuiteExample = new TestSuiteExampleBuilder()
                .withId("sakuli-123")
                .withStartDate(startDate)
                .withStopDate(DateUtils.addSeconds(startDate, 120))
                .withWarningTime(100)
                .withCriticalTime(150)
                .withTestCases(Collections.singletonList(
                        new TestCaseExampleBuilder().withState(TestCaseState.WARNING_IN_STEP)
                                .withId("case-warning")
                                .withStartDate(startDate)
                                .withStopDate(DateUtils.addSeconds(startDate, 20))
                                .withWarningTime(19)
                                .withCriticalTime(25)
                                .withTestCaseSteps(Arrays.asList(new TestCaseStepExampleBuilder()
                                                .withName("step1")
                                                .withState(TestCaseStepState.WARNING)
                                                .withStartDate(startDate)
                                                .withStopDate(DateUtils.addSeconds(startDate, 10))
                                                .withWarningTime(9)
                                                .buildExample(),
                                        new TestCaseStepBuilder("step2_not_started")
                                                .withState(TestCaseStepState.INIT)
                                                .build()))
                                .buildExample()
                ))
                .buildExample();
        testSuiteExample.refreshState();

        assertEquals(AbstractPerformanceDataBuilder.getTestSuitePerformanceData(testSuiteExample), "suite__state=1;;;; " +
                "suite__warning=100s;;;; " +
                "suite__critical=150s;;;; " +
                "suite_sakuli-123=120.00s;100;150;; " +
                "c_001__state=1;;;; " +
                "c_001__warning=19s;;;; " +
                "c_001__critical=25s;;;; " +
                "c_001_case-warning=20.00s;19;25;; " +
                "s_001_001_step1=10.00s;9;;; " +
                "s_001_002_step2_not_started=U;;;;"
        );
    }
}