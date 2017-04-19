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

/**** Exclude this global variables from JSLint Warnings ****/
/* global navigator, window, java, Packages,saveResult,step, $output, _set, _stopOnError, _logExceptionAsFailure,_resolvePath,_include, $sahi_userdata, $guid, $capture, initialize */

/**
 * Logger - Logging functions to do 'debug, 'info', 'warning' and 'error' log entries.
 *
 * @namespace Logger
 */
function Logger() {
}

/**
 * make a error-log over Java backend into the log file.
 * This won't stop the execution of the test case.
 *
 * @param {String} message as a String
 * @memberOf Logger
 * @method logError
 */
Logger.logError = function (message) {
    Packages.org.sakuli.actions.logging.Logger.logError(message);
};

/**
 * make a debug-log over Java backend into the log file.
 *
 * @param {String} message as a String
 * @memberOf Logger
 * @method logWarning
 */
Logger.logWarning = function (message) {
    Packages.org.sakuli.actions.logging.Logger.logWarning(message);
};

/**
 * make a info-log over Java backend into the log file.
 *
 * @param {String} message as a String
 * @memberOf Logger
 * @method logInfo
 */
Logger.logInfo = function (message) {
    Packages.org.sakuli.actions.logging.Logger.logInfo(message);
};
/**
 * make a debug-log over Java backend into the log file.
 *
 * @param {String} message as a String
 * @memberOf Logger
 * @method logDebug
 */
Logger.logDebug = function (message) {
    Packages.org.sakuli.actions.logging.Logger.logDebug(message);
};

//}



