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

package org.sakuli.exceptions;

import java.nio.file.Path;

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
     * creates a {@link SakuliException} from a {@link String} and stores the Path to screenshot
     *
     * @param message
     * @param screenshot
     */
    public SakuliExceptionWithScreenshot(String message, Path screenshot, boolean resumeOnException) {
        super(message, resumeOnException);
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

    public SakuliExceptionWithScreenshot(Throwable e, Path screenshot, boolean resumeOnException) {
        super(e, resumeOnException);
        this.screenshot = screenshot;
    }

    public Path getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(Path screenshot) {
        this.screenshot = screenshot;
    }
}
