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
        env.logInfo("clipboad:", env.getClipboard())
    } else if (action == "pasteMasked") {
        env.pasteMasked(str);
    } else if (action == "type") {
        env.type(str);
    } else if (action == "typeMasked") {
        env.typeMasked(str);
    }
    env.logInfo("COUNT:" + i);
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
        env.logInfo("Try " + i + " start --------------");

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

        env.logInfo($a + ", " + $b + ", " + $c + ", " + $d + ", " + $e);
        if ($a.toString !== pw || $b.toString !== pw || $c.toString !== pw || $d.toString !== pw || $e.toString !== pw) {
            _alert("MISMATCH!");
            env.logError("MISMATCH-at-cound:" + i);

        } else {
            env.logInfo("SAME: '" + $u + "' and '" + $p + "'");
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
