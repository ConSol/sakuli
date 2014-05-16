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

package de.consol.sakuli.exceptions;

import de.consol.sakuli.BaseTest;
import de.consol.sakuli.actions.screenbased.ScreenshotActions;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.properties.ActionProperties;
import de.consol.sakuli.datamodel.properties.SakuliProperties;
import de.consol.sakuli.datamodel.state.TestCaseState;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.loader.ScreenActionLoader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SakuliExceptionHandlerTest extends BaseTest {
    private TestSuite testSuite;
    private TestCase testCase;
    private String screenShotFolder = "src/test/resources/de/consol/sakuli/exceptions/screenshots";
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
        when(sakuliProperties.isLogResumOnException()).thenReturn(true);
        when(loader.getTestSuite()).thenReturn(testSuite);
        when(loader.getCurrentTestCase()).thenReturn(testCase);
    }

    // TODO: add test for SakuliActionExceptions

    @Test
    public void testHandleException() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        testling.handleException(new Exception(testExcMessage));
        Assert.assertTrue(testSuite.getException() instanceof SakuliException);
        Assert.assertTrue(testSuite.getException().getMessage().contains(testExcMessage));
        Assert.assertEquals(testSuite.getScreenShotPath(), expectedScreenshotPath);

        //test Suppressed
        String excpMessage2 = "ExceptionSuppressed";
        testling.handleException(new Exception(excpMessage2));
        Assert.assertTrue(testSuite.getExceptionMessages().contains(excpMessage2));
        Assert.assertTrue(testSuite.getExceptionMessages().contains(testExcMessage));
        Assert.assertEquals(testSuite.getState(), TestSuiteState.ERRORS);


    }

    @Test
    public void testHandleExceptionForTestCases() throws Exception {
        setUp();
        testling.handleException(new Exception(testExcMessage));
        Assert.assertTrue(testCase.getException() instanceof SakuliExceptionWithScreenshot);
        Assert.assertTrue(testCase.getException().getMessage().contains(testExcMessage));
        Assert.assertEquals(testCase.getScreenShotPath(), expectedScreenshotPath);

        //test Suppressed
        String excpMessage2 = "ExceptionSuppressed";
        testling.handleException(new Exception(excpMessage2));
        Assert.assertTrue(testCase.getExceptionMessages().contains(excpMessage2));
        Assert.assertTrue(testCase.getExceptionMessages().contains(testExcMessage));
        Assert.assertEquals(testCase.getState(), TestCaseState.ERRORS);
        Assert.assertEquals(testSuite.getState(), TestSuiteState.ERRORS);

        //test Proxy Exception
        when(loader.getCurrentTestCase()).thenReturn(null);
        testling.handleException(new SakuliProxyException("FAILURE"));
        Assert.assertNull(testSuite.getException());
    }

    @Test
    public void testHandleSakuliProxyExceptionForTestCases() throws Exception {
        setUp();
        when(loader.getCurrentTestCase()).thenReturn(null);
        testling.handleException(new SakuliProxyException(testExcMessage));
        Assert.assertTrue(testSuite.getException().getCause() instanceof SakuliProxyException);
        Assert.assertTrue(testSuite.getException().getMessage().contains(testExcMessage));
        Assert.assertEquals(testSuite.getState(), TestSuiteState.ERRORS);
    }

    @Test
    public void testGetScreenshotFile() throws Exception {
        SakuliExceptionWithScreenshot ex = new SakuliExceptionWithScreenshot(testExcMessage, expectedScreenshotPath);
        Assert.assertEquals(SakuliExceptionHandler.getScreenshotFile(ex), expectedScreenshotPath);
        Path aspectedScreenShotFolder2 = Paths.get(screenShotFolder);
        ex.setScreenshot(null);
        ex.addSuppressed(new SakuliExceptionWithScreenshot(testExcMessage, aspectedScreenShotFolder2));
        Assert.assertEquals(SakuliExceptionHandler.getScreenshotFile(ex), aspectedScreenShotFolder2);
    }
}
