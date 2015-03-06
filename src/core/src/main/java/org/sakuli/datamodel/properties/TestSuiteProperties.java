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

package org.sakuli.datamodel.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck Date: 09.05.14
 */
@Component
public class TestSuiteProperties {

    public static final String TEST_SUITE_FOLDER = "sakuli.testsuite.folder";
    public static final String TEST_SUITE_PROPERTIES_FILE_NAME = "testsuite.properties";
    public static final String TEST_SUITE_PROPERTIES_FILE_APPENDER = File.separator + TEST_SUITE_PROPERTIES_FILE_NAME;
    public static final String TEST_SUITE_SUITE_FILE_NAME = "testsuite.suite";
    public static final String TEST_SUITE_SUITE_FILE_APPENDER = File.separator + TEST_SUITE_SUITE_FILE_NAME;

    public static final String SUITE_ID = "testsuite.id";
    public static final String SUITE_NAME = "testsuite.name";
    public static final String WARNING_TIME = "testsuite.warningTime";
    public static final String CRITICAL_TIME = "testsuite.criticalTime";
    public static final String BROWSER_NAME = "testsuite.browser";

    /**
     * Special propety to enable java-based defining of test cases
     */
    public static final String LOAD_TEST_CASES_AUTOMATIC_PROPERTY = "saklui.load.testcases.automatic";

    @Value("${" + TEST_SUITE_FOLDER + "}")
    private String testSuiteFolderPropertyValue;
    private Path testSuiteFolder;
    @Value("${" + SUITE_ID + "}")
    private String testSuiteId;
    @Value("${" + SUITE_NAME + "}")
    private String testSuiteName;
    @Value("${" + WARNING_TIME + "}")
    private int warningTime;
    @Value("${" + CRITICAL_TIME + "}")
    private int criticalTime;
    private Path testSuiteSuiteFile;
    @Value("${" + BROWSER_NAME + "}")
    private String browserName;
    @Value("${" + LOAD_TEST_CASES_AUTOMATIC_PROPERTY + ":true}") //default = TRUE
    private boolean loadTestCasesAutomatic;

    @PostConstruct
    public void initFolders() {
        testSuiteFolder = Paths.get(testSuiteFolderPropertyValue).normalize();
        testSuiteSuiteFile = Paths.get(testSuiteFolder.toString() + TEST_SUITE_SUITE_FILE_APPENDER).normalize();
    }

    public Path getTestSuiteFolder() {
        return testSuiteFolder;
    }

    public void setTestSuiteFolder(Path testSuiteFolder) {
        this.testSuiteFolder = testSuiteFolder;
    }

    public String getTestSuiteFolderPropertyValue() {
        return testSuiteFolderPropertyValue;
    }

    public void setTestSuiteFolderPropertyValue(String testSuiteFolderPropertyValue) {
        this.testSuiteFolderPropertyValue = testSuiteFolderPropertyValue;
    }

    public Path getTestSuiteSuiteFile() {
        return testSuiteSuiteFile;
    }

    public void setTestSuiteSuiteFile(Path testSuiteSuiteFile) {
        this.testSuiteSuiteFile = testSuiteSuiteFile;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    public void setCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public boolean isLoadTestCasesAutomatic() {
        return loadTestCasesAutomatic;
    }

    public void setLoadTestCasesAutomatic(boolean loadTestCasesAutomatic) {
        this.loadTestCasesAutomatic = loadTestCasesAutomatic;
    }
}
