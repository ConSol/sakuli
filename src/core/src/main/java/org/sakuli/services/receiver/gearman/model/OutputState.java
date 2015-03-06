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

package org.sakuli.services.receiver.gearman.model;

import org.sakuli.datamodel.state.SakuliState;

/**
 * @author tschneck
 *         Date: 11.07.14
 */
public enum OutputState {
    OK(0, "[OK]"),
    WARNING(1, "[WARN]"),
    CRITICAL(2, "[CRIT]"),
    UNKNOWN(3, "[UNKN]");
    private final int errorCode;
    private final String shortState;

    OutputState(int errorCode, String shortState) {
        this.errorCode = errorCode;
        this.shortState = shortState;
    }

    public static OutputState lookupSakuliState(SakuliState sakuliState) {
        if (sakuliState != null) {
            int errorCode = sakuliState.getNagiosErrorCode();
            if (OK.errorCode == errorCode) {
                return OK;
            } else if (WARNING.errorCode == errorCode) {
                return WARNING;
            } else if (CRITICAL.errorCode == errorCode) {
                return CRITICAL;
            }
        }
        return UNKNOWN;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getShortState() {
        return shortState;
    }
}
