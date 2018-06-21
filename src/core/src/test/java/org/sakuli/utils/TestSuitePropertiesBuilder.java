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

package org.sakuli.utils;

import static org.testng.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.sakuli.datamodel.properties.TestSuiteProperties;

/**
 * @author tschneck Date: 22.05.14
 */
public class TestSuitePropertiesBuilder {

    private TestSuiteProperties testSuiteProperties;

    public TestSuitePropertiesBuilder(Class<?> classDef, String resourcePath, String suiteId) throws URISyntaxException {
        this.testSuiteProperties = getTestProps(classDef, resourcePath, suiteId);
    }

    public TestSuitePropertiesBuilder withFilters(String... filters) {
        return withFilters(Arrays.asList(filters));
    }

    public TestSuitePropertiesBuilder withFilters(List<String> filters) {
        this.testSuiteProperties.setTestCaseFilters(filters);
        return this;
    }

    public TestSuiteProperties build() {
        return this.testSuiteProperties;
    }

    private TestSuiteProperties getTestProps(Class<?> classDef, String resourcePath, String suiteId) throws URISyntaxException {
        Path folderPath = Paths.get(classDef.getResource(resourcePath).toURI());
        assertTrue(Files.exists(folderPath));
        assertTrue(Files.isDirectory(folderPath));

        TestSuiteProperties props = new TestSuiteProperties();
        props.setTestSuiteFolder(folderPath);
        props.setTestSuiteSuiteFile(Paths.get(folderPath.toString() + TestSuiteProperties.TEST_SUITE_SUITE_FILE_APPENDER));
        props.setTestSuiteId(suiteId);
        props.setLoadTestCasesAutomatic(true);
        return props;
    }
}
