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

package de.consol.sakuli.actions.environment;

import de.consol.sakuli.actions.Action;
import de.consol.sakuli.actions.ModifySahiTimer;
import de.consol.sakuli.actions.logging.LogToResult;
import de.consol.sakuli.actions.screenbased.Region;
import de.consol.sakuli.actions.screenbased.RegionImpl;
import de.consol.sakuli.actions.screenbased.TypingUtil;
import de.consol.sakuli.loader.ScreenActionLoader;
import org.sikuli.script.App;
import org.sikuli.script.IRobot;
import org.sikuli.script.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * This is a Singeton because the Function should be stateless
 *
 * @author Tobias Schneck
 */
public class Environment implements Action {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean resumeOnException;
    private ScreenActionLoader loader;
    private TypingUtil<Environment> typingUtil;


    public Environment(boolean resumeOnException, ScreenActionLoader Loader) {
        this.resumeOnException = resumeOnException;
        this.loader = Loader;
        this.typingUtil = new TypingUtil<>(this);
    }

    /**
     * set a new default similarity for the screen capturing methods.
     *
     * @param similarity double value between 0 and 1, default = 0.8f
     * @return this {@link Environment} or NULL on errors.
     */
    @LogToResult(message = "set similarity level", logClassInstance = false)
    public Environment setSimilarity(Double similarity) {
        if (similarity >= 0 && similarity <= 1) {
            this.loader.getSettings().setMinSimilarity(similarity);
            logger.info("The similarity level have been set to " + similarity);
        } else {
            loader.getExceptionHandler().handleException("The similartiy must be a double value between 0 and 1!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * @return a {@link Region} object from the current focused window
     * or NULL on errors.
     */
    @LogToResult(logClassInstance = false)
    public Region getRegionFromFocusedWindow() {
        org.sikuli.script.Region origRegion = App.focusedWindow();
        if (origRegion != null) {
            return new Region(origRegion, resumeOnException, loader);
        }
        loader.getExceptionHandler().handleException("couldn't extract a Region from the current focused window", resumeOnException);
        return null;
    }

    /**
     * Takes a screenshot of the current screen and saves it to the overgiven path.
     * If there ist just a file name, the screenshot will be saved in your testsuite log folder.
     *
     * @param pathName "pathname/filname.format" or just "filename.format"<br>
     *                 for example "test.png".
     */
    @LogToResult(message = "take a screenshot from the current screen and save it to the file system", logClassInstance = false)
    public String takeScreenshot(final String pathName) {
        Path folderPath;
        String message;
        if (pathName.contains(File.separator)) {
            folderPath = Paths.get(pathName.substring(0, pathName.lastIndexOf(File.separator)));
            message = pathName.substring(pathName.lastIndexOf(File.separator) + 1, pathName.lastIndexOf("."));
        } else {
            folderPath = loader.getActionProperties().getScreenShotFolder();
            message = pathName.substring(0, pathName.lastIndexOf("."));
        }

        try {
            loader.getScreen().capture();
            return loader.getScreenshotActions().takeScreenshot(message, folderPath, pathName.substring(pathName.lastIndexOf(".") + 1)).toFile().getAbsolutePath();
        } catch (IOException e) {
            loader.getExceptionHandler().handleException("Can't create Screenshot for path '" + pathName + "': " + e.getMessage(), resumeOnException);
        }
        return null;
    }

    /**
     * Blocks the current testcase execution for x seconds
     *
     * @param seconds to sleep
     * @return this {@link Environment} or NULL on errors.
     */
    @LogToResult(message = "sleep and do nothing for x seconds", logClassInstance = false)
    public Environment sleep(Integer seconds) {

        try {
//            // TODO move to separate action
//            int trigger = 10;
//            Session session = loader.getRhinoScriptRunner().getSession();
//            if (seconds == trigger && session != null) {
//                session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, "500");
//                logger.info("PAUSE SAHI-PROXY!");
//                return this;
//            }
//            if (seconds == trigger + 1 && session != null) {
//                logger.info("NOW UNPAUSE SAHI-PROXY!");
//                session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR,null);
//                return this;
//            }

            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            loader.getExceptionHandler().handleException(e, true);
            return null;
        }
        return this;
    }

    /**
     * @return the current content of the clipboard as {@link String} or NULL on errors
     */
    @LogToResult(message = "get string from system clipboard", logClassInstance = false)
    public String getClipboard() {
        return App.getClipboard();
    }

    /**
     * sets the String paramter to the system clipboard
     *
     * @param text as {@link String}
     * @return this {@link Environment}.
     */
    @LogToResult(message = "put to clipboard", logClassInstance = false)
    public Environment setClipboard(String text) {
        App.setClipboard(text);
        return this;
    }

    /**
     * pastes the current clipboard content into the focused area.
     * Will do the same as "STRG + C".
     *
     * @return this {@link Environment}.
     */
    @ModifySahiTimer
    @LogToResult(message = "paste the current clipboard into the focus", logClassInstance = false)
    public Environment pasteClipboard() {
        int mod = Key.getHotkeyModifier();
        IRobot r = loader.getScreen().getRobot();
        r.keyDown(mod);
        r.keyDown(KeyEvent.VK_V);
        r.keyUp(KeyEvent.VK_V);
        r.keyUp(mod);
        return this;
    }

    /**
     * copy the current selected item or text to the clipboard.
     * Will do the same as "STRG + V".
     *
     * @return this {@link Environment}.
     */
    @ModifySahiTimer
    @LogToResult(message = "copy the current selection to the clipboard", logClassInstance = false)
    public Environment copyIntoClipboard() {
        int mod = Key.getHotkeyModifier();
        IRobot r = loader.getScreen().getRobot();
        r.keyDown(mod);
        r.keyDown(KeyEvent.VK_C);
        r.keyUp(KeyEvent.VK_C);
        r.keyUp(mod);
        return this;
    }

    /********************************************************
     * PASTE FUNCTIONS
     *******************************************************/


    /**
     * {@link de.consol.sakuli.actions.screenbased.TypingUtil#paste(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false)
    public Environment paste(String text) {
        return typingUtil.paste(text);
    }

    /**
     * {@link de.consol.sakuli.actions.screenbased.TypingUtil#pasteMasked(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false, logArgs = false)
    public Environment pasteMasked(String text) {
        return typingUtil.pasteMasked(text);
    }

    /**
     * {@link de.consol.sakuli.actions.screenbased.TypingUtil#pasteAndDecrypt(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false, logArgs = false)
    public Environment pasteAndDecrypt(String text) {
        return typingUtil.pasteAndDecrypt(text);
    }


    /********************************************************
     * TYPE FUNCTIONS
     *******************************************************/

    /**
     * See {@link de.consol.sakuli.actions.screenbased.TypingUtil#type(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type over system keyboard", logClassInstance = false)
    public Environment type(String text) {
        return typingUtil.type(text, null);
    }

    /**
     * See {@link de.consol.sakuli.actions.screenbased.TypingUtil#type(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type with pressed modifiers", logClassInstance = false)
    public Environment type(String text, String optModifiers) {
        return typingUtil.type(text, optModifiers);
    }

    /**
     * See {@link de.consol.sakuli.actions.screenbased.TypingUtil#typeMasked(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type over system keyboard", logClassInstance = false, logArgs = false)
    public Environment typeMasked(String text) {
        return typingUtil.typeMasked(text, null);
    }

    /**
     * See {@link de.consol.sakuli.actions.screenbased.TypingUtil#typeMasked(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type with pressed modifiers", logClassInstance = false, logArgs = false)
    public Environment typeMasked(String text, String optModifiers) {
        return typingUtil.typeMasked(text, optModifiers);
    }

    /**
     * See {@link de.consol.sakuli.actions.screenbased.TypingUtil#typeAndDecrypt(String, String)} .
     */
    @ModifySahiTimer
    @LogToResult(message = "decrypt and type over system keyboard", logClassInstance = false, logArgs = false)
    public Environment typeAndDecrypt(String text) {
        return typingUtil.typeAndDecrypt(text, null);
    }

    /**
     * See {@link de.consol.sakuli.actions.screenbased.TypingUtil#typeAndDecrypt(String, String)} .
     */
    @ModifySahiTimer
    @LogToResult(message = "decrypt and type with pressed modifiers", logClassInstance = false, logArgs = false)
    public Environment typeAndDecrypt(String text, String optModifiers) {
        return typingUtil.typeAndDecrypt(text, optModifiers);
    }

    /*********************
     * MOUSE WHEEL FUNCTIONS
     *********************/

    /**
     * {@link TypingUtil#mouseWheelDown(int)}
     */
    @LogToResult(message = "move mouse wheel down x times")
    public Environment mouseWheelDown(int steps) {
        return typingUtil.mouseWheelDown(steps);
    }

    /**
     * {@link TypingUtil#mouseWheelUp(int)}
     */
    @LogToResult(message = "move mouse wheel up x times")
    public Environment mouseWheelUp(int steps) {
        return typingUtil.mouseWheelUp(steps);
    }


    /**
     * {@link de.consol.sakuli.actions.screenbased.TypingUtil#decryptSecret(String)}
     */
    @LogToResult(logClassInstance = false, logArgs = false)
    public String decryptSecret(String secret) {
        return typingUtil.decryptSecret(secret);
    }

    @Override
    public boolean getResumeOnException() {
        return resumeOnException;
    }

    @Override
    public ScreenActionLoader getLoader() {
        return loader;
    }

    @Override
    public RegionImpl getActionRegion() {
        return RegionImpl.toRegion(loader.getScreen(), resumeOnException, loader);
    }
}
