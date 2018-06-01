/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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
var testCase = new TestCase(50, 70);
var env = new Environment();
var screen = new Region();
var appCalc = new Application("/usr/bin/gnome-calculator");
var appGedit = new Application("/usr/bin/gedit");

function checkCentOS() {
    var dist = env.runCommand('cat /etc/os-release').getOutput();
    if (dist.match(/NAME=.*CentOS.*/)) {
        Logger.logInfo('Detected distribution: CentOS  >> overwrite some image patterns');
        testCase.addImagePaths("centos");
    }
}

try {
    checkCentOS();
    _highlight(_link("SSL Manager"));
    _highlight(_link("Logs"));
    _highlight(_link("Online Documentation"));
    _highlight(_link("Test Pages"));
    _highlight(_link("Sample Application"));
    screen.find("sahi_logo.png").highlight();
    testCase.endOfStep("Test Sahi landing page", 10);

    appCalc.open();
    screen.waitForImage("calculator.png", 5).mouseMove().highlight();
    env.type("525");
    env.sleep(2);

    var calcRegion = appCalc.getRegion();
    calcRegion.find("plus.png").highlight().click().type("100");
    calcRegion.find("result.png").highlight().click();
    screen.waitForImage("625", 5).highlight();
    testCase.endOfStep("Calculation", 25);

    appGedit.open();
    screen.waitForImage("gedit.png", 10).highlight();
    env.paste("Initial test passed. Sakuli, Sahi and Sikuli seem to work fine. Exiting...");
    testCase.endOfStep("Editor", 10);

} catch (e) {
    testCase.handleException(e);
} finally {
    appCalc.close(true); //silent
    appGedit.kill(true);  //silent, without exit prompt
    testCase.saveResult();
}
