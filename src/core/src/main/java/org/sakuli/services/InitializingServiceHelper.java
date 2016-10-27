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

import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.loader.BeanLoader;

import java.io.FileNotFoundException;

/**
 * @author Tobias Schneck
 */
public class InitializingServiceHelper {
    /**
     * Invokes all available InitializingServices to init the test suite data.
     *
     * @throws FileNotFoundException
     */
    public static void invokeInitializingServcies() throws FileNotFoundException {
        BeanLoader.loadMultipleBeans(InitializingService.class).values().stream()
                .sorted(new PrioritizedServiceComparator<>())
                .forEach(s -> {
                    try {
                        s.initTestSuite();
                    } catch (SakuliInitException e) {
                        throw new SakuliRuntimeException(e);
                    }
                });
    }
}
