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

/**
 * Enum which represents the Sahi-Case-Stati in file "sahi_return_codes"
 *
 * @author tschneck
 *         Date: 21.06.13
 */
public enum TestCaseStepState implements SakuliState {
    /**
     * value = 0
     */
    OK(0, "ok"),

    /**
     * value = 1
     */
    WARNING(1, "warning"),

    /**
     * value = 1
     */
    ERRORS(4, "EXCEPTION"),

    /**
     * state before the execution
     */
    INIT(-1, "initialized");

    private final int errorCode;
    private final String stateDescription;

    TestCaseStepState(int i, String stateDescription) {
        this.errorCode = i;
        this.stateDescription = stateDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public int getNagiosErrorCode() {
        switch (this) {
            case OK:
                return 0;
            case WARNING:
                return 1;
            case ERRORS:
                return 2;
        }
        return 3;
    }

    @Override
    public String getNagiosStateDescription() {
        return stateDescription;
    }

    @Override
    public boolean isOk() {
        return this.equals(OK);
    }

    @Override
    public boolean isWarning() {
        return this.equals(WARNING);
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    @Override
    public boolean isError() {
        return this.equals(ERRORS);
    }

    @Override
    public boolean isFinishedWithoutErrors() {
        return !Arrays.asList(INIT, ERRORS).contains(this);
    }


}