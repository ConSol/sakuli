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

package org.sakuli.actions.environment;

import net.sf.sahi.util.OSUtils;
import org.sakuli.actions.Action;
import org.sakuli.actions.ModifySahiTimer;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.actions.screenbased.TypingUtil;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.loader.BeanLoader;
import org.sakuli.loader.ScreenActionLoader;
import org.sakuli.utils.CommandLineUtil;
import org.sakuli.utils.CommandLineUtil.CommandLineResult;
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

    /**
     * Creates a new Environment object, to control all non-region actions like typing or pasting.
     */
    public Environment() {
        this(false);
    }

    /**
     * Creates a new Environment object, to control all non-region actions like typing or pasting.
     *
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     */
    public Environment(boolean resumeOnException) {
        this.resumeOnException = resumeOnException;
        this.loader = BeanLoader.loadScreenActionLoader();
        this.typingUtil = new TypingUtil<>(this);
    }

    /**
     * Runs the assigned command on the host and returns the result. __Attention:__ this is OS depended feature! So be
     * aware which os you are running, maybe us to check {@link Environment#isLinux()}  or {@link Environment#isWindows()}.
     *
     * @param command OS depended command as {@link CommandLineResult}
     * @return the result as {@link String}
     * @throws SakuliException if the command won't exit with value 0
     */
    public static CommandLineResult runCommand(String command) throws SakuliException {
        return runCommand(command, true);
    }

    /**
     * Equal to {@link #runCommand(String)}, but with option to avoid throwing an exception if the exit code != 0
     */
    public static CommandLineResult runCommand(String command, boolean throwException) throws SakuliException {
        return CommandLineUtil.runCommand(command, throwException);
    }

    /**
     * set a new default similarity of the screen capturing methods. To reset the similarty call {@link
     * #resetSimilarity()}.
     *
     * @param similarity double value between 0 and 1, default = {@link ActionProperties#defaultRegionSimilarity}
     * @return this {@link Environment} or NULL on errors.
     */
    @LogToResult(message = "set similarity level", logClassInstance = false)
    public Environment setSimilarity(double similarity) {
        if (similarity >= 0 && similarity <= 1) {
            this.loader.getSettings().setMinSimilarity(similarity);
        } else {
            loader.getExceptionHandler().handleException("The similartiy must be a double value between 0 and 1!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * Resets the current similarty of the screen capturing methods to the original default value of
     * {@link ActionProperties#defaultRegionSimilarity}.
     *
     * @return this {@link Environment} or NULL on errors.
     */
    @LogToResult(message = "reset similarity level to default")
    public Environment resetSimilarity() {
        this.loader.getSettings().restetMinSimilarity();
        return this;
    }

    /**
     * @return a {@link Region} object from the current focused window or NULL on errors.
     */
    @LogToResult(logClassInstance = false)
    public Region getRegionFromFocusedWindow() {
        org.sikuli.script.Region origRegion = App.focusedWindow();
        if (origRegion != null) {
            return new Region(origRegion, resumeOnException);
        }
        loader.getExceptionHandler().handleException("couldn't extract a Region from the current focused window", resumeOnException);
        return null;
    }

    /**
     * Takes a screenshot of the current screen and saves it to the overgiven path. If there ist just a file name, the
     * screenshot will be saved in your testsuite log folder.
     *
     * @param pathName "pathname/filname.format" or just "filename.format"<br> for example "test.png".
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
        return typingUtil.sleep(seconds * 1000L);
    }

    /**
     * Blocks the current testcase execution for x seconds
     *
     * @param seconds to sleep
     * @return this {@link Environment} or NULL on errors.
     */
    @LogToResult(message = "sleep and do nothing for x seconds", logClassInstance = false)
    public Environment sleep(Double seconds) {
        return typingUtil.sleep((long) (seconds * 1000L));
    }

    /**
     * Blocks the current testcase execution for x milliseconds
     *
     * @param milliseconds to sleep
     * @return this {@link Environment} or NULL on errors.
     */
    @LogToResult(message = "sleep and do nothing for x milliseconds", logClassInstance = false)
    public Environment sleepMs(Integer milliseconds) {
        return typingUtil.sleep(Long.valueOf(milliseconds));
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
     * Clean the content of the clipboard.
     */
    @LogToResult(logClassInstance = false)
    public Environment cleanClipboard() {
        Application.setClipboard(" ");
        return this;
    }

    /**
     * pastes the current clipboard content into the focused area. Will do the same as "STRG + C".
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
     * copy the current selected item or text to the clipboard. Will do the same as "STRG + V".
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

    /**
     * {@link org.sakuli.actions.screenbased.TypingUtil#paste(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false)
    public Environment paste(String text) {
        return typingUtil.paste(text);
    }

    /**
     * {@link org.sakuli.actions.screenbased.TypingUtil#pasteMasked(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false, logArgs = false)
    public Environment pasteMasked(String text) {
        return typingUtil.pasteMasked(text);
    }

    /**
     * {@link org.sakuli.actions.screenbased.TypingUtil#pasteAndDecrypt(String)}.
     */
    @ModifySahiTimer
    @LogToResult(logClassInstance = false, logArgs = false)
    public Environment pasteAndDecrypt(String text) {
        return typingUtil.pasteAndDecrypt(text);
    }

    /**
     * See {@link org.sakuli.actions.screenbased.TypingUtil#type(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type over system keyboard", logClassInstance = false)
    public Environment type(String text) {
        return typingUtil.type(text, null);
    }

    /**
     * See {@link org.sakuli.actions.screenbased.TypingUtil#type(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type with pressed modifiers", logClassInstance = false)
    public Environment type(String text, String optModifiers) {
        return typingUtil.type(text, optModifiers);
    }

    /**
     * See {@link org.sakuli.actions.screenbased.TypingUtil#typeMasked(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type over system keyboard", logClassInstance = false, logArgs = false)
    public Environment typeMasked(String text) {
        return typingUtil.typeMasked(text, null);
    }

    /**
     * See {@link org.sakuli.actions.screenbased.TypingUtil#typeMasked(String, String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "type with pressed modifiers", logClassInstance = false, logArgs = false)
    public Environment typeMasked(String text, String optModifiers) {
        return typingUtil.typeMasked(text, optModifiers);
    }

    /**
     * See {@link org.sakuli.actions.screenbased.TypingUtil#typeAndDecrypt(String, String)} .
     */
    @ModifySahiTimer
    @LogToResult(message = "decrypt and type over system keyboard", logClassInstance = false, logArgs = false)
    public Environment typeAndDecrypt(String text) {
        return typingUtil.typeAndDecrypt(text, null);
    }

    /**
     * See {@link org.sakuli.actions.screenbased.TypingUtil#typeAndDecrypt(String, String)} .
     */
    @ModifySahiTimer
    @LogToResult(message = "decrypt and type with pressed modifiers", logClassInstance = false, logArgs = false)
    public Environment typeAndDecrypt(String text, String optModifiers) {
        return typingUtil.typeAndDecrypt(text, optModifiers);
    }

    /**
     * See {@link TypingUtil#keyDown(String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "press key down", logClassInstance = false)
    public Environment keyDown(String keys) {
        return typingUtil.keyDown(keys);
    }

    /**
     * See {@link TypingUtil#keyUp(String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "press key up", logClassInstance = false)
    public Environment keyUp(String keys) {
        return typingUtil.keyUp(keys);
    }

    /**
     * See {@link TypingUtil#write(String)}.
     */
    @ModifySahiTimer
    @LogToResult(message = "interpret and write the following expresion", logClassInstance = false)
    public Environment write(String text) {
        return typingUtil.write(text);
    }

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
     * {@link org.sakuli.actions.screenbased.TypingUtil#decryptSecret(String)}
     */
    @LogToResult(logClassInstance = false, logArgs = false)
    public String decryptSecret(String secret) {
        return typingUtil.decryptSecret(secret);
    }

    /**
     * @return true, if the OS is any instance of an Windows based OS
     */
    @LogToResult(logClassInstance = false, logArgs = false)
    public boolean isWindows() {
        switch (OSUtils.identifyOS()) {
            case "xp":
                return true;
            case "nt":
                return true;
            default:
                return false;
        }
    }

    /**
     * @return true, if the OS is any instance of an Linux based OS
     */
    @LogToResult(logClassInstance = false, logArgs = false)
    public boolean isLinux() {
        switch (OSUtils.identifyOS()) {
            case "linux":
                return true;
            default:
                return false;
        }
    }

    /**
     * @return a identifier of the current OS
     */
    @LogToResult(logClassInstance = false, logArgs = false)
    public String getOsIdentifier() {
        return OSUtils.identifyOS();
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
