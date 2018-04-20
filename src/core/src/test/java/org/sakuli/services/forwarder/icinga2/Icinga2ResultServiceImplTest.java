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
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Request;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Result;
import org.sakuli.services.forwarder.icinga2.model.builder.Icinga2CheckResultBuilder;
import org.sakuli.services.forwarder.icinga2.model.builder.Icinga2RequestBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author tschneck
 * Date: 2/23/16
 */
public class Icinga2ResultServiceImplTest {
    @Mock
    private Icinga2Properties properties;
    @Mock
    private Icinga2RestCient icinga2RestCient;
    @Mock
    private WebTarget webTarget;
    @Mock
    private Invocation.Builder invocationBuilder;
    @Mock
    private Response response;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private Icinga2CheckResultBuilder icinga2CheckResultBuilder;
    @InjectMocks
    private Icinga2ResultServiceImpl testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveAllResults() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        map.put("code", "200.00");
        map.put("status", "Successfully processed");
        result.setResults(Collections.singletonList(map));
        mockAndReturn(result);
        when(icinga2CheckResultBuilder.build()).thenReturn(getRequestExample());
        testling.teardownTestSuite(new TestSuite());
        verify(exceptionHandler, never()).handleException(any(Exception.class));
        verify(icinga2CheckResultBuilder).build();
    }

    @Test(expectedExceptions = SakuliForwarderException.class,
            expectedExceptionsMessageRegExp = "Unexpected result of REST-POST.*")
    public void testSaveAllResultsWrongCode() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        map.put("code", "20.00");
        map.put("status", "Successfully processed");
        result.setResults(Collections.singletonList(map));
        mockAndReturn(result);
        when(icinga2CheckResultBuilder.build()).thenReturn(getRequestExample());
        testling.teardownTestSuite(new TestSuite());
    }

    @Test(expectedExceptions = SakuliForwarderException.class,
            expectedExceptionsMessageRegExp = "Unexpected result of REST-POST.*")
    public void testSaveAllResultsWrongStatus() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        map.put("code", "200.00");
        map.put("status", "Unsuccessfully processed");
        result.setResults(Collections.singletonList(map));
        when(icinga2CheckResultBuilder.build()).thenReturn(getRequestExample());
        mockAndReturn(result);
        testling.teardownTestSuite(new TestSuite());
    }

    @Test
    public void testPrintJson() throws Exception {
        String json = Icinga2ResultServiceImpl.convertToJSON(Entity.json(getRequestExample()));
        Assert.assertEquals(json, String.format("{%n" +
                "  \"check_source\" : \"check_sakuli\",%n" +
                "  \"exit_status\" : 0,%n" +
                "  \"performance_data\" : [ \"suite__state=0;;;;\", \"suite__warning=50s;;;;\", \"suite__critical=60s;;;;\", \"suite_example_ubuntu_0=49.31s;50;60;;\", \"c_001__state=0;;;;\", \"c_001__warning=40s;;;;\", \"c_001__critical=50s;;;;\", \"c_001_case1=25.22s;40;50;;\", \"s_001_001_Test_Sahi_landing_page=1.96s;15;;;\", \"s_001_002_Calculation=8.96s;30;;;\", \"s_001_003_Editor=8.38s;30;;;\" ],%n" +
                "  \"plugin_output\" : \"Sakuli suite 'example_ubuntu_0' (ID: 25100) ran in 49.31 seconds.\"%n" +
                "}"
        ));

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

    private void mockAndReturn(Icinga2Result result) {
        when(icinga2RestCient.getTargetCheckResult()).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(invocationBuilder);
        when(webTarget.getUri()).thenReturn(URI.create(""));
        when(invocationBuilder.post(any(Entity.class))).thenReturn(response);
        when(response.readEntity(Icinga2Result.class)).thenReturn(result);
    }
}