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

package org.sakuli.actions;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.exceptions.NonScreenshotException;
import org.sakuli.exceptions.SakuliActionException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliValidationException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author tschneck Date: 25.07.13
 */
public class TestCaseActionTest extends BaseTest {
    @InjectMocks
    private TestCaseAction testling;
    @Mock
    private TestSuite testSuiteMock;
    @Mock
    private SakuliExceptionHandler exceptionHandlerMock;
    private TestCase sample = new TestCase("testling", "1234_");
    private Map<String, TestCase> testCases;


    @BeforeMethod
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        testCases = new HashMap<>();
        testCases.put(sample.getId(), sample);
        when(loaderMock.getTestSuite()).thenReturn(testSuiteMock);
        when(loaderMock.getCurrentTestCase()).thenReturn(sample);
        when(loaderMock.getExceptionHandler()).thenReturn(exceptionHandlerMock);
        when(testSuiteMock.getId()).thenReturn("");
        when(testSuiteMock.getTestCases()).thenReturn(testCases);
        when(testSuiteMock.getTestCase(sample.getId())).thenReturn(sample);
        when(testSuiteMock.checkTestCaseID(sample.getId())).thenReturn(true);
        when(testSuiteMock.getTestSuiteFolder()).thenReturn(Paths.get(TEST_FOLDER_PATH));
    }


    @Test
    public void testGetTestCaseId() throws Exception {
        Assert.assertEquals(testling.getIdFromPath(
                "testSuiteFolder"
                        + File.separator
                        + "1234_"
                        + File.separator
                        + "_tc.js"
        ), sample.getId());
        Assert.assertEquals(sample.getId(), testling.getIdFromPath(
                "1234_" + File.separator + "_tc.js"));

    }

    @Test
    public void testSaveTestCaseResult() throws Throwable {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        Date startDate = new Date(new Date().getTime() - 8000);
        Date stopDate = new Date();
        String url = "http://oxid/shop/";
        String browser = "Mozilla/5.0 (JUNIT TEST 4) Gecko/2010..";
        doNothing().when(testSuiteMock).setBrowserInfo(argument.capture());

        testling.saveResult(
                sample.getId(),
                "" + startDate.getTime(),
                "" + stopDate.getTime(),
                url,
                browser
        );

        assertEquals(startDate, testSuiteMock.getTestCases().get(sample.getId()).getStartDate());
        assertEquals(stopDate, testSuiteMock.getTestCases().get(sample.getId()).getStopDate());
        assertEquals(browser, argument.getValue());
        assertEquals(url, testSuiteMock.getTestCases().get(sample.getId()).getLastURL());


    }

    @Test
    public void testAddTestCaseStep() throws Throwable {
        sample.setWarningTime(0);
        sample.setCriticalTime(0);

        testling.addTestCaseStep(
                "step for JUnit",
                "" + (new Date().getTime() - 3000),
                "" + (new Date().getTime()),
                2 //warning
        );
        TestCaseStep step = testSuiteMock.getTestCases().get(sample.getId()).getSteps().get(0);
        assertNotNull(step);
        assertEquals(step.getName(), "step_for_JUnit");
        assertTrue("duraion is correct", step.getDuration() > 2.9 && step.getDuration() < 3.1);
        assertEquals(step.getState(), TestCaseStepState.WARNING);

        testling.addTestCaseStep(
                "step2 for JUnit",
                "" + (new Date().getTime() - 3000),
                "" + (new Date().getTime()),
                5 //no warning
        );
        TestCaseStep step2 = testSuiteMock.getTestCases().get(sample.getId()).getSteps().get(1);
        assertNotNull(step2);
        assertEquals(step2.getName(), "step2_for_JUnit");
        assertTrue("duraion is correct", step.getDuration() > 2.9 && step.getDuration() < 3.1);
        assertEquals(step2.getState(), TestCaseStepState.OK);


    }

    @Test
    public void testInitTestCase() throws Exception {
        String tcID = sample.getId();
        testling.init(tcID, 4, 5, ".");

        verify(loaderMock).init(anyString(), anyString());
        Assert.assertEquals(testSuiteMock.getTestCase(tcID).getWarningTime(), 4);
        Assert.assertEquals(testSuiteMock.getTestCase(tcID).getCriticalTime(), 5);
    }

    @Test
    public void testInitTestCaseNegativTimes() throws Exception {
        String tcID = sample.getId();
        testling.init(tcID, -4, -5, ".");
        verify(exceptionHandlerMock, times(1)).handleException(anyString(), anyBoolean());
    }

    @Test
    public void testInitTestCaseNonsensTimes() throws Exception {
        String tcID = sample.getId();
        testling.init(tcID, 5, 4, ".");
        verify(exceptionHandlerMock, times(1)).handleException(anyString(), anyBoolean());
    }

    @Test
    public void testHandleException() throws Exception {
        String tcExcMessage = "test exception handler";
        testling.handleException(tcExcMessage);
        verify(exceptionHandlerMock, times(1)).handleException(eq(tcExcMessage), anyBoolean());
    }

    @Test
    public void testGetTestCaseFolderPath() throws Exception {
        String folderName = "test_case_folder";
        Path folderpath = Paths.get(TEST_FOLDER_PATH + File.separator + folderName + File.separator + "tc.js");
        sample.setTcFile(folderpath);

        assertEquals(testling.getTestCaseFolderPath(),
                Paths.get(TEST_FOLDER_PATH + File.separator + folderName).toAbsolutePath().toString());
    }

    @Test
    public void testGetTestSuiteFolderPath() throws Exception {
        String expectedPath = Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString();
        assertEquals(testling.getTestSuiteFolderPath(), expectedPath);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testThrowExceptionNoScreenshot() throws Exception {
        String exMessage = "TEST";
        testling.throwException(exMessage, false);
        ArgumentCaptor<Throwable> ac = ArgumentCaptor.forClass(Throwable.class);
        verify(exceptionHandlerMock).handleException(ac.capture());
        assertEquals(ac.getValue().getMessage(), exMessage);
        assertTrue(ac.getValue() instanceof SakuliValidationException);
        assertTrue(ac.getValue() instanceof NonScreenshotException);
        assertFalse(ac.getValue() instanceof SakuliActionException);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testThrowExceptionWithScreenshot() throws Exception {
        String exMessage = "TEST";
        testling.throwException(exMessage, true);
        ArgumentCaptor<Throwable> ac = ArgumentCaptor.forClass(Throwable.class);
        verify(exceptionHandlerMock).handleException(ac.capture());
        assertEquals(ac.getValue().getMessage(), exMessage);
        assertFalse(ac.getValue() instanceof SakuliValidationException);
        assertFalse(ac.getValue() instanceof NonScreenshotException);
        assertTrue(ac.getValue() instanceof SakuliActionException);
    }
}
