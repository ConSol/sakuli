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
 * Environment - Represents the environment of the current test host.
 *
 * @param {Boolean} optResumeOnException (optional) if this parameter is undefined, it will be false.
 * @namespace Environment
 */
function Environment(optResumeOnException) {
    if (undefined == optResumeOnException) {
        optResumeOnException = Boolean(false);
    }
    return initEnvironment(this, Packages.org.sakuli.loader.BeanLoader.loadEnvironment(optResumeOnException));
}

/**
 * @private (internal loader function)
 */
function initEnvironment(that, javaObject) {

    /**
     * Set a new default similarity for the screen capturing methods.
     * @param {number} similarity value between 0 and 1, default = 0.8
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method setSimilarity
     */
    that.setSimilarity = function (similarity) {
        return update(that.javaObject.setSimilarity(similarity));
    };

    /**
     * Resets the current similarty of the screen capturing methods to the original default value of 0.8.
     * @return this {@link Environment} or NULL on errors.
     * @memberOf Environment
     * @method resetSimilarity
     */
    that.resetSimilarity = function () {
        return update(that.javaObject.resetSimilarity());
    };

    /**
     * Get a Region object from the current focused window
     * @return a Region object from the current focused window
     *         or NULL on errors.
     * @memberOf Environment
     * @method getRegionFromFocusedWindow
     */
    that.getRegionFromFocusedWindow = function () {
        return initRegion({}, that.javaObject.getRegionFromFocusedWindow());
    };

    /**
     * Takes a screenshot of the current screen and saves it to the overgiven path.
     * If there ist just a file name, the screenshot will be saved in your testsuite log folder.
     *
     * @param {String} pathName `pathname/filname.format` or just `filename.format`
     * @example
     *  ```
     *  environment.takeScreenshot("test.jpg");
     *  ```
     * @memberOf Environment
     * @method takeScreenshot
     */
    that.takeScreenshot = function (pathName) {
        return that.javaObject.takeScreenshot(pathName);
    };

    /**
     * Takes a screenshot of the current screen and add the current timestamp in the file name like e.g.:
     * @example
     * ```
     * env.takeScreenshotWithTimestamp("my-screenshot");
     * ```
     * saved under: `mytestsuite/testcase1/2017_08_03_14_06_13_255_my_screenshot.png`
     *
     *
     * @param {String} filenamePostfix postfix for the final filename
     *                                 Default: screenshot
     * @param {String} optFolderPath   optional FolderPath, where to save the screenshot.
     *                                 If null or empty: testscase folder will be used
     * @param {string} optFormat       optional format, for the screenshot (currently supported: jpg and png)
     *                                 If null or empty use property `sakuli.screenshot.format`
     * @return {String} file path to the created screenshot OR null on errors
     * @memberOf Environment
     * @method takeScreenshotWithTimestamp
     */
    that.takeScreenshotWithTimestamp = function (filenamePostfix, optFolderPath, optFormat) {
        if (undefined == filenamePostfix) {
            filenamePostfix = "screenshot";
        }
        if (undefined == optFolderPath) {
            optFolderPath = '';
        }
        if (undefined == optFormat) {
            optFormat = '';
        }
        var path = that.javaObject.takeScreenshotWithTimestamp(filenamePostfix, optFolderPath, optFormat);
        return path != null ? path.toString() : null;
    };


    /**
     * Blocks the current testcase execution for x seconds
     *
     * @param {number} seconds to sleep
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method sleep
     */
    that.sleep = function (seconds) {
        return update(that.javaObject.sleep(Packages.java.lang.Double.valueOf(seconds)));
    };

    /**
     * Blocks the current testcase execution for x milliseconds
     *
     * @param {number} milliseconds to sleep
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method sleepMs
     */
    that.sleepMs = function (milliseconds) {
        return update(that.javaObject.sleepMs(milliseconds));
    };

    /**
     * @return the current content of the clipboard as String or NULL on errors
     * @memberOf Environment
     * @method getClipboard
     */
    that.getClipboard = function () {
        return that.javaObject.getClipboard();
    };

    /**
     * sets the String paramter to the system clipboard
     *
     * @param {String} text text as string
     * @return this Environment.
     * @memberOf Environment
     * @method setClipboard
     */
    that.setClipboard = function (text) {
        return update(that.javaObject.setClipboard(text));
    };

    /**
     * pastes the current clipboard content into the focused area.
     * Will do the same as "STRG + V".
     *
     * @return this Environment.
     * @memberOf Environment
     * @method pasteClipboard
     */
    that.pasteClipboard = function () {
        return update(that.javaObject.pasteClipboard());
    };


    /**
     * copy the current selected item or text to the clipboard.
     * Will do the same as "STRG + C".
     *
     * @return this Environment.
     * @memberOf Environment
     * @method copyIntoClipboard
     */
    that.copyIntoClipboard = function () {
        return update(that.javaObject.copyIntoClipboard());
    };

    /**
     * Clean the content of the clipboard.
     *
     * @memberOf Environment
     * @method cleanClipboard
     */
    that.cleanClipboard = function () {
        return update(that.javaObject.cleanClipboard());
    };


    /**
     * pastes the text at the current position of the focus/carret <br/>using the
     * clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)
     *
     * @param {String} text a string, which might contain unicode characters
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method paste
     */
    that.paste = function (text) {
        return update(that.javaObject.paste(text));
    };


    /**
     * makes a masked paste(String) without any logging.
     *
     * @param {String} text a string, which might contain unicode characters
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method pasteMasked
     */
    that.pasteMasked = function (text) {
        return update(that.javaObject.pasteMasked(text));
    };

    /**
     * combines pasteMasked(String) and decryptSecret(String).
     *
     * @param {String} text encrypted secret
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method pasteAndDecrypt
     */
    that.pasteAndDecrypt = function (text) {
        return update(that.javaObject.pasteAndDecrypt(text));
    };

    /**
     * Enters the given text one character/key after another using keyDown/keyUp.
     * <p/>
     * About the usable Key constants see documentation of Key.
     * The function could also type UTF-8 unicode characters, if the OS supports it.
     * The text is entered at the current position of the focus.
     *
     * @param {String} text containing characters and/or Key constants
     * @param {String} optModifiers (optional) an String with only Key constants.
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method type
     */
    that.type = function (text, optModifiers) {
        if (undefined == optModifiers) {
            return update(that.javaObject.type(text));
        }
        return update(that.javaObject.type(text, optModifiers));
    };

    /**
     * Enters the given text one character/key after another using keyDown/keyUp.
     * The entered text will be masked at the logging.
     * <p/>
     * About the usable Key constants see documentation of Key.
     * The function could also type UTF-8 unicode characters, if the OS supports it.
     * The text is entered at the current position of the focus.
     *
     * @param {String} text containing characters and/or Key constants
     * @param {String} optModifiers (optional) an String with only Key constants.
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method typeMasked
     */
    that.typeMasked = function (text, optModifiers) {
        if (undefined == optModifiers) {
            return update(that.javaObject.typeMasked(text));
        }
        return update(that.javaObject.typeMasked(text, optModifiers));
    };


    /**
     * Decrypt and enters the given text one character/key after another using keyDown/keyUp.
     * The entered text will be masked at the logging. For the details of the decryption see decryptSecret(String).
     * <p/>
     * About the usable Key constants see documentation of Key.
     * The function could also type UTF-8 unicode characters, if the OS supports it.
     * The text is entered at the current position of the focus.
     *
     * @param {String} text containing characters and/or Key constants
     * @param {String} optModifiers (optional) an String with only Key constants.
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method typeAndDecrypt
     */
    that.typeAndDecrypt = function (text, optModifiers) {
        if (undefined == optModifiers) {
            return update(that.javaObject.typeAndDecrypt(text))
        }
        return update(that.javaObject.typeAndDecrypt(text, optModifiers));
    };

    /**
     * Decrypt a encrypted secret and returns the value at runtime.
     * The decryption will only work like described at https://github.com/ConSol/sakuli/blob/master/docs/manual/testdefinition/advanced-topics/sakuli-encryption.adoc .
     * There will be no logging with the decrypted secret during this step.
     * <p/>
     * To create a encrypted secret see "sakuli-manual.md".
     *
     * @param {String} secret encrypted secret as String
     * @return decrypted String
     * @memberOf Environment
     * @method decryptSecret
     */
    that.decryptSecret = function (secret) {
        return that.javaObject.decryptSecret(secret);
    };

    /**
     * Press and hold the given keys including modifier keys <br/>
     * use the key constants defined in class Key, <br/>
     * which only provides a subset of a US-QWERTY PC keyboard layout <br/>
     * might be mixed with simple characters<br/>
     * use + to concatenate Key constants
     *
     * @param {String} keys valid keys
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method keyDown
     */
    that.keyDown = function (keys) {
        return update(that.javaObject.keyDown(keys));
    };

    /**
     * release the given keys (see Environment.keyDown(...)).
     *
     * @param {String} keys valid keys
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method keyUp
     */
    that.keyUp = function (keys) {
        return update(that.javaObject.keyUp(keys));
    };

    /**
     * Compact alternative for type() with more options <br/>
     * - special keys and options are coded as #XN. or #X+ or #X- <br/>
     * where X is a refrence for a special key and N is an optional repeat factor <br/>
     * A modifier key as #X. modifies the next following key<br/>
     * the trailing . ends the special key, the + (press and hold) or - (release) does the same, <br/>
     * but signals press-and-hold or release additionally.<br/>
     * except #W / #w all special keys are not case-sensitive<br/>
     * a #wn. inserts a wait of n millisecs or n secs if n less than 60 <br/>
     * a #Wn. sets the type delay for the following keys (must be &gt; 60 and denotes millisecs)
     * - otherwise taken as normal wait<br/>
     * Example: wait 2 secs then type CMD/CTRL - N then wait 1 sec then type DOWN 3 times<br/>
     * Windows/Linux: write("#w2.#C.n#W1.#d3.")<br/>
     * Mac: write("#w2.#M.n#W1.#D3.")<br/>
     * for more details about the special key codes and examples consult the sikuliX docs <br/>
     *
     * @param {String} text a coded text interpreted as a series of key actions (press/hold/release)
     * @return this Environment or NULL on errors.
     * @memberOf Environment
     * @method write
     */
    that.write = function (text) {
        return update(that.javaObject.write(text));
    };


    /*********************
     * MOUSE WHEEL FUNCTIONS
     *********************/
    /**
     * move the mouse pointer to the given target location and move the
     * wheel the given steps down.
     *
     * @param {number} steps the number of steps
     * @memberOf Environment
     * @method mouseWheelDown
     */
    that.mouseWheelDown = function (steps) {
        return update(that.javaObject.mouseWheelDown(steps));
    };

    /**
     * move the mouse pointer to the given target location and move the
     * wheel the given steps up.
     *
     * @param {number} steps the number of steps
     * @memberOf Environment
     * @method mouseWheelUp
     */
    that.mouseWheelUp = function (steps) {
        return update(that.javaObject.mouseWheelUp(steps));
    };

    /**
     * @return {boolean} true, if the OS is any instance of an Windows based OS
     * @memberOf Environment
     * @method isWindows
     */
    that.isWindows = function () {
        return that.javaObject.isWindows();
    };

    /**
     * @return {boolean} true, if the OS is any instance of an Linux based OS
     * @memberOf Environment
     * @method isLinux
     */
    that.isLinux = function () {
        return that.javaObject.isLinux();
    };

    /**
     * @return {string} identifier of the current OS
     * @memberOf Environment
     * @method getOsIdentifier
     */
    that.getOsIdentifier = function () {
        return that.javaObject.getOsIdentifier();
    };


    /**
     * Runs the assigned command on the host and returns the result. __Attention:__ this is OS depended feature! So be
     * aware which os you are running, maybe us to check {@link Environment#isLinux()}  or {@link Environment#isWindows()}.
     *
     * @param {string} command OS depended command as {@link String}
     * @param {boolean} optThrowException defines if an exception should be thrown, if the exit code != 0
     * @return the result as `CommandLineResult` object, you can use the methods `result.getOutput()` and `result.getExitCode()`
     * @example
     *  ```
     *  var app;
     *  if(environmen.runCommand('uname --machine') == 'x86_64'){
     *      //open app from other path
     *      app = new Application('/lib64/appname');
     *  } else {
     *      app = new Application('/lib/appname');
     *  }
     *  ```
     * @memberOf Environment
     * @method runCommand
     */
    that.runCommand = function (command, optThrowException) {
        if (undefined == optThrowException) {
            optThrowException = Boolean(true);
        }
        return that.javaObject.runCommand(command, optThrowException);
    };

    /**
     * Reads out the environment variable with the assigned key
     *
     * @param {string} key of environment variable as {@link String}
     * @return {string} value or null
     * @memberOf Environment
     * @method getEnv
     */
    that.getEnv = function (key) {
        return that.javaObject.getEnv(key);
    }

    /**
     * Reads out the property value with the assigned key
     *
     * @param {string} key of property as {@link String}
     * @return {string} value or null
     * @memberOf Environment
     * @method getEnv
     */
    that.getProperty = function (key) {
        return that.javaObject.getProperty(key);
    }


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
        return initEnvironment({}, updatedJavaObject);
    }

    return that;
}



