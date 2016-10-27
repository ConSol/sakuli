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

    for (i = 0; i < 10000; i++) {
        for (j = 0; j < 20; j++) {
            if (j != 100) {
                env.paste(j + "#1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0");
                env.type(Key.ENTER);
            }

        }
        //env.sleep(2);
        //for (k = 0; k < 3; k++) {
        //    env.type(k + "#a-b-c-d-e-f-g-h-i-j-a-b-c-d-e-f-g-h-i-j-a-b-c-d-e-f-g-h-i-j");
        //    env.type(Key.ENTER);
        //}
        //
        //env.sleep(9999);
        env.setSimilarity(0.99);
        screen.waitForImage("eval", 1).highlight();
        env.type("a", Key.CTRL);
        env.type(Key.DELETE)
    }

} catch (e) {
    testCase.handleException(e);
} finally {

    appNotepad.close(true);
    testCase.saveResult();
}
