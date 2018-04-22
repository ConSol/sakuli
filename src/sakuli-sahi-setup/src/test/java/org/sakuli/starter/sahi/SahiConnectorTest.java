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

package org.sakuli.starter.sahi;

import net.sf.sahi.ant.Report;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.test.TestRunner;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.starter.sahi.datamodel.actions.LogResult;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.sakuli.starter.sahi.exceptions.SahiActionException;
import org.sakuli.starter.sahi.helper.SahiProxy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.sakuli.AbstractBaseTest.*;


public class SahiConnectorTest {
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
        when(testSuiteMock.getAbsolutePathOfTestSuiteFile()).thenReturn(Paths.get(TEST_FOLDER_PATH).toString() + File.separator + "testsuite.suite");
    }

    @Test
    public void testGetTestRunner() throws Exception {
        when(sahiProxyProperties.getProxyPort()).thenReturn(9000);
        final TestRunner testRunner = testling.getTestRunner();
        final String s = testRunner.toString();
        assertContains(s, "sahiHost = localhost");
        assertContains(s, "port = 9000");
        assertContains(s, "threads = 1");
    }

    @Test
    public void testStartSahiTestSuiteFAILURE() throws Exception {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        doReturn(SAKULI_HOME_FOLDER_PATH).when(testling).getIncludeFolderJsPath();
        when(testRunnerMock.execute()).thenReturn("FAILURE");
        when(testSuiteMock.getStopDate()).thenReturn(new Date());
        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(testSuiteMock).setStopDate(any(Date.class));
        verify(sakuliExceptionHandler).handleException(any(SakuliInitException.class));
        verify(sahiProxy).shutdown();
        verify(testling, never()).reconnect(any(Exception.class));
    }

    @Test
    public void testStartSahiTestSuiteOK() throws Exception {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        doReturn(SAKULI_HOME_FOLDER_PATH).when(testling).getIncludeFolderJsPath();
        when(testRunnerMock.execute()).thenReturn("OK");
        when(testSuiteMock.getStopDate()).thenReturn(new Date());
        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(testSuiteMock).setStopDate(any(Date.class));
        verify(sakuliExceptionHandler, never()).handleException(any(SakuliInitException.class));
        verify(sahiProxy).shutdown();
        verify(testling, never()).reconnect(any(Exception.class));
    }

    @Test
    public void testStartSahiTestSuiteReconnect() throws Exception {
        TestRunner testRunnerMock = mock(TestRunner.class);
        doReturn(testRunnerMock).when(testling).getTestRunner();
        when(testRunnerMock.execute()).thenThrow(new ConnectException("TEST"));
        doNothing().when(testling).reconnect(any(Exception.class));
        doReturn(SAKULI_HOME_FOLDER_PATH).when(testling).getIncludeFolderJsPath();

        testling.startSahiTestSuite();
        verify(testRunnerMock).addReport(any(Report.class));
        verify(testRunnerMock).setInitJS(anyString());
        verify(sakuliExceptionHandler, never()).handleException(any(SakuliInitException.class));
        verify(sahiProxy).shutdown();
        verify(testling).reconnect(any(Exception.class));
    }

    @Test
    public void testGetIncludFolderJsPath() throws Exception {
        Path pathMock = mock(Path.class);
        when(sahiProxyProperties.getJsLibFolder()).thenReturn(pathMock);
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
    public void testReconnectOK() throws Exception {
        Path pathMock = mock(Path.class);
        when(sahiProxyProperties.getJsLibFolder()).thenReturn(pathMock);
        when(pathMock.toAbsolutePath()).thenReturn(pathMock);
        when(pathMock.toString()).thenReturn("/sakuli/src/main/include");

        testling.countConnections = 3;
        when(sahiProxyProperties.getMaxConnectTries()).thenReturn(3);
        testling.reconnect(new Exception("Test"));
        verify(testling).startSahiTestSuite();
    }

    @Test(expectedExceptions = InterruptedException.class)
    public void testReconnectFAILURE() throws Exception {
        Path pathMock = mock(Path.class);
        when(sahiProxyProperties.getJsLibFolder()).thenReturn(pathMock);
        when(pathMock.toAbsolutePath()).thenReturn(pathMock);
        when(pathMock.toString()).thenReturn("/sakuli/src/main/include");

        testling.countConnections = 4;
        when(sahiProxyProperties.getMaxConnectTries()).thenReturn(3);
        testling.reconnect(new Exception("Test"));
        verify(testling, never()).startSahiTestSuite();
    }

    @Test(expectedExceptions = SakuliInitException.class, expectedExceptionsMessageRegExp = "Error - java.io.FileNotFoundException.* - during reading in testsuite.suite file 'unvalid-testsuite.suite'")
    public void testUnvalidTestSuiteFile() throws Exception {
        when(testSuiteMock.getAbsolutePathOfTestSuiteFile()).thenReturn(Paths.get("unvalid-testsuite.suite").toString());
        testling.checkTestSuiteFile();
    }

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    @Test
    public void testIsSahiTimoutException() throws Exception {
        Stream.of(Pair.of("Script did not start within 150 seconds. => ERROR ... @CALL:", true),
                Pair.of("some other error message", false)).forEach(p -> {

            String sahiError = p.getLeft();
            Exception testException = new SakuliExceptionWithScreenshot(new SahiActionException
                    (new LogResult("error", ResultType.FAILURE, "debug info", sahiError)), null);

            Stream.of(testException, testException.getCause(), new SakuliCheckedException(sahiError))
                    .forEach(e -> Assert.assertEquals(SahiConnector.isSahiScriptTimout(e), (boolean) p.getRight()));


        });

    }
}
