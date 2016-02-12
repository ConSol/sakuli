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
var testCase = new TestCase(40, 50);


/******************************
 * Description of the test case
 ******************************/
try {


//Login
    //make ssh portforwarding `
    _navigateTo("http://localhost:8001/shop");
    _highlight(_link("Anmelden"));
    _click(_link("Anmelden"));
    _highlight(_label("E-Mail-Adresse"));
    _click(_label("E-Mail-Adresse"));
    _setValue(_textbox("lgn_usr[1]"), "sahimon.itoring@googlemail.com");
    _setValue(_password("lgn_pwd"), "sahimon");
    _highlight(_submit("Anmelden"));
    _click(_submit("Anmelden"));
    testCase.endOfStep("Login", 5);

//Warenkorb löschen
    _highlight(_link("Warenkorb"));
    _click(_link("Warenkorb"));
    if (!_condition(_isVisible(_div("Der Warenkorb ist leer.")))) {
        _highlight(_checkbox("checkAll"));
        _click(_checkbox("checkAll"));
        _highlight(_submit("entfernen"));
        _click(_submit("entfernen"));
    }
    testCase.endOfStep("Warenkorb leeren", 6);

    _highlight(_link("Startseite"));
    _click(_link("Startseite"));
//
////Artikel 1
//    _highlight(_link("Kiteboarding \u00BB"));
//    _click(_link("Kiteboarding \u00BB"));
//    _highlight(_image("Zubeh\u00F6r"));
//    _click(_image("Zubeh\u00F6r"));
//    _highlight(_link("Klebeband DACRON KITEFIX"));
//    _click(_link("Klebeband DACRON KITEFIX"));
//    _highlight(_span("Variante w\u00E4hlen"));
//    _click(_span("Variante w\u00E4hlen"));
//    _highlight(_link("schwarz"));
//    _click(_link("schwarz"));
//    _highlight(_textbox("am"), "3");
//    _setValue(_textbox("am"), "3");
//    _highlight(_submit("in den Warenkorb"));
//    _click(_submit("in den Warenkorb"));
//    testCase.endOfStep("Art Klebeband DACRON", 15);
//
////Artikel 2
//    _highlight(_link("Wakeboarding \u00BB"));
//    _click(_link("Wakeboarding \u00BB"));
//    _highlight(_image("Wakeboards"));
//    _click(_image("Wakeboards"));
//    _highlight(_span("Wakeboard LIQUID FORCE GROOVE 2010"));
//    _click(_span("Wakeboard LIQUID FORCE GROOVE 2010"));
//    _highlight(_submit("in den Warenkorb"));
//    _click(_submit("in den Warenkorb"));
//    testCase.endOfStep("Art Wakeboard GROOVE 2010", 12);
//
////Artikel 3
//    _highlight(_link("Bekleidung \u00BB"));
//    _click(_link("Bekleidung \u00BB"));
//    _highlight(_link("F\u00FCr Sie"));
//    _click(_link("F\u00FCr Sie"));
//    _highlight(_image("Shirts & Co."));
//    _click(_image("Shirts & Co."));
//    _highlight(_link("Kuyichi 1/2 Sleeve Shirt"));
//    _click(_link("Kuyichi 1/2 Sleeve Shirt"));
//    _highlight(_submit("in den Warenkorb"));
//    _click(_submit("in den Warenkorb"));
//    testCase.endOfStep("Art Shirt Kuyichi", 13);
//
//prüfe Warenkorb
    java.lang.System.out.println("NOW SHOULD COME a ERROR: ");
    _highlight(_link("Warenkorb"));
    _click(_link("Warenkorb"));
//    _assert(_isVisible(_link("Klebeband DACRON KITEFIX, schwarz")));
//    _assert(_isVisible(_link("Wakeboard LIQUID FORCE GROOVE 2010")));
//    _assert(_isVisible(_link("Kuyichi 1/2 Sleeve Shirt")));
    testCase.endOfStep("Warenkorb checken", 3);
//
////Ausloggen
//    _highlight(_link("Logout"));
//    _click(_link("Logout"));


    /************************************************
     * Exception handling and shutdown of test case
     **********************************************/
} catch (e) {
    java.lang.System.out.println("CATCH-ERROR");
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
