/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.datamodel.builder;

import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.state.TestCaseStepState;

/**
 * @author Tobias Schneck
 */
public class TestCaseStepBuilder implements Builder<TestCaseStep> {
    private final String name;
    private TestCaseStepState stepState;

    public TestCaseStepBuilder(String name) {
        this.name = name;
    }


    @Override
    public TestCaseStep build() {
        TestCaseStep newTestCase = new TestCaseStep();
        newTestCase.setName(name);
        newTestCase.setState(stepState != null ? stepState : TestCaseStepState.INIT);
        return newTestCase;
    }

    public TestCaseStepBuilder withState(TestCaseStepState stepState) {
        this.stepState = stepState;
        return this;
    }
}
