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

package de.consol.sakuli.datamodel;

import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import de.consol.sakuli.datamodel.state.TestCaseState;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.exceptions.SakuliException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tschneck Date: 10.06.13
 */
@Component
public class TestSuite extends AbstractSakuliTest<SakuliException, TestSuiteState> {

    private String id;
    //browser name where to start the test execution
    private String browserName;
    //additional browser infos from sahi proxy
    private String browserInfo;
    private String host;
    private Path testSuiteFolder;
    private Path testSuiteFile;
    private int dbJobPrimaryKey = -1;
    private Map<String, TestCase> testCases;

    public TestSuite() {
    }

    @Autowired
    public TestSuite(TestSuiteProperties properties) {
        id = properties.getTestSuiteId();
        name = properties.getTestSuiteName();
        //if name is not set, use the id
        if (StringUtils.isEmpty(name)) {
            name = id;
        }
        warningTime = properties.getWarningTime();
        criticalTime = properties.getCriticalTime();
        testSuiteFolder = properties.getTestSuiteFolder();
        testSuiteFile = properties.getTestSuiteSuiteFile();
        browserName = properties.getBrowserName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshState() {
        if (testCases == null) {
            addException(new SakuliException("NO TEST CASES FOUND !!!"));
            state = TestSuiteState.ERRORS;
        } else if (exception != null) {
            state = TestSuiteState.ERRORS;
        } else {
            for (TestCase tc : testCases.values()) {

                tc.refreshState();
                if (tc.getState() == null) {
                    tc.addException(new SakuliException("ERROR: NO RESULT STATE SET"));
                    state = TestSuiteState.ERRORS;
                } else if (tc.getState().equals(TestCaseState.ERRORS)) {
                    state = TestSuiteState.ERRORS;
                } else if (tc.getState().equals(TestCaseState.CRITICAL)
                        && state.getErrorCode() < TestSuiteState.CRITICAL_IN_CASE.getErrorCode()) {
                    state = TestSuiteState.CRITICAL_IN_CASE;
                } else if (tc.getState().equals(TestCaseState.WARNING)
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_CASE.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_CASE;
                } else if (tc.getState().equals(TestCaseState.WARNING_IN_STEP)
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_STEP.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_STEP;
                } else if (tc.getState().equals(TestCaseState.OK)
                        && state.getErrorCode() < TestSuiteState.OK.getErrorCode()) {
                    state = TestSuiteState.OK;
                }
            }
            if (state.getErrorCode() < TestSuiteState.ERRORS.getErrorCode()) {
                if (criticalTime > 0
                        && getDuration() > criticalTime
                        && state.getErrorCode() < TestSuiteState.CRITICAL_IN_SUITE.getErrorCode()) {
                    state = TestSuiteState.CRITICAL_IN_SUITE;
                } else if (warningTime > 0
                        && getDuration() > warningTime
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_SUITE.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_SUITE;
                }
            }
        }
    }

    @Override
    public String getResultString() {
        String stout = "\n=========== test suite \"" + getId() + "\" ended with " + getState() + " ================="
                + "\ntest suite id: " + this.getId()
                + "\nguid: " + this.getGuid()
                + super.getResultString()
                + "\ndb primary key of job table: " + this.getDbJobPrimaryKey()
                + "\nbrowser: " + this.getBrowserInfo();
        if (testCases != null && !testCases.isEmpty()) {
            for (TestCase tc : testCases.values()) {
                stout += tc.getResultString();
            }
        }
        return stout;
    }

    @Override
    public String getExceptionMessages() {
        String errorMessages = super.getExceptionMessages();
        if (errorMessages == null) {
            errorMessages = "";
        }
        for (TestCase testCase : getTestCases().values()) {
            if (testCase.getExceptionMessages() != null) {
                errorMessages += "\n" + testCase.getExceptionMessages();
            }
        }
        return errorMessages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbsolutePathOfTestSuiteFile() {
        return testSuiteFile == null ? null : testSuiteFile.toAbsolutePath().toString();
    }

    public Path getTestSuiteFolder() {
        return testSuiteFolder;
    }

    public String getBrowserName() {
        return browserName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(Map<String, TestCase> testCases) {
        this.testCases = testCases;
    }

    public int getDbJobPrimaryKey() {
        return dbJobPrimaryKey;
    }

    public void setDbJobPrimaryKey(int dbJobPrimaryKey) {
        this.dbJobPrimaryKey = dbJobPrimaryKey;
    }

    public String getBrowserInfo() {
        if (browserInfo == null || browserInfo.isEmpty()) {
            return browserName;
        }
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    /**
     * @return a unique identifier for each execution of the test suite
     */
    public String getGuid() {
        return id + "__" + GUID_DATE_FORMATE.format(startDate);
    }

    public void addTestCase(String testCaseId, TestCase testCase) {
        if (this.testCases == null) {
            this.testCases = new HashMap<>();
        }
        this.testCases.put(testCaseId, testCase);
    }

    public TestCase getTestCase(String testCaseId) {
        if (this.getTestCases().containsKey(testCaseId)) {
            return this.getTestCases().get(testCaseId);
        }
        return null;
    }

    /**
     * checks if the test case id is valid
     *
     * @return true if valid
     */
    public boolean checkTestCaseID(String testCaseID) {
        return testCaseID != null && this.getTestCases() != null && this.getTestCases().containsKey(testCaseID);
    }

    public TestCase getTestCaseByDBKey(int primaryKeyOfTestCase) {
        for (TestCase testCase : testCases.values()) {
            if (testCase.getDbPrimaryKey() == primaryKeyOfTestCase) {
                return testCase;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TestSuite{" +
                super.toString() +
                ", id='" + id + '\'' +
                ", browserName='" + browserName + '\'' +
                ", browserInfo='" + browserInfo + '\'' +
                ", host='" + host + '\'' +
                ", testSuiteFolder=" + testSuiteFolder +
                ", testSuiteFile=" + testSuiteFile +
                ", dbJobPrimaryKey=" + dbJobPrimaryKey +
                ", testCases=" + testCases +
                '}';
    }
}
