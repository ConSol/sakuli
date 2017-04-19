/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.datamodel.builder;

import org.joda.time.DateTime;
import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.state.TestCaseStepState;

/**
 * @author Tobias Schneck
 */
public class TestCaseStepBuilder implements Builder<TestCaseStep> {
    private final String name;
    private TestCaseStepState stepState;
    private DateTime creationDate;

    public TestCaseStepBuilder(String name) {
        this.name = name;
        creationDate = new DateTime();
    }


    @Override
    public TestCaseStep build() {
        TestCaseStep newTestCase = new TestCaseStep();
        newTestCase.setName(name);
        newTestCase.setState(stepState != null ? stepState : TestCaseStepState.INIT);
        newTestCase.setCreationDate(creationDate);
        return newTestCase;
    }

    public TestCaseStepBuilder withState(TestCaseStepState stepState) {
        this.stepState = stepState;
        return this;
    }

    public TestCaseStepBuilder withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}
