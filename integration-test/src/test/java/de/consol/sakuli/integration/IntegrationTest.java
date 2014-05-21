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

package de.consol.sakuli.integration;

import java.io.File;

/**
 * basic interface  for all INTEGRATION tests
 * <p/>
 * to run your test als integration test annotate it with
 * <br/>
 * {@code @Test(groups = IntegrationTest.GROUP)}
 *
 * @author tschneck
 *         Date: 09.04.14
 */
public interface IntegrationTest {
    public static final String GROUP = "integration";
    public static final String INCLUDE_FOLDER_PATH = ".." + File.separator + "core" + File.separator + "src" + File.separator + "main" + File.separator + "_include";
    public static final String TEST_FOLDER_PATH = "." + File.separator + "src" + File.separator + "test" + File.separator +
            "resources" + File.separator + "_testsuite4IT";

}
