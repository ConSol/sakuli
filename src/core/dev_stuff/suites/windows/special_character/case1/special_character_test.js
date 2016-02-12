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
var screen = new Region();
var appNotepad = new Application("notepad.exe");


try {
    appNotepad.open();
    env.sleep(3);

    env.type("C:\\Program Files (x86)\\Mozilla Firefox").sleep(3);

    env.type("e", Key.WIN);
    new Region().find("pc_logo").right(100).highlight()
        .click()
        .type("C:\\Program Files (x86)\\Mozilla Firefox" + Key.ENTER);
    env.sleep(3).type(Key.F4, Key.ALT);
} catch (e) {
    testCase.handleException(e);
} finally {

    appNotepad.close(true);
    testCase.saveResult();
}
