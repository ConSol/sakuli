/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.starter;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliProxyException;
import de.consol.sakuli.starter.proxy.SahiProxy;
import net.sf.sahi.ant.Report;
import net.sf.sahi.test.TestRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.net.ConnectException;
import java.util.Date;

import static org.mockito.Mockito.*;


public class SahiConnectorTest {
    @Mock
    private SakuliExceptionHandler sakuliExceptionHandler;
    @Mock
    private SahiProxy sahiProxy;
    @Mock
    private TestSuite testSuiteMock;
    @Spy
    @InjectMocks
    private SahiConnector testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReconnect() throws Exception {
        //TODO impl
    }

    @Test
    public void testStartSahiTestSuiteFAILURE() throws Throwable {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        when(testRunnerMock.execute()).thenReturn("FAILURE");
        when(testSuiteMock.getStopDate()).thenReturn(new Date());
        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(testSuiteMock).setStopDate(any(Date.class));
        verify(sakuliExceptionHandler).handleException(any(SakuliProxyException.class));
        verify(sahiProxy).shutdown();
        verify(testling, never()).reconnect(any(Exception.class));
    }

    @Test
    public void testStartSahiTestSuiteOK() throws Throwable {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        when(testRunnerMock.execute()).thenReturn("OK");
        when(testSuiteMock.getStopDate()).thenReturn(new Date());
        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(testSuiteMock).setStopDate(any(Date.class));
        verify(sakuliExceptionHandler, never()).handleException(any(SakuliProxyException.class));
        verify(sahiProxy).shutdown();
        verify(testling, never()).reconnect(any(Exception.class));
    }

    @Test
    public void testStartSahiTestSuiteReconnect() throws Throwable {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        when(testRunnerMock.execute()).thenThrow(new ConnectException("TEST"));
        doNothing().when(testling).reconnect(any(Exception.class));
        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(sakuliExceptionHandler, never()).handleException(any(SakuliProxyException.class));
        verify(sahiProxy).shutdown();
        verify(testling).reconnect(any(Exception.class));
    }

    @Test
    public void testGetIncludFolderJsPath() throws Exception {
        if (File.separator.equals("/")) {
            when(testSuiteMock.getIncludeFolderPath()).thenReturn("/sakuli/src/main/include");
            String result = testling.getIncludeFolderJsPath();
            Assert.assertEquals("/sakuli/src/main/include/sakuli.inc", result);
        } else {
            when(testSuiteMock.getIncludeFolderPath()).thenReturn("D:\\sakuli\\src\\main\\_include");
            String result = testling.getIncludeFolderJsPath();
            Assert.assertEquals("D:\\\\sakuli\\\\src\\\\main\\\\_include\\\\sakuli.inc", result);
        }
    }
}
