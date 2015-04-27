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

package org.sakuli.actions.screenbased;

import org.sikuli.script.Button;

/**
 * Enum represents the different mouse action buttons.
 *
 * @author tschneck
 *         Date: 20.03.15
 */
public enum MouseButton {

    LEFT(Button.LEFT),
    RIGHT(Button.RIGHT),
    MIDDLE(Button.MIDDLE);


    private int value;

    private MouseButton(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
