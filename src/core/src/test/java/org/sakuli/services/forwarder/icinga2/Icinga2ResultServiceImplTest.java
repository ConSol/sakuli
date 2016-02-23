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
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Result;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author tschneck
 *         Date: 2/23/16
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
        testling.saveAllResults();
        verify(exceptionHandler, never()).handleException(any(Exception.class));
    }

    @Test
    public void testSaveAllResultsWrongCode() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        map.put("code", "20.00");
        map.put("status", "Successfully processed");
        result.setResults(Collections.singletonList(map));
        mockAndReturn(result);
        testling.saveAllResults();
        verify(exceptionHandler).handleException(any(Exception.class));
    }

    @Test
    public void testSaveAllResultsWrongStatus() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        map.put("code", "200.00");
        map.put("status", "Unsuccessfully processed");
        result.setResults(Collections.singletonList(map));
        mockAndReturn(result);
        testling.saveAllResults();
        verify(exceptionHandler).handleException(any(Exception.class));
    }

    private void mockAndReturn(Icinga2Result result) {
        when(icinga2RestCient.getTargetCheckResult()).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(invocationBuilder);
        when(webTarget.getUri()).thenReturn(URI.create(""));
        when(invocationBuilder.post(any(Entity.class))).thenReturn(response);
        when(response.readEntity(Icinga2Result.class)).thenReturn(result);
    }
}