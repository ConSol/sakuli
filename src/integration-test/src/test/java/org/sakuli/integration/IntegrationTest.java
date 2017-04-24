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

package org.sakuli.integration;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Basic interface  for all INTEGRATION tests. To run your test als integration test annotate it with
 * {@code @Test(groups = IntegrationTest.GROUP)}.
 *
 * @author tschneck Date: 09.04.14
 */
public interface IntegrationTest {
    String GROUP = "integration";
    String GROUP_UI = "ui";
    String SAKULI_MAIN_FOLDER_PATH = StringUtils.replace(
            "../sakuli-common/src/main/resources/org/sakuli/common", "/", File.separator);

}
