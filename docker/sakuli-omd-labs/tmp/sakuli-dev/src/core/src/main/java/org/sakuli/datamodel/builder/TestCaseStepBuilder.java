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
