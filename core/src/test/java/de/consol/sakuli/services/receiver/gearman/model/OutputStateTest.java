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

package de.consol.sakuli.services.receiver.gearman.model;

import de.consol.sakuli.datamodel.state.SakuliState;
import de.consol.sakuli.datamodel.state.TestCaseState;
import de.consol.sakuli.datamodel.state.TestCaseStepState;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class OutputStateTest {

    @DataProvider(name = "states")
    public static Object[][] states() {
        return new Object[][]{
                {null, OutputState.UNKNOWN},
                {TestSuiteState.RUNNING, OutputState.UNKNOWN},
                {TestSuiteState.OK, OutputState.OK},
                {TestSuiteState.WARNING_IN_SUITE, OutputState.WARNING},
                {TestSuiteState.WARNING_IN_STEP, OutputState.WARNING},
                {TestSuiteState.WARNING_IN_CASE, OutputState.WARNING},
                {TestSuiteState.CRITICAL_IN_SUITE, OutputState.CRITICAL},
                {TestSuiteState.CRITICAL_IN_CASE, OutputState.CRITICAL},
                {TestSuiteState.ERRORS, OutputState.CRITICAL},
                {TestCaseState.OK, OutputState.OK},
                {TestCaseState.WARNING, OutputState.WARNING},
                {TestCaseState.WARNING_IN_STEP, OutputState.WARNING},
                {TestCaseState.CRITICAL, OutputState.CRITICAL},
                {TestCaseState.ERRORS, OutputState.CRITICAL},
                {TestCaseStepState.OK, OutputState.OK},
                {TestCaseStepState.WARNING, OutputState.WARNING},
        };

    }

    @Test(dataProvider = "states")
    public void testLookupSakuliState(SakuliState state, OutputState expectedState) throws Exception {
        assertEquals(OutputState.lookupSakuliState(state), expectedState);
    }
}