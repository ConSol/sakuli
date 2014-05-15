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
 * Application Class
 *
 * @param optResumeOnException if this parameter is undefined, it will be false.
 *****************************************************************************************************/

/**
 * CONSTRUCTOR: Application - Represents an application.
 * @param {String} `applicationNameOrPath` Path to the application file. Example: C:\Windows\system32\notepad.exe
 * @param {Boolean} `optResumeOnException` Determines whether to ignore exceptions from this class. If this parameter is undefined, it will be false.
 */
function Application(applicationNameOrPath, optResumeOnException) {
    if (undefined == optResumeOnException) {
        optResumeOnException = new Boolean(false);
    }
    return loadApplication(Packages.de.consol.sakuli.loader.BeanLoader.loadApplication(applicationNameOrPath, optResumeOnException), optResumeOnException);
}

function loadApplication(javaObject, resumeOnException) {
    var that = {};
    var update;

    /**
     * Opens the created application.
     * For application with a long load time you may need to change the default sleep time with setSleepTime(...).
     *
     * @return this Application object.
     */
    that.open = function () {
        return update(that.javaObject.open());
    };

    /**
     * Focuses the current application, if the application is in the background.
     *
     * @return this Application object.
     */
    that.focus = function () {
        return update(that.javaObject.focus());
    };

    /**
     * Focuses a specific window of the application.
     *
     * @param {Integer} `windowNumber` identifies the window
     * @return this Application object.
     */
    that.focusWindow = function (windowNumber) {
        return update(that.javaObject.focusWindow(windowNumber));
    };

    /**
     * Closes the already existing application.
     *
     * @return this Application object.
     */
    that.closeApp = function () {
        return update(that.javaObject.closeApp());
    };

    /**
     * Sets the sleep time in seconds of the application actions to handle with long loading times.
     * The default sleep time is set to 1 seconds.
     *
     * @param {Integer} `seconds` sleep time in seconds
     * @return this Application object.
     */
    that.setSleepTime = function (seconds) {
        return update(that.javaObject.setSleepTime(seconds));
    };

    /**
     * Creates and returns a Region object from the application.
     *
     * @return a Region object.
     */
    that.getRegion = function () {
        return loadRegion(that.javaObject.getRegion(), that.resumeOnException);
    };

    /**
     * Creates and returns a Region object from a specific window of the application.
     *
     * @param {Integer} `windowNumber` identifies the window
     * @return a Region object.
     */
    that.getRegionForWindow = function (windowNumber) {
        return loadRegion(that.javaObject.getRegionForWindow(windowNumber), that.resumeOnException);
    };

    /**
     * @return the name of the current application.
     */
    that.getName = function () {
        return that.javaObject.getName();
    };


    /*****************************************************************************************************
     * INTERNAL CLASS FUNCTIONS - NOT REACHABLE IN THE TEST CASE EXECUTION
     *****************************************************************************************************/
    that.javaObject = javaObject;
    that.resumeOnException = new Boolean(resumeOnException);

    update = function (updatedJavaObject) {
        if (undefined == updatedJavaObject || updatedJavaObject == null) {
            return undefined;
        }
        return new loadApplication(updatedJavaObject, that.resumeOnException);
    };

    return that;
};



