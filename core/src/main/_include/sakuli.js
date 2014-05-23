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

/**** Exclude this global variables from JSLint Warnings ****/
/* global navigator, window, java, Packages,saveResult,step, $output, _set, _stopOnError, _logExceptionAsFailure,_resolvePath,_include, $sahi_userdata, $guid, $capture, initialize */


/*****************************************************************************************************
 * INCLUDE THE ACTIONS
 *****************************************************************************************************/
_include("sakuli_Application.js");
_include("sakuli_Environment.js");
_include("sakuli_Key.js");
_include("sakuli_Region.js");

/*****************************************************************************************************
 * TEST CASE OBJECT
 *****************************************************************************************************/

/**
 * CONSTRUCTOR: TestCase - Sets the current test case folder as default folder and calls TestCaseWithImagePathArray(...).
 * @param {Integer} `warningTime` threshold in seconds
 * @param {Integer} `criticalTime` threshold in seconds
 */
function TestCase(warningTime, criticalTime) {
    var defaultImagePath = _resolvePath();
    defaultImagePath = defaultImagePath.substring(0, defaultImagePath.lastIndexOf(Packages.java.io.File.separator));
    java.lang.System.out.println("set default path for images to the test case folder \"" + defaultImagePath + "\"");

    return new TestCaseWithImagePath(warningTime, criticalTime, defaultImagePath);
}

/**
 * CONSTRUCTOR: TestCase - This function initializes the Sakuli object and sets the warning and critical time for this test case.
 * @param {Integer} `warningTime` threshold in seconds
 * @param {Integer} `criticalTime` threshold in seconds
 * @param {String} `picPath` Path to the folder containing the pictures for these test cases.
 *
 * @returns an initialized Sakuli object.
 */
function TestCaseWithImagePath(warningTime, criticalTime, picPath) {
    return new TestCaseWithImagePathArray(warningTime, criticalTime, new Array(picPath));
}

/**
 * This function initializes the Sakuli object and sets the warning and critical time for this test case.
 * @param {Integer} `warningTime` threshold in seconds
 * @param {Integer} `criticalTime` threshold in seconds
 * @param {String[]} `picPathArray` An Array of Paths to the folders containing all the pictures for these test cases.
 *
 * @returns an initialized Sakuli object.
 */
function TestCaseWithImagePathArray(warningTime, criticalTime, picPathArray) {
    var that = {};
    var env, init;

    that.javaObject = null;
    that.tcID = null;
    env = null;

    /*****************************************************************************************************
     * TEST CASE HANDLING FUNCTIONS
     *****************************************************************************************************/

    /**
     * A step allows to sub-divide a case to measure logical units, such as "login", "load report" etc. in its particular runtime.
     * Together with the test Case, a special "step" timer is started. Each time endOfStep is called, the current timer value is read out,
     * stored with the step name (first parameter) and gets resetted . If the runtime exceeds the step threshold (second parameter), the step is saved with state "WARNING".
     * @param {String} `stepName`
     * @param {Integer} `warningTime` threshold in seconds
     */
    that.endOfStep = function (stepName, warningTime) {
        var currentTime = (new Date()).getTime();
        //call the backend
        that.javaObject.addTestCaseStep(stepName, that.stepStartTime, currentTime, warningTime);
        //set stepstart for the next step
        that.stepStartTime = currentTime;
    };


    /**
     * Handles any Exception or Error. The handleException function calls the Java backend and stores the Exception to the database.
     *
     * Use it at the end of a catch-block. Example:
     *        try {
     *          ... do something  
     *      } catch (e) {  
     *          sakuli.handleException(e);  
     *      }
     *
     * @param {Exception} `e` Any Exception or Error
     */
    that.handleException = function (e) {
        if (e.javaException instanceof java.lang.Exception) {
            env.logDebug("FROM HANDLE JAVA-EXCEPTION:" + e.javaException);
            that.javaObject.handleException(e.javaException);
        } else if (e.javaException instanceof java.lang.RuntimeException) {
            env.logDebug("FROM HANDLE RUNTIME-EXCEPTION:" + e.javaException);
            that.javaObject.handleException(e.javaException);
        } else {
            if (e !== "||STOP EXECUTION||") {
                env.logDebug("FROM HANDLE RHINO-EXCEPTION:" + e);
                that.javaObject.handleException(e);
            }
        }
        _logExceptionAsFailure(e);
    };


    /**
     * Saves the results of the current test case to the database.
     *
     * Should be called in finally-block of the test case. Example:
     *        try {
     *          ... do something  
     *		} catch (e) {  
     *          sakuli.handleException(e);  
     *      } finally {  
     *          sakuli.saveResult();  
     *      }
     */
    that.saveResult = function () {
        env.logInfo("=========== SAVE Test Case '" + that.tcID + "' ==================");
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
     * Returns the current id of this test case.
     * @returns id
     */
    that.getID = function () {
        return that.tcID;
    };

    /*****************************************************************************************************
     * INTERNAL CLASS FUNCTIONS - NOT REACHABLE IN THE TEST CASE EXECUTION
     *****************************************************************************************************/

    /**
     * (internal function)
     * This function will be called automatically on the startup of TestCase().
     */
    init = function () {
        _stopOnError();
        /**
         * get @type {Environment}
         */
        env = new Environment();
        /***
         * init the java backed
         */
        env.logDebug("get Backend - step 1 (load backend)");
        that.javaObject = Packages.de.consol.sakuli.loader.BeanLoader.loadTestCaseAction();
        env.logDebug("get Backend - step 2 (get the test case id)");
        that.tcID = that.javaObject.getIdFromPath(_resolvePath());

        /**
         * - set the start time for this cass and for the first step
         * - set the warning and critical time.
         */
        that.startTime = (new Date()).getTime();
        that.stepStartTime = (new Date()).getTime();
        that.javaObject.init(that.tcID, warningTime, criticalTime, picPathArray);
        env.logInfo("Now start to execute the test case '" + that.tcID + "' ! \n ....\n ...\n....");
    };

    init();
    return that;
}


