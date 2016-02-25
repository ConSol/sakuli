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

package org.sakuli.services.forwarder.icinga2.model.builder;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.sakuli.BaseTest.assertRegExMatch;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author tschneck
 *         Date: 8/26/15
 */
public class Icinga2PerformanceDataBuilderTest {

    @Spy
    @InjectMocks
    private Icinga2PerformanceDataBuilder testling;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuild() throws Exception {
        ReflectionTestUtils.setField(testling, "testSuite", new TestSuiteExampleBuilder().buildExample());
        Iterator<String> it = testling.build().iterator();
        assertEquals(it.next(), "suite__state=3;;;;");
        assertRegExMatch(it.next(), "suite_UnitTest_\\d*=U;;;;");
        assertEquals(it.next(), "c_001__state=0;;;;");
        assertEquals(it.next(), "c_001__warning=4s;;;;");
        assertEquals(it.next(), "c_001__critical=5s;;;;");
        assertRegExMatch(it.next(), "c_001_UNIT_TEST_CASE_\\d*=3.00s;4;5;;");
        assertEquals(it.next(), "s_001_001_step_for_unit_test=1.00s;2;;;");
        assertFalse(it.hasNext());
    }

}