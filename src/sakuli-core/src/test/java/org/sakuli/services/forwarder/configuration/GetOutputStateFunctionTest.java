/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.services.forwarder.configuration;

import org.sakuli.datamodel.state.SakuliState;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.services.forwarder.OutputState;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 23/02/18.
 */
public class GetOutputStateFunctionTest {

    @DataProvider
    public Object[][] getOutputStateDP() {
        return new Object[][] {
                { null, OutputState.UNKNOWN},
                { TestCaseStepState.OK, OutputState.OK},
                { TestCaseState.OK, OutputState.OK},
                { TestSuiteState.OK, OutputState.OK},
                { TestCaseStepState.WARNING, OutputState.WARNING},
                { TestCaseState.WARNING, OutputState.WARNING},
                { TestCaseState.WARNING_IN_STEP, OutputState.WARNING},
                { TestSuiteState.WARNING_IN_CASE, OutputState.WARNING},
                { TestSuiteState.WARNING_IN_STEP, OutputState.WARNING},
                { TestSuiteState.WARNING_IN_SUITE, OutputState.WARNING},
                { TestCaseStepState.ERRORS, OutputState.CRITICAL},
                { TestCaseState.CRITICAL, OutputState.CRITICAL},
                { TestCaseState.ERRORS, OutputState.CRITICAL},
                { TestSuiteState.CRITICAL_IN_CASE, OutputState.CRITICAL},
                { TestSuiteState.CRITICAL_IN_SUITE, OutputState.CRITICAL},
                { TestSuiteState.ERRORS, OutputState.CRITICAL},
        };
    }

    @Test(dataProvider = "getOutputStateDP")
    public void getOutputState(SakuliState state, OutputState expectedOutputState) {
        GetOutputStateFunction getOutputStateFunction = new GetOutputStateFunction();
        assertEquals(getOutputStateFunction.execute(Arrays.asList(state)), expectedOutputState);
    }

}