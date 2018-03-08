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

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.state.TestCaseState;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 23/02/18.
 */
public class GetOutputDurationFunctionTest {

    @DataProvider
    public Object[][] getOutputDurationDP() {
        return new Object[][] {
                { TestCaseState.ERRORS, new Date(1519370000), new Date(1519377000), "U"},
                { TestCaseState.OK, new Date(1519377570), new Date(1519377000), "U"},
                { TestCaseState.WARNING, new Date(1519360000), new Date(1519377234), "17.23s"},
                { TestCaseState.WARNING, new Date(1519360000), new Date(1519377235), "17.24s"},
        };
    }

    @Test(dataProvider = "getOutputDurationDP")
    public void getOutputDuration(TestCaseState state, Date startDate, Date stopDate, String expectedOutputDuration) {
        TestCase tc = new TestCase("testCase", "testCaseId");
        tc.setStartDate(startDate);
        tc.setStopDate(stopDate);
        tc.setState(state);
        GetOutputDurationFunction getOutputDurationFunction = new GetOutputDurationFunction();
        assertEquals(getOutputDurationFunction.execute(Arrays.asList(tc)), expectedOutputDuration);
    }

}