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

/**** Exclude this global variables from JSLint Warnings ****/
/* global navigator, window, java, Packages,saveResult,step, $output, _set, _stopOnError, _logExceptionAsFailure,_resolvePath,_include, $sahi_userdata, $guid, $capture, initialize */


/*****************************************************************************************************
 * KEY constants - representing modifier keys
 *****************************************************************************************************/

Key = {
    SPACE: " ",
    ENTER: '\n',
    BACKSPACE: "\b",
    TAB: "\t",
    ESC: "\u001b",
    UP: "\ue000",
    RIGHT: "\ue001",
    DOWN: "\ue002",
    LEFT: "\ue003",
    PAGE_UP: "\ue004",
    PAGE_DOWN: "\ue005",
    DELETE: "\ue006",
    END: "\ue007",
    HOME: "\ue008",
    INSERT: "\ue009",
    F1: "\ue011",
    F2: "\ue012",
    F3: "\ue013",
    F4: "\ue014",
    F5: "\ue015",
    F6: "\ue016",
    F7: "\ue017",
    F8: "\ue018",
    F9: "\ue019",
    F10: "\ue01A",
    F11: "\ue01B",
    F12: "\ue01C",
    F13: "\ue01D",
    F14: "\ue01E",
    F15: "\ue01F",
    SHIFT: "\ue020",
    CTRL: "\ue021",
    ALT: "\ue022",
    ALTGR: "\ue043",
    META: "\ue023",
    CMD: "\ue023",
    WIN: "\ue042",
    PRINTSCREEN: "\ue024",
    SCROLL_LOCK: "\ue025",
    PAUSE: "\ue026",
    CAPS_LOCK: "\ue027",
    NUM0: "\ue030",
    NUM1: "\ue031",
    NUM2: "\ue032",
    NUM3: "\ue033",
    NUM4: "\ue034",
    NUM5: "\ue035",
    NUM6: "\ue036",
    NUM7: "\ue037",
    NUM8: "\ue038",
    NUM9: "\ue039",
    SEPARATOR: "\ue03A",
    NUM_LOCK: "\ue03B",
    ADD: "\ue03C",
    MINUS: "\ue03D",
    MULTIPLY: "\ue03E",
    DIVIDE: "\ue03F",
    DECIMAL: "\ue040",
    CONTEXT: "\ue041"
};




