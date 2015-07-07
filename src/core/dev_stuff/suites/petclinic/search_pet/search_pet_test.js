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

    clickHighlight(_link("Find owners"));
    _setValue(_textbox("lastName"), "Black");
    env.sleep(presentationDelay);
    clickHighlight(_submit("Find Owner"));
    testCase.endOfStep("find owner", 10);

    env.sleep(presentationDelay);
    clickHighlight(_link("Edit Pet"));
    var today = new Date();
    // all variable names which should be respected by Sahi functions must start with an '$'.
    var $todayString = today.getFullYear() + '/' + today.getMonth() + '/' + today.getDay();
    Logger.logInfo("todayString: " + $todayString);
    _setValue(_textbox("birthDate"), $todayString);
    _setValue(_textbox("name"), "Lucky Sakuli Bird");
    env.sleep(presentationDelay);
    clickHighlight(_submit("Update Pet"));
    testCase.endOfStep("update pet", 10);

    env.sleep(presentationDelay);
    clickHighlight(_link("Add Visit"));
    var $nextYear = (today.getFullYear() + 1) + '/' + today.getMonth() + '/' + today.getDay();
    Logger.logInfo("nextYearString: " + $nextYear);
    _setValue(_textbox("date"), $nextYear);
    _setValue(_textbox("description"), "a new visist next year");
    env.sleep(presentationDelay);
    clickHighlight(_submit("Add Visit"));
    testCase.endOfStep("add visit", 10);


    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
