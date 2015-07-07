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
_dynamicInclude($testSuiteFolder + "/common_functions.js");
var testCase = new TestCase(40, 50);
var env = new Environment();

var presentationDelay = 0; //seconds

/******************************
 * Description of the test case
 ******************************/
try {
    assertHighlight(_link("Home"));
    assertHighlight(_link("Find owners"));
    assertHighlight(_link("Veterinarians"));
    assertHighlight(_link("Error"));
    assertHighlight(_link("Help"));
    testCase.endOfStep("validate entry menu", 10);

    clickHighlight(_link("Veterinarians"));
    assertHighlight(_tableHeader("Name"));
    assertHighlight(_tableHeader("Specialties"));
    assertHighlight(_link("View as XML"));
    assertHighlight(_link(/Subscribe to.*/));
    env.sleep(presentationDelay);
    testCase.endOfStep("validate veteris menu", 10);

    clickHighlight(_tableHeader("Name"));
    env.sleep(presentationDelay);
    _highlight(_row(1));
    _assertContainsText('Sharon Jenkins', _row(1));
    _highlight(_row(6));
    _assertContainsText('Helen Leary', _row(6));
    env.sleep(presentationDelay);
    clickHighlight(_tableHeader("Name"));
    env.sleep(presentationDelay);
    _highlight(_row(1));
    _assertContainsText('Helen Leary', _row(1));
    _highlight(_row(6));
    _assertContainsText('Sharon Jenkins', _row(6));
    env.sleep(presentationDelay);
    testCase.endOfStep("check name sorting", 10);

    clickHighlight(_tableHeader("Specialties"));
    env.sleep(presentationDelay);
    _highlight(_row(1));
    _assertContainsText('Linda Douglas', _row(1));
    _assertContainsText('dentistry surgery', _row(1));
    _highlight(_row(6));
    _assertContainsText('Rafael Ortega', _row(6));
    _assertContainsText('surgery', _row(6));
    env.sleep(presentationDelay);
    clickHighlight(_tableHeader("Specialties"));
    env.sleep(presentationDelay);
    _highlight(_row(1));
    _assertContainsText('Rafael Ortega', _row(1));
    _assertContainsText('surgery', _row(1));
    _highlight(_row(6));
    _assertContainsText('Linda Douglas', _row(6));
    _assertContainsText('dentistry surgery', _row(6));
    env.sleep(presentationDelay);
    testCase.endOfStep("check specialty sorting", 10);

    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
