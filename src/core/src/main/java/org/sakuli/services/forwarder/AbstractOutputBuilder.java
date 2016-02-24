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

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.services.forwarder.gearman.TextPlaceholder;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosFormatter;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosOutputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.SortedSet;

import static org.sakuli.services.forwarder.gearman.TextPlaceholder.*;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
public abstract class AbstractOutputBuilder {
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");
    protected static final String TABLE_ROW_HEADER = "<tr valign=\"top\"><td class=\"{{TD_CSS_CLASS}}\">";
    protected static final String TABLE_ROW_FOOTER = "</td></tr>";
    protected static Logger logger = LoggerFactory.getLogger(NagiosOutputBuilder.class);
    @Autowired
    protected ScreenshotDivConverter screenshotDivConverter;
    @Autowired
    protected TestSuite testSuite;

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

    static String generateStepInformation(SortedSet<TestCaseStep> steps) {
        StringBuilder sb = new StringBuilder();
        steps.stream()
                .filter(step -> step.getState().isWarning())
                .forEach(step ->
                        sb.append(", step \"")
                                .append(step.getName())
                                .append("\" (")
                                .append(NagiosFormatter.formatToSec(step.getDuration()))
                                .append(" /warn at ")
                                .append(NagiosFormatter.formatToSec(step.getWarningTime()))
                                .append(")"));
        return sb.toString();
    }

    protected abstract String getOutputScreenshotDivWidth();

    protected String formatTestSuiteTableStateMessage(TestSuite testSuite, String templateSuiteTable) {
        String unfilledResult = TABLE_ROW_HEADER
                + templateSuiteTable
                + TABLE_ROW_FOOTER;
        String result = replacePlaceHolder(unfilledResult, getTextPlaceholder(testSuite));
        logger.debug("{{xxx}} patterns in string '{}' replaced with message '{}'", unfilledResult, result);
        return result;
    }

    protected String formatTestSuiteSummaryStateMessage(TestSuite testSuite, String templateSuiteSummary) {
        String result = replacePlaceHolder(templateSuiteSummary, getTextPlaceholder(testSuite));
        logger.debug("{{xxx}} patterns in string '{}' replaced with message '{}'", templateSuiteSummary, result);
        return result;
    }

    protected PlaceholderMap getTextPlaceholder(TestSuite testSuite) {
        PlaceholderMap placeholderMap = new PlaceholderMap();
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        ScreenshotDiv screenshotDiv = screenshotDivConverter.convert(testSuite.getException(), getOutputScreenshotDivWidth());
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
            summary.append(": '").append(ERROR_MESSAGE.getPattern()).append("'");
        } else if (state.isWarning()) {
            summary.append(": threshold ").append(WARN_THRESHOLD.getPattern()).append("s");
        } else if (state.isCritical()) {
            summary.append(": threshold ").append(CRITICAL_THRESHOLD.getPattern()).append("s");
        }
        return summary.toString();
    }

    protected String formatTestCaseTableStateMessage(TestCase tc, String templateCaseOutput) {
        String unfilledResult = TABLE_ROW_HEADER
                + templateCaseOutput
                + TABLE_ROW_FOOTER;
        String result = replacePlaceHolder(unfilledResult, getTextPlaceholder(tc));
        logger.debug("{{xxx}} patterns in string '{}' replaced with message '{}'", unfilledResult, result);
        return result;
    }

    protected PlaceholderMap getTextPlaceholder(TestCase testCase) {
        PlaceholderMap placeholderMap = new PlaceholderMap();
        OutputState outputState = OutputState.lookupSakuliState(testCase.getState());
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
        placeholderMap.put(ERROR_SCREENSHOT, generateTestCaseScreenshotsHTML(testCase));
        placeholderMap.put(STEP_INFORMATION, generateStepInformation(testCase.getStepsAsSortedSet()));
        placeholderMap.put(CASE_FILE, testCase.getTcFile() != null ? testCase.getTcFile().toString() : null);
        placeholderMap.put(CASE_START_URL, testCase.getStartUrl());
        placeholderMap.put(CASE_LAST_URL, testCase.getLastURL());
        placeholderMap.put(TD_CSS_CLASS, "service" + outputState.name());
        return placeholderMap;
    }

    /**
     * Generates '<div></div>' tag for the screenshots included in the {@link TestCase} and the suppressed {@link TestCaseStep}s.
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected String generateTestCaseScreenshotsHTML(TestCase testCase) {
        StringBuilder sb = new StringBuilder();
        ScreenshotDiv caseDiv = screenshotDivConverter.convert(testCase.getException(), getOutputScreenshotDivWidth());
        if (caseDiv != null) {
            sb.append(caseDiv.getPayloadString());
        }
        for (TestCaseStep step : testCase.getStepsAsSortedSet()) {
            ScreenshotDiv stepDiv = screenshotDivConverter.convert(step.getException(), getOutputScreenshotDivWidth());
            if (stepDiv != null) {
                sb.append(stepDiv.getPayloadString());
            }
        }
        return sb.toString();
    }
}
