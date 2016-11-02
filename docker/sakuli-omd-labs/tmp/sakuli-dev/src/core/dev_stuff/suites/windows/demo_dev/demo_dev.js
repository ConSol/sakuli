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

/* global _include,_alert, _navigateTo,_isFF,_isChrome, backEndInit, getFilePathOfTestcase, sakuliInit, writeTC2DB, _wait, navigate */

/*************************************
 * Initialization of the JAVA backend
 * and set warning and critical time
 *************************************/
_dynamicInclude($includeFolder);
var testCase = new TestCase(60, 70);
var env = new Environment();
var screen = new Region();
var appNotepad = new Application("notepad.exe");
var appCalc = new Application("calc.exe");

//vars consol.labs
var $cl_home = "http://labs.consol.de";
var $cl_projekte = "Projekte";
var $cl_c_mysql_h = "check_mysql_health";
var $cl_c_oracle_h = "check_oracle_health";

/**********
 * TAB+ALT
 *********/
function switchWindow() {
    env.type(Key.TAB, Key.ALT);
}

/***************
 * Go back to notepad
 **************/
function backToNotepad() {
    return appNotepad.focus();
}


/******************************
 * Description of the test case
 ******************************/
try {
    /************************
     * Step for Notepad
     ***********************/
    appNotepad.open();
    env.keyDown(Key.SHIFT);
    screen.find("notepad++").rightClick();
    screen.waitForImage("run_as_admin",5).highlight();
    env.keyUp(Key.SHIFT);

    backToNotepad().getRegion().highlight();
    env.type("Welcome to")
        .type("99", Key.SHIFT)  //types on german keyboard "))"
        .type("Sakuli")
        .type("88", Key.SHIFT)  //types on german keyboard "(("
        .type(Key.ENTER + Key.ENTER);

    //env.keyDown(Key.ALT).type(Key.NUM0 + Key.NUM9 + Key.NUM2).keyUp(Key.ALT).type("\n\n");
    env.type("I will help you to test your projects, like webapplications...\n");
    appNotepad.getRegion().type("\nmichael.bopp").type("q", Key.ALTGR).type("lidl.com");
    env.sleep(2);
    testCase.endOfStep("notepad", 20);

    /************************
     * Step for labs.consol
     ***********************/
    //switchWindow();
    //_navigateTo($cl_home);
    //_highlight(_link($cl_projekte));
    //_click(_link($cl_projekte));
    //Logger.logInfo("LAST-URL: " + testCase.getLastURL());
    //env.sleep(5);
    //env.takeScreenshot("testscreenshot.png");    //will be saved in the test suite folder
    //_highlight(_link($cl_c_mysql_h));
    //_click(_link($cl_c_mysql_h));
    //_highlight(_link($cl_c_oracle_h));
    //_click(_link($cl_c_oracle_h));
    //_setValue(_textbox("s"), "nagios");
    //_click(_link("Home[1]"));
    //testCase.endOfStep("project", 20);


    /*****************
     * print test client
     *****************/
    backToNotepad();
    env.type("I can also test client applications, like calc...\n");
    env.sleep(2);
    testCase.endOfStep("print_test_client", 10);


    /**************
     * Open calc
     *************/
    appCalc.open();
    env.sleep(2);
    testCase.endOfStep("open_calc", 5);
    env.setSimilarity(0.99);
    var calculatorRegion = appCalc.getRegion();
    calculatorRegion.type("525");
    env.sleep(2);
    calculatorRegion.find("plus.png").click().type("100");
    calculatorRegion.find("calculate.png").click();
    testCase.endOfStep("calculate 525 +100", 20);

    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    // appNotepad.close(true);
    new Application("Editor").kill();
    appCalc.close(true);
    testCase.saveResult();
}
