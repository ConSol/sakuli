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
var testCase = new TestCase(20, 30);
var env = new Environment();
var screen = new Region();
var appCalc = new Application("/usr/bin/gnome-calculator");
var appGedit = new Application("/usr/bin/gedit");

function checkCentOS() {
    var dist = env.runCommand('cat /etc/os-release').getOutput();
    if (dist.match(/NAME=.*CentOS.*/)) {
        Logger.logInfo('Detected distribution: CentOS  >> override some image patterns');
        testCase.addImagePaths("centos");
    }
}

try {
    //uncomment this line to get the containerized test started and to play around
    //env.sleep(100000000);
    checkCentOS();
    //uncomment this line to raise an exception which sends a screenshot to OMD
    // _highlight(_link("SSL Manager_DOES_NOT_EXIST"));

    _highlight(_link("SSL Manager"));
    _highlight(_link("Logs"));
    _highlight(_link("Online Documentation"));
    _highlight(_link("Test Pages"));
    _highlight(_link("Sample Application"));

    testCase.endOfStep("Test Sahi landing page", 5);
    appCalc.open();

    screen.waitForImage("calculator.png", 5);
    env.type("525");
    env.sleep(2);
    var calcRegion = appCalc.getRegion();
    calcRegion.find("plus.png").click().type("100");
    calcRegion.find("result.png").click();
    screen.waitForImage("625", 10);
    testCase.endOfStep("Calculation", 10);

    appGedit.open();
    screen.waitForImage("gedit.png", 20);
    env.paste("Initial test passed. Sakuli, Sahi and Sikuli seem to work fine. Exiting...");
    testCase.endOfStep("Editor", 10);
    env.sleep(4);
} catch (e) {
    testCase.handleException(e);
} finally {
    appCalc.kill(true);  //silent
    appGedit.kill(true); //silent
    testCase.saveResult();
}
