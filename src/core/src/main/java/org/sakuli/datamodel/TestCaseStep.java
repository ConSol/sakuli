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
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.exceptions.SakuliException;

/**
 * test case step based Exceptions and critical times will be currently not supported in {@link
 * org.sakuli.actions.TestCaseAction}.
 *
 * @author tschneck Date: 18.06.13
 */
public class TestCaseStep extends AbstractTestDataEntity<SakuliException, TestCaseStepState> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshState() {
        //if a error set error
        if (exception != null) {
            state = TestCaseStepState.ERRORS;
            return;
        }
        //if a step exceed the runtime set WARNING
        TestCaseStepState newState;
        if (warningTime > 0 && getDuration() > warningTime) {
            newState = TestCaseStepState.WARNING;
        } else if (startDate != null && stopDate != null) {
            newState = TestCaseStepState.OK;
        } else {
            newState = TestCaseStepState.INIT;
        }
        if (state == null || newState.getErrorCode() > state.getErrorCode()) {
            state = newState;
        }
    }

    @Override
    public String getResultString() {
        return "\n\t\t======== test case step \"" + this.getName() + "\" ended with " + getState() + " ================="
                + super.getResultString().replace("\n", "\n\t\t");
    }

    @Override
    public String toString() {
        return "TestCaseStep{"
                + super.toString()
                + "}";
    }

    /**
     * Currently the name and the id of a test case step is same value
     */
    @Override
    public void setName(String name) {
        name = StringUtils.replace(name, " ", "_");
        super.setName(name);
        super.setId(name);
    }

    /**
     * Currently the name and the id of a test case step is same value
     */
    @Override
    public void setId(String id) {
        this.setName(id);
    }
}
