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

package de.consol.sakuli.builder;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * @author tschneck
 *         Date: 08.05.2014
 */
public class TestSuiteBuilder implements ExampleBuilder<TestSuite> {

    private TestSuiteState state;
    private String host;
    private String id;

    public TestSuite buildExample() {
        state = ((state == null) ? TestSuiteState.RUNNING : state);
        host = ((host == null) ? "localhost" : host);
        id = ((id == null) ? "UnitTest_" + System.nanoTime() : id);

        TestSuite newTestSuite = new TestSuite();
        newTestSuite.setName("Unit Test Sample Test Suite");
        newTestSuite.setId(id);
        newTestSuite.setStartDate(new Date());
        newTestSuite.setStopDate(DateUtils.addMinutes(new Date(), 2));
        newTestSuite.setWarningTime(0);
        newTestSuite.setCriticalTime(0);
        newTestSuite.setState(state);
        newTestSuite.setHost(host);
        newTestSuite.setBrowserInfo("Firefox Test Browser");
        TestCase testCase = new TestCaseBuilder().buildExample();
        newTestSuite.addTestCase(testCase.getId(), testCase);
        return newTestSuite;
    }

    public TestSuiteBuilder withState(TestSuiteState state) {
        this.state = state;
        return this;
    }

    public TestSuiteBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public TestSuiteBuilder withId(String id) {
        this.id = id;
        return this;
    }
}
