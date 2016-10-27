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

/**
 * @author tschneck
 *         Date: 06.08.13
 */
public class SakuliCipherException extends SakuliException {

    public String interfaceLog;

    /**
     * @param message exception message
     */
    public SakuliCipherException(String message, String interfaceLog) {
        super(message);
        this.interfaceLog = interfaceLog;
    }

    /**
     * @param e any {@link Throwable}
     */
    public SakuliCipherException(Throwable e, String interfaceLog) {
        super(e);
        this.interfaceLog = interfaceLog;
    }

    /**
     * creates a exception with the main message and adds the {@link Throwable}
     *
     * @param mainMessage
     * @param suppressedThrowable
     */
    public SakuliCipherException(String mainMessage, String interfaceLog, Throwable suppressedThrowable) {
        super(mainMessage);
        this.addSuppressed(suppressedThrowable);
        this.interfaceLog = interfaceLog;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n==> Interfaces: " + interfaceLog;
    }
}
