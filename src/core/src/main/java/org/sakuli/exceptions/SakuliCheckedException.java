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
 * Date: 20.06.13
 */
public class SakuliCheckedException extends Exception implements SakuliException {

    /**
     * creates a new {@link SakuliCheckedException} from type {@link Throwable}
     *
     * @param message message of the exception
     */
    public SakuliCheckedException(String message) {
        super(message);
    }

    /**
     * wraps a {@link Throwable} to a {@link SakuliCheckedException}
     *
     * @param e
     */
    public SakuliCheckedException(Throwable e) {
        //use this constructor to avoid to get the classname as prefix in the exception message
        super(e.getLocalizedMessage(), e);
    }

    /**
     * wraps a {@link Throwable} to a {@link SakuliCheckedException}
     *
     * @param suppressedException
     * @param message
     */
    public SakuliCheckedException(Throwable suppressedException, String message) {
        super(message);
        this.addSuppressed(suppressedException);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }

}
