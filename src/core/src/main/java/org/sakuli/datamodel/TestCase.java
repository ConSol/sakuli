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

package org.sakuli.datamodel;

import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.exceptions.SakuliException;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * @author tschneck Date: 17.06.13
 */
public class TestCase extends AbstractTestDataEntity<SakuliException, TestCaseState> {

    /**
     * {@link #id} and {@link # startUrl} will be set with the method {@link org.sakuli.starter.SahiConnector#init()}
     */
    private String startUrl;
    /**
     * will be set with the method {@link org.sakuli.actions.TestCaseAction#saveResult(String, String, String,
     * String, String)}
     */
    private String lastURL;
    private List<TestCaseStep> steps;

    private Path tcFile;

    /**
     * Creates a TestCase
     *
     * @param name       descriptive name of the test case
     * @param testCaseId id for this test_case
     */
    public TestCase(String name, String testCaseId) {
        this.name = name;
        this.id = testCaseId;
        /**
         * needed to be set to -1, so the function {@link org.sakuli.actions.TestCaseAction#addTestCaseStep(String, String, String, String, int)}
         * can check if the method {@link org.sakuli.actions.TestCaseAction#initTestCaseWarningAndCritical(String, int, int)}
         * have been called at the beginning of this test case.
         */
        warningTime = -1;
        criticalTime = -1;
        this.state = TestCaseState.INIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshState() {
        if (exception != null) {
            state = TestCaseState.ERRORS;
            return;
        }
        boolean stepWarning = false;
        if (steps != null) {
            for (TestCaseStep step : steps) {
                step.refreshState();
                switch (step.getState()) {
                    case WARNING:
                        stepWarning = true;
                        break;
                    case ERRORS:
                        state = TestCaseState.ERRORS;
                        return;
                }
            }
        }
        TestCaseState newState;
        if (criticalTime > 0 && getDuration() > criticalTime) {
            newState = TestCaseState.CRITICAL;
        } else if (warningTime > 0 && getDuration() > warningTime) {
            newState = TestCaseState.WARNING;
        } else if (stepWarning) {
            newState = TestCaseState.WARNING_IN_STEP;
        } else {
            newState = TestCaseState.OK;
        }
        if (state == null || newState.getErrorCode() > state.getErrorCode()) {
            state = newState;
        }
    }

    @Override
    public String getResultString() {
        String stout = "\n\t======== test case \"" + getId() + "\" ended with " + getState() + " ================="
                + "\n\ttest case id: " + this.getId()
                + super.getResultString().replace("\n", "\n\t")
                + "\n\tstart URL: " + this.getStartUrl()
                + "\n\tlast URL: " + this.getLastURL();

        //steps
        for (TestCaseStep step : getStepsAsSortedSet()) {
            stout += step.getResultString();
        }
        return stout;
    }

    /**
     * Getter and Setter**
     */

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getLastURL() {
        return lastURL;
    }

    public void setLastURL(String lastURL) {
        this.lastURL = lastURL;
    }

    public List<TestCaseStep> getSteps() {
        return steps == null ? new ArrayList<>() : steps;
    }

    public void setSteps(List<TestCaseStep> steps) {
        this.steps = steps;
    }

    public void addStep(TestCaseStep step) {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        if (!steps.contains(step)) {
            steps.add(step);
        }
        Collections.sort(steps);
    }

    @Override
    public String toString() {
        return "TestCase{" +
                super.toString() +
                ", id='" + id + '\'' +
                ", startUrl='" + startUrl + '\'' +
                ", lastURL='" + lastURL + '\'' +
                ", steps=" + steps +
                ", tcFile=" + tcFile +
                "} ";
    }

    public String getActionValueString() {
        return "id=" + getId()
                + ", name=" + getName();
    }

    public Path getTcFile() {
        return tcFile;
    }

    public void setTcFile(Path tcFile) {
        this.tcFile = tcFile;
    }


    /**
     * @return all {@link TestCaseStep}s as {@link SortedSet} or a empty set if no test case steps are specified.
     */
    public SortedSet<TestCaseStep> getStepsAsSortedSet() {
        if (!CollectionUtils.isEmpty(steps)) {
            return new TreeSet<>(steps);
        }
        return new TreeSet<>();
    }

    @Override
    public String getExceptionMessages(boolean flatFormatted) {
        String caseErrorMessage = super.getExceptionMessages(flatFormatted);
        if (caseErrorMessage == null) {
            caseErrorMessage = "";
        }
        for (TestCaseStep step : getStepsAsSortedSet()) {
            final String stepErrorMessage = step.getExceptionMessages(flatFormatted);

            if (isNotBlank(stepErrorMessage)) {
                if (flatFormatted && isNotBlank(caseErrorMessage)) {
                    caseErrorMessage += " - ";
                }
                if (!flatFormatted) {
                    caseErrorMessage += "\n\t";
                }
                caseErrorMessage += "STEP '" + step.getId() + "': " + stepErrorMessage;
            }
        }
        return caseErrorMessage;
    }
}
