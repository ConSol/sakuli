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

import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Optional;

/**
 * @author tschneck
 * Date: 20.06.13
 */
public class SakuliCheckedException extends Exception implements SakuliException {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<? extends AbstractTestDataEntity> asyncTestDataRef = Optional.empty();

    /**
     * creates a new {@link SakuliException} from type {@link Exception}
     *
     * @param message message of the exception
     */
    public SakuliCheckedException(String message) {
        super(message);
    }

    /**
     * wraps a {@link Exception} to a {@link SakuliException}
     *
     * @param e
     */
    public SakuliCheckedException(Exception e) {
        //use this constructor to avoid to get the classname as prefix in the exception message
        super(e.getLocalizedMessage(), e);
    }

    /**
     * wraps a {@link Exception} to a {@link SakuliException}
     *
     * @param suppressedException
     * @param message
     */
    public SakuliCheckedException(Exception suppressedException, String message) {
        super(message);
        this.addSuppressed(suppressedException);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }

    /**
     * Provides the meta information on which execution step this exception is thrown, like e.g. {@link org.sakuli.services.TeardownService#handleTeardownException(Exception, boolean, AbstractTestDataEntity)} will use it.
     *
     * @return a reference on the {@link AbstractTestDataEntity} which was executed at the point of the exception is thrown.
     */
    public Optional<? extends AbstractTestDataEntity> getAsyncTestDataRef() {
        return asyncTestDataRef;
    }

    /**
     * Set additional meta information to provide on which execution step in async code like the {@link org.sakuli.services.TeardownService#handleTeardownException(Exception, boolean, AbstractTestDataEntity)} will use it.
     *
     * @param testDataRef extends {@link AbstractTestDataEntity}
     */
    @Override
    public <T extends AbstractTestDataEntity> void setAsyncTestDataRef(T testDataRef) {
        this.asyncTestDataRef = Optional.ofNullable(testDataRef);
    }
}
