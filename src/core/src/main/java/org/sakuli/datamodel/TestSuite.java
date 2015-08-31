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

package org.sakuli.datamodel;

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * @author tschneck Date: 10.06.13
 */
@Component
public class TestSuite extends AbstractTestDataEntity<SakuliException, TestSuiteState> {

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
        if (exception != null) {
            state = TestSuiteState.ERRORS;
        } else if (testCases == null) {
            state = TestSuiteState.RUNNING;
        } else {
            for (TestCase tc : testCases.values()) {
                tc.refreshState();

                //if errors are found suite state is always error!
                if (tc.getState() == null) {
                    tc.addException(new SakuliException("ERROR: NO RESULT STATE SET"));
                    state = TestSuiteState.ERRORS;
                } else if (tc.getState().equals(TestCaseState.ERRORS)) {
                    state = TestSuiteState.ERRORS;
                }
                //if at least on test case has not finished, test suite is still running!
                else if (tc.getStopDate() == null) {
                    state = TestSuiteState.RUNNING;
                    return;
                }
                //now check if thresholds didn't exceed
                else if (tc.getState().equals(TestCaseState.CRITICAL)
                        && state.getErrorCode() < TestSuiteState.CRITICAL_IN_CASE.getErrorCode()) {
                    state = TestSuiteState.CRITICAL_IN_CASE;
                } else if (tc.getState().equals(TestCaseState.WARNING)
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_CASE.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_CASE;
                } else if (tc.getState().equals(TestCaseState.WARNING_IN_STEP)
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_STEP.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_STEP;
                }
                //all thresholds ok, no errors, case finished
                else if (tc.getState().equals(TestCaseState.OK)
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
        String stout = "\n=========== RESULT of SAKULI Testsuite \"" + getId() + "\" - " + getState() + " ================="
                + "\ntest suite id: " + this.getId()
                + "\nguid: " + this.getGuid()
                + super.getResultString()
                + "\ndb primary key of job table: " + this.getDbJobPrimaryKey()
                + "\nbrowser: " + this.getBrowserInfo();
        if (!CollectionUtils.isEmpty(testCases)) {
            for (TestCase tc : getTestCasesAsSortedSet()) {
                stout += tc.getResultString();
            }
        }
        return stout;
    }

    @Override
    public String getExceptionMessages(boolean flatFormatted) {
        String suiteErrorMessage = super.getExceptionMessages(flatFormatted);
        if (suiteErrorMessage == null) {
            suiteErrorMessage = "";
        }
        for (TestCase testCase : getTestCasesAsSortedSet()) {
            final String tcErrorMessage = testCase.getExceptionMessages(flatFormatted);

            if (isNotBlank(tcErrorMessage)) {
                if (isNotBlank(suiteErrorMessage)) {
                    suiteErrorMessage += flatFormatted ? " -- " : "\n";
                }
                suiteErrorMessage += "CASE '" + testCase.getId() + "': " + tcErrorMessage;
            }
        }
        return suiteErrorMessage;
    }

    public String getAbsolutePathOfTestSuiteFile() {
        return testSuiteFile == null ? null : testSuiteFile.toAbsolutePath().toString();
    }

    public Path getTestSuiteFolder() {
        return testSuiteFolder;
    }

    public void setTestSuiteFolder(Path testSuiteFolder) {
        this.testSuiteFolder = testSuiteFolder;
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
        Date guidDate = startDate != null ? startDate : new Date();
        return id + "__" + GUID_DATE_FORMATE.format(guidDate);
    }

    public void addTestCase(String testCaseId, TestCase testCase) {
        if (this.testCases == null) {
            this.testCases = new HashMap<>();
        }
        this.testCases.put(testCaseId, testCase);
    }

    public void addTestCase(TestCase testCase) {
        this.addTestCase(testCase.getId(), testCase);
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

    /**
     * @return all {@link TestCase}s as {@link SortedSet} or a empty set if no test cases are specified.
     */
    public SortedSet<TestCase> getTestCasesAsSortedSet() {
        if (!CollectionUtils.isEmpty(testCases)) {
            return new TreeSet<>(testCases.values());
        }
        return new TreeSet<>();
    }

}
