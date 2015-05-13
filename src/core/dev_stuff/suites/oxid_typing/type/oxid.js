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

_dynamicInclude($includeFolder);
var testCase = new TestCase(60, 70);
var env = new Environment();

// 1234ABCabc
// pOlGA6bgmv+BemmtqKMHsw==

var action = "type";
var i = 0;
function fill(str) {
    i++;
    if (action == "paste") {
        env.paste(str);
        Logger.logInfo("clipboad:", env.getClipboard())
    } else if (action == "pasteMasked") {
        env.pasteMasked(str);
    } else if (action == "type") {
        env.type(str);
    } else if (action == "typeMasked") {
        env.typeMasked(str);
    }
    Logger.logInfo("COUNT:" + i);
}

try {

//    var app = Application("notepad.exe");
//    app.open().getRegion().click();
    _focus(_textbox("invadr[oxuser__oxfname]"));
    _setValue(_textbox("invadr[oxuser__oxfname]"), "ddd");

    env.sleep(3);
    _setValue(_textbox("invadr[oxuser__oxfname]"), "DONE");
    for (i = 0; i < 15; i++) {
        var pw = "adkajld()!";
        Logger.logInfo("Try " + i + " start --------------");

        _focus(_textbox("invadr[oxuser__oxfname]"));
        //env.pasteMasked(pw);
        fill(pw);
        env.sleep(1);
        _focus(_textbox("invadr[oxuser__oxlname]"));
        //env.pasteMasked(pw);
        fill(pw);
        env.sleep(1);
        _focus(_textbox("invadr[oxuser__oxcompany]"));
        //env.pasteMasked(pw);
        fill(pw);
        env.sleep(1);
        _focus(_textbox("invadr[oxuser__oxaddinfo]"));
        //env.pasteMasked(pw);
        fill(pw);
        env.sleep(1);
        _focus(_textbox("invadr[oxuser__oxstreet]"));
        //env.pasteMasked(pw);
        fill(pw);
        env.sleep(1);

        var $a, $b, $c, $d, $e;
        _set($a, _getValue(_textbox("invadr[oxuser__oxfname]")));
        _set($b, _getValue(_textbox("invadr[oxuser__oxlname]")));
        _set($c, _getValue(_textbox("invadr[oxuser__oxcompany]")));
        _set($d, _getValue(_textbox("invadr[oxuser__oxaddinfo]")));
        _set($e, _getValue(_textbox("invadr[oxuser__oxstreet]")));

        Logger.logInfo($a + ", " + $b + ", " + $c + ", " + $d + ", " + $e);
        if ($a.toString !== pw || $b.toString !== pw || $c.toString !== pw || $d.toString !== pw || $e.toString !== pw) {
            _alert("MISMATCH!");
            Logger.logError("MISMATCH-at-cound:" + i);

        } else {
            Logger.logInfo("SAME: '" + $u + "' and '" + $p + "'");
        }

        _setValue(_textbox("invadr[oxuser__oxfname]"), "");
        _setValue(_textbox("invadr[oxuser__oxlname]"), "");
        _setValue(_textbox("invadr[oxuser__oxcompany]"), "");
        _setValue(_textbox("invadr[oxuser__oxaddinfo]"), "");
        _setValue(_textbox("invadr[oxuser__oxstreet]"), "");
        env.sleep(1);
    }


} catch (e) {
    testCase.handleException(e);
} finally {
    testCase.saveResult();
}
