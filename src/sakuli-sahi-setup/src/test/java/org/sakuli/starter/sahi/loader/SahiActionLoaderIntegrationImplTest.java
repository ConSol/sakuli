/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.starter.sahi.loader;

import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.mockito.*;
import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;

/**
 * @author tschneck
 *         Date: 4/19/17
 */
public class SahiActionLoaderIntegrationImplTest {

    public static final String TESTCASE_ID = "testcase1";
    @Mock
    private RhinoScriptRunner rhinoScriptRunner;
    @Mock
    private Session session;
    @Mock
    private SahiProxyProperties sahiProxyProperties;
    @Mock
    private SakuliExceptionHandler exceptionHandler;

    @InjectMocks
    private SahiActionLoaderIntegrationImpl testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(rhinoScriptRunner.getSession()).thenReturn(session);
    }

    @Test
    public void testInit() throws Exception {
        Mockito.when(sahiProxyProperties.isRequestDelayActive()).thenReturn(true);
        testling.initTestCase(new TestCase(TESTCASE_ID, TESTCASE_ID));
        Mockito.verify(exceptionHandler, Mockito.never()).handleException(Matchers.any(Exception.class));
        Mockito.verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR, "true");
    }

    @Test
    public void testInitNoSahiDelay() throws Exception {
        Mockito.when(sahiProxyProperties.isRequestDelayActive()).thenReturn(false);
        testling.initTestCase(new TestCase(TESTCASE_ID, TESTCASE_ID));
        Mockito.verify(exceptionHandler, Mockito.never()).handleException(Matchers.any(Exception.class));
        Mockito.verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR, "false");
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testInitNoSahiSession() throws Exception {
        Mockito.when(rhinoScriptRunner.getSession()).thenReturn(null);

        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        testling.initTestCase(new TestCase(TESTCASE_ID, TESTCASE_ID));
        Mockito.verify(exceptionHandler).handleException(ac.capture(), eq(false));
        Assert.assertEquals(ac.getValue(), "cannot init rhino script runner with sakuli custom delay variable 'sakuli-delay-active'");
        Mockito.verify(session, Mockito.never()).setVariable(Matchers.anyString(), Matchers.anyString());
    }
}