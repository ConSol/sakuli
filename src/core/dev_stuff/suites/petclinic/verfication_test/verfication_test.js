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

/* global _include,_alert, _navigateTo,_isFF,_isChrome, sakuliInit, getFilePathOfTestcase, sakuliInit, writeTC2DB, _wait, navigate */

/*************************************
 * Initialization of the JAVA backend
 * and set warning and critical time
 *************************************/
_dynamicInclude($includeFolder);
var testCase = new TestCase(40, 50);
var env = new Environment();
var appGedit = new Application("/usr/bin/gedit");
var appNotepad = new Application("notepad.exe");

/******************************
 * Description of the test case
 ******************************/
try {
    _highlight(_link("SSL Manager"));
    _highlight(_link("Logs"));
    _highlight(_link("Online Documentation"));
    _highlight(_link("Test Pages"));
    _highlight(_link("Sample Application"));

    testCase.endOfStep("Test Sahi landing page", 30);

    if (env.isWindows()) {
        appNotepad.open().getRegion().waitForImage("notepad.png", 10);
    }
    if (env.isLinux) {
        appGedit.open().getRegion().waitForImage("gedit.png", 10);
    }
    env.paste("Initial test passed. Sakuli, Sahi and Sikuli seem to work fine. Exiting...")
        .sleep(2);
    testCase.endOfStep("Test Editor App", 30);
    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    appGedit.close(true);    //silent
    appNotepad.close(true);  //silent
    testCase.saveResult();
}
