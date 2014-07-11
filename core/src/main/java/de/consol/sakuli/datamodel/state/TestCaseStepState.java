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

package de.consol.sakuli.datamodel.state;

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
    WARNING(1, "warning");

    private final int errorCode;
    private final String stateDescription;

    private TestCaseStepState(int i, String stateDescription) {
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
        }
        return 3;
    }

    @Override
    public String getNagiosStateDescription() {
        return stateDescription;
    }


}