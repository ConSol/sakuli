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
var editorApp = new Application("notepad", true);
/******************************
 * Description of the test case
 ******************************/
try {
    _navigateTo("http://99.99.99.16/wr-applet");

    env.setSimilarity(0.7).setClipboard("TEST");
    var testString = env.getClipboard();
    if (testString != "TEST") {
        throw "NOT EQUAL " + testString + " to TEST";
    }
    env.resetSimilarity();

    var noEd = new RegionRectangle(0, 0, 50, 50).highlight(1).exists("notepad_logo");

    if (noEd == null) {
        Logger.logInfo("NULL");
    }
    if (noEd != null) {
        Logger.logInfo("NOT NULL");

    }
    if (undefined == noEd) {
        Logger.logInfo("UNDEFINED OR NULL");

    }


    var windowsLogo = new Region();
    windowsLogo.find("windows_logo.png");

    env.sleep(2);

    var windowsLogo2 = new Region().find("windows_logo.png");
    windowsLogo2.move(50, -50).highlight(1);
    windowsLogo2.click().doubleClick().rightClick();
    testCase.endOfStep("windows", 10);

    /***
     * NEW STEP
     ***/
    editorApp.setSleepTime(1).open().focus();
    //
    //
    //var secWarn = new Region().waitForImage("1_1_sec_warn.png", 35)
    //    .setH(900)
    //    .highlight(2);
    //secWarn.find("1_1_sec_warn_execute")
    //    .click();

    //secWarn.highlight(2);
    //testCase.endOfStep("securtiy warning", 25);


    var notepadRegion = editorApp.focus().getRegion();
    notepadRegion.waitForImage("notepad_logo", 5).highlight();


    env.setClipboard("... CLIPBORADTEST").pasteClipboard()
        .paste("  ... now differtent text")
        .pasteMasked(" ... now a masked Text!")
        //.pasteAndDecrypt("YQfHiKymG9sI9zf9c5+Feg==")
        .type(Key.ENTER + Key.ENTER + "type a Text")

        .type("now MAX")
        .sleep(1)

        .type(Key.ALT + Key.SPACE + "x")
        .sleep(5)
        .type(Key.ENTER + "small", Key.SHIFT)
        .type(Key.ALT + Key.SPACE + "w")
        .sleep(2)
        .type(Key.WIN);

    var extractedText = notepadRegion.highlight(5).
        move(0, 50).highlight(2)
        .setW(300).highlight(2)
        .setH(100).highlight(2)
        .extractText();
    Logger.logInfo(extractedText);

    editorApp.focus();
    env.typeMasked(Key.ENTER + "a masked text" + Key.ENTER)
        .typeMasked("small" + Key.ENTER, Key.SHIFT)
        //.typeAndDecrypt("YQfHiKymG9sI9zf9c5+Feg==")
        .type(Key.ENTER);
    //.typeAndDecrypt("YQfHiKymG9sI9zf9c5+Feg==", Key.SHIFT);

    notepadRegion.move(0, 50).setH(40).deleteChars(100)
        .grow(20).grow(-18, -18)
        .typeMasked(Key.ENTER + "a masked text" + Key.ENTER)
        .typeMasked("small" + Key.ENTER, Key.SHIFT)
        //.typeAndDecrypt("YQfHiKymG9sI9zf9c5+Feg==")
        .type(Key.ENTER)
        //.typeAndDecrypt("YQfHiKymG9sI9zf9c5+Feg==", Key.SHIFT)
        .paste("  ... now differtent text")
        .pasteMasked(" ... now a masked Text!")
        //.pasteAndDecrypt("YQfHiKymG9sI9zf9c5+Feg==")
    ;

    editorApp.getRegion().find("notepad_logo").highlight(1);

    env.takeScreenshot(_resolvePath() + "\\..\\..\\test.png");

    env.sleep(5);

    editorApp.close();
    testCase.endOfStep("external Application test", 11);


    /***
     * NEW STEP
     ***/



    Logger.logDebug("Sahi wait for 2 seconds!!!");
    _wait(2000);
    testCase.endOfStep("Type into amount 500", 20);


    //Logger.logError("DAS ERROR LOG");


    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/

} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}