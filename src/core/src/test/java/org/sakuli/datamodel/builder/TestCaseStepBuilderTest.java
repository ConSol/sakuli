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

package org.sakuli.datamodel.builder;

import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 8/21/15
 */
public class TestCaseStepBuilderTest {

    @Test
    public void testBuild() throws Exception {
        TestCaseStep step = new TestCaseStepBuilder("step for unit test").build();
        assertEquals(step.getId(), "step_for_unit_test");
        assertEquals(step.getName(), "step_for_unit_test");
        assertEquals(step.getState(), TestCaseStepState.INIT);
    }
}