/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.services.receiver.gearman.model.builder;

import de.consol.sakuli.datamodel.Builder;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestCaseStep;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.state.TestCaseStepState;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.services.receiver.gearman.GearmanProperties;
import de.consol.sakuli.services.receiver.gearman.TextPlaceholder;
import de.consol.sakuli.services.receiver.gearman.model.NagiosOutput;
import de.consol.sakuli.services.receiver.gearman.model.OutputState;
import de.consol.sakuli.services.receiver.gearman.model.PlaceholderMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import static de.consol.sakuli.services.receiver.gearman.TextPlaceholder.*;

/**
 * @author tschneck
 *         Date: 11.07.14
 */
public class OutputBuilder implements Builder<NagiosOutput> {
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");
    private static final String TABLE_HEADER = "<table style=\"border-collapse: collapse;\">";
    private static final String TABLE_FOOTER = "</table>";
    private static final String TABLE_ROW_HEADER = "<tr valign=\"top\"><td class=\"{{TD_CSS_CLASS}}\">";
    private static final String TABLE_ROW_FOOTER = "</td></tr>";
    /**
     * name = value; warning; critical
     */
    private static final String PERFORMANCE_DATA_TEMPLATE = "%s=%s;%s;%s;;";
    private static Logger logger = LoggerFactory.getLogger(OutputBuilder.class);
    private String statusSummary;
    private String performanceData;

    public static String replacePlaceHolder(String message, PlaceholderMap placeholderStringMap) {
        String modifiedString = message;
        for (Map.Entry<TextPlaceholder, String> entry : placeholderStringMap.entrySet()) {
            modifiedString = StringUtils.replace(modifiedString, entry.getKey().getPattern(), entry.getValue());
        }
        //check if still placeholders can be resolved
        for (TextPlaceholder placeholder : placeholderStringMap.keySet()) {
            if (StringUtils.contains(modifiedString, placeholder.getPattern())) {
                return replacePlaceHolder(modifiedString, placeholderStringMap);
            }
        }
        return modifiedString;
    }

    /**
     * Add to the assigned 'performanceData' a new data set in respect of the template {@link #PERFORMANCE_DATA_TEMPLATE}.
     */
    public static String addPerformanceDataRow(String performanceData, String name, String value, String warning, String critical) {
        performanceData = (performanceData == null) ? "" : performanceData;
        name = (name == null) ? "" : StringUtils.replace(name.trim(), " ", "_");
        value = (value == null) ? "" : value.trim();
        warning = (warning == null) ? "" : warning.trim();
        critical = (critical == null) ? "" : critical.trim();

        //format string and remove not needed spaces at beginn and ending
        return performanceData.concat(" ").concat(String.format(PERFORMANCE_DATA_TEMPLATE, name, value, warning, critical)).trim();
    }

    /**
     * Small wrapper for non overview data for the {@link #addPerformanceDataRow(String, String, String, String, String)}
     */
    public static String addPerformanceDataRow(String performanceData, String name, float duration, int warningTime, int criticalTime) {
        String warningTimeString = (warningTime == 0) ? "" : String.valueOf(warningTime);
        String criticalTimeString = (criticalTime == 0) ? "" : String.valueOf(criticalTime);
        return addPerformanceDataRow(performanceData, name, formatToSec(duration), warningTimeString, criticalTimeString);
    }

    public static String formatToSec(float duration) {
        return String.format(Locale.ENGLISH, "%.2fs", duration);
    }

    public static String formatToSec(int duration) {
        return duration + "s";
    }

    @Override
    public NagiosOutput build() {
        NagiosOutput output = new NagiosOutput();
        output.setStatusSummary(statusSummary);
        output.setPerformanceData(performanceData);
        return output;
    }

    public OutputBuilder withTestSuite(TestSuite testSuite, GearmanProperties properties) {
        statusSummary = getStatusSummary(testSuite, properties);
        performanceData = getPerformanceData(testSuite) + " [" + properties.getNagiosCheckCommand() + "]";
        return this;
    }

    /**
     * Generates the performance data for the nagios server as an {@link String}.
     *
     * @param testSuite finshed {@link TestSuite}
     * @return formatted payload string
     */
    protected String getPerformanceData(TestSuite testSuite) {
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        String data = "";
        data = addPerformanceDataRow(data, "suite__state", String.valueOf(outputState.getErrorCode()), null, null);
        data = addPerformanceDataRow(data, "suite_" + testSuite.getId(), testSuite.getDuration(), testSuite.getWarningTime(), testSuite.getCriticalTime());

        int i = 1;
        for (TestCase tc : testSuite.getTestCasesAsSortedSet()) {
            OutputState tcOutputState = OutputState.lookupSakuliState(tc.getState());
            data = addPerformanceDataRow(data, "c_" + i + "__state_" + tc.getId(), String.valueOf(tcOutputState.getErrorCode()), null, null);
            data = addPerformanceDataRow(data, "c_" + i + "_" + tc.getId(), tc.getDuration(), tc.getWarningTime(), tc.getCriticalTime());
            int j = 1;
            for (TestCaseStep step : tc.getStepsAsSortedSet()) {
                data = addPerformanceDataRow(data, "s_" + i + "_" + j + "_" + step.getId(), step.getDuration(), step.getWarningTime(), 0);
                j++;
            }
            i++;
        }
        return data;
    }

    /**
     * Generates the displayed overview data for the nagios server as an {@link String}.
     *
     * @param testSuite finshed {@link TestSuite}
     * @return formatted payload string
     */
    protected String getStatusSummary(TestSuite testSuite, GearmanProperties properties) {
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        StringBuilder sb = new StringBuilder();
        sb.append(outputState.name())
                .append(" - ")
                .append(formatTestSuiteSummaryStateMessage(testSuite, properties))
                .append(NagiosOutput.DETAILS_SEPARATOR)
                .append(TABLE_HEADER)
                .append(formatTestSuiteTableStateMessage(testSuite, properties));

        for (TestCase tc : testSuite.getTestCasesAsSortedSet()) {
            sb.append(formatTestCaseTableStateMessage(tc, properties));
        }
        sb.append(TABLE_FOOTER);
        return sb.toString();
    }

    protected String formatTestCaseTableStateMessage(TestCase tc, GearmanProperties properties) {
        String unfilledResult = TABLE_ROW_HEADER
                + properties.lookUpOutputString(tc.getState())
                + TABLE_ROW_FOOTER;
        String result = replacePlaceHolder(unfilledResult, getTextPlaceholder(tc));
        logger.debug("{{xxx}} patterns in string '{}' replaced with message '{}'", unfilledResult, result);
        return result;
    }

    protected String formatTestSuiteTableStateMessage(TestSuite testSuite, GearmanProperties properties) {
        String unfilledResult = TABLE_ROW_HEADER
                + properties.getOutputSuiteTable()
                + TABLE_ROW_FOOTER;
        String result = replacePlaceHolder(unfilledResult, getTextPlaceholder(testSuite));
        logger.debug("{{xxx}} patterns in string '{}' replaced with message '{}'", unfilledResult, result);
        return result;
    }

    protected String formatTestSuiteSummaryStateMessage(TestSuite testSuite, GearmanProperties properties) {
        String unfilledResult = properties.getOutputSuiteSummary();
        String result = replacePlaceHolder(unfilledResult, getTextPlaceholder(testSuite));
        logger.debug("{{xxx}} patterns in string '{}' replaced with message '{}'", unfilledResult, result);
        return result;
    }

    private PlaceholderMap getTextPlaceholder(TestCase testCase) {
        PlaceholderMap placeholderMap = new PlaceholderMap();
        OutputState outputState = OutputState.lookupSakuliState(testCase.getState());
        placeholderMap.put(STATE, outputState.name());
        placeholderMap.put(STATE_SHORT, outputState.getShortState());
        placeholderMap.put(STATE_DESC, testCase.getState().getNagiosStateDescription());
        placeholderMap.put(ID, testCase.getId());
        placeholderMap.put(DURATION, String.format(Locale.ENGLISH, "%.2f", testCase.getDuration()));
        placeholderMap.put(LAST_RUN, (testCase.getStopDate() == null) ? "xx" : dateFormat.format(testCase.getStopDate()));
        placeholderMap.put(WARN_THRESHOLD, String.valueOf(testCase.getWarningTime()));
        placeholderMap.put(CRITICAL_THRESHOLD, String.valueOf(testCase.getCriticalTime()));
        placeholderMap.put(ERROR_MESSAGE, testCase.getExceptionMessages(true));
        placeholderMap.put(STEP_INFORMATION, generateStepInformation(testCase.getStepsAsSortedSet()));

        placeholderMap.put(TD_CSS_CLASS, "service" + outputState.name());
        return placeholderMap;
    }

    private String generateStepInformation(SortedSet<TestCaseStep> steps) {
        StringBuilder sb = new StringBuilder();
        Iterator<TestCaseStep> it = steps.iterator();
        while (it.hasNext()) {
            TestCaseStep step = it.next();
            if (TestCaseStepState.WARNING.equals(step.getState())) {
                sb.append("step \"")
                        .append(step.getName())
                        .append("\" (")
                        .append(formatToSec(step.getDuration()))
                        .append(" /warn at ")
                        .append(formatToSec(step.getWarningTime()))
                        .append(")");
            }
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private PlaceholderMap getTextPlaceholder(TestSuite testSuite) {
        PlaceholderMap placeholderMap = new PlaceholderMap();
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        placeholderMap.put(STATE, outputState.name());
        placeholderMap.put(STATE_SHORT, outputState.getShortState());
        placeholderMap.put(STATE_DESC, testSuite.getState().getNagiosStateDescription());
        placeholderMap.put(STATE_SUMMARY, generateStateSummary(testSuite.getState()));
        placeholderMap.put(ID, testSuite.getId());
        placeholderMap.put(DURATION, String.format(Locale.ENGLISH, "%.2f", testSuite.getDuration()));
        placeholderMap.put(LAST_RUN, (testSuite.getStopDate() == null) ? "xx" : dateFormat.format(testSuite.getStopDate()));
        placeholderMap.put(WARN_THRESHOLD, String.valueOf(testSuite.getWarningTime()));
        placeholderMap.put(CRITICAL_THRESHOLD, String.valueOf(testSuite.getCriticalTime()));
        placeholderMap.put(ERROR_MESSAGE, testSuite.getExceptionMessages(true));
        placeholderMap.put(TD_CSS_CLASS, "service" + outputState.name());
        return placeholderMap;
    }

    private String generateStateSummary(TestSuiteState state) {
        StringBuilder summary = new StringBuilder(STATE_DESC.getPattern());
        if (state.isError()) {
            summary.append(": \"").append(ERROR_MESSAGE.getPattern()).append("\"");
        } else if (state.isWarning()) {
            summary.append(": threshold ").append(WARN_THRESHOLD.getPattern()).append("s");
        } else if (state.isCritical()) {
            summary.append(": threshold ").append(CRITICAL_THRESHOLD.getPattern()).append("s");
        }
        return summary.toString();
    }
}
