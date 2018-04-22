/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Request;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
public class Icinga2CheckResultBuilderTest {

    @Mock
    private Icinga2PerformanceDataBuilder performanceDataBuilder;
    @Mock
    private Icinga2OutputBuilder outputBuilder;
    @Mock
    private TestSuite testSuite;
    @InjectMocks
    private Icinga2CheckResultBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuild() throws Exception {
        when(testSuite.getState()).thenReturn(TestSuiteState.OK);
        String perfData = "sample_performance_data";
        when(performanceDataBuilder.build()).thenReturn(Collections.singletonList(perfData));
        String output = "sample_output";
        when(outputBuilder.build()).thenReturn(output);

        Icinga2Request result = testling.build();
        assertEquals(result.getCheck_source(), "check_sakuli");
        assertEquals(result.getExit_status(), 0);
        assertEquals(result.getPerformance_data().size(), 1);
        assertEquals(result.getPerformance_data().iterator().next(), perfData);
        assertEquals(result.getPlugin_output(), output);
    }
}