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
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.services.forwarder.gearman.TextPlaceholder;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static org.sakuli.services.forwarder.gearman.TextPlaceholder.*;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
public abstract class AbstractOutputBuilder {
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YY HH:mm:ss");
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ScreenshotDivConverter screenshotDivConverter;
    @Autowired
    protected TestSuite testSuite;

    public static String replacePlaceHolder(String message, PlaceholderMap placeholderStringMap) {
        if (StringUtils.isBlank(message)) {
            throw new SakuliRuntimeException("Template for resolving test suite output is EMPTY!");
        }
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
        steps.stream().filter(step -> step.getState().isWarning() || step.getState().isError())
                .forEach(step -> {
                    sb.append(", step \"").append(step.getName()).append("\" ");
                    if (step.getState().isError()) {
                        sb.append("EXCEPTION: ").append(step.getExceptionMessages(true));
                    } else {
                        sb.append("over runtime (")
                                .append(NagiosFormatter.formatToSec(step.getDuration()))
                                .append("/warn at ")
                                .append(NagiosFormatter.formatToSec(step.getWarningTime()))
                                .append(")");
                    }
                });
        return sb.toString();
    }

    static String generateCaseInformation(SortedSet<TestCase> cases) {
        StringBuilder sb = new StringBuilder();
        cases.stream().filter(c -> c.getState().isWarning() || c.getState().isCritical() || c.getState().isError())
                .forEach(c -> {
                    sb.append(", case \"").append(c.getName()).append("\" ");
                    if (c.getState().isError()) {
                        sb.append("EXCEPTION: ").append(c.getExceptionMessages(true));
                    } else {
                        sb.append("over runtime (").append(NagiosFormatter.formatToSec(c.getDuration()));
                        if (c.getState().isCritical()) {
                            sb.append("/crit at ").append(NagiosFormatter.formatToSec(c.getCriticalTime()));
                        } else {
                            sb.append("/warn at ").append(NagiosFormatter.formatToSec(c.getWarningTime()));
                        }
                        sb.append(")");
                    }
                });
        return sb.toString();
    }

    protected static String cutTo(String string, int summaryMaxLength) {
        if (string != null && string.length() > summaryMaxLength) {
            return StringUtils.substring(string, 0, summaryMaxLength) + " ...";
        }
        return string;
    }

    protected abstract int getSummaryMaxLength();

    protected abstract String getOutputScreenshotDivWidth();

    protected String formatTestSuiteSummaryStateMessage(TestSuite testSuite, String templateSuiteSummary) {
        if (StringUtils.isBlank(templateSuiteSummary)) {
            throw new SakuliRuntimeException("Template for resolving test suite output is EMPTY!");
        }
        String result = replacePlaceHolder(templateSuiteSummary, getTextPlaceholder(testSuite));
        LOGGER.debug("{{xxx}} patterns in template '{}' replaced with message '{}'", templateSuiteSummary, result);
        return cutTo(result, getSummaryMaxLength());
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
        placeholderMap.put(CASE_INFORMATION, generateCaseInformation(testSuite.getTestCasesAsSortedSet()));
        placeholderMap.put(STEP_INFORMATION,
                testSuite.getTestCasesAsSortedSet().stream()
                        .map(c -> generateStepInformation(c.getStepsAsSortedSet()))
                        .collect(Collectors.joining()));
        return placeholderMap;
    }

    private String  generateStateSummary(TestSuiteState state) {
        StringBuilder summary = new StringBuilder(state.isError() ? "" : STATE_DESC.getPattern());
        switch (state) {
            case OK:
                summary.append(" (").append(DURATION.getPattern()).append("s)");
                break;
            case WARNING_IN_STEP:
                summary.append(STEP_INFORMATION.getPattern());
                break;
            case WARNING_IN_CASE:
                summary.append(CASE_INFORMATION.getPattern());
                break;
            case WARNING_IN_SUITE:
                summary.append(" (").append(DURATION.getPattern()).append("s/warn at ").append(WARN_THRESHOLD.getPattern()).append("s)");
                break;
            case CRITICAL_IN_CASE:
                summary.append(CASE_INFORMATION.getPattern());
                break;
            case CRITICAL_IN_SUITE:
                summary.append(" (").append(DURATION.getPattern()).append("s/crit at ").append(CRITICAL_THRESHOLD.getPattern()).append("s)");
                break;
            case ERRORS:
                summary.append("(").append(DURATION.getPattern()).append("s) ").append(STATE_DESC.getPattern()).append(": '").append(ERROR_MESSAGE.getPattern()).append("'");
                break;
            default:
                break;
        }
        return summary.toString();
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
        ScreenshotDiv caseDiv = screenshotDivConverter.convert(testCase.getException());
        if (caseDiv != null) {
            sb.append(caseDiv.getPayloadString());
        }
        for (TestCaseStep step : testCase.getStepsAsSortedSet()) {
            ScreenshotDiv stepDiv = screenshotDivConverter.convert(step.getException());
            if (stepDiv != null) {
                sb.append(stepDiv.getPayloadString());
            }
        }
        return sb.toString();
    }
}
