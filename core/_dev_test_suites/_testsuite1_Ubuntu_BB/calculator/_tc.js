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

/* global _include,_alert, _navigateTo,_isFF,_isChrome, backEndInit, getFilePathOfTestcase, sakuliInit, writeTC2DB, _wait, navigate */

/*************************************
 * Initialization of the JAVA backend
 * and set warning and critical time
 *************************************/

_dynamicInclude($includeFolder);
var testCase = new TestCase(80, 100);
var env = new Environment();

/******************************
 * Description of the test case
 ******************************/
try {

    //get calculator app
    var calculatorApp = new Application("galculator").setSleepTime(1);
    try {
        //open the app
        calculatorApp.open();
        testCase.endOfStep("Open Calculator", 3);

        //caculate
        //env.setSimilarity(0.9);
        var calculatorRegion = calculatorApp.getRegion();
        calculatorRegion.type("525");
        env.sleep(2);
        calculatorRegion.find("plus.png").click().type("100");
        calculatorRegion.find("calculate").click();
        testCase.endOfStep("calculate 525 +100", 20);

        env.sleep(2);
        calculatorRegion.find("sqrt").click();
        env.sleep(2);
        testCase.endOfStep("sqrt on 625", 10);

        calculatorRegion.find("clear").click();
        env.sleep(2);
        // finally close the calculator app!
    } finally {

        calculatorApp.closeApp();
        testCase.endOfStep("Close Calculator", 10);
    }

    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
