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

import de.consol.sakuli.datamodel.state.TestCaseStepState;
import de.consol.sakuli.exceptions.SakuliException;

import java.util.Date;

/**
 * test case step based Exceptions and critical times will be currently not supported in {@link de.consol.sakuli.actions.TestCaseActions}.
 *
 * @author tschneck
 *         Date: 18.06.13
 */
// TODO check if component scan with scope prototyp is possible
public class TestCaseStep extends AbstractSakuliTest<SakuliException, TestCaseStepState> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshState() {
        //if a step exceed the runtime set WARNING
        if (warningTime > 0 && getDuration() > warningTime) {
            state = TestCaseStepState.WARNING;
        } else {
            state = TestCaseStepState.OK;
        }
    }

    @Override
    public String getResultString() {
        return "\n\t\t======== test case step \"" + this.getName() + "\" ended with " + getState() + " ================="
                + super.getResultString().replace("\n", "\n\t\t");
    }

    public void setDbPrimaryKey(int dbPrimaryKey) {
        this.dbPrimaryKey = dbPrimaryKey;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
