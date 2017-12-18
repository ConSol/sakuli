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

/**
 * Wrapper class for {@link org.sikuli.script.Key} Represents the javascript file "sakuli_Key.js"
 * <p>
 * All non-character keys are represented by *Key* constants which can be used in type functions.
 * <p>
 * The following {@link Key} values are available:
 * <p>
 * `SPACE`, `ENTER`, `BACKSPACE`, `TAB`, `ESC`, `UP`, `RIGHT`, `DOWN`, `LEFT`, `PAGE_UP`, `PAGE_DOWN`, `DELETE`, `END`,
 * `HOME`, `INSERT`, `F1`, `F2`, `F3`, `F4`, `F5`, `F6`, `F7`, `F8`, `F9`, `F10`, `F11`, `F12`, `F13`, `F14`, `F15`,
 * `SHIFT`, `CTRL`, `ALT`, `ALTGR`, `META`, `CMD`, `WIN`, `PRINTSCREEN`, `SCROLL_LOCK`, `PAUSE`, `CAPS_LOCK`, `NUM0`,
 * `NUM1`, `NUM2`, `NUM3`, `NUM4`, `NUM5`, `NUM6`, `NUM7`, `NUM8`, `NUM9`, `SEPARATOR`, `NUM_LOCK`, `ADD`, `MINUS`,
 * `MULTIPLY`, `DIVIDE`, `DECIMAL`, `CONTEXT`
 * <p>
 * Example:
 * <p>
 * Press `F1`:
 * <p>
 * env.type(Key.F1);
 * <p>
 * Close a window by typing the shortcut `ALT + F4`
 * <p>
 * // the second parameter is the held (="modifier") key
 * env.type(Key.F4, Key.ALT);
 * <p>
 * Open the file manager on Windows with shortcut `WIN + e`:
 * <p>
 * env.type("e", Key.META)
 * <p>
 * Do something application specific with shortcut `CTRL + ALT + b` (CTRL + ALT = ALTGR):
 * <p>
 * env.type("b", Key.ALTGR)
 * <p>
 * Closing an window over typing the short cut `ALT + F4`:
 * <p>
 * env.type(Key.F4, Key.ALT);
 * <p>
 * __Using `Key.ALTGR` on Unix:__
 * TIP: To enable the key command `ALTGR` on unix systems please bind it to `CTRL + ALT`. For more information
 * see [stackexchange.com - how-to-bind-altgr-to-ctrl-alt](http://unix.stackexchange.com/questions/157834/how-to-bind-altgr-to-ctrl-alt).
 *
 * @author tschneck Date: 08.01.15
 */
public class Key extends org.sikuli.script.Key {

}

