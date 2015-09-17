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
 * __All Sahi-API__ functions are natively usable in Sakuli. For a complete documentation, see
 * [Sahi-API](http://sahi.co.in/w/all-apis).
 *
 * @namespace Sahi-API
 */


/*****************************************************************************************************
 * INCLUDE THE ACTIONS
 *****************************************************************************************************/
_include("sakuli_Application.js");
_include("sakuli_Environment.js");
_include("sakuli_Key.js");
_include("sakuli_MouseButton.js");
_include("sakuli_Region.js");
_include("sakuli_Logger.js");

/*****************************************************************************************************
 * TEST CASE OBJECT
 *****************************************************************************************************/

/**
 * TestCase - initializes the Sakuli object and sets the warning and critical time for this test case.
 * @example
 * ```
 * var testCase = new TestCase(20,30, "path-to/image-folder-name");
 * ```
 *
 * @param {number} warningTime threshold in seconds. If the threshold is set to 0,
 *                 the execution time will never exceed, so the state will be always OK!
 * @param {number} criticalTime threshold in seconds. If the threshold is set to 0,
 *                 the execution time will never exceed, so the state will be always OK!
 * @param {String[]} optImagePathArray (optional) Path or Array of Paths to the folder containing the image patterns
 *                                     for these test cases.
 *
 * @returns an initialized Sakuli object.
 * @namespace TestCase
 */
function TestCase(warningTime, criticalTime, optImagePathArray) {
    var that = {};
    var env;

    that.javaObject = null;
    that.tcID = null;
    env = null;

    if (undefined == optImagePathArray) {
        optImagePathArray = new Array(resolveDefaultImagePath());
    }
    else if (optImagePathArray instanceof String) {
        optImagePathArray = new Array(optImagePathArray);
    }
    /*****************************************************************************************************
     * TEST CASE HANDLING FUNCTIONS
     *****************************************************************************************************/

    /**
     * Adds the additional paths to the current image library of the TestCase.
     * If a relative path is assigned, the current testcase folder will be used as current directory.
     *
     * @param {string} imagePaths one or more path strings
     * @memberOf TestCase
     * @method   addImagePaths
     */
    that.addImagePaths = function (imagePaths) {
        if (imagePaths instanceof  String) {
            that.javaObject.addImagePathsAsString(new Array(imagePaths));
        } else {
            that.javaObject.addImagePathsAsString(imagePaths);
        }
    };

    /**
     * A step allows to sub-divide a case to measure logical units, such as "login", "load report" etc. in its
     * particular runtime. Together with the test Case, a special "step" timer is started. Each time endOfStep is
     * called, the current timer value is read out, stored with the step name (first parameter) and gets resetted . If
     * the runtime exceeds the step threshold (second parameter), the step is saved with state "WARNING".
     * @param {String} stepName
     * @param {number} optWarningTime (optional) threshold in seconds, default = 0. If the threshold is set to 0,
     *                 the execution time will never exceed, so the state will be always OK!
     * @memberOf TestCase
     * @method endOfStep
     */
    that.endOfStep = function (stepName, optWarningTime) {
        if (undefined == optWarningTime) {
            optWarningTime = 0;
        }
        if (undefined == warningTime) {
            warningTime = 0;
        }
        var currentTime = (new Date()).getTime();
        //call the backend
        that.javaObject.addTestCaseStep(stepName, that.stepStartTime, currentTime, optWarningTime);
        //set stepstart for the next step
        that.stepStartTime = currentTime;
    };


    /**
     * Handles any Exception or Error. The handleException function calls the Java backend and stores the Exception
     * for further processing.
     *
     * Use it at the end of a catch-block.
     * @example
     * ```
     * try {
     *   ... do something
     * } catch (e) {
     *     sakuli.handleException(e);
     * }
     * ```
     *
     * @param {Error} e any Exception or Error
     * @memberOf TestCase
     * @method handleException
     */
    that.handleException = function (e) {
        if (e.javaException instanceof java.lang.Exception) {
            Logger.logDebug("FROM HANDLE JAVA-EXCEPTION:" + e.javaException);
            that.javaObject.handleException(e.javaException);
        } else if (e.javaException instanceof java.lang.RuntimeException) {
            Logger.logDebug("FROM HANDLE RUNTIME-EXCEPTION:" + e.javaException);
            that.javaObject.handleException(e.javaException);
        } else {
            if (e !== "||STOP EXECUTION||") {
                Logger.logDebug("FROM HANDLE RHINO-EXCEPTION:" + e);
                that.javaObject.handleException(e);
            }
        }
        _logExceptionAsFailure(e);
    };


    /**
     * Saves the results of the current test case for further processing.
     *
     * Should be called in finally-block of the test case:
     * @example
     *  ```
     *  try {
     *      ... do something
     *  } catch (e) {
     *      sakuli.handleException(e);
     *  } finally {
     *      sakuli.saveResult();
     *  }
     *  ```
     *
     * @memberOf TestCase
     * @method saveResult
     */
    that.saveResult = function () {
        Logger.logInfo("=========== SAVE Test Case '" + that.tcID + "' ==================");
        //create the values
        var stopTime, lastURL = "", browser = "";
        stopTime = (new Date()).getTime();
        _set(lastURL, window.document.location.href);
        _set(browser, navigator.userAgent);
        // Agent description can contain semicolon, replace globally
        browser = browser.replace(/;/g, ',');
        //call the backend
        that.javaObject.saveResult(that.tcID, that.startTime, stopTime, lastURL, browser);
    };

    /**
     * Returns the __current__ id of this test case.
     * @returns {String} id
     * @memberOf TestCase
     * @method getID
     */
    that.getID = function () {
        return that.tcID;
    };

    /**
     * Updates and returns the URL of the last visited URL
     * @returns {String} last visited URL
     * @memberOf TestCase
     * @method getLastURL
     */
    that.getLastURL = function () {
        var lastURL = "";
        _set(lastURL, window.document.location.href);
        that.javaObject.setLastURL(lastURL);
        return that.javaObject.getLastURL();
    };

    /**
     * @return {String} the folder path of the current testcase.
     * @memberOf TestCase
     * @method getTestCaseFolderPath
     */
    that.getTestCaseFolderPath = function () {
        return that.javaObject.getTestCaseFolderPath();
    };

    /**
     * @return {String} the folder path of the current testcase.
     * @memberOf TestCase
     * @method getTestSuiteFolderPath
     */
    that.getTestSuiteFolderPath = function () {
        return that.javaObject.getTestSuiteFolderPath();
    };

    /**
     * Creates a new test case based exception with an optional screenshot at the calling time.
     * Will be called from sakuli.js or in side of 'org.sakuli.javaDSL.AbstractSakuliTest'.
     *
     * @param {String}   message error message
     * @param {Boolean} screenshot enable / disable screenshot functionality
     * @memberOf TestCase
     * @method throwException
     */
    that.throwException = function (message, screenshot) {
        if (undefined == screenshot) {
            screenshot = true;
        }
        that.javaObject.throwException(message, screenshot)
    };

    /*****************************************************************************************************
     * INTERNAL CLASS FUNCTIONS - NOT REACHABLE IN THE TEST CASE EXECUTION
     *****************************************************************************************************/

    /**
     * @private
     * (internal function)
     * @returns the default image path of this testcase
     */
    function resolveDefaultImagePath() {
        var defaultImagePath = _resolvePath();
        defaultImagePath = defaultImagePath.substring(0, defaultImagePath.lastIndexOf(Packages.java.io.File.separator));
        java.lang.System.out.println("set default path for images to the test case folder \"" + defaultImagePath + "\"");
        return defaultImagePath;
    }

    /**
     * @private
     * (internal function)
     * This function will be called automatically on the startup of TestCase().
     */
    function init() {
        _stopOnError();
        /**
         * get @type {Environment}
         */
        env = new Environment();
        /***
         * init the java backed
         */
        Logger.logDebug("get Backend - step 1 (load backend)");
        that.javaObject = Packages.org.sakuli.loader.BeanLoader.loadTestCaseAction();
        Logger.logDebug("get Backend - step 2 (get the test case id)");
        that.tcID = that.javaObject.getIdFromPath(_resolvePath());

        /**
         * - set the start time for this cass and for the first step
         * - set the warning and critical time.
         */
        that.startTime = (new Date()).getTime();
        that.stepStartTime = (new Date()).getTime();
        that.javaObject.init(that.tcID, warningTime, criticalTime, optImagePathArray);
        Logger.logInfo("Now start to execute the test case '" + that.tcID + "' ! \n ....\n ...\n....");
    }

    init();
    return that;
}