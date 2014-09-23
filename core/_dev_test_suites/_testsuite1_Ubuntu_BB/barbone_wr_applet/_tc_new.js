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


/******************************
 * Description of the test case
 ******************************/
try {

    var environment = new Environment(false).setSimilarity(0.5);

    environment.setClipboard("TEST");
    var testString = environment.getClipboard();
    if (testString != "TEST") {
        throw "NOT EQUAL " + testString + " to TEST";
    }


    /***
     * NEW STEP
     ***/
    // !!! JUST A SAMPLE FOR LINUX -> there must be os depedent calls !!!

    var editorApp = new Application("gedit").setSleepTime(3);
    try {
        editorApp.open();


        var notepadRegion = editorApp.getRegion();

        notepadRegion.paste("foobar(),.-;:");
        notepadRegion.paste(editorApp.getName());

        notepadRegion.highlight(2);


        var newNotpadRegion = new RegionImage("gedit_doc_logo").grow(500, 300);
        newNotpadRegion.click().paste("BLA BAL BAL");

        var extractText = new RegionImage("gedit_doc_logo").find().move(-5, 20).grow(50, 0).extractText();
        newNotpadRegion.typeMasked(extractText);

    } finally {
        //environment.type(Key.ALT + Key.F4);
        editorApp.closeApp();
        new Region().waitForImage("gedit_close_without_saving", 10).click();
        environment.sleep(1);
        testCase.endOfStep("external Application test", 25);
    }

    /***
     * NEW STEP
     ***/
    _navigateTo("http://99.99.99.16/wr-applet", true);
//    var secWarn = testCase.wait("1_1_sec_warn.png", 20);
//    testCase.clickOnRegion(testCase.findInRegion("1_1_sec_warn_execute", secWarn.below(300)));
    testCase.endOfStep("securtiy warning", 25);


//    //sakuli.takeScreenshot("/home/SAKULI/bevorIchAkzeptiere.png");
//    var ichAkzeptiere = sakuli.find("1_1_ichakzeptiere.png");
//    var aktz_cb = sakuli.findInRegion("1_1_sec_warn_execute", ichAkzeptiere);
//    sakuli.clickOnRegion(aktz_cb);
//    sakuli.click("1_1_sec_warn_execute");
//    sakuli.endOfStep("security warning", 20);

//    /***
//     * NEW STEP
//     ***/
//    testCase.wait("2_app.png", 10);
//    //type in Ammount
////    sakuli.typeEncrypted("yIXge0AKW8TDeDHDepJOeg==", "2_betrag.png");
//
//    var betragRegion = testCase.find("2_betrag").below(25);
//    betragRegion.setW(45);
//    var textFromBetrag = testCase.extractText(betragRegion);
//    testCase.logInfo("Extractet TEXT: " + textFromBetrag);
//    if (textFromBetrag != "Betrag") {
//        throw "now valid Text " + textFromBetrag;
//    }
//    testCase.type("560", "2_betrag.png");
//
//    //select currency "Tschechische Krone"
//    var currencyLine = testCase.find("3_fw_kronen.png");
//    var fwButton = testCase.findInRegion("3_fw_button.png", currencyLine);
//    testCase.clickOnRegion(fwButton);

//    //click OK-Button
//    testCase.click("4_ok_button");
    environment.logInfo("Sahi wait for 2 seconds!!!");
    _wait(2000);
    testCase.endOfStep("Type into amount 500", 20);


//    /***
//     * NEW STEP
//     ***/
//    testCase.logInfo("select currency 'Japanische Yen'");
//    var currencyLine = testCase.find("5_fw_yen.png");
//    var fwbutton = testCase.findInRegion("3_fw_button.png", currencyLine);
//    testCase.clickOnRegion(fwbutton);
//    //click OK-Button
//    testCase.click("4_ok_button");
//    testCase.endOfStep("Select new currency", 5);
//
//
//    /***
//     * NEW STEP
//     ***/
//    testCase.logInfo("Sahi wait for 1 seconds!!!");
//    _wait(1000);
//    testCase.click("6_beenden.png");
//    testCase.endOfStep("applet executed", 2);
//
//
//    /***
//     * NEW STEP
//     ***/
//    testCase.logInfo("Sahi wait for 2 seconds!!!");
//    _wait(2000);
//    _alert("Alert for Sikuli click action");
//    testCase.click("7_browser_ok");
//    //navigate to finished site
//    _navigateTo("http://99.99.99.16/wr-applet/finished", true);
//    testCase.logInfo("Sahi wait for 2 seconds!!!");
//    _wait(2000);
//    testCase.endOfStep("click on alert and go to 'finished' site", 7);

    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}