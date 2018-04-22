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

/**
 * @author tschneck
 * Date: 2/23/16
 */

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.SortedSet;

public abstract class AbstractPerformanceDataBuilder {
    /**
     * name = value; warning; critical
     */
    private static final String PERFORMANCE_DATA_TEMPLATE = "%s=%s;%s;%s;;";
    @Autowired
    protected TestSuite testSuite;

    /**
     * Add to the assigned 'performanceData' a new data set in respect of the template {@link
     * #PERFORMANCE_DATA_TEMPLATE}.
     */
    static String addPerformanceDataRow(String performanceData, String name, String value, String warning, String critical) {
        performanceData = (performanceData == null) ? "" : performanceData;
        name = (name == null) ? "" : StringUtils.replace(name.trim(), " ", "_");
        value = (value == null) ? "" : value.trim();
        warning = (warning == null) ? "" : warning.trim();
        critical = (critical == null) ? "" : critical.trim();

        //format string and remove not needed spaces at beginn and ending
        return performanceData.concat(" ").concat(String.format(PERFORMANCE_DATA_TEMPLATE, name, value, warning, critical)).trim();
    }

    /**
     * Small wrapper for non overview data for the {@link #addPerformanceDataRow(String, String, String, String,
     * String)}
     */
    static String addPerformanceDataRow(String performanceData, String name, float duration, int warningTime, int criticalTime) {
        String warningTimeString = (warningTime > 0) ? String.valueOf(warningTime) : null;
        String criticalTimeString = (criticalTime > 0) ? String.valueOf(criticalTime) : null;
        return addPerformanceDataRow(performanceData, name, TimeFormatter.formatToSec(duration), warningTimeString, criticalTimeString);
    }

    /**
     * Small wrapper for threshold data like`suite__warning` or `case__warning`.
     */
    static String addPerformanceDataRow4Threshold(String performanceData, String name, int warningTime, int criticalTime) {
        if (warningTime > 0) {
            performanceData = addPerformanceDataRow(performanceData, name + "__warning", TimeFormatter.formatToSec(warningTime), null, null);
        }
        if (criticalTime > 0) {
            performanceData = addPerformanceDataRow(performanceData, name + "__critical", TimeFormatter.formatToSec(criticalTime), null, null);
        }
        return performanceData;
    }

    /**
     * Add a line without any performance values and only with values to the performance data.
     * See {@link #addPerformanceDataRow(String, String, String, String, String)}
     */
    static String addUnknownPerformanceDataRow(String data, String stepName) {
        return addPerformanceDataRow(data, stepName, "U", null, null);
    }

    /**
     * Generates the performance data for assigned {@link TestSuite}as an {@link String}.
     *
     * @param testSuite instance of the current {@link TestSuite}
     * @return formatted payload string
     */
    protected static String getTestSuitePerformanceData(TestSuite testSuite) {
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        String data = "";
        data = addPerformanceDataRow(data, "suite__state", String.valueOf(outputState.getErrorCode()), null, null);
        data = addPerformanceDataRow4Threshold(data, "suite", testSuite.getWarningTime(), testSuite.getCriticalTime());
        final String suiteName = "suite_" + testSuite.getId();
        if (testSuite.getState() != null && testSuite.getState().isFinishedWithoutErrors()) {
            data = addPerformanceDataRow(data, suiteName, testSuite.getDuration(), testSuite.getWarningTime(), testSuite.getCriticalTime());
        } else {
            //add data performance data with unknown state
            data = addUnknownPerformanceDataRow(data, suiteName);
        }

        //add testcase data
        data = addTestCasePerformanceData(data, testSuite.getTestCasesAsSortedSet());
        return data;
    }

    /**
     * Generates the performance data for assigned {@link TestCase}s as an {@link String}.
     *
     * @param data      already produced performance data
     * @param testCases {@link SortedSet} of {@link TestCase}s
     * @return formatted payload string
     */
    protected static String addTestCasePerformanceData(String data, SortedSet<TestCase> testCases) {
        int i = 1;
        for (TestCase tc : testCases) {
            OutputState tcOutputState = OutputState.lookupSakuliState(tc.getState());
            String prefix = String.format("c_%03d", i);
            data = addPerformanceDataRow(data, prefix + "__state", String.valueOf(tcOutputState.getErrorCode()), null, null);
            data = addPerformanceDataRow4Threshold(data, prefix, tc.getWarningTime(), tc.getCriticalTime());
            final String caseName = prefix + "_" + tc.getId();
            if (tc.getState() != null && tc.getState().isFinishedWithoutErrors()) {
                data = addPerformanceDataRow(data, caseName, tc.getDuration(), tc.getWarningTime(), tc.getCriticalTime());
            } else {
                //add data performance data with unknown state
                data = addUnknownPerformanceDataRow(data, caseName);
            }
            data = addTestCaseStepPerformanceData(data, tc.getStepsAsSortedSet(), i);
            i++;
        }
        return data;
    }

    /**
     * Generates the performance data for assigned {@link TestCaseStep}s as an {@link String}.
     *
     * @param data            already produced performance data
     * @param testCaseSteps   {@link SortedSet} of {@link TestCaseStep}s
     * @param countOfTestCase current count of the parent {@link TestCase}
     * @return formatted payload string
     */
    protected static String addTestCaseStepPerformanceData(String data, SortedSet<TestCaseStep> testCaseSteps, int countOfTestCase) {
        int j = 1;
        for (TestCaseStep step : testCaseSteps) {
            final String stepName = String.format("s_%03d_%03d_%s", countOfTestCase, j, step.getId());
            if (step.getState().isFinishedWithoutErrors()) {
                data = addPerformanceDataRow(data, stepName, step.getDuration(), step.getWarningTime(), 0);
            } else {
                //add data performance data with unknown state
                data = addUnknownPerformanceDataRow(data, stepName);
            }
            j++;
        }
        return data;
    }
}
