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

package org.sakuli.starter.sahi.datamodel.actions;

import net.sf.sahi.report.ResultType;
import net.sf.sahi.report.TestResult;
import org.sakuli.datamodel.actions.LogLevel;

import java.util.Arrays;

/**
 * Wrapper for the {@link TestResult} class with getters.
 *
 * @author Tobias Schneck
 */
public class LogResult extends TestResult {

    private final String message;
    private final ResultType type;
    private final String debugInfo;
    private final String failureMsg;

    public LogResult(String message, ResultType type, String debugInfo, String failureMsg) {
        super(clean(message), type, clean(debugInfo), clean(failureMsg));
        this.message = clean(message);
        this.type = type;
        this.debugInfo = clean(debugInfo);
        this.failureMsg = clean(failureMsg);
    }

    public static ResultType lookupResultType(LogLevel logLevel) {
        if (LogLevel.ERROR.equals(logLevel)) {
            return ResultType.ERROR;
        }
        if (Arrays.asList(LogLevel.INFO, LogLevel.WARNING).contains(logLevel)) {
            return ResultType.INFO;
        }
        return null;
    }

    private static String clean(String string) {
        if (string != null) {
            return string.replace("\n", " ").trim();
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public ResultType getType() {
        return type;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public String getFailureMsg() {
        return failureMsg;
    }

    @Override
    public String toString() {
        return type.getName() + " - " + toErrorMessage();
    }

    public String toErrorMessage() {
        StringBuilder builder = new StringBuilder();
        if (failureMsg != null && (type.equals(ResultType.ERROR) || type.equals(ResultType.FAILURE))) {
            builder.append(failureMsg).append(" => ");
        }
        return builder.append(message).append(" ... @CALL: ").append(debugInfo).toString();

    }
}
