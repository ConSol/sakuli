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
import org.sakuli.exceptions.SakuliCipherException;
import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author Tobias Schneck
 */
public class TypingUtil<A extends Action> {
    private A action;

    public TypingUtil(A action) {
        this.action = action;
    }

    /********************************************************
     * PASTE FUNCTIONS
     *******************************************************/

    /**
     * pastes the text at the current position of the focus/carret
     * <br>
     * using the clipboard and strg/ctrl/cmd-v (paste keyboard shortcut)
     *
     * @param text a string, which might contain unicode characters
     * @return this {@link A} or NULL on errors.
     */
    public A paste(String text) {
        return pasteImpl(text, true);
    }

    /**
     * makes a masked {@link #paste(String)} without any logging.
     *
     * @param text a string, which might contain unicode characters
     * @return this {@link A} or NULL on errors.
     */
    public A pasteMasked(String text) {
        return pasteImpl(text, false);
    }

    /**
     * combines {@link #pasteMasked(String)} and {@link #decryptSecret(String)}.
     *
     * @param text encrypted secret
     * @return this {@link A} or NULL on errors.
     */
    public A pasteAndDecrypt(String text) {
        return pasteImpl(decryptSecret(text), false);
    }


    private A pasteImpl(String text, boolean logging) {
        int returnVal = action.getActionRegion().paste(text);
        if (returnVal != 1) {
            if (!logging) {
                text = "*****";
            }
            action.getLoader().getExceptionHandler().handleException("during pasting of text \"" + text + "\" something went wrong.", action.getActionRegion(), action.getResumeOnException());
            return null;
        }
        return action;
    }


    /********************************************************
     * TYPE FUNCTIONS
     *******************************************************/

    /**
     * Enters the given text one character/key after another using keyDown/keyUp.
     * <p>
     * About the usable Key constants see documentation of {@link org.sikuli.script.Key}.
     * The function could also type UTF-8 unicode characters, if the OS supports it.
     * The text is entered at the current position of the focus.
     * </p>
     *
     * @param text         containing characters and/or {@link org.sikuli.script.Key} constants
     * @param optModifiers (optional) an String with only {@link org.sikuli.script.Key} constants.
     * @return this {@link A} or NULL on errors.
     */
    public A type(String text, String optModifiers) {
        if (StringUtils.isEmpty(optModifiers)) {
            return typeImpl(text, true);
        }
        return typeModifiedImpl(text, optModifiers, true);
    }

    /**
     * Enters the given text one character/key after another using keyDown/keyUp.
     * The entered text will be masked at the logging.
     * <p>
     * About the usable Key constants see documentation of {@link org.sikuli.script.Key}.
     * The function could also type UTF-8 unicode characters, if the OS supports it.
     * The text is entered at the current position of the focus.
     * </p>
     *
     * @param text         containing characters and/or {@link org.sikuli.script.Key} constants
     * @param optModifiers (optional) an String with only {@link org.sikuli.script.Key} constants.
     * @return this {@link A} or NULL on errors.
     */
    public A typeMasked(String text, String optModifiers) {
        if (StringUtils.isEmpty(optModifiers)) {
            return typeImpl(text, false);
        }
        return typeModifiedImpl(text, optModifiers, false);
    }

    /**
     * Decrypt and enters the given text one character/key after another using keyDown/keyUp.
     * The entered text will be masked at the logging. For the deatails of the decryption see {@link #decryptSecret(String)}.
     * <p>
     * About the usable Key constants see documentation of {@link org.sikuli.script.Key}.
     * The function could also type UTF-8 unicode characters, if the OS supports it.
     * The text is entered at the current position of the focus.
     * </p>
     *
     * @param text         containing characters and/or {@link org.sikuli.script.Key} constants
     * @param optModifiers (optional) an String with only {@link org.sikuli.script.Key} constants.
     * @return this {@link A} or NULL on errors.
     */
    public A typeAndDecrypt(String text, String optModifiers) {
        if (StringUtils.isEmpty(optModifiers)) {
            return typeImpl(decryptSecret(text), false);
        }
        return typeModifiedImpl(decryptSecret(text), optModifiers, false);
    }

    private A typeImpl(String text, boolean logging) {
        int returnValue = action.getActionRegion().type(text);
        /**
         * this is needed because in the methode {@link org.sikuli.script.Region#keyin(Object, String, int)}
         * will be reset the type delay to 0.0.
         * */
        action.getLoader().loadSettingDefaults();
        if (returnValue == 1) {
            return action;
        }
        if (logging) {
            text = "****";
        }
        action.getLoader().getExceptionHandler().handleException("could n't type in the text \"" + text + "\"", action.getActionRegion(), action.getResumeOnException());
        return null;
    }


    private A typeModifiedImpl(String text, String modifiers, boolean logging) {
        int returnValue = action.getActionRegion().type(text, modifiers);
        /**
         * this is needed because in the methode {@link org.sikuli.script.Region#keyin(Object, String, int)}
         * will be reset the type delay to 0.0.
         * */
        action.getLoader().loadSettingDefaults();
        if (returnValue == 1) {
            return action;
        }
        if (logging) {
            text = "****";
        }
        action.getLoader().getExceptionHandler().handleException("could n't type with modifiers the text \"" + text + "\"", action.getActionRegion(), action.getResumeOnException());
        return null;

    }

    /**
     * press and hold the given keys including modifier keys <br>use the key constants defined in class Key, <br>which
     * only provides a subset of a US-QWERTY PC keyboard layout <br>might be mixed with simple characters
     * <br>use + to concatenate Key constants
     *
     * @param keys valid keys
     */
    public A keyDown(String keys) {
        try {
            action.getActionRegion().keyDown(keys);
        } catch (Exception e) {
            action.getLoader().getExceptionHandler().handleException(e, action.getActionRegion(), action.getResumeOnException());
        }
        return action;
    }

    /**
     * release the given keys (see {@link TypingUtil#keyDown(String)}).
     *
     * @param keys valid keys
     */
    public A keyUp(String keys) {
        try {
            action.getActionRegion().keyUp(keys);
        } catch (Exception e) {
            action.getLoader().getExceptionHandler().handleException(e, action.getActionRegion(), action.getResumeOnException());
        }
        return action;
    }

    /**
     * Compact alternative for type() with more options <br>
     * - special keys and options are coded as #XN. or #X+ or #X- <br>
     * where X is a refrence for a special key and N is an optional repeat factor <br>
     * A modifier key as #X. modifies the next following key<br>
     * the trailing . ends the special key, the + (press and hold) or - (release) does the same, <br>
     * but signals press-and-hold or release additionally.<br>
     * except #W / #w all special keys are not case-sensitive<br>
     * a #wn. inserts a wait of n millisecs or n secs if n less than 60 <br>
     * a #Wn. sets the type delay for the following keys (must be &gt; 60 and denotes millisecs)
     * - otherwise taken as normal wait<br>
     * Example: wait 2 secs then type CMD/CTRL - N then wait 1 sec then type DOWN 3 times<br>
     * Windows/Linux: write("#w2.#C.n#W1.#d3.")<br>
     * Mac: write("#w2.#M.n#W1.#D3.")<br>
     * for more details about the special key codes and examples consult the docs <br>
     *
     * @param text a coded text interpreted as a series of key actions (press/hold/release)
     */
    public A write(String text) {
        try {
            action.getActionRegion().write(text);
            // will needed because sikuli write method will modify the type delay settings.
            action.getLoader().loadSettingDefaults();
        } catch (Exception e) {
            action.getLoader().getExceptionHandler().handleException(e, action.getActionRegion(), action.getResumeOnException());
        }
        return action;
    }

    /*
     *Decrypt a encrypted secret and returns the value at runtime.
     * The decryption will only work if the encryption and decryption happen on the same physical machine.
     * There will be no logging with the decrypted secret during this step.
     * <p/>
     * To create a encrypted secret see "README.txt".
     *
     * @param secret encrypted secret as {@link String}
     * @return decrypted {@link String}
     */
    public String decryptSecret(String secret) {
        try {
            return action.getLoader().getCipherUtil().decrypt(secret);
        } catch (SakuliCipherException e) {
            action.getLoader().getExceptionHandler().handleException(e, action.getResumeOnException());
        }
        return null;
    }

    /**
     * move the mouse pointer to the given target location and move the
     * wheel the given steps down.
     *
     * @param steps the number of steps
     */
    public A mouseWheelDown(int steps) {
        return mouseWheel(Button.WHEEL_DOWN, steps);
    }

    /**
     * move the mouse pointer to the given target location and move the
     * wheel the given steps up.
     *
     * @param steps the number of steps
     */
    public A mouseWheelUp(int steps) {
        return mouseWheel(Button.WHEEL_UP, steps);
    }

    private A mouseWheel(int wheelDirection, int steps) {
        int ret;
        try {
            ret = action.getActionRegion().wheel(action, wheelDirection, steps);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        if (ret != 1) {
            action.getLoader().getExceptionHandler().handleException("could not interact with the mouse wheel in this region", action.getActionRegion(), action.getResumeOnException());
            return null;
        }
        return action;
    }

    /**
     * Blocks the current testcase execution for x milliseconds
     *
     * @param milliseconds to sleep
     * @return this {@link A} or NULL on errors.
     */
    public A sleep(Integer milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            action.getLoader().getExceptionHandler().handleException(e, true);
            return null;
        }
        return action;
    }
}
