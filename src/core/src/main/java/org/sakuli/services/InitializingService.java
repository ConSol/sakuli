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

package org.sakuli.services;

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.exceptions.SakuliInitException;

/**
 * Service Interface for all initializing actions.
 *
 * @author tschneck
 *         Date: 22.05.14
 */
public interface InitializingService extends PrioritizedService {

    /**
     * initialize the test suite object:
     * <ol>
     * <li>check {@link TestSuiteProperties#testSuiteSuiteFile}</li>
     * <li>load the {@link TestCase}s</li>
     * </ol>
     */
    void initTestSuite() throws SakuliInitException;
}
