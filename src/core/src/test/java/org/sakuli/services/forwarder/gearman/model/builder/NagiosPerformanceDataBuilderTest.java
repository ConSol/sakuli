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

package org.sakuli.services.forwarder.gearman.model.builder;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.GearmanPropertiesTestHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author tschneck
 *         Date: 8/26/15
 */
public class NagiosPerformanceDataBuilderTest {

    @Mock
    private GearmanProperties gearmanProperties;
    @Spy
    @InjectMocks
    private NagiosPerformanceDataBuilder testling;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuild() throws Exception {
        GearmanPropertiesTestHelper.initMock(gearmanProperties);
        ReflectionTestUtils.setField(testling, "testSuite", new TestSuiteExampleBuilder().buildExample());
        BaseTest.assertRegExMatch(testling.build(), "suite__state=\\d;;;; suite_UnitTest.*; \\[check_sakuli\\]");
    }

}