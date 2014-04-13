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
var testCase = new TestCase(60, 70);
var env = new Environment();
var appCalc = new Application("galculator");

/******************************
 * Description of the test case
 ******************************/
try {
    appCalc.open();
    env.sleep(2);
    var regionCalc = appCalc.getRegion();

    regionCalc.waitOnVanish("calculate", 3);
//    // TODO RH here should be defined a callback:
//    regionCalc.waitOnVanish("calculate", 3, function(){
//        //callback action
//        var appEditor = new Application("gedit").open();
//        appEditor.type("TEST");
//        appEditor.close();
//
//    });

    appCalc.closeApp();
} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
