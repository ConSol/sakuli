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

/**
 * @author tschneck
 *         Date: 20.06.13
 */
public class SakuliException extends Exception {

    public boolean resumeOnException = false;

    /**
     * creates a new {@link SakuliException} from type {@link Throwable}
     *
     * @param message message of the exception
     */
    public SakuliException(String message) {
        super(message);
    }

    /**
     * wraps a {@link Throwable} to a {@link SakuliException}
     *
     * @param e
     */
    public SakuliException(Throwable e) {
        super(e);
    }

    /**
     * wraps a {@link Throwable} to a {@link SakuliException}
     *
     * @param suppressedException
     * @param message
     */
    public SakuliException(Throwable suppressedException, String message) {
        super(message);
        this.addSuppressed(suppressedException);
    }

    /**
     * creates a new {@link SakuliException} from type {@link Throwable}
     *
     * @param message message of the exception
     */
    public SakuliException(String message, boolean resumeOnException) {
        super(message);
        this.resumeOnException = resumeOnException;
    }

    /**
     * wraps a {@link Throwable} to a {@link SakuliException}
     *
     * @param resumeOnException if false the RhinoScript
     * @param e
     */
    public SakuliException(Throwable e, boolean resumeOnException) {
        super(e);
        this.resumeOnException = resumeOnException;
    }

}
