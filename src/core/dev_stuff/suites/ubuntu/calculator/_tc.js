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
var testCase = new TestCase(80, 100);
var env = new Environment();
var screen = new Region();

/******************************
 * Description of the test case
 ******************************/
try {

    //get calculator app
    var appCalc = new Application("/usr/bin/gnome-calculator");
    var appGedit = new Application("/usr/bin/gedit");
    try {
        appCalc.open();
        screen.waitForImage("gcalculator.png", 20);
        testCase.endOfStep("init");

        env.type("525");
        env.sleep(2);
        var calcRegion = appCalc.getRegion();
        env.setSimilarity(0.1);
        if (!calcRegion.exists("plus", 2)) {
            Logger.logWarning("PLUS not detected!");
        }
        env.resetSimilarity();
        calcRegion.find("plus.png").click().type("100");
        //appCalc.getRegion().find("plus.png").mouseMove().sleep(2).mouseDown(MouseButton.LEFT).sleep(1).mouseUp(MouseButton.LEFT).type("100");
        calcRegion.find("result.png").click();
        testCase.endOfStep("calculation", 12);

        appGedit.open();
        screen.waitForImage("gedit.png", 20);
        //TODO TS check why getRegion don't work as expected!
        var reg = appCalc.focus().getRegion().grow(50).type(Key.ESC);
        //reg.takeScreenshot("reg");
        var reg2 = new RegionRectangle(reg.getX(), reg.getY(), reg.getW(), reg.getH());
        //reg.takeScreenshot("reg2");
        reg2.waitForImage("gcalculator", 2);
        appGedit.focus();
        env.paste("Initial test passed. Sakuli, Sahi and Sikuli seem to work fine. Exiting...");
        env.sleep(2);
        testCase.endOfStep("gedit paste", 1);

        env.type(Key.ENTER + "Also type special characters like: # ß ! § $ & < > |");
        env.type(Key.ENTER);
        env.keyDown(Key.SHIFT);
        env.type("uppercase");    //Upper would remove the keyDown effekt!
        env.keyUp(Key.SHIFT);
        env.type(Key.ENTER + "Also type special characters like: # ß ! § $ & < > |");
        env.sleep(2);
        testCase.endOfStep("type special characters");

        env.keyDown(Key.ALT);
        appGedit.getRegion().grow(100).find("print").click();
        env.sleep(3);

        env.keyDown(Key.SHIFT);
        // finally close the calculator app!
    } finally {

        appCalc.close(true);
        appGedit.kill();
        //if (screen.exists("close-without-saving", 1)) {
        //    screen.find("close-without-saving").click();
        //}
        testCase.endOfStep("Close Calculator");
    }

    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
