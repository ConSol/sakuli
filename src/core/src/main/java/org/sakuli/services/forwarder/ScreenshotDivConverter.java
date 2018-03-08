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

package org.sakuli.services.forwarder;

import org.apache.commons.lang.StringUtils;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.checkmk.ProfileCheckMK;
import org.sakuli.services.forwarder.gearman.ProfileGearman;
import org.sakuli.services.forwarder.icinga2.ProfileIcinga2;
import org.sakuli.services.forwarder.json.ProfileJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author tschneck Date: 05.09.14
 */
@ProfileGearman
@ProfileIcinga2
@ProfileCheckMK
@ProfileJson
@Component
public class ScreenshotDivConverter {

    public static final String REGEX_SRC_DATA_IMAGE_BASE64 = "src=\"data:image\\/[\\w]{3,4};base64,[^\\s]*\" >";

    @Autowired
    private SakuliExceptionHandler exceptionHandler;

    public static String removeBase64ImageDataString(String string) {
        if (StringUtils.isNotEmpty(string)) {
            return string.replaceAll(REGEX_SRC_DATA_IMAGE_BASE64, "src=\"\" >");
        }
        return string;
    }

    public ScreenshotDiv convert(Throwable e) {
        if (e != null) {
            String base64String = extractScreenshotAsBase64(e);
            String format = extractScreenshotFormat(e);
            if (base64String != null && format != null) {
                ScreenshotDiv screenshotDiv = new ScreenshotDiv();
                screenshotDiv.setFormat(format);
                screenshotDiv.setBase64screenshot(base64String);
                screenshotDiv.setId(ScreenshotDiv.DEFAULT_SAKULI_SCREENSHOT_DIV_ID + screenshotDiv.hashCode());
                return screenshotDiv;
            }
        }
        return null;
    }

    protected String extractScreenshotAsBase64(Throwable exception) {
        if (exception instanceof SakuliExceptionWithScreenshot) {
            Path screenshotPath = ((SakuliExceptionWithScreenshot) exception).getScreenshot();
            if (screenshotPath != null) {
                try {
                    byte[] binaryScreenshot = Files.readAllBytes(screenshotPath);
                    String base64String = new BASE64Encoder().encode(binaryScreenshot);
                    for (String newLine : Arrays.asList("\n", "\r")) {
                        base64String = StringUtils.remove(base64String, newLine);
                    }
                    return base64String;
                } catch (IOException e) {
                    exceptionHandler.handleException(new SakuliForwarderException(e,
                            String.format("error during the BASE64 encoding of the screenshot '%s'", screenshotPath.toString())));
                }
            }
        }
        return null;
    }

    protected String extractScreenshotFormat(Throwable exception) {
        if (exception instanceof SakuliExceptionWithScreenshot) {
            Path screenshotPath = ((SakuliExceptionWithScreenshot) exception).getScreenshot();
            if (screenshotPath != null) {
                return StringUtils.substringAfterLast(screenshotPath.getFileName().toString(), ".");
            }
        }
        return null;
    }
}
