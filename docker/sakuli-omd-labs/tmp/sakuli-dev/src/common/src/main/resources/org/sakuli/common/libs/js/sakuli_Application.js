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
 * Application Class - Represents an application.
 *
 * @example
 * ```
 * //windows
 * var editor = new Application("notepad.exe");
 * //linux
 * var editor = new Application("gedit");
 * ```
 *
 * @param {String} applicationNameOrPath Path to the application file. Example: `C:\Windows\system32\notepad.exe`
 * @param {Boolean} optResumeOnException Determines whether to ignore exceptions from this class. If this parameter is
 *     undefined, it will be false.
 * @returns an initialized {Application} object.
 * @namespace Application
 */
function Application(applicationNameOrPath, optResumeOnException) {
    if (undefined == optResumeOnException) {
        optResumeOnException = Boolean(false);
    }
    return initApplication(this, Packages.org.sakuli.loader.BeanLoader.loadApplication(applicationNameOrPath, optResumeOnException));
}

function initApplication(that, javaObject) {

    /**
     * Opens the created application.
     * For application with a long load time you may need to change the default sleep time with setSleepTime(...).
     *
     * @return this Application object.
     * @memberOf Application
     * @method open
     */
    that.open = function () {
        return update(that.javaObject.open());
    };

    /**
     * Focuses the current application, if the application is in the background.
     *
     * @return this Application object.
     * @memberOf Application
     * @method focus
     */
    that.focus = function () {
        return update(that.javaObject.focus());
    };

    /**
     * Focuses a specific window of the application.
     *
     * @param {number} windowNumber identifies the window
     * @return this Application object.
     * @memberOf Application
     * @method focusWindow
     */
    that.focusWindow = function (windowNumber) {
        return update(that.javaObject.focusWindow(windowNumber));
    };

    /**
     * Closes the already existing application.
     *
     * @param {boolean} optSilent (optional) if true, no exception will be thrown on errors and stop the test execution.
     * @return this Application object.
     * @memberOf Application
     * @method close
     */
    that.close = function (optSilent) {
        if (undefined == optSilent) {
            optSilent = Boolean(false);
        }
        return update(that.javaObject.closeApp(optSilent));
    };

    /**
     * Kill the already existing application hardly.
     *
     * @param {boolean} optSilent (optional) if true, no exception will be thrown on errors.
     * @return this Application object.
     * @memberOf Application
     * @method kill
     */
    that.kill = function (optSilent) {
        if (undefined == optSilent) {
            optSilent = Boolean(false);
        }
        return update(that.javaObject.kill(optSilent));
    };

    /**
     * Sets the sleep time in seconds of the application actions to handle with long loading times.
     * The default sleep time is set to 1 seconds.
     *
     * @param {number} seconds sleep time in seconds
     * @return this Application object.
     * @memberOf Application
     * @method setSleepTime
     */
    that.setSleepTime = function (seconds) {
        return update(that.javaObject.setSleepTime(seconds));
    };

    /**
     * Creates and returns a Region object from the application.
     *
     * @return a Region object.
     * @memberOf Application
     * @method getRegion
     */
    that.getRegion = function () {
        return initRegion({}, that.javaObject.getRegion());
    };

    /**
     * Creates and returns a Region object from a specific window of the application.
     *
     * @param {number} windowNumber identifies the window
     * @return a Region object.
     * @memberOf Application
     * @method getRegionForWindow
     */
    that.getRegionForWindow = function (windowNumber) {
        return initRegion({}, that.javaObject.getRegionForWindow(windowNumber));
    };

    /**
     * @return the name of the current application.
     * @memberOf Application
     * @method getName
     */
    that.getName = function () {
        return that.javaObject.getName();
    };


    /*****************************************************************************************************
     * INTERNAL CLASS FUNCTIONS - NOT REACHABLE IN THE TEST CASE EXECUTION
     *****************************************************************************************************/
    that.javaObject = javaObject;
    /**
     * @private (internal function)
     */
    function update(updatedJavaObject) {
        if (undefined == updatedJavaObject || updatedJavaObject == null) {
            return undefined;
        }
        //return new instance to keep old reference stable
        return initApplication({}, updatedJavaObject);
    }

    return that;
}



