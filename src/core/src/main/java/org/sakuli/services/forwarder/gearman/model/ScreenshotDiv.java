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

/**
 * Represents the screenshot div TAG in nagios output.
 *
 * @author tschneck
 *         Date: 05.09.14
 */
public class ScreenshotDiv implements NagiosPayloadString {
    public static final String DEFAULT_SAKULI_SCREENSHOT_DIV_ID = "sakuli_screenshot";

    public static final String STYLE_TEMPLATE = "<style>" +
                ".modalDialog {width:%s;}" +
                ".modalDialog:target {width:auto;margin: 20px auto;overflow: scroll;position: fixed;top: 0;right: 0;bottom: 0;left: 0;z-index: 99999;opacity:1;pointer-events: auto;}" +
                ".modalDialog:target .close {display:block;}" +
                ".modalDialog:target .screenshot {width:100%%;border:2px solid #333;}" +
                ".screenshot {width:98%%;border:2px solid gray;display: block;margin-left:auto;margin-right:auto;margin-bottom:4px;cursor:-webkit-zoom-in; cursor:-moz-zoom-in;}" +
                ".close {display:none;background: #aaa;color: #fff;line-height: 25px;position: absolute;right: 10px;text-align: center;top: 25px;width: 65px;text-decoration: none;font-weight: bold;-webkit-border-radius: 12px;-moz-border-radius: 12px;border-radius: 12px;}" +
                ".close:hover {background: #333;}" +
            "</style>";

    private static final String TEMPLATE =
            "<div id=\"%s\">" +
                "<div id=\"openModal\" class=\"modalDialog\">" +
                    "<a href=\"#close\" title=\"Close\" class=\"close\">Close X</a>" +
                    "<a href=\"#openModal\"><img class=\"screenshot\" src=\"data:image/%s;base64,%s\" ></a>" +
                "</div>" +
            "</div>";

    /**
     * ID vor the DIV TAG
     */
    private String id;
    /**
     * BASE64 encoded screenshot
     */
    private String base64screenshot;
    /**
     * File format of the bas64 encoded image
     */
    private String format;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBase64screenshot() {
        return base64screenshot;
    }

    public void setBase64screenshot(String base64screenshot) {
        this.base64screenshot = base64screenshot;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getPayloadString() {
        return String.format(TEMPLATE, id, format, base64screenshot);
    }

}
