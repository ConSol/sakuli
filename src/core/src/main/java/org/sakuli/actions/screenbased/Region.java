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

package org.sakuli.actions.screenbased;

import org.sakuli.actions.Action;
import org.sakuli.actions.ModifySahiTimer;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.loader.BeanLoader;
import org.sakuli.loader.ScreenActionLoader;
import org.sikuli.script.Screen;

/**
 * @author Tobias Schneck
 */
public class Region implements Action {

    private RegionImpl regionImpl;
    private TypingUtil<Region> typingUtil;

    /**
     * Creates a new Region of the complete screen.
     */
    public Region() {
        this(false);
    }

    /**
     * Creates a new Region of the complete screen.
     *
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     */
    public Region(boolean resumeOnException) {
        this.regionImpl = new RegionImpl(resumeOnException, getScreenActionLoader());
        typingUtil = new TypingUtil<>(this);
    }

    /**
     * Creates a new Region from the position parameters.
     *
     * @param x x position of a rectangle on the screen.
     * @param y x position of a rectangle on the screen.
     * @param w weight of the rectangle stating by x,y
     * @param h height of the rectangle stating by x,y
     */
    public Region(int x, int y, int w, int h) {
        this(x, y, w, h, false);
    }

    /**
     * Creates a new Region from the position parameters.
     *
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     * @param x                 x position of a rectangle on the screen.
     * @param y                 x position of a rectangle on the screen.
     * @param w                 weight of the rectangle stating by x,y
     * @param h                 height of the rectangle stating by x,y
     */
    public Region(int x, int y, int w, int h, boolean resumeOnException) {
        this.regionImpl = new RegionImpl(x, y, w, h, resumeOnException, getScreenActionLoader());
        typingUtil = new TypingUtil<>(this);
    }

    /**
     * Wrapper for the {@link org.sikuli.script.Region} objects.
     */
    public Region(org.sikuli.script.Region region, boolean resumeOnException) {
        this.regionImpl = new RegionImpl(region, resumeOnException, getScreenActionLoader());
        typingUtil = new TypingUtil<>(this);
    }

    /**
     * Wrapper for the {@link RegionImpl} objects.
     */
    public Region(RegionImpl regionImpl) {
        this.regionImpl = regionImpl;
        typingUtil = new TypingUtil<>(this);
    }

    public ScreenActionLoader getScreenActionLoader() {
        return BeanLoader.loadScreenActionLoader();
    }

    /**
     * Finds an image inside this region immediately.
     *
     * @param imageName name of the preloaded picture
     * @return the found {@link Region} or if the target can't be found {@code null}.
     */
    @LogToResult(message = "find the image in this region")
    public Region find(String imageName) {
        return update(regionImpl.find(imageName));
    }

    /**
     * Finds a target in this {@link Region} immediately;
     *
     * @return the found {@link Region} or if the target can't be found {@code null}.
     */
    @LogToResult(message = "find in this region another region")
    public Region findRegion(Region region) {
        return update(regionImpl.findRegion(region.getRegionImpl()));
    }

    /**
     * Check whether the given image  is visible in the region.
     *
     * @return this {@link Region} or null
     */
    @LogToResult(message = "check if this image is visible in this region")
    public Region exists(String imageName) {
        return update(regionImpl.exists(imageName));
    }

    /**
     * Check whether the given image  is visible in the region in x seconds.
     *
     * @return this {@link Region} or null
     */
    @LogToResult(message = "check if this region is visible")
    public Region exists(String imageName, int seconds) {
        return update(regionImpl.exists(imageName, seconds));
    }

    /**
     * makes a mouse click on the center of the {@link Region}.
     *
     * @return the {@link Region} or NULL on errors.
     */
    @ModifySahiTimer
    @LogToResult
    public Region click() {
        return update(regionImpl.clickMe());
    }


    /**
     * makes a double click on the center of the {@link Region}.
     *
     * @return the {@link Region} or NULL on errors.
     */
    @ModifySahiTimer
    @LogToResult
    public Region doubleClick() {
        return update(regionImpl.doubleClickMe());
    }


    /**
     * makes a rigth click on the center of the {@link Region}.
     *
     * @return the {@link Region} or NULL on errors.
     */
    @ModifySahiTimer
    @LogToResult
    public Region rightClick() {
        return update(regionImpl.rightClickMe());
    }

    /**
     * Move the mouse pointer the center of the {@link Region} and "hovers" it.
     *
     * @return the {@link Region} or NULL on errors.
     */
    @ModifySahiTimer
    @LogToResult
    public Region mouseMove() {
        return update(regionImpl.mouseMoveMe());
    }

    /**
     * Low-level mouse action to press the assigned {@link MouseButton} on the current position. <p> Example: Press and
     * release the right mouse button vor 3 seconds on a specified region: <br/> Region region = new
     * Region().find("your-pattern.png"); <br/> region.mouseDown(MouseButton.RIGHT).sleep(3).mouseUp(MouseButton.RIGHT);
     * <br/> </p>
     *
     * @return the {@link Region} or NULL on errors.
     */
    @ModifySahiTimer
    @LogToResult
    public Region mouseDown(MouseButton mouseButton) {
        return update(regionImpl.mouseDown(mouseButton));
    }

    /**
     * Low-level mouse action to release the assigned {@link MouseButton}. <p> Example: Press and release the right
     * mouse button vor 3 seconds on a specified region: <br/> Region region = new Region().find("your-pattern.png");
     * <br/> region.mouseDown(MouseButton.RIGHT).sleep(3).mouseUp(MouseButton.RIGHT); <br/> </p>
     *
     * @return the {@link Region} or NULL on errors.
     */
    @ModifySahiTimer
    @LogToResult
    public Region mouseUp(MouseButton mouseButton) {
        return update(regionImpl.mouseUp(mouseButton));
    }


    /**
     * Blocks and waits until a target which is specified by the optImageName is found in the hole {@link Screen} within
     * a given time period in seconds.
     *
     * @param imageName name of the image pattern
     * @param seconds   the maximum time to waitFor in seconds
     * @return a {@link Region} object representing the region occupied by the found target, or null if the target can
     * not be found within the given time.
     */
    @LogToResult(message = "wait for image for x seconds")
    public Region waitForImage(String imageName, int seconds) {
        return update(regionImpl.waitForImage(imageName, seconds));
    }

    /**
     * {@link TypingUtil#paste(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false)
    public Region paste(String text) {
        return typingUtil.paste(text);
    }

    /**
     * {@link TypingUtil#pasteMasked(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false, logArgs = false)
    public Region pasteMasked(String text) {
        return typingUtil.pasteMasked(text);
    }

    /**
     * {@link TypingUtil#pasteAndDecrypt(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false, logArgs = false)
    public Region pasteAndDecrypt(String text) {
        return typingUtil.pasteAndDecrypt(text);
    }


    /**
     * See {@link TypingUtil#type(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type over system keyboard", logClassInstance = false)
    public Region type(String text) {
        return typingUtil.type(text, null);
    }

    /**
     * See {@link TypingUtil#type(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type with pressed modifiers", logClassInstance = false)
    public Region type(String text, String optModifiers) {
        return typingUtil.type(text, optModifiers);
    }

    /**
     * See {@link TypingUtil#typeMasked(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type over system keyboard", logClassInstance = false, logArgs = false)
    public Region typeMasked(String text) {
        return typingUtil.typeMasked(text, null);
    }

    /**
     * See {@link TypingUtil#typeMasked(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type with pressed modifiers", logClassInstance = false, logArgs = false)
    public Region typeMasked(String text, String optModifiers) {
        return typingUtil.typeMasked(text, optModifiers);
    }

    /**
     * See {@link TypingUtil#typeAndDecrypt(String, String)} .
     */
    @ModifySahiTimer
    @LogToResult(message = "decrypt and type over system keyboard", logClassInstance = false, logArgs = false)
    public Region typeAndDecrypt(String text) {
        return typingUtil.typeAndDecrypt(text, null);
    }

    /**
     * See {@link TypingUtil#typeAndDecrypt(String, String)} .
     */
    @ModifySahiTimer
    @LogToResult(message = "decrypt and type with pressed modifiers", logClassInstance = false, logArgs = false)
    public Region typeAndDecrypt(String text, String optModifiers) {
        return typingUtil.typeAndDecrypt(text, optModifiers);
    }

    /**
     * See {@link TypingUtil#keyDown(String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "press key down", logClassInstance = false)
    public Region keyDown(String keys) {
        return typingUtil.keyDown(keys);
    }

    /**
     * See {@link TypingUtil#keyUp(String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "press key up", logClassInstance = false)
    public Region keyUp(String keys) {
        return typingUtil.keyUp(keys);
    }

    /**
     * See {@link TypingUtil#write(String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "interpret and write the following expresion", logClassInstance = false)
    public Region write(String text) {
        return typingUtil.write(text);
    }

    /**
     * delete a amount of chars in a field
     *
     * @param amountOfChars number of chars to delete
     * @return this {@link Region} or null on errors
     */
    @ModifySahiTimer
    @LogToResult
    public Region deleteChars(int amountOfChars) {
        return update(regionImpl.deleteChars(amountOfChars));

    }

    /**
     * {@link TypingUtil#mouseWheelDown(int)}
     */
    @LogToResult(message = "move mouse wheel down x times")
    public Region mouseWheelDown(int steps) {
        return typingUtil.mouseWheelDown(steps);
    }

    /**
     * {@link TypingUtil#mouseWheelUp(int)}
     */
    @LogToResult(message = "move mouse wheel up x times")
    public Region mouseWheelUp(int steps) {
        return typingUtil.mouseWheelUp(steps);
    }


    /**
     * Set a offset to a specific {@link Region} and returns the new {@link Region} object. The offset function will
     * move the Region's rectangle with x to the right and with y to the left. The size of the rectangle will be the
     * same.
     *
     * @param offsetX x-value for the offset action
     * @param offsetY y-value for the offset action
     * @return a {@link Region} with the new coordinates
     */
    @LogToResult(message = "move this region")
    public Region move(int offsetX, int offsetY) {
        return update(regionImpl.move(offsetX, offsetY));
    }

    /**
     * create a region enlarged range pixels on each side
     *
     * @param range of pixels
     * @return a new {@link Region}
     */
    @LogToResult(message = "grow on each side")
    public Region grow(int range) {
        return update(regionImpl.grow(range));
    }

    /**
     * create a region with enlarged range pixels
     *
     * @param width  in pixels to grow in both directions
     * @param height in pixels to grow in both directions
     * @return a new {@link Region}
     */
    @LogToResult(message = "grow on each side with width and height")
    public Region grow(int width, int height) {
        return update(regionImpl.grow(width, height));
    }

    /**
     * @return a new {@link Region} that is defined above the current region’s top border with a height of range number
     * of pixels.
     */
    @LogToResult()
    public Region above(int range) {
        return update(regionImpl.above(range));
    }

    /**
     * @return a new {@link Region} that is defined below the current region’s top border with a height of range number
     * of pixels.
     */
    @LogToResult()
    public Region below(int range) {
        return update(regionImpl.below(range));
    }

    /**
     * @return a new {@link Region} that is defined on the left the current region’s top border with a height of range
     * number of pixels.
     */
    @LogToResult()
    public Region left(int range) {
        return update(regionImpl.left(range));
    }


    /**
     * @return a new {@link Region} that is defined on the right the current region’s top border with a height of range
     * number of pixels.
     */
    @LogToResult()
    public Region right(int range) {
        return update(regionImpl.right(range));
    }

    /**
     * @return height as int value
     */
    @LogToResult
    public int getH() {
        return regionImpl.getH();
    }

    /**
     * set the height, based form the upper left corner downsides
     */
    @LogToResult(message = "set new height for this region")
    public Region setH(int height) {
        this.regionImpl.setH(height);
        return this;
    }

    /**
     * @return width as int value
     */
    @LogToResult
    public int getW() {
        return regionImpl.getW();
    }

    /**
     * set the width, based form the upper left corner to the right
     */
    @LogToResult(message = "set new width for this region")
    public Region setW(int width) {
        this.regionImpl.setW(width);
        return this;
    }

    /**
     * @return X coordinate of the upper left corner
     */
    @LogToResult
    public int getX() {
        return regionImpl.getX();
    }

    /**
     * set the X coordinate of the upper left corner.
     */
    @LogToResult(message = "set new x coordinate for this region")
    public Region setX(int x) {
        this.regionImpl.setX(x);
        return this;
    }

    /**
     * @return Y coordinate of the upper left corner
     */
    @LogToResult
    public int getY() {
        return regionImpl.getY();
    }

    /**
     * set the Y coordinate of the upper left corner.
     */
    @LogToResult(message = "set new y coordinate for this region")
    public Region setY(int y) {
        this.regionImpl.setY(y);
        return this;
    }

    /**
     * highlights this {@link Region} for x seconds
     */
    @LogToResult
    public Region highlight(int seconds) {
        regionImpl.highlight(seconds);
        return this;
    }

    /**
     * highlights this {@link Region} for the deault time
     */
    @LogToResult
    public Region highlight() {
        regionImpl.highlight(getLoader().getSettings().DefaultHighlightTime);
        return this;
    }

    /**
     * Blocks the current testcase execution for x seconds
     *
     * @param seconds to sleep
     * @return this {@link Region} or NULL on errors.
     */
    @LogToResult(message = "sleep and do nothing for x seconds", logClassInstance = false)
    public Region sleep(Integer seconds) {
        return typingUtil.sleep(seconds);
    }

    /**
     * @return from this region a extracted Text via OCR as {@link String}
     */
    @LogToResult(message = "extract text via OCR from Region")
    public String extractText() {
        return regionImpl.extractText();
    }


    @Override
    public String toString() {
        return regionImpl.toString();
    }

    /**
     * @return gets the inherit java object for not yet wrapped methods
     */
    public RegionImpl getRegionImpl() {
        return regionImpl;
    }

    /**
     * updats the inherit java object after modifaction with {@link #getRegionImpl}.
     */
    public void setRegionImpl(RegionImpl regionImpl) {
        this.regionImpl = regionImpl;
    }

    private Region update(RegionImpl regionImpl) {
        return regionImpl != null ? new Region(regionImpl, getResumeOnException()) : null;
    }

    private Region update(org.sikuli.script.Region region) {
        return region != null ? new Region(region, getResumeOnException()) : null;

    }

    @Override
    public boolean getResumeOnException() {
        return regionImpl.getResumeOnException();
    }

    @Override
    public ScreenActionLoader getLoader() {
        return regionImpl.getLoader();
    }

    @Override
    public RegionImpl getActionRegion() {
        return regionImpl;
    }
}
