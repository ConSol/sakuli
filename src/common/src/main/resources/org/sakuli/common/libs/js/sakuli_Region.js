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
 * Region - Represents a region as a part of or the hole screen.
 *
 * @param {Boolean} optResumeOnException if true, the test execution won't stop on an occurring error. Default: false.
 *
 * @example `var screen = new Region();   //represents the hole screen`
 * @namespace Region
 */
function Region(optResumeOnException) {
    if (undefined == optResumeOnException) {
        optResumeOnException = Boolean(false);
    }
    return initRegion(this, Packages.org.sakuli.loader.BeanLoader.loadRegion(optResumeOnException));
}

/**
 * RegionRectangle (extends [Region](#Region)) - Represents a region specified by the x and y coordinates, width and
 * height as a part of the screen.
 *
 * @param {number} x – x position of a rectangle on the screen.
 * @param {number} y – y position of a rectangle on the screen.
 * @param {number} w – width of a rectangle in pixel.
 * @param {number} h – height of a rectangle in pixel.
 * @param {Boolean} optResumeOnException (optional) if true, the test execution won't stop on an occurring error.
 *     Default: false.
 *
 * @example
 * ```
 * var notepadRegion = new RegionRectangle(0,0,100,100);
 * //represents a region which start at x=0, y=o (left upper corner) and have a size of 100px * 100px.
 * ```
 * @namespace RegionRectangle
 */
function RegionRectangle(x, y, w, h, optResumeOnException) {
    if (undefined == optResumeOnException) {
        optResumeOnException = Boolean(false);
    }
    return initRegion(this, Packages.org.sakuli.loader.BeanLoader.loadRegionRectangle(x, y, w, h, optResumeOnException));
}

/**
 * Internal class to implement all Region functions.
 *
 * @private (internal function)
 */
function initRegion(that, javaObject) {

    /**
     * Finds an image inside this region immediately.
     *
     * @param {String} imageName name of the preloaded picture
     *        (if not set, the find operation will take place on the predefined region object.)
     * @return the found Region or if the target can't be found  null.
     * @memberOf Region
     * @method find
     */
    that.find = function (imageName) {
        return update(that.javaObject.find(imageName));
    };

    /**
     * Finds a target in this Region immediately;
     *
     * @return the found Region or if the target can't be found null.
     * @memberOf Region
     * @method findRegion
     */
    that.findRegion = function (region) {
        return update(that.javaObject.findRegion(region));
    };


    /**************************************
     * EXISTS FUNCTIONS
     *************************************/
    /**
     * Check whether the give pattern is visible on the screen.
     *
     * @param {String} imageName if set, the function search inside the given region for the image
     * @param {number} optWaitSeconds if set, the function search for x seconds for the pattern.
     * @return this Region or null
     * @memberOf Region
     * @method exists
     */
    that.exists = function (imageName, optWaitSeconds) {
        if (undefined == optWaitSeconds) {
            return update(that.javaObject.exists(imageName));
        }
        return update(that.javaObject.exists(imageName, optWaitSeconds));
    };


    /**
     * makes a mouse click on the center of the Region.
     *
     * @return the Region or NULL on errors.
     * @memberOf Region
     * @method click
     */
    that.click = function () {
        return update(that.javaObject.click());
    };

    /**
     * makes a double click on the center of the Region.
     *
     * @return the Region or NULL on errors.
     * @memberOf Region
     * @method doubleClick
     */
    that.doubleClick = function () {
        return update(that.javaObject.doubleClick());
    };


    /**
     * makes a right click on the center of the Region.
     *
     * @return the Region or NULL on errors.
     * @memberOf Region
     * @method rightClick
     */
    that.rightClick = function () {
        return update(that.javaObject.rightClick());
    };

    /**
     * Move the mouse pointer to the center of the {@link Region} and "hovers" it.
     *
     * @returns the {@link Region} or NULL on errors.
     * @memberOf Region
     * @method mouseMove
     */
    that.mouseMove = function () {
        return update(that.javaObject.mouseMove());
    };

    /**
     * Low-level mouse action to press the assigned {@link MouseButton} on the current position.
     *
     * @example Press and release the right mouse button vor 3 seconds on a specified region:
     * ```
     * var region = new Region().find("your-pattern.png");
     * region.mouseDown(MouseButton.RIGHT).sleep(3).mouseUp(MouseButton.RIGHT);
     * ```
     *
     * @param mouseButton on of {@link MouseButton} values
     * @returns the {@link Region} or NULL on errors.
     * @memberOf Region
     * @method mouseDown
     */
    that.mouseDown = function (mouseButton) {
        return update(that.javaObject.mouseDown(mouseButton))
    };

    /**
     * Low-level mouse action to release the assigned {@link MouseButton}.
     *
     * @example Press and release the right mouse button vor 3 seconds on a specified region:
     * ```
     * var region = new Region().find("your-pattern.png");
     * region.mouseDown(MouseButton.RIGHT).sleep(3).mouseUp(MouseButton.RIGHT);
     * ```
     * @param mouseButton on of {@link MouseButton} values
     * @returns the {@link Region} or NULL on errors.
     * @memberOf Region
     * @method mouseUp
     */
    that.mouseUp = function (mouseButton) {
        return update(that.javaObject.mouseUp(mouseButton))
    };

    /**
     * Blocks and waits until a target which is specified by the optImageName is found in the hole
     * Screen within a given time period in seconds.
     *
     * @param {String} imageName name of the image pattern
     * @param {number} seconds   the maximum time to waitFor in seconds
     * @return a Region object representing the region occupied by the found target,
     *         or null if the target can not be found within the given time.
     * @memberOf Region
     * @method waitForImage
     */
    that.waitForImage = function (imageName, seconds) {
        return update(that.javaObject.waitForImage(imageName, seconds));
    };

    /**
     * pastes the text at the current position of the focus/carret <br/>using the
     * clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)
     *
     * @param {String} text as a string, which might contain unicode characters
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method paste
     */
    that.paste = function (text) {
        return update(that.javaObject.paste(text));
    };


    /**
     * makes a masked paste(String) without any logging.
     *
     * @param {String} text a string, which might contain unicode characters
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method pasteMasked
     */
    that.pasteMasked = function (text) {
        return update(that.javaObject.pasteMasked(text));
    };

    /**
     * combines pasteMasked(String) and decryptSecret(String).
     *
     * @param {String} text encrypted secret
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method pasteAndDecrypt
     */
    that.pasteAndDecrypt = function (text) {
        return update(that.javaObject.pasteAndDecrypt(text));
    };

    /**
     * Enters the given text one character/key after another using keyDown/keyUp.
     * <p/>
     * About the usable Key constants see documentation of Key.
     * The function use a subset of a US-QWERTY PC keyboard layout to type the text.
     * The text is entered at the current position of the focus.
     *
     * @param {String} text containing characters and/or Key constants
     * @param {String} optModifiers (optional) an String with only Key constants.
     * @return this Region or NULL on errors.
     * @memberOf Region
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
     * The function use a subset of a US-QWERTY PC keyboard layout to type the text.
     * The text is entered at the current position of the focus.
     *
     * @param {String} text containing characters and/or Key constants
     * @param {String} optModifiers (optional) an String with only Key constants.
     * @return this Region or NULL on errors.
     * @memberOf Region
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
     * The entered text will be masked at the logging. For the deatails of the decryption see decryptSecret(String).
     * <p/>
     * About the usable Key constants see documentation of Key.
     * The function use a subset of a US-QWERTY PC keyboard layout to type the text.
     * The text is entered at the current position of the focus.
     *
     * @param {String} text containing characters and/or Key constants
     * @param {String} optModifiers (optional) an String with only Key constants.
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method typeAndDecrypt
     */
    that.typeAndDecrypt = function (text, optModifiers) {
        if (undefined == optModifiers) {
            return update(that.javaObject.typeAndDecrypt(text))
        }
        return update(that.javaObject.typeAndDecrypt(text, optModifiers));
    };


    /**
     * Press and hold the given keys including modifier keys <br/>
     * use the key constants defined in class Key, <br/>
     * which only provides a subset of a US-QWERTY PC keyboard layout <br/>
     * might be mixed with simple characters<br/>
     * use + to concatenate Key constants
     *
     * @param {String} keys valid keys
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method keyDown
     */
    that.keyDown = function (keys) {
        return update(that.javaObject.keyDown(keys));
    };

    /**
     * release the given keys (see Region.keyDown(...)).
     *
     * @param {String} keys valid keys
     * @return this Region or NULL on errors.
     * @memberOf Region
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
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method write
     */
    that.write = function (text) {
        return update(that.javaObject.write(text));
    };


    /**
     * delete a amount of chars in a field
     *
     * @param {number} amountOfChars number of chars to delete
     * @return this Region or null on errors
     * @memberOf Region
     * @method deleteChars
     */
    that.deleteChars = function (amountOfChars) {
        return update(that.javaObject.deleteChars(amountOfChars));
    };


    /*********************
     * MOUSE WHEEL FUNCTIONS
     *********************/
    /**
     * move the mouse pointer to the given target location and move the
     * wheel the given steps down.
     *
     * @param {number} steps the number of steps
     * @memberOf Region
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
     * @memberOf Region
     * @method mouseWheelUp
     */
    that.mouseWheelUp = function (steps) {
        return update(that.javaObject.mouseWheelUp(steps));
    };


    /**
     * Set a offset to a specific Region and returns the new Region object.
     * The offset function will move the Region's rectangle with x to the right and with y to the left.
     * The size of the rectangle will be the same.
     *
     * @param {number} offsetX x-value for the offset action
     * @param {number} offsetY y-value for the offset action
     * @return a Region with the new coordinates
     * @memberOf Region
     * @method move
     */
    that.move = function (offsetX, offsetY) {
        return update(that.javaObject.move(offsetX, offsetY));
    };

    /**
     * create a region enlarged range pixels on each side
     *
     * @param {number} range of pixels
     * @return a new Region
     * @memberOf Region
     * @method grow
     */
    that.grow = function (range) {
        return update(that.javaObject.grow(range));
    };

    /**
     * create a region with enlarged range pixels
     *
     * @param {number} width  in pixels to grow in both directions
     * @param {number} height in pixels to grow in both directions
     * @return a new Region
     * @memberOf Region
     * @method grow
     */
    that.grow = function (width, height) {
        if (undefined == height) {
            //prevents js wrong function matching
            return update(that.javaObject.grow(width));
        }
        return update(that.javaObject.grow(width, height));
    };

    /**
     * @return  a new Region that is defined above the current region’s top border
     * with a height of range number of pixels.
     * @param {number} range of pixels
     *
     * @memberOf Region
     * @method above
     */
    that.above = function (range) {
        return update(that.javaObject.above(range));
    };

    /**
     * @return  a new Region that is defined below the current region’s top border
     * with a height of range number of pixels.
     * @param {number} range of pixels
     *
     * @memberOf Region
     * @method below
     */
    that.below = function (range) {
        return update(that.javaObject.below(range));
    };

    /**
     * @return  a new Region that is defined on the left the current region’s top border
     * with a height of range number of pixels.
     * @param {number} range of pixels
     *
     * @memberOf Region
     * @method left
     */
    that.left = function (range) {
        return update(that.javaObject.left(range));
    };

    /**
     * @return  a new Region that is defined on the right the current region’s top border
     * with a height of range number of pixels.
     * @param {number} range of pixels
     *
     * @memberOf Region
     * @method right
     */
    that.right = function (range) {
        return update(that.javaObject.right(range));
    };


    /**
     * set the height, based form the upper left corner downsides
     * @param {number} height in pixels
     *
     * @memberOf Region
     * @method setH
     */
    that.setH = function (height) {
        return update(that.javaObject.setH(height));
    };

    /**
     * @return height as int value
     * @memberOf Region
     * @method getH
     */
    that.getH = function () {
        return that.javaObject.getH();
    };

    /**
     * set the width, based form the upper left corner to the right
     * @param {number} width
     *
     * @memberOf Region
     * @method setW
     */
    that.setW = function (width) {
        return update(that.javaObject.setW(width));
    };

    /**
     * @return width as int value
     * @memberOf Region
     * @method getW
     */
    that.getW = function () {
        return that.javaObject.getW();
    };

    /**
     * set the X coordinate of the upper left corner.
     * @param {number} x
     * @memberOf Region
     * @method setX
     */
    that.setX = function (x) {
        return update(that.javaObject.setX(x));
    };

    /**
     * @return width as int value
     * @memberOf Region
     * @method getX
     */
    that.getX = function () {
        return that.javaObject.getX();
    };

    /**
     * set the Y coordinate of the upper left corner.
     * @param {number} y
     * @memberOf Region
     * @method setY
     */
    that.setY = function (y) {
        return update(that.javaObject.setY(y));
    };

    /**
     * @return Y coordinate of the upper left corner
     * @memberOf Region
     * @method getY
     */
    that.getY = function () {
        return that.javaObject.getY();
    };

    /**
     * @param {number} seconds highlights this Region for x seconds
     * or the default time
     *
     * @memberOf Region
     * @method highlight
     */
    that.highlight = function (seconds) {
        if (undefined == seconds) {
            return update(that.javaObject.highlight());
        }
        return update(that.javaObject.highlight(seconds));
    };

    /**
     * Takes a screenshot of the current Region in the screen and saves it the current testcase folder
     * with the assigned filename.
     *
     * @param {String} filename name of the screenshot, e.g. `region_screenshot`.
     *                 Default: screenshot
     *
     * @return {String} file path to the created screenshot OR null on errors
     * @memberOf Region
     * @method takeScreenShot
     */
    that.takeScreenShot = function (filename) {
        if (undefined == filename) {
            filename = "screenshot";
        }
        var path = that.javaObject.takeScreenShot(filename);
        return path != null ? path.toString() : null;
    };

    /**
     * Blocks the current testcase execution for x seconds
     *
     * @param {number} seconds to sleep
     * @return this Region or NULL on errors.
     * @memberOf Region
     * @method sleep
     */
    that.sleep = function (seconds) {
        return update(that.javaObject.sleep(seconds));
    };

    /**
     * @return from this region a extracted Text as String
     * @memberOf Region
     * @method extractText
     */
    that.extractText = function () {
        return that.javaObject.extractText();
    };


    /**
     * gets the inherit java object for not yet wrapped methods
     * @returns {*}
     * @private
     */
    that.getRegionImpl = function () {
        return that.javaObject.getRegionImpl();
    };

    /**
     * param: `modifiedJavaObject`
     * updates the inherit java object after modifaction with getRegionImpl.
     * returns: {*}
     * @private
     */
    that.setRegionImpl = function (modifiedJavaObject) {
        that.javaObject.setRegionImpl(modifiedJavaObject);
        return that;
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
        return initRegion({}, updatedJavaObject);
    }

    return that;
}



