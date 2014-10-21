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

package de.consol.sakuli.services;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import de.consol.sakuli.exceptions.SakuliProxyException;

/**
 * Service Interface for all initializing actions.
 *
 * @author tschneck
 *         Date: 22.05.14
 */
public interface InitializingService {

    /**
     * initialize the test suite object:
     * <ol>
     * <li>check {@link TestSuiteProperties#testSuiteSuiteFile}</li>
     * <li>load the {@link TestCase}s</li>
     * </ol>
     */
    void initTestSuite() throws SakuliProxyException;
}
