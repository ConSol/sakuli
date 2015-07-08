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

_dynamicInclude($includeFolder);
var env = new Environment();
var testCase = new TestCase(60, 70, overloadScreenshots());
var screen = new Region();
var appCalc;
var appGedit;

try {
    appCalc = getCalcApp().open();
    screen.waitForImage("calculator.png", 20);

    env.type("525");
    env.sleep(2);
    env.setSimilarity(0.99);
    screen.find("plus.png").click().type("100");
    env.resetSimilarity();
    screen.find("result.png").click();
    screen.waitForImage("625", 10);
    screen.find("edit").click();
    screen.find("copy").click();
    testCase.endOfStep("Calculation", 30);

    appGedit = getEditorApp().open();
    screen.waitForImage("editor_header.png", 20);
    env.type("Now paste Result into the editor\n");
    env.pasteClipboard();
    screen.exists("editor_result");
    testCase.endOfStep("Editor", 30);
    env.sleep(4);
} catch (e) {
    testCase.handleException(e);
} finally {
    appCalc.close(true);  //silent
    appGedit.close(true); //silent
    if (env.isLinux() && screen.exists("close-without-saving", 1)) {
        screen.find("close-without-saving").click();
    }
    testCase.saveResult();
}

function overloadScreenshots() {
    if (env.isWindows()) {
        return $testSuiteFolder + "/calculator/windows";
    }
    return null;
}
function getCalcApp() {
    if (env.isLinux()) {
        return new Application("/usr/bin/gnome-calculator");
    }
    if (env.isWindows()) {
        return new Application("calc.exe");
    }
}

function getEditorApp() {
    if (env.isLinux()) {
        return new Application("/usr/bin/gedit");
    }
    if (env.isWindows()) {
        return new Application("notepad.exe");
    }
}