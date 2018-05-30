/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliRuntimeException;

import java.util.Optional;

/**
 * Service Interface which will be called on different teardown phases of the {@link AbstractTestDataEntity} objects
 *
 * @author tschneck
 * Date: 2/12/16
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface TeardownService extends PrioritizedService {

    /**
     * see {@link #tearDown(Optional, boolean)}.
     */
    default void tearDown(Optional<AbstractTestDataEntity> dataEntity) {
        tearDown(dataEntity, false);
    }

    /**
     * Triggers the default actions if some {@link AbstractTestDataEntity} are called by {@link org.sakuli.datamodel.helper.TestSuiteHelper}.
     * Default caller method for:
     * - {@link #teardownTestSuite(TestSuite)}
     * - {@link #teardownTestCase(TestCase)}
     * - {@link #teardownTestCaseStep(TestCaseStep)}
     *
     * @param dataEntity instace of {@link AbstractTestDataEntity}
     * @param asyncCall  indicates if a call is triggerd in an async process to the main process to use the correect exception handling, see {@link #handleTeardownException(Exception, boolean, AbstractTestDataEntity)}.
     */
    default void tearDown(Optional<AbstractTestDataEntity> dataEntity, boolean asyncCall) {
        try {
            dataEntity.filter(TestSuite.class::isInstance).map(TestSuite.class::cast)
                    .ifPresent(this::teardownTestSuite);

            dataEntity.filter(TestCase.class::isInstance).map(TestCase.class::cast)
                    .ifPresent(this::teardownTestCase);

            dataEntity.filter(TestCaseStep.class::isInstance).map(TestCaseStep.class::cast)
                    .ifPresent(this::teardownTestCaseStep);
        } catch (Exception e) {
            handleTeardownException(e, asyncCall, dataEntity.get());
        }
    }

    /**
     * Define exception handling when {@link #tearDown(Optional)} will throw an exception.
     * Can't implement in an function context like here.
     *
     * @param e           any {@link Exception}
     * @param async       defines if the caller is an asynchronous procedure to the main process of {@link org.sakuli.starter.SakuliStarter}
     * @param testDataRef Provides the meta information on which execution step this exception is thrown. See {@link org.sakuli.exceptions.SakuliException#setAsyncTestDataRef(AbstractTestDataEntity)}
     */
    void handleTeardownException(@NonNull Exception e, boolean async, @NonNull AbstractTestDataEntity testDataRef);

    /**
     * Triggers the different implementations of the {@link TeardownService} for the {@link TestSuite} object.
     * On Exception a {@link RuntimeException} should be thrown to be catched from {@link #tearDown(Optional, boolean)}
     */
    default void teardownTestSuite(@NonNull TestSuite testSuite) throws RuntimeException {
        throw new SakuliRuntimeException("Method 'teardownTestSuite' is not implemented for forwarder class " + getClass().getSimpleName());
    }

    /**
     * Triggers the different implementations of the {@link TeardownService} for the {@link TestCase} object.
     * On Exception a {@link RuntimeException} should be thrown to be catched from {@link #tearDown(Optional, boolean)}
     */
    default void teardownTestCase(@NonNull TestCase testCase) throws RuntimeException {
        throw new SakuliRuntimeException("Method 'teardownTestCase' is not implemented for forwarder class " + getClass().getSimpleName());
    }

    /**
     * Triggers the different implementations of the {@link TeardownService} for the {@link TestCaseStep} object.
     * On Exception a {@link RuntimeException} should be thrown to be catched from {@link #tearDown(Optional, boolean)}
     */
    default void teardownTestCaseStep(@NonNull TestCaseStep testCaseStep) throws RuntimeException {
        throw new SakuliRuntimeException("Method 'teardownTestCaseStep' is not implemented for forwarder class " + getClass().getSimpleName());
    }

}
