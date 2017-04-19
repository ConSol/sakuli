/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.javaDSL.service;

import net.sf.sahi.client.Browser;
import org.sakuli.datamodel.properties.TestSuiteProperties;

/**
 * Interface for all Sahi initializing tasks
 *
 * @author tschneck
 *         Date: 07.05.15
 */
public interface SahiInitializingService {

    /**
     * Initialize a Browser which is controlled by Sahi.
     * The Browser is configurable in the {@link TestSuiteProperties}.
     *
     * @return a initialized instance of {@link Browser}.
     */
    Browser getBrowser();

}
