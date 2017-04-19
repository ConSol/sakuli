/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.utils;

import org.sakuli.actions.environment.Application;
import org.sakuli.actions.screenbased.Key;
import org.sakuli.loader.BeanLoader;
import org.sikuli.script.IRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author tschneck
 *         Date: 2/26/16
 */
public class CleanUpHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanUpHelper.class);

    public static void releaseAllModifiers() {
        IRobot robot = BeanLoader.loadScreenActionLoader().getScreen().getRobot();
        LOGGER.debug("release all modifier keys!");

        Arrays.asList(Key.C_CTRL, Key.C_ALT, Key.C_ALTGR, Key.C_SHIFT, Key.C_WIN, Key.C_CAPS_LOCK)
                .forEach(i -> robot.typeChar(i, IRobot.KeyMode.RELEASE_ONLY));
    }

    public static void cleanClipboard() {
        LOGGER.debug("clear Clipboard content!");
        Application.setClipboard(" ");
    }
}
