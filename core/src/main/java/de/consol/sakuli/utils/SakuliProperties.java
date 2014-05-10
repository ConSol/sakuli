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

package de.consol.sakuli.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck
 *         Date: 09.05.14
 */
@Component
public class SakuliProperties {

    // TODO TS go on here and centralize all properties, set TEST_SUITE_FOLDER and INCLUDE_FOLDER over system Properies or before Context runs up.
    public static final String TEST_SUITE_FOLDER = "sakuli.test.suite.folder";
    public static final String INCLUDE_FOLDER = "sakuli.include.folder";
    public static final String LOG_FOLDER = "sakuli.log.folder";
    public static final String LOG_PATTERN = "sakuli.log.pattern";

    @Value("${" + TEST_SUITE_FOLDER + "}")
    private String testSuiteFolderPropertyValue;
    private Path testSuiteFolder;
    @Value("${" + INCLUDE_FOLDER + "}")
    private String includeFolderPropertyValue;
    private Path includeFolder;
    @Value("${" + LOG_FOLDER + "}")
    private String logFolderPropertyValue;
    private Path logFolder;

    @Value("${" + LOG_PATTERN + "}")
    private String logPattern;

    @PostConstruct
    public void initFolders() {
        testSuiteFolder = Paths.get(testSuiteFolderPropertyValue);
        includeFolder = Paths.get(includeFolderPropertyValue);
        logFolder = Paths.get(logFolderPropertyValue);
    }

    public String getTestSuiteFolderPropertyValue() {
        return testSuiteFolderPropertyValue;
    }

    public void setTestSuiteFolderPropertyValue(String testSuiteFolderPropertyValue) {
        this.testSuiteFolderPropertyValue = testSuiteFolderPropertyValue;
    }

    public Path getTestSuiteFolder() {
        return testSuiteFolder;
    }

    public void setTestSuiteFolder(Path testSuiteFolder) {
        this.testSuiteFolder = testSuiteFolder;
    }

    public String getIncludeFolderPropertyValue() {
        return includeFolderPropertyValue;
    }

    public void setIncludeFolderPropertyValue(String includeFolderPropertyValue) {
        this.includeFolderPropertyValue = includeFolderPropertyValue;
    }

    public Path getIncludeFolder() {
        return includeFolder;
    }

    public void setIncludeFolder(Path includeFolder) {
        this.includeFolder = includeFolder;
    }

    public String getLogFolderPropertyValue() {
        return logFolderPropertyValue;
    }

    public void setLogFolderPropertyValue(String logFolderPropertyValue) {
        this.logFolderPropertyValue = logFolderPropertyValue;
    }

    public Path getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(Path logFolder) {
        this.logFolder = logFolder;
    }

    public String getLogPattern() {
        return logPattern;
    }

    public void setLogPattern(String logPattern) {
        this.logPattern = logPattern;
    }
}
