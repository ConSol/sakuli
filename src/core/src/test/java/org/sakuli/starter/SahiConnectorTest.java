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

package org.sakuli.starter;

import net.sf.sahi.ant.Report;
import net.sf.sahi.test.TestRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliProxyException;
import org.sakuli.starter.helper.SahiProxy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class SahiConnectorTest extends BaseTest {
    @Mock
    private SakuliExceptionHandler sakuliExceptionHandler;
    @Mock
    private SahiProxy sahiProxy;
    @Mock
    private TestSuite testSuiteMock;
    @Mock
    private SakuliProperties sakuliProperties;
    @Mock
    private SahiProxyProperties sahiProxyProperties;
    @Spy
    @InjectMocks
    private SahiConnector testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(sakuliProperties.getLogFolder()).thenReturn(Paths.get(TEST_FOLDER_PATH));
        when(testSuiteMock.getTestSuiteFolder()).thenReturn(Paths.get(TEST_FOLDER_PATH));
    }


    @Test
    public void testStartSahiTestSuiteFAILURE() throws Throwable {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        doReturn(SAKULI_HOME_FOLDER_PATH).when(testling).getIncludeFolderJsPath();
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
        doReturn(SAKULI_HOME_FOLDER_PATH).when(testling).getIncludeFolderJsPath();
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
        doReturn(SAKULI_HOME_FOLDER_PATH).when(testling).getIncludeFolderJsPath();

        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(sakuliExceptionHandler, never()).handleException(any(SakuliProxyException.class));
        verify(sahiProxy).shutdown();
        verify(testling).reconnect(any(Exception.class));
    }

    @Test
    public void testGetIncludFolderJsPath() throws Exception {
        Path pathMock = mock(Path.class);
        when(sakuliProperties.getJsLibFolder()).thenReturn(pathMock);
        when(pathMock.toAbsolutePath()).thenReturn(pathMock);

        if (File.separator.equals("/")) {
            when(pathMock.toString()).thenReturn("/sakuli/src/main/include");
            String result = testling.getIncludeFolderJsPath();
            Assert.assertEquals("/sakuli/src/main/include/sakuli.js", result);
        } else {
            when(pathMock.toString()).thenReturn("D:\\sakuli\\src\\main\\_include");
            String result = testling.getIncludeFolderJsPath();
            Assert.assertEquals("D:\\\\sakuli\\\\src\\\\main\\\\_include\\\\sakuli.js", result);
        }
    }

    @Test
    public void testReconnectOK() throws Throwable {
        Path pathMock = mock(Path.class);
        when(sakuliProperties.getJsLibFolder()).thenReturn(pathMock);
        when(pathMock.toAbsolutePath()).thenReturn(pathMock);
        when(pathMock.toString()).thenReturn("/sakuli/src/main/include");

        testling.countConnections = 3;
        when(sahiProxyProperties.getMaxConnectTries()).thenReturn(3);
        testling.reconnect(new Exception("Test"));
        verify(testling).startSahiTestSuite();
    }

    @Test(expectedExceptions = InterruptedException.class)
    public void testReconnectFAILURE() throws Throwable {
        Path pathMock = mock(Path.class);
        when(sakuliProperties.getJsLibFolder()).thenReturn(pathMock);
        when(pathMock.toAbsolutePath()).thenReturn(pathMock);
        when(pathMock.toString()).thenReturn("/sakuli/src/main/include");

        testling.countConnections = 4;
        when(sahiProxyProperties.getMaxConnectTries()).thenReturn(3);
        testling.reconnect(new Exception("Test"));
        verify(testling, never()).startSahiTestSuite();
    }
}
