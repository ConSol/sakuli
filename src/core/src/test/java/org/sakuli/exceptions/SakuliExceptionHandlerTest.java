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

package org.sakuli.exceptions;

import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.actions.screenbased.ScreenshotActions;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.loader.ScreenActionLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SakuliExceptionHandlerTest extends BaseTest {
    private TestSuite testSuite;
    private TestCase testCase;
    private String screenShotFolder = "src/test/resources/org/sakuli/exceptions/screenshots";
    private String testExcMessage = "TEST IT";
    private Path expectedScreenshotPath;
    private HashMap<String, TestCase> testCases;

    @Mock
    private ScreenshotActions screenshotActionsMock;
    @Mock
    private ScreenActionLoader loader;
    @Mock
    private SakuliProperties sakuliProperties;
    @Mock
    private ActionProperties actionProperties;
    @Mock
    private Report sahiReport;

    @InjectMocks
    private SakuliExceptionHandler testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private void setUp() throws Exception {
        expectedScreenshotPath = Paths.get(screenShotFolder + "test.jpg");
        when(screenshotActionsMock.takeScreenshot(anyString(), any(Path.class))).thenReturn(expectedScreenshotPath);
        when(loader.getScreenshotActions()).thenReturn(screenshotActionsMock);
        testCase = new TestCase("testling", "1234_");
        testCases = new HashMap<>();
        testCases.put(testCase.getId(), testCase);
        testSuite = new TestSuite();
        testSuite.setState(TestSuiteState.RUNNING);
        ReflectionTestUtils.setField(testSuite, "testCases", testCases);
        when(loader.getActionProperties()).thenReturn(actionProperties);
        when(actionProperties.isTakeScreenshots()).thenReturn(true);
        when(loader.getSakuliProperties()).thenReturn(sakuliProperties);
        when(sakuliProperties.isSuppressResumedExceptions()).thenReturn(false);
        when(loader.getSahiReport()).thenReturn(sahiReport);
        when(loader.getTestSuite()).thenReturn(testSuite);
        when(loader.getCurrentTestCase()).thenReturn(testCase);

    }

    @Test
    public void testHandleException() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        testling.handleException(new Exception(testExcMessage));
        assertTrue(testSuite.getException() instanceof SakuliException);
        assertTrue(testSuite.getException().getMessage().contains(testExcMessage));
        assertEquals(testSuite.getScreenShotPath(), expectedScreenshotPath);

        //test Suppressed
        String excpMessage2 = "ExceptionSuppressed";
        testling.handleException(new Exception(excpMessage2));
        assertTrue(testSuite.getExceptionMessages().contains(excpMessage2));
        assertTrue(testSuite.getExceptionMessages().contains(testExcMessage));
        assertEquals(testSuite.getState(), TestSuiteState.ERRORS);


    }

    @Test
    public void testHandleExceptionForTestCases() throws Exception {
        setUp();
        testling.handleException(new Exception(testExcMessage));
        assertTrue(testCase.getException() instanceof SakuliExceptionWithScreenshot);
        assertTrue(testCase.getException().getMessage().contains(testExcMessage));
        assertEquals(testCase.getScreenShotPath(), expectedScreenshotPath);

        //test Suppressed
        String excpMessage2 = "ExceptionSuppressed";
        testling.handleException(new Exception(excpMessage2));
        assertTrue(testCase.getExceptionMessages().contains(excpMessage2));
        assertTrue(testCase.getExceptionMessages().contains(testExcMessage));
        assertEquals(testCase.getState(), TestCaseState.ERRORS);
        assertEquals(testSuite.getState(), TestSuiteState.ERRORS);

        //test Proxy Exception
        when(loader.getCurrentTestCase()).thenReturn(null);
        testling.handleException(new SakuliProxyException("FAILURE"));
        Assert.assertNull(testSuite.getException());
    }

    @Test
    public void testSakuliReceiverException() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        SakuliReceiverException receiverException = new SakuliReceiverException("RECEIVER_EXCEPTION");

        testling.handleException(receiverException, true);
        verify(screenshotActionsMock, never()).takeScreenshotAndHighlight(anyString(), any(Path.class), any(RegionImpl.class));
        verify(screenshotActionsMock, never()).takeScreenshot(anyString(), any(Path.class));
        verify(screenshotActionsMock, never()).takeScreenshot(anyString(), any(Path.class), anyString());
        verify(sahiReport).addResult(anyString(), any(ResultType.class), anyString(), anyString());
        assertEquals(testSuite.getException(), receiverException);
        assertEquals(((SakuliException) testSuite.getException()).resumeOnException, true);
    }

    @Test
    public void testSakuliActionExceptionTakeUnsuccessfulScreenshotFromRegion() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        SakuliActionException sakuliActionException = new SakuliActionException("ACTION_EXCEPTION");

        testling.handleException(sakuliActionException, mock(RegionImpl.class), true);
        verify(screenshotActionsMock).takeScreenshotAndHighlight(anyString(), any(Path.class), any(RegionImpl.class));
        verify(screenshotActionsMock).takeScreenshot(anyString(), any(Path.class));
        verify(screenshotActionsMock, never()).takeScreenshot(anyString(), any(Path.class), anyString());
        verify(sahiReport).addResult(anyString(), any(ResultType.class), anyString(), anyString());
        assertTrue(testSuite.getException() instanceof SakuliExceptionWithScreenshot);
        assertEquals(((SakuliExceptionWithScreenshot) testSuite.getException()).getScreenshot(), expectedScreenshotPath);
        assertEquals(((SakuliException) testSuite.getException()).resumeOnException, true);
    }

    @Test
    public void testSakuliActionExceptionTakeSuccessfulScreenshotFromRegion() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        when(screenshotActionsMock.takeScreenshotAndHighlight(anyString(), any(Path.class), any(RegionImpl.class))).thenReturn(expectedScreenshotPath);
        when(screenshotActionsMock.takeScreenshot(anyString(), any(Path.class))).thenReturn(null);
        SakuliActionException sakuliActionException = new SakuliActionException("ACTION_EXCEPTION");

        testling.handleException(sakuliActionException, mock(RegionImpl.class), true);
        verify(screenshotActionsMock).takeScreenshotAndHighlight(anyString(), any(Path.class), any(RegionImpl.class));
        verify(screenshotActionsMock, never()).takeScreenshot(anyString(), any(Path.class));
        verify(screenshotActionsMock, never()).takeScreenshot(anyString(), any(Path.class), anyString());
        verify(sahiReport).addResult(anyString(), any(ResultType.class), anyString(), anyString());
        assertTrue(testSuite.getException() instanceof SakuliExceptionWithScreenshot);
        assertEquals(((SakuliExceptionWithScreenshot) testSuite.getException()).getScreenshot(), expectedScreenshotPath);
        assertEquals(((SakuliException) testSuite.getException()).resumeOnException, true);
    }

    @Test
    public void testHandleSakuliProxyExceptionForTestCases() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        testling.handleException(new SakuliProxyException(testExcMessage));
        assertTrue(testSuite.getException().getCause() instanceof SakuliProxyException);
        assertTrue(testSuite.getException().getMessage().contains(testExcMessage));
        assertEquals(testSuite.getState(), TestSuiteState.ERRORS);
    }

    @Test
    public void testGetScreenshotFile() throws Exception {
        SakuliExceptionWithScreenshot ex = new SakuliExceptionWithScreenshot(testExcMessage, expectedScreenshotPath);
        assertEquals(SakuliExceptionHandler.getScreenshotFile(ex), expectedScreenshotPath);
        Path expectedScreenShotFolder2 = Paths.get(screenShotFolder);
        ex.setScreenshot(null);
        ex.addSuppressed(new SakuliExceptionWithScreenshot(testExcMessage, expectedScreenShotFolder2));
        assertEquals(SakuliExceptionHandler.getScreenshotFile(ex), expectedScreenShotFolder2);
    }

    @Test
    public void testGetAllExceptions() throws Exception {
        TestSuite ts = new TestSuite();
        ts.addException(new SakuliException("bla"));
        TestCase tc = new TestCase(null, null);
        tc.addException(new SakuliException("bla2"));
        ts.addTestCase(tc);
        List<Throwable> allExceptions = SakuliExceptionHandler.getAllExceptions(ts);
        assertEquals(allExceptions.size(), 2);
    }
}
