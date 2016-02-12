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
var testCase = new TestCase(60, 70);
var env = new Environment();
var screen = new Region();
var appNotepad = new Application("notepad.exe");
var appCalc = new Application("calc.exe");

try {
    _highlight(_link("SSL Manager"));
    _highlight(_link("Logs"));
    _highlight(_link("Online Documentation"));
    _highlight(_link("Test Pages"));
    _highlight(_link("Sample Application"));

    testCase.endOfStep("Test Sahi landing page",30);
    appCalc.open();
    screen.waitForImage("calculator.png", 20);

    env.type("525");
    env.sleep(1);
    env.setSimilarity(0.99);
    screen.find("plus.png").highlight().click().type("100");
    screen.find("result.png").highlight().click();
	screen.waitForImage("625",10).highlight();
    env.resetSimilarity();
    testCase.endOfStep("Calculation",30);

    appNotepad.open();
    appNotepad.getRegion().waitForImage("notepad.png", 20).highlight();
    env.paste("Initial test passed. Sakuli, Sahi and Sikuli seem to work fine. Exiting...");
    testCase.endOfStep("Editor",30);
    env.sleep(2);
} catch (e) {
    testCase.handleException(e);
} finally {
    appCalc.close(true);      //silent
    appNotepad.close(true);   //silent
    testCase.saveResult();
}
