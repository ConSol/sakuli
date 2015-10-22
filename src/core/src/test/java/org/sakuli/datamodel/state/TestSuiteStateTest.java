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

package org.sakuli.datamodel.state;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.sakuli.datamodel.state.TestSuiteState.*;

public class TestSuiteStateTest {

    @DataProvider(name = "states")
    public static Object[][] states() {
        return new Object[][]{
                {RUNNING, 3},
                {OK, 0},
                {WARNING_IN_SUITE, 1},
                {WARNING_IN_CASE, 1},
                {WARNING_IN_STEP, 1},
                {CRITICAL_IN_CASE, 2},
                {CRITICAL_IN_SUITE, 2},
                {ERRORS, 2},
        };
    }

    @DataProvider(name = "statesNoError")
    public static Object[][] statesNoError() {
        return new Object[][]{
                {RUNNING, false},
                {OK, true},
                {WARNING_IN_SUITE, true},
                {WARNING_IN_CASE, true},
                {WARNING_IN_STEP, true},
                {CRITICAL_IN_CASE, true},
                {CRITICAL_IN_SUITE, true},
                {ERRORS, false},
        };
    }

    @Test(dataProvider = "states")
    public void testGetNagiosErrorCode(TestSuiteState state, int expectedNagiosCode) throws Exception {
        Assert.assertEquals(state.getNagiosErrorCode(), expectedNagiosCode);
    }

    @Test(dataProvider = "statesNoError")
    public void testFinishedWithouErrors(TestSuiteState state, boolean noError) throws Exception {
        Assert.assertEquals(state.isFinishedWithoutErrors(), noError);
    }
}