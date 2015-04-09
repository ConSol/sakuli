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
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.exceptions.SakuliException;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author tschneck
 *         Date: 08.05.2014
 */
public class TestCaseExampleBuilder implements ExampleBuilder<TestCase> {


    private List<TestCaseStep> steps;
    private String name;
    private String id;
    private TestCaseState state;
    private Date startDate;
    private Date stopDate;
    private int warningTime;
    private int criticalTime;
    private SakuliException exception;
    private Path testCaseFile;
    private String lastURL;
    private String startURL;


    public TestCaseExampleBuilder() {
        name = "Unit Test Case";
        id = "UNIT_TEST_CASE_" + System.nanoTime();
        state = TestCaseState.OK;
        startDate = new Date();
        stopDate = DateUtils.addSeconds(startDate, 3);
        warningTime = 4;
        criticalTime = 5;
        startURL = "http://www.start-url.com";
        lastURL = "http://www.last-url.com";
        steps = Arrays.asList(new TestCaseStepExampleBuilder().buildExample());
    }

    @Override
    public TestCase buildExample() {
        TestCase testCase = new TestCase(name, id);
        testCase.setState(state);
        testCase.setStartDate(startDate);
        testCase.setStopDate(stopDate);
        testCase.setWarningTime(warningTime);
        testCase.setCriticalTime(criticalTime);
        testCase.addException(exception);
        testCase.setTcFile(testCaseFile);
        testCase.setStartUrl(startURL);
        testCase.setLastURL(lastURL);
        testCase.setSteps(steps);
        return testCase;
    }

    public TestCaseExampleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TestCaseExampleBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TestCaseExampleBuilder withState(TestCaseState testCaseState) {
        this.state = testCaseState;
        return this;
    }


    public TestCaseExampleBuilder withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public TestCaseExampleBuilder withStopDate(Date stopDate) {
        this.stopDate = stopDate;
        return this;
    }

    public TestCaseExampleBuilder withWarningTime(int warningTime) {
        this.warningTime = warningTime;
        return this;
    }

    public TestCaseExampleBuilder withTestCaseSteps(List<TestCaseStep> testCaseSteps) {
        this.steps = testCaseSteps;
        return this;
    }

    public TestCaseExampleBuilder withCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
        return this;
    }

    public TestCaseExampleBuilder withException(SakuliException exception) {
        this.exception = exception;
        return this;
    }

    public TestCaseExampleBuilder withTestCaseFile(Path testCaseFile) {
        this.testCaseFile = testCaseFile;
        return this;
    }
}
