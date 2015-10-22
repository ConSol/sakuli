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

package org.sakuli.datamodel.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum which represents the Sahi-Case-Stati in file "sahi_return_codes"
 *
 * @author tschneck
 *         Date: 21.06.13
 */
public enum TestSuiteState implements SakuliState {
    /**
     * value = 0
     */
    OK(0, "ok"),

    /**
     * value = 1
     */
    WARNING_IN_STEP(1, "warning in step"),

    /**
     * value = 2
     */
    WARNING_IN_CASE(2, "warning in case"),

    /**
     * value = 3
     */
    WARNING_IN_SUITE(3, "warning"),

    /**
     * value = 4
     */
    CRITICAL_IN_CASE(4, "critical case"),

    /**
     * value = 4
     */
    CRITICAL_IN_SUITE(5, "critical"),

    /**
     * value = 6
     */
    ERRORS(6, "EXCEPTION"),

    /**
     * state during the execution
     */
    RUNNING(-1, "suite still running");
    private final int errorCode;
    private final String stateDescription;

    TestSuiteState(int i, String stateDescription) {
        this.errorCode = i;
        this.stateDescription = stateDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getNagiosErrorCode() {
        if (isOk()) {
            return 0;
        } else if (isWarning()) {
            return 1;
        } else if (isCritical()) {
            return 2;
        }
        return 3;
    }

    @Override
    public boolean isOk() {
        return getOkCodes().contains(this);
    }

    @Override
    public boolean isWarning() {
        return getWarningCodes().contains(this);
    }

    @Override
    public boolean isCritical() {
        return getCriticalCodes().contains(this);
    }

    @Override
    public boolean isError() {
        return this.equals(ERRORS);
    }

    @Override
    public boolean isFinishedWithoutErrors() {
        return !Arrays.asList(ERRORS, RUNNING).contains(this);
    }


    @Override
    public String getNagiosStateDescription() {
        return stateDescription;
    }


    public List<TestSuiteState> getWarningCodes() {
        return Arrays.asList(WARNING_IN_SUITE, WARNING_IN_CASE, WARNING_IN_STEP);
    }

    public List<TestSuiteState> getOkCodes() {
        return Collections.singletonList(OK);
    }

    public List<TestSuiteState> getCriticalCodes() {
        return Arrays.asList(CRITICAL_IN_SUITE, CRITICAL_IN_CASE, ERRORS);
    }

}