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

package org.sakuli.starter.sahi.action.screenbased;

import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.actions.environment.Environment;
import org.sakuli.actions.screenbased.TypingUtil;
import org.sakuli.actions.screenbased.UserInterfaceInputActionCallback;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.aop.AopBaseTest;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.sakuli.starter.sahi.loader.SahiActionLoader;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ModifySahiTimerCallbackTest extends AopBaseTest {

    @Spy
    private ModifySahiTimerCallback testling;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        BeanLoader.loadBean(SahiProxyProperties.class).setRequestDelayMs(null);
    }

    @Test
    public void testCallbackTriggered() throws Exception {
        final Collection<UserInterfaceInputActionCallback> callbacks = BeanLoader.loadMultipleBeans(UserInterfaceInputActionCallback.class).values();
        assertEquals(callbacks.size(), 1);
        TypingUtil typingUtil = mock(TypingUtil.class);
        Environment testAction = new Environment(false);
        ReflectionTestUtils.setField(testAction, "typingUtil", typingUtil);
        testAction.type("nothing");

        verify(typingUtil).type(anyString(), anyString());
        assertLastLine(logFile, testAction.getClass().getSimpleName(), LogLevel.INFO,
                "Environment.type() - type over system keyboard with arg(s) [nothing]");
        final UserInterfaceInputActionCallback cb = callbacks.stream().findFirst().get();
        verify(cb).afterUserInterfaceInput(any());
        verify(cb).beforeUserInterfaceInput(any());

    }

    @Test
    public void testModifySahiTimer() throws Exception {
        RhinoScriptRunner runner = mock(RhinoScriptRunner.class);
        Session session = mock(Session.class);
        when(runner.getSession()).thenReturn(session);
        SahiActionLoader sahiActionLoader = BeanLoader.loadBean(SahiActionLoader.class);
        sahiActionLoader.setRhinoScriptRunner(runner);
        sahiActionLoader.getSahiProxyProperties().setRequestDelayMs(500);
        assertTrue(sahiActionLoader.getSahiProxyProperties().isRequestDelayActive());
        doReturn(1000).when(testling).determineDelay(any(JoinPoint.class), any(SahiActionLoader.class));

        doReturn(LoggerFactory.getLogger(this.getClass())).when(testling).getLogger(any(JoinPoint.class));
        doReturn("sig.method()").when(testling).getClassAndMethodAsString(any(JoinPoint.class));

        //test modifcation of timer to 1000ms
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, "1000");
        verify(sahiActionLoader.getBaseActionLoader().getExceptionHandler(), never()).handleException(any(Exception.class));
        assertLastLine(logFile, "sahi-proxy-timer", LogLevel.INFO, "sahi-proxy-timer modified to 1000 ms");

        //test reset timer
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, null);
        verify(sahiActionLoader.getBaseActionLoader().getExceptionHandler(), never()).handleException(any(Exception.class));
        assertLastLine(logFile, "sahi-proxy-timer", LogLevel.INFO, "reset sahi-proxy-timer");
    }

    @Test
    public void testDisabledModifySahiTimer() throws Exception {
        RhinoScriptRunner runner = mock(RhinoScriptRunner.class);
        Session session = mock(Session.class);
        when(runner.getSession()).thenReturn(session);
        SahiActionLoader sahiActionLoader = BeanLoader.loadBean(SahiActionLoader.class);
        sahiActionLoader.setRhinoScriptRunner(runner);
        assertFalse(sahiActionLoader.getSahiProxyProperties().isRequestDelayActive());

        doReturn(LoggerFactory.getLogger(this.getClass())).when(testling).getLogger(any(JoinPoint.class));
        doReturn("sig.method()").when(testling).getClassAndMethodAsString(any(JoinPoint.class));

        //test modifcation of timer is disabled
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session, never()).setVariable(anyString(), anyString());
        verify(sahiActionLoader.getBaseActionLoader().getExceptionHandler(), never()).handleException(any(Exception.class));

        //test no session available
        sahiActionLoader.getSahiProxyProperties().setRequestDelayMs(500);
        assertTrue(sahiActionLoader.getSahiProxyProperties().isRequestDelayActive());
        when(runner.getSession()).thenReturn(null);
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session, never()).setVariable(anyString(), anyString());
        verify(sahiActionLoader.getBaseActionLoader().getExceptionHandler(), never()).handleException(any(Exception.class));
    }


    @Test
    public void testDetermineDelay() throws Exception {
        SahiActionLoader loader = BeanLoader.loadBean(SahiActionLoader.class);
        loader.getSahiProxyProperties().setRequestDelayMs(500);
        loader.getBaseActionLoader().getActionProperties().setTypeDelay(0.05);

        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("pasteSomething");
        assertEquals(testling.determineDelay(joinPoint, loader).intValue(), 500);

        when(signature.getName()).thenReturn("typeMasked");
        when(joinPoint.getArgs()).thenReturn(new String[]{"1", "MOD"});
        assertEquals(testling.determineDelay(joinPoint, loader).intValue(), 550);

        when(joinPoint.getArgs()).thenReturn(new String[]{"12characters", "MOD"});
        assertEquals(testling.determineDelay(joinPoint, loader).intValue(), 12 * 550);
    }
}