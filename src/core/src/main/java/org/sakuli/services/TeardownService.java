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
public interface TeardownService extends PrioritizedService {

    default void tearDown(Optional<AbstractTestDataEntity> dataEntity) {
        dataEntity.filter(TestSuite.class::isInstance).map(TestSuite.class::cast)
                .ifPresent(this::teardownTestSuite);

        dataEntity.filter(TestCase.class::isInstance).map(TestCase.class::cast)
                .ifPresent(this::teardownTestCase);

        dataEntity.filter(TestCaseStep.class::isInstance).map(TestCaseStep.class::cast)
                .ifPresent(this::teardownTestCaseStep);
    }

    /**
     * Triggers the different implementations of the {@link TeardownService} for the {@link TestSuite} object.
     */
    default void teardownTestSuite(@NonNull TestSuite testSuite) {
        throw new SakuliRuntimeException("Method 'teardownTestSuite' is not implemented for forwarder class " + getClass().getSimpleName());
    }

    /**
     * Triggers the different implementations of the {@link TeardownService} for the {@link TestCase} object.
     */
    default void teardownTestCase(@NonNull TestCase testCase) {
        throw new SakuliRuntimeException("Method 'teardownTestCase' is not implemented for forwarder class " + getClass().getSimpleName());
    }

    /**
     * Triggers the different implementations of the {@link TeardownService} for the {@link TestCaseStep} object.
     */
    default void teardownTestCaseStep(@NonNull TestCaseStep testCaseStep) {
        throw new SakuliRuntimeException("Method 'teardownTestCaseStep' is not implemented for forwarder class " + getClass().getSimpleName());
    }

}
