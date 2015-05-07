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

package org.sakuli.loader;

import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * autor tschneck
 */
public class BaseActionEnvironmentLoaderTest {
    @Mock
    private RhinoScriptRunner rhinoScriptRunner;
    @Mock
    private Session session;
    @Mock
    private SahiProxyProperties sahiProxyProperties;
    @Mock
    private TestSuiteProperties testSuiteProperties;
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private SakuliProperties sakuliProperties;

    @InjectMocks
    private BaseActionLoaderImpl testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(rhinoScriptRunner.getSession()).thenReturn(session);
    }

    @Test
    public void testInit() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("Test", testCaseId));
        when(sahiProxyProperties.isRequestDelayActive()).thenReturn(true);
        when(sakuliProperties.isLoadJavaScriptEngine()).thenReturn(true);
        testling.init(testCaseId, ".");
        verify(exceptionHandler, never()).handleException(any(Exception.class));
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR, "true");
    }

    @Test
    public void testInitNoSahiDelay() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("Test", testCaseId));
        when(sahiProxyProperties.isRequestDelayActive()).thenReturn(false);
        when(sakuliProperties.isLoadJavaScriptEngine()).thenReturn(true);
        testling.init(testCaseId, ".");
        verify(exceptionHandler, never()).handleException(any(Exception.class));
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR, "false");
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testInitNoSahiSession() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("Test", testCaseId));
        when(rhinoScriptRunner.getSession()).thenReturn(null);
        when(sakuliProperties.isLoadJavaScriptEngine()).thenReturn(true);

        ArgumentCaptor<SakuliException> ac = ArgumentCaptor.forClass(SakuliException.class);
        testling.init(testCaseId, ".");
        verify(exceptionHandler).handleException(ac.capture());
        Assert.assertEquals(ac.getValue().getMessage(), "cannot init rhino script runner with sakuli custom delay variable 'sakuli-delay-active'");
        verify(session, never()).setVariable(anyString(), anyString());
    }

    @Test
    public void testInitTCNull() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(null);
        testling.init(testCaseId, ".");
        verify(exceptionHandler, times(1)).handleException(any(SakuliException.class));
    }

    @Test
    public void testInitImagePathNull() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("test", testCaseId));
        testling.init(testCaseId, new String[]{});
        verify(exceptionHandler, times(1)).handleException(any(IOException.class));
    }

    @Test
    public void testInitImagePathEmpty() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("test", testCaseId));
        testling.init(testCaseId, new String[0]);
        verify(exceptionHandler, times(1)).handleException(any(IOException.class));
    }
}
