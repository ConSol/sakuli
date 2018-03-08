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

package org.sakuli.services.forwarder.icinga2;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Request;
import org.sakuli.services.forwarder.icinga2.model.builder.Icinga2CheckResultBuilder;
import org.sakuli.services.forwarder.icinga2.model.builder.Icinga2RequestBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 2/22/16
 */
public class ManualIcinga2ResultTest {

    @Mock
    private Icinga2Properties properties;
    @InjectMocks
    private Icinga2ResultServiceImpl testling;
    @InjectMocks
    private Icinga2RestCient icinga2RestCient;
    @Mock
    private Icinga2CheckResultBuilder icinga2CheckResultBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(icinga2CheckResultBuilder.build()).thenReturn(getRequestExample());
    }

    @Test(enabled = false)
    public void testSaveAllResults() throws Exception {
        when(properties.getApiUsername()).thenReturn("root");
        when(properties.getApiPassword()).thenReturn("rootroot");
        when(properties.getApiURL()).thenReturn("https://localhost:5665/v1/actions/process-check-result?service=sakuliclient01!sakuli_demo");
        ReflectionTestUtils.setField(testling, "icinga2RestCient", icinga2RestCient);
        testling.saveAllResults(new TestSuite());
    }

    protected Icinga2Request getRequestExample() {
        return new Icinga2RequestBuilder()
                .withCheckSource("check_sakuli")
                .withExitStatus(0)
                .addPerformanceData("suite__state=0;;;;")
                .addPerformanceData("suite__warning=50s;;;;")
                .addPerformanceData("suite__critical=60s;;;;")
                .addPerformanceData("suite_example_ubuntu_0=49.31s;50;60;;")
                .addPerformanceData("c_001__state=0;;;;")
                .addPerformanceData("c_001__warning=40s;;;;")
                .addPerformanceData("c_001__critical=50s;;;;")
                .addPerformanceData("c_001_case1=25.22s;40;50;;")
                .addPerformanceData("s_001_001_Test_Sahi_landing_page=1.96s;15;;;")
                .addPerformanceData("s_001_002_Calculation=8.96s;30;;;")
                .addPerformanceData("s_001_003_Editor=8.38s;30;;;")
                .withPluginOutput("Sakuli suite 'example_ubuntu_0' (ID: 25100) ran in 49.31 seconds.")
                .build();
    }
}