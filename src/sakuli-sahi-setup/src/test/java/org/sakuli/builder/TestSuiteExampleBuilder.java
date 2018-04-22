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

package org.sakuli.builder;

import org.apache.commons.lang.time.DateUtils;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliCheckedException;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author tschneck
 * Date: 08.05.2014
 */
public class TestSuiteExampleBuilder implements ExampleBuilder<TestSuite> {

    private TestSuiteState state;
    private String host;
    private String id;
    private Date stopDate;
    private Date startDate;
    private SakuliCheckedException exception;
    private List<TestCase> testCases;
    private int warningTime;
    private int criticalTime;
    private Path folder;
    private String browserInfo;


    public TestSuiteExampleBuilder() {
        this.state = TestSuiteState.RUNNING;
        this.host = "localhost";
        this.id = "UnitTest_" + System.nanoTime();
        this.startDate = new GregorianCalendar(2014, 7, 17, 14, 0).getTime();
        this.stopDate = DateUtils.addMinutes(startDate, 2);
        this.testCases = Collections.singletonList(new TestCaseExampleBuilder().buildExample());
        this.warningTime = 0;
        this.criticalTime = 0;
        this.folder = null;
        this.browserInfo = "firefox";
    }

    public TestSuite buildExample() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("Unit Test Sample Test Suite");
        testSuite.setId(id);
        testSuite.setStartDate(startDate);
        testSuite.setStopDate(stopDate);
        testSuite.setWarningTime(warningTime);
        testSuite.setCriticalTime(criticalTime);
        testSuite.setState(state);
        testSuite.setHost(host);
        testSuite.setBrowserInfo("Firefox Test Browser");
        testSuite.addException(exception);
        testSuite.setTestSuiteFolder(folder);
        testSuite.setBrowserInfo(browserInfo);
        for (TestCase testCase : testCases) {
            testSuite.addTestCase(testCase.getId(), testCase);
        }
        return testSuite;
    }

    public TestSuiteExampleBuilder withState(TestSuiteState state) {
        this.state = state;
        return this;
    }

    public TestSuiteExampleBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public TestSuiteExampleBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TestSuiteExampleBuilder withStopDate(Date stopDate) {
        this.stopDate = stopDate;
        return this;
    }

    public TestSuiteExampleBuilder withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public TestSuiteExampleBuilder withException(SakuliCheckedException exception) {
        this.exception = exception;
        return this;
    }

    public TestSuiteExampleBuilder withTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
        return this;
    }

    public TestSuiteExampleBuilder withWarningTime(int warningTime) {
        this.warningTime = warningTime;
        return this;
    }

    public TestSuiteExampleBuilder withCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
        return this;
    }

    public TestSuiteExampleBuilder withTestSuiteFolder(Path folder) {
        this.folder = folder;
        return this;
    }
}
