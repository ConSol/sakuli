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

_dynamicInclude($includeFolder);
var testCase = new TestCase(40, 50);
var env = new Environment();

//vars consol.labs
var $cl_home = "http://labs.consol.de";
var $cl_projekte = "Projects";
var $cl_c_mysql_h = "check_mysql_health";
var $cl_c_oracle_h = "check_oracle_health";

/******************************
 * Description of the test case
 ******************************/
try {
    _navigateTo($cl_home);
    _highlight(_link($cl_projekte));
    _click(_link($cl_projekte));
    Logger.logInfo("LAST-URL: " + testCase.getLastURL());
    env.sleep(2);
    //env.takeScreenshot("testscreenshot.png");    //will be saved in the test suite folder
    _highlight(_link($cl_c_mysql_h));
    _click(_link($cl_c_mysql_h));
    _highlight(_link($cl_c_oracle_h));
    _click(_link($cl_c_oracle_h));
    _setValue(_textbox("s"), "nagios");
    _click(_link("Home[1]"));
    testCase.endOfStep("project", 20);


} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}

//switchWindow();
