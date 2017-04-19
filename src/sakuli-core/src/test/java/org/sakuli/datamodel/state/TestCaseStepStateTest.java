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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.sakuli.datamodel.state.TestCaseStepState.*;
import static org.testng.Assert.*;

public class TestCaseStepStateTest {

    @DataProvider(name = "states")
    public static Object[][] states() {
        return new Object[][]{
                {OK, 0},
                {WARNING, 1},
                {ERRORS, 2},
                {INIT, 3},
        };

    }

    @Test(dataProvider = "states")
    public void testGetNagiosErrorCode(TestCaseStepState state, int expectedErrorCode) throws Exception {
        assertEquals(state.getNagiosErrorCode(), expectedErrorCode);
    }

    @Test
    public void testFinishedWithoutErrors() throws Exception {
        assertTrue(OK.isFinishedWithoutErrors());
        assertTrue(WARNING.isFinishedWithoutErrors());
        assertFalse(INIT.isFinishedWithoutErrors());
        assertFalse(ERRORS.isFinishedWithoutErrors());
    }
}