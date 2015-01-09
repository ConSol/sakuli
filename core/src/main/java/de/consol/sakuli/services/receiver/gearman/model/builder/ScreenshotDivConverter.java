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

package de.consol.sakuli.services.receiver.gearman.model.builder;

import de.consol.sakuli.datamodel.Converter;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliExceptionWithScreenshot;
import de.consol.sakuli.exceptions.SakuliReceiverException;
import de.consol.sakuli.services.receiver.gearman.GearmanProperties;
import de.consol.sakuli.services.receiver.gearman.ProfileGearman;
import de.consol.sakuli.services.receiver.gearman.model.ScreenshotDiv;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author tschneck Date: 05.09.14
 */
@ProfileGearman
@Component
public class ScreenshotDivConverter implements Converter<ScreenshotDiv, Throwable> {

    @Autowired
    private SakuliExceptionHandler exceptionHandler;
    @Autowired
    private GearmanProperties gearmanProperties;

    @Override
    public ScreenshotDiv convert(Throwable e) {
        if (e != null) {
            String base64String = extractScreenshotAsBase64(e);
            String format = extractScreenshotFormat(e);
            if (base64String != null && format != null) {
                ScreenshotDiv screenshotDiv = new ScreenshotDiv();
                screenshotDiv.setFormat(format);
                screenshotDiv.setBase64screenshot(base64String);
                String divID = ScreenshotDiv.DEFAULT_SAKULI_SCREENSHOT_DIV_ID;
                screenshotDiv.setId(divID);
                screenshotDiv.setWidth(gearmanProperties.getOutputScreenshotDivWidth());
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
                    return StringUtils.remove(base64String, "\n");
                } catch (IOException e) {
                    exceptionHandler.handleException(new SakuliReceiverException(e,
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
