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

package org.sakuli;

/**
 * Property Class to access the property files inside of the Junit-Tests
 *
 * @author tschneck
 *         Date: 24.06.13
 */
public class PropertyHolder {

    private String logFile;
    private String testSuiteFolder;
    private String screenShotFolder;

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getTestSuiteFolder() {
        return testSuiteFolder;
    }

    public void setTestSuiteFolder(String testSuiteFolder) {
        this.testSuiteFolder = testSuiteFolder;
    }

    public String getScreenShotFolder() {
        return screenShotFolder;
    }

    public void setScreenShotFolder(String screenShotFolder) {
        this.screenShotFolder = screenShotFolder;
    }

}
