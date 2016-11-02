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

package org.sakuli.actions.logging;

import org.sakuli.datamodel.actions.LogLevel;

/**
 * The logging will take place in the {@link org.sakuli.aop.RhinoAspect}.
 * This are only EntryPoints for {@link org.sakuli.aop.RhinoAspect#doLoggingLog(org.aspectj.lang.JoinPoint, LogToResult)}.
 *
 * @author tschneck
 *         Date: 24.06.13
 */
public class Logger {

    @LogToResult(logArgsOnly = true, level = LogLevel.INFO)
    public static void logInfo(String message) {
    }

    @LogToResult(logArgsOnly = true, level = LogLevel.ERROR)
    public static void logError(String message) {
    }

    @LogToResult(logArgsOnly = true, level = LogLevel.WARNING)
    public static void logWarning(String message) {
    }

    @LogToResult(logArgsOnly = true, level = LogLevel.DEBUG)
    public static void logDebug(String message) {
    }

}
