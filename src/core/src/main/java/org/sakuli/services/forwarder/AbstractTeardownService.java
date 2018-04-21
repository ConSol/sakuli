/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.services.TeardownService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract root class for {@link org.sakuli.services.TeardownService}s
 * Contains impelementations with states.
 */
public abstract class AbstractTeardownService implements TeardownService {

    @Autowired
    private SakuliExceptionHandler exceptionHandler;

    @Override
    public void handleTeardownException(@NonNull Exception e, boolean async, @NonNull AbstractTestDataEntity testDataRef) {
        if (async) {
            exceptionHandler.handleException(addTestRef(e, testDataRef));
        } else {
            exceptionHandler.handleException(e);
        }
    }

    Exception addTestRef(@NonNull Exception e, @NonNull AbstractTestDataEntity testDataRef) {
        if (!(e instanceof SakuliException)) {
            e = new SakuliCheckedException(e);
        }
        ((SakuliException) e).setAsyncTestDataRef(testDataRef);
        return e;
    }
}
