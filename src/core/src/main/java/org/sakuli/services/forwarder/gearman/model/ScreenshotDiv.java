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

    private static final String STYLE_TEMPLATE = "<style>" +
                ".modalDialog {position: fixed;top: 0;right: 0;bottom: 0;left: 0;z-index: 99999;opacity:0;pointer-events: none;}" +
                ".modalDialog:target {opacity:1;pointer-events: auto;}" +
                ".modalDialog > div {width: auto;margin: 20px auto;overflow: scroll;border: 2px solid #333}" +
                ".close {background: #aaa;color: #fff;line-height: 25px;position: absolute;right: 10px;text-align: center;top: 25px;width: 65px;text-decoration: none;font-weight: bold;-webkit-border-radius: 12px;-moz-border-radius: 12px;border-radius: 12px;}" +
                ".close:hover {background: #333;}" +
            "</style>";

    private static final String TEMPLATE = "<div id=\"openModal\" class=\"modalDialog\">" +
                "<div>" +
                    "<a href=\"#close\" title=\"Close\" class=\"close\">Close X</a>" +
                    "<img style=\"width:100%%;\" src=\"data:image/%s;base64,%s\" >" +
                "</div>" +
            "</div>" +
            "<div style=\"width:%s\" id=\"%s\">" +
                "<a href=\"#openModal\"><img style=\"width:98%%;border:2px solid gray;display: block;margin-left:auto;margin-right:auto;margin-bottom:4px;cursor:-webkit-zoom-in; cursor:-moz-zoom-in;\" src=\"data:image/%s;base64,%s\" ></a>" +
            "</div>";

    /**
     * Width of HTML DIV tag, e.g. "640px"
     */
    private String width;
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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

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
        return STYLE_TEMPLATE + String.format(TEMPLATE, format, base64screenshot, width, id, format, base64screenshot);
    }

}
