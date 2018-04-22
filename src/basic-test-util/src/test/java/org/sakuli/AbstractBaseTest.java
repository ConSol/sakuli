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

package org.sakuli;

import java.nio.file.Paths;

import static org.testng.Assert.assertTrue;

/**
 * @author tschneck Date: 25.07.13
 */
public abstract class AbstractBaseTest extends AbstractLogAwareTest {

    public static final String SAKULI_HOME_FOLDER_PATH = Paths.get("../sakuli-common/src/main/resources/org/sakuli/common")
            .toAbsolutePath().normalize().toString();
    public static final String TEST_FOLDER_PATH = getResource("/_testsuite4JUnit");
    public static final String TEST_CONTEXT_PATH = "JUnit-beanRefFactory.xml";

    public static void assertRegExMatch(String string, String regex) {
        assertTrue(string.matches(regex),
                String.format("string '%s' won't match to regex '%s'", string, regex));
    }

    public static void assertContains(String string, String contains) {
        assertTrue(string.contains(contains),
                String.format("string '%s' won't contain '%s'", string, contains));
    }

    protected String getTestContextPath() {
        return TEST_CONTEXT_PATH;
    }

    protected String getSakuliHomeFolderPath() {
        return SAKULI_HOME_FOLDER_PATH;
    }

    protected String getTestFolderPath() {
        return TEST_FOLDER_PATH;
    }
}
