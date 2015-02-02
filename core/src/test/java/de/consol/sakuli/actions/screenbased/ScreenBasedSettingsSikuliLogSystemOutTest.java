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

package de.consol.sakuli.actions.screenbased;

import de.consol.sakuli.actions.settings.ScreenBasedSettings;
import de.consol.sakuli.aop.AopBaseTest;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.utils.LoggerInitializer;
import org.sikuli.basics.Debug;
import org.testng.annotations.Test;

public class ScreenBasedSettingsSikuliLogSystemOutTest extends AopBaseTest {

    @Test
    public void testDoHandleSikuliLog() throws Throwable {
        ScreenBasedSettings testling = BeanLoader.loadBean(ScreenBasedSettings.class);
        testling.setDefaults();
        BeanLoader.loadBean(LoggerInitializer.class).initLoggerContext();
        System.out.println("LOG-FOLDER: " + logFile);

        //errors
        Debug.error("SAKULI-ERROR-message");
        assertLastLine(logFile, "SAKULI-", LogLevel.INFO, "[error] SAKULI-ERROR-message");

        //enter message like for typing
        Debug.enter("SAKULI-ENTER-message");
        assertLastLine(logFile, "SAKULI-", LogLevel.INFO, "[profile] entering: SAKULI-ENTER-message");

        //info messages
        Debug.info("SAKULI-INFO-message");
        assertLastLine(logFile, "SAKULI-", LogLevel.INFO, "[info] SAKULI-INFO-message");

        //debug messages
        Debug.log(-3, "SAKULI-DEBUG-message");
        assertLastLine(logFile, "SAKULI-", LogLevel.INFO, "[debug] SAKULI-DEBUG-message");
    }
}