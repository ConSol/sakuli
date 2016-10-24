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

package org.sakuli.exceptions;

import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author tschneck
 *         Date: 20.06.13
 */
public class SakuliExceptionWithScreenshot extends SakuliException {

    private Path screenshot;

    /**
     * creates a {@link SakuliException} from a {@link String} and stores the Path to screenshot
     *
     * @param message
     * @param screenshot
     */
    public SakuliExceptionWithScreenshot(String message, Path screenshot) {
        super(message);
        this.screenshot = screenshot;
    }


    /**
     * creates a {@link SakuliException} from a {@link Throwable} and stores the Path to screenshot
     *
     * @param e
     * @param screenshot
     */
    public SakuliExceptionWithScreenshot(Throwable e, Path screenshot) {
        super(e);
        this.screenshot = screenshot;
    }

    public Path getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(Path screenshot) {
        this.screenshot = screenshot;
    }

    @Override
    public String extractScreenshotAsBase64() {
        if (getScreenshot() != null) {
            try {
                byte[] binaryScreenshot = Files.readAllBytes(getScreenshot());
                String base64String = new BASE64Encoder().encode(binaryScreenshot);
                for (String newLine : Arrays.asList("\n", "\r")) {
                    base64String = StringUtils.remove(base64String, newLine);
                }
                return base64String;
            } catch (IOException e) {
                throw new SakuliRuntimeException(
                        String.format("error during the BASE64 encoding of the screenshot '%s'", getScreenshot().toString()), e);
            }
        }
        return null;
    }

    @Override
    public String extractScreenshotFormat() {
        if (getScreenshot() != null) {
            return StringUtils.substringAfterLast(getScreenshot().getFileName().toString(), ".");
        }
        return null;
    }

}
