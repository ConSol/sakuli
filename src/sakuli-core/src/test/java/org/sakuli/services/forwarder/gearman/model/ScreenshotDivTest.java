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

package org.sakuli.services.forwarder.gearman.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ScreenshotDivTest {

    @Test
    public void testGetPayloadString() throws Exception {
        ScreenshotDiv testling = new ScreenshotDiv();
        testling.setId("test-id");
        testling.setFormat("jpg");
        testling.setBase64screenshot("00001111");

        assertEquals(testling.getPayloadString(), "<div id=\"test-id\">" +
                    "<div id=\"openModal_test-id\" class=\"modalDialog\">" +
                        "<a href=\"#close\" title=\"Close\" class=\"close\">Close X</a>" +
                        "<a href=\"#openModal_test-id\"><img class=\"screenshot\" src=\"data:image/jpg;base64,00001111\" ></a>" +
                    "</div>" +
                "</div>");
    }
}