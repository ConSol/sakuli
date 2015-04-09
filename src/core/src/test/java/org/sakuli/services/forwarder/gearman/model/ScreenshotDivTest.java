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
        testling.setWidth("600px");
        testling.setId("test-id");
        testling.setFormat("jpg");
        testling.setBase64screenshot("00001111");

        assertEquals(testling.getPayloadString(), "<div style=\"width:600px\" id=\"test-id\">" +
                "<img style=\"width:98%;border:2px solid gray;display: block;margin-left:auto;margin-right:auto;margin-bottom:4px\" " +
                "src=\"data:image/jpg;base64,00001111\" >" +
                "</div>");
    }
}