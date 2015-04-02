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

package org.sakuli.aop;

import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.actions.environment.Environment;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.actions.screenbased.TypingUtil;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;
import org.sakuli.loader.ScreenActionLoader;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class ModifySahiTimerAspectTest extends AopBaseTest {

    @Spy
    private ModifySahiTimerAspect testling;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler For IntelliJ you can
     * make a right click on the maven target 'aspectj:compile' and mark it with the option 'Execute After Make'.
     */
    @Test
    public void testCallEnvironmentMethod() throws Exception {
        ScreenActionLoader loaderMock = mock(ScreenActionLoader.class);
        TypingUtil typingUtil = mock(TypingUtil.class);
        when(typingUtil.type(anyString(), anyString())).thenReturn(null);
        Environment env = new Environment(false);
        ReflectionTestUtils.setField(env, "typingUtil", typingUtil);
        ReflectionTestUtils.setField(env, "loader", loaderMock);
        env.type("BLA");
        assertLastLine(logFile, "Environment", LogLevel.INFO, "Environment.type() - type over system keyboard with arg(s) [BLA]");
    }

    @Test
    public void testCallRegionMethod() throws Exception {
        TypingUtil typingUtil = mock(TypingUtil.class);
        when(typingUtil.type(anyString(), anyString())).thenReturn(null);
        Region region = new RegionTestMock();
        ReflectionTestUtils.setField(region, "typingUtil", typingUtil);
        region.type("BLA");
        assertLastLine(logFile, "Environment", LogLevel.INFO, "Environment.type() - type over system keyboard with arg(s) [BLA]");
    }

    @Test
    public void testModifySahiTimer() throws Exception {
        RhinoScriptRunner runner = mock(RhinoScriptRunner.class);
        Session session = mock(Session.class);
        when(runner.getSession()).thenReturn(session);
        BaseActionLoader baseActionLoader = BeanLoader.loadBaseActionLoader();
        baseActionLoader.setRhinoScriptRunner(runner);
        when(baseActionLoader.getSahiProxyProperties().isRequestDelayActive()).thenReturn(true);
        doReturn(1000).when(testling).determineDelay(any(JoinPoint.class), any(BaseActionLoader.class));

        doReturn(LoggerFactory.getLogger(this.getClass())).when(testling).getLogger(any(JoinPoint.class));
        doReturn("sig.method()").when(testling).getClassAndMethodAsString(any(JoinPoint.class));

        //test modifcation of timer to 1000ms
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, "1000");
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));
        assertLastLine(logFile, "sahi-proxy-timer", LogLevel.INFO, "sahi-proxy-timer modified to 1000 ms");

        //test reset timer
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, null);
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));
        assertLastLine(logFile, "sahi-proxy-timer", LogLevel.INFO, "reset sahi-proxy-timer");
    }

    @Test
    public void testDisabledModifySahiTimer() throws Exception {
        RhinoScriptRunner runner = mock(RhinoScriptRunner.class);
        Session session = mock(Session.class);
        when(runner.getSession()).thenReturn(session);
        BaseActionLoader baseActionLoader = BeanLoader.loadBaseActionLoader();
        baseActionLoader.setRhinoScriptRunner(runner);
        when(baseActionLoader.getSahiProxyProperties().isRequestDelayActive()).thenReturn(false);

        doReturn(LoggerFactory.getLogger(this.getClass())).when(testling).getLogger(any(JoinPoint.class));
        doReturn("sig.method()").when(testling).getClassAndMethodAsString(any(JoinPoint.class));

        //test modifcation of timer is disabled
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session, never()).setVariable(anyString(), anyString());
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));

        //test no session available
        when(baseActionLoader.getSahiProxyProperties().isRequestDelayActive()).thenReturn(true);
        when(runner.getSession()).thenReturn(null);
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session, never()).setVariable(anyString(), anyString());
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));
    }


    @Test
    public void testDetermineDelay() throws Exception {
        BaseActionLoader loader = BeanLoader.loadBaseActionLoader();
        when(loader.getSahiProxyProperties().getRequestDelayMs()).thenReturn(500);
        loader.getActionProperties().setTypeDelay(0.05);

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

    private class RegionTestMock extends Region {
        public RegionTestMock() {
            super(mock(RegionImpl.class));
        }

        @Override
        public ScreenActionLoader getScreenActionLoader() {
            return mock(ScreenActionLoader.class);
        }
    }
}