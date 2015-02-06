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

package org.sakuli.integration.builder;

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.state.TestCaseState;

import java.util.Date;

/**
 * @author tschneck
 *         Date: 08.05.2014
 */
public class TestCaseBuilder {


    public static TestCase createEmptyTestCase() {
        return createEmptyTestCase("Integration Test Case", "IT_TEST_CASE_" + System.nanoTime());
    }

    public static TestCase createEmptyTestCase(String name, String id) {
        TestCase newTestCase = new TestCase(name, id);
        newTestCase.setState(TestCaseState.OK);
        newTestCase.setStartDate(new Date());
        newTestCase.setStopDate(new Date());
        return newTestCase;
    }
}
