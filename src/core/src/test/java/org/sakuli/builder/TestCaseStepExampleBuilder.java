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
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.state.TestCaseStepState;

import java.util.Date;

/**
 * @author tschneck
 *         Date: 14.07.14
 */
public class TestCaseStepExampleBuilder implements ExampleBuilder<TestCaseStep> {
    private TestCaseStepState state;
    private String name;
    private int warningTime;
    private Date stopDate;
    private Date startDate;

    public TestCaseStepExampleBuilder() {
        this.state = TestCaseStepState.OK;
        this.name = "step for unit test";
        this.warningTime = 2;
        this.startDate = new Date();
        this.stopDate = DateUtils.addSeconds(startDate, 1);
    }

    @Override
    public TestCaseStep buildExample() {
        TestCaseStep step = new TestCaseStepBuilder(name).build();
        step.setStartDate(startDate);
        step.setStopDate(stopDate);
        step.setWarningTime(warningTime);
        step.setName(name);
        step.setState(state);
        return step;
    }

    public TestCaseStepExampleBuilder withState(TestCaseStepState state) {
        this.state = state;
        return this;
    }

    public TestCaseStepExampleBuilder withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public TestCaseStepExampleBuilder withStopDate(Date stopDate) {
        this.stopDate = stopDate;
        return this;
    }

    public TestCaseStepExampleBuilder withWarningTime(int warningTime) {
        this.warningTime = warningTime;
        return this;
    }

    public TestCaseStepExampleBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
