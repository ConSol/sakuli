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

import static org.sakuli.datamodel.state.TestCaseState.*;
import static org.testng.Assert.assertEquals;

public class TestCaseStateTest {

    @DataProvider(name = "states")
    public static Object[][] states() {
        return new Object[][]{
                {OK, 0},
                {WARNING, 1},
                {WARNING_IN_STEP, 1},
                {CRITICAL, 2},
                {ERRORS, 2},
                {INIT, 3},
        };

    }

    @DataProvider(name = "statesNoErrors")
    public static Object[][] statesNoErrors() {
        return new Object[][]{
                {OK, true},
                {WARNING, true},
                {WARNING_IN_STEP, true},
                {CRITICAL, true},
                {ERRORS, false},
                {INIT, false},
        };

    }

    @Test(dataProvider = "states")
    public void testGetNagiosErrorCode(TestCaseState state, int expectedErrorCode) throws Exception {
        assertEquals(state.getNagiosErrorCode(), expectedErrorCode);
    }

    @Test(dataProvider = "statesNoErrors")
    public void testFinishedWithouErrors(TestCaseState state, boolean noError) throws Exception {
        Assert.assertEquals(state.isFinishedWithoutErrors(), noError);
    }
}