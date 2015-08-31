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

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.ProfileGearman;
import org.sakuli.services.forwarder.gearman.TextPlaceholder;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.sakuli.services.forwarder.gearman.model.OutputState;
import org.sakuli.services.forwarder.gearman.model.PlaceholderMap;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;

import static org.sakuli.services.forwarder.gearman.TextPlaceholder.*;

/**
 * @author tschneck Date: 11.07.14
 */
@ProfileGearman
@Component
public class OutputBuilder implements Builder<NagiosOutput> {
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");
    private static final String TABLE_HEADER = "<table style=\"border-collapse: collapse;\">";
    private static final String TABLE_FOOTER = "</table>";
    private static final String TABLE_ROW_HEADER = "<tr valign=\"top\"><td class=\"{{TD_CSS_CLASS}}\">";
    private static final String TABLE_ROW_FOOTER = "</td></tr>";

    private static Logger logger = LoggerFactory.getLogger(OutputBuilder.class);

    @Autowired
    private ScreenshotDivConverter screenshotDivConverter;
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private GearmanProperties gearmanProperties;
    @Autowired
    private PerformanceDataBuilder performanceDataBuilder;

    public static String replacePlaceHolder(String message, PlaceholderMap placeholderStringMap) {
        String modifiedString = message;
        for (TextPlaceholder key : placeholderStringMap.keySet()) {
            modifiedString = StringUtils.replace(modifiedString, key.getPattern(), placeholderStringMap.get(key));
        }
        //check if still placeholders can be resolved
        for (TextPlaceholder placeholder : placeholderStringMap.keySet()) {
            if (StringUtils.contains(modifiedString, placeholder.getPattern())) {
                return replacePlaceHolder(modifiedString, placeholderStringMap);
            }
        }
        return modifiedString;
    }

    @Override
    public NagiosOutput build() {
        NagiosOutput output = new NagiosOutput();
        output.setStatusSummary(getStatusSummary(testSuite, gearmanProperties));
        output.setPerformanceData(performanceDataBuilder.build());
        return output;
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
                .append(StringUtils.remove(formatTestSuiteSummaryStateMessage(testSuite, properties), "\n"))
                .append(NagiosOutput.DETAILS_SEPARATOR)
                .append(TABLE_HEADER)
                .append(StringUtils.remove(formatTestSuiteTableStateMessage(testSuite, properties), "\n"));

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

    protected PlaceholderMap getTextPlaceholder(TestCase testCase) {
        PlaceholderMap placeholderMap = new PlaceholderMap();
        OutputState outputState = OutputState.lookupSakuliState(testCase.getState());
        ScreenshotDiv screenshotDiv = screenshotDivConverter.convert(testCase.getException());
        placeholderMap.put(STATE, outputState.name());
        placeholderMap.put(STATE_SHORT, outputState.getShortState());
        placeholderMap.put(STATE_DESC, (testCase.getState() == null)
                ? TestCaseState.ERRORS.getNagiosStateDescription()
                : testCase.getState().getNagiosStateDescription());
        placeholderMap.put(NAME, testCase.getName());
        placeholderMap.put(ID, testCase.getId());
        placeholderMap.put(DURATION, String.format(Locale.ENGLISH, "%.2f", testCase.getDuration()));
        placeholderMap.put(START_DATE, (testCase.getStartDate() == null) ? "xx" : dateFormat.format(testCase.getStartDate()));
        placeholderMap.put(STOP_DATE, (testCase.getStopDate() == null) ? "xx" : dateFormat.format(testCase.getStopDate()));
        placeholderMap.put(WARN_THRESHOLD, String.valueOf(testCase.getWarningTime()));
        placeholderMap.put(CRITICAL_THRESHOLD, String.valueOf(testCase.getCriticalTime()));
        placeholderMap.put(ERROR_MESSAGE, testCase.getExceptionMessages(true));
        placeholderMap.put(ERROR_SCREENSHOT, screenshotDiv != null ? screenshotDiv.getPayloadString() : null);
        placeholderMap.put(STEP_INFORMATION, generateStepInformation(testCase.getStepsAsSortedSet()));
        placeholderMap.put(CASE_FILE, testCase.getTcFile() != null ? testCase.getTcFile().toString() : null);
        placeholderMap.put(CASE_START_URL, testCase.getStartUrl());
        placeholderMap.put(CASE_LAST_URL, testCase.getLastURL());
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
                        .append(NagiosFormatter.formatToSec(step.getDuration()))
                        .append(" /warn at ")
                        .append(NagiosFormatter.formatToSec(step.getWarningTime()))
                        .append(")");
            }
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    protected PlaceholderMap getTextPlaceholder(TestSuite testSuite) {
        PlaceholderMap placeholderMap = new PlaceholderMap();
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        ScreenshotDiv screenshotDiv = screenshotDivConverter.convert(testSuite.getException());
        placeholderMap.put(STATE, outputState.name());
        placeholderMap.put(STATE_SHORT, outputState.getShortState());
        placeholderMap.put(STATE_DESC, testSuite.getState().getNagiosStateDescription());
        placeholderMap.put(SUITE_SUMMARY, generateStateSummary(testSuite.getState()));
        placeholderMap.put(NAME, testSuite.getName());
        placeholderMap.put(ID, testSuite.getId());
        placeholderMap.put(DURATION, String.format(Locale.ENGLISH, "%.2f", testSuite.getDuration()));
        placeholderMap.put(START_DATE, (testSuite.getStartDate() == null) ? "xx" : dateFormat.format(testSuite.getStartDate()));
        placeholderMap.put(STOP_DATE, (testSuite.getStopDate() == null) ? "xx" : dateFormat.format(testSuite.getStopDate()));
        placeholderMap.put(WARN_THRESHOLD, String.valueOf(testSuite.getWarningTime()));
        placeholderMap.put(CRITICAL_THRESHOLD, String.valueOf(testSuite.getCriticalTime()));
        placeholderMap.put(ERROR_SCREENSHOT, screenshotDiv != null ? screenshotDiv.getPayloadString() : null);
        placeholderMap.put(ERROR_MESSAGE, testSuite.getExceptionMessages(true));
        placeholderMap.put(SUITE_FOLDER, testSuite.getTestSuiteFolder() != null ? testSuite.getTestSuiteFolder().toString() : null);
        placeholderMap.put(HOST, testSuite.getHost());
        placeholderMap.put(BROWSER_INFO, testSuite.getBrowserInfo());
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
