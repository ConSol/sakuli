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
import org.sakuli.datamodel.TestAction;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.exceptions.NonScreenshotException;
import org.sakuli.exceptions.SakuliActionException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliValidationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

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
    @Mock
    private SakuliProperties sakuliProperties;
    private TestCase sample;
    private Map<String, TestCase> testCases;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        sample = new TestCase("testling", "1234_");
        testCases = new HashMap<>();
        testCases.put(sample.getId(), sample);
        reset(loaderMock);
        when(loaderMock.getTestSuite()).thenReturn(testSuiteMock);
        when(loaderMock.getCurrentTestCase()).thenReturn(sample);
        when(loaderMock.getCurrentTestCaseStep()).thenCallRealMethod();
        when(loaderMock.getExceptionHandler()).thenReturn(exceptionHandlerMock);
        when(loaderMock.getSakuliProperties()).thenReturn(sakuliProperties);
        when(testSuiteMock.getId()).thenReturn("");
        when(testSuiteMock.getTestCases()).thenReturn(testCases);
        when(testSuiteMock.getTestCase(sample.getId())).thenReturn(sample);
        when(testSuiteMock.checkTestCaseID(sample.getId())).thenReturn(true);
        when(testSuiteMock.getTestSuiteFolder()).thenReturn(Paths.get(TEST_FOLDER_PATH));
    }

    @DataProvider
    public Object[][] testGetTestCaseIdDP() {
        return new Object[][]{
                {"testSuiteFolder"
                        + File.separator
                        + "1234_"
                        + File.separator
                        + "_tc.js", "1234_"},
                {"1234_" + File.separator + "_tc.js", "1234_"},
        };
    }

    @Test(dataProvider = "testGetTestCaseIdDP")
    public void testGetTestCaseId(String pathToTestCaseFile, String expectedTestCaseId) throws Exception {
        assertEquals(testling.getIdFromPath(pathToTestCaseFile), expectedTestCaseId);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "getIdFromPath"}});
    }

    @Test
    public void testSaveTestCaseResult() throws Exception {
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
                browser,
                false
        );

        assertEquals(startDate, testSuiteMock.getTestCases().get(sample.getId()).getStartDate());
        assertEquals(stopDate, testSuiteMock.getTestCases().get(sample.getId()).getStopDate());
        assertEquals(browser, argument.getValue());
        assertEquals(url, testSuiteMock.getTestCases().get(sample.getId()).getLastURL());
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "saveResult"}});
    }

    @Test
    public void testAddTestCaseStep() throws Exception {
        sample.setWarningTime(0);
        sample.setCriticalTime(0);

        long now = new Date().getTime();
        testling.addTestCaseStep(
                "step for JUnit",
                "" + (now - 3000),
                "" + now,
                2,
                5,
                false
        );
        TestCaseStep step = testSuiteMock.getTestCases().get(sample.getId()).getSteps().get(0);
        assertNotNull(step);
        assertEquals(step.getName(), "step_for_JUnit");
        assertEquals(step.getDuration(), 3.0f, "duration is not correct");
        assertEquals(step.getState(), TestCaseStepState.WARNING);
        verifyTestStepHasActions(step, new Object[][]{{"TestCaseAction", "addTestCaseStep"}});

        testling.addTestCaseStep(
                "step2 for JUnit",
                "" + (now + 300),
                "" + (now + 4300),
                5,
                5,
                false
        );
        TestCaseStep step2 = testSuiteMock.getTestCases().get(sample.getId()).getSteps().get(1);
        assertNotNull(step2);
        assertEquals(step2.getName(), "step2_for_JUnit");
        assertEquals(step2.getDuration(), 4.0f, "duration is not correct");
        assertEquals(step2.getState(), TestCaseStepState.OK);
        verifyTestStepHasActions(step2, new Object[][]{{"TestCaseAction", "addTestCaseStep"}});
    }

    @Test
    public void testAddTestCaseStepWithAlreadyInitializedStep() throws Exception {
        sample.setWarningTime(0);
        sample.setCriticalTime(0);
        TestCaseStep predefinedStep = new TestCaseStep();
        predefinedStep.setId("step for JUnit");
        ArrayList<TestCaseStep> steps = new ArrayList<>();
        steps.add(predefinedStep);
        sample.setSteps(steps);

        TestCaseStep step = testSuiteMock.getTestCases().get(sample.getId()).getSteps().get(0);
        assertNotNull(step);
        assertEquals(step.getName(), "step_for_JUnit");
        step.refreshState();
        assertEquals(step.getState(), TestCaseStepState.INIT);
        long currentTime = new Date().getTime();
        testling.addTestCaseStep(
                "step for JUnit",
                "" + (currentTime - 10000),
                "" + currentTime,
                9,
                10,
                false
        );

        List<TestCaseStep> testCaseSteps = sample.getSteps();
        assertEquals(testCaseSteps.size(), 1);
        TestCaseStep testCaseStep = testCaseSteps.get(0);
        assertNotNull(testCaseStep);
        assertEquals(testCaseStep.getName(), "step_for_JUnit");
        assertEquals(testCaseStep.getDuration(), 10.0f, "duration is not correct");
        assertEquals(testCaseStep.getState(), TestCaseStepState.WARNING);
        verifyTestStepHasActions(testCaseStep, new Object[][]{{"TestCaseAction", "addTestCaseStep"}});
    }

    @Test
    public void testInitTestCase() throws Exception {
        String tcID = sample.getId();
        testling.init(tcID, 4, 5, ".");

        verify(loaderMock).init(anyString(), anyString());
        assertEquals(testSuiteMock.getTestCase(tcID).getWarningTime(), 4);
        assertEquals(testSuiteMock.getTestCase(tcID).getCriticalTime(), 5);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "init"}});
    }

    @Test
    public void testInitTestCaseWithCustomID() throws Exception {

        String tcID = sample.getId();
        String newId = "new-id";
        testling.initWithCaseID(tcID, newId, 4, 5, ".");

        verify(loaderMock).init(eq(tcID), anyString());
        assertEquals(sample.getId(), newId);
        assertEquals(sample.getWarningTime(), 4);
        assertEquals(sample.getCriticalTime(), 5);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "initWithCaseID"}});
    }

    @DataProvider
    public Object[][] testInitTestCaseExceptionHandlingDP() {
        return new Object[][]{
                {-4, -5, true}, //negativ times
                {5, 4, true},   //warning time bigger than critical time
                {0, 0, false},   //zero times
                {4, 5, false},   //zero times
                {0, -8, true},  //negativ critical time
        };
    }

    @Test(dataProvider = "testInitTestCaseExceptionHandlingDP")
    public void testInitTestCaseExceptionHandling(int warningTime, int criticalTime, boolean expectedExceptionHandling) throws Exception {
        String tcID = sample.getId();
        testling.init(tcID, warningTime, criticalTime, ".");
        verify(exceptionHandlerMock, times(expectedExceptionHandling ? 1 : 0)).handleException(anyString(), anyBoolean());
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "init"}});
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
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "getTestCaseFolderPath"}});
    }

    @Test
    public void testGetTestSuiteFolderPath() throws Exception {
        String expectedPath = Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString();
        assertEquals(testling.getTestSuiteFolderPath(), expectedPath);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "getTestSuiteFolderPath"}});
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testThrowExceptionNoScreenshot() throws Exception {
        String exMessage = "TEST";
        testling.throwException(exMessage, false);
        ArgumentCaptor<Exception> ac = ArgumentCaptor.forClass(Exception.class);
        verify(exceptionHandlerMock).handleException(ac.capture());
        assertEquals(ac.getValue().getMessage(), exMessage);
        assertTrue(ac.getValue() instanceof SakuliValidationException);
        assertTrue(ac.getValue() instanceof NonScreenshotException);
        assertFalse(ac.getValue() instanceof SakuliActionException);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "throwException"}});
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testThrowExceptionWithScreenshot() throws Exception {
        String exMessage = "TEST";
        testling.throwException(exMessage, true);
        ArgumentCaptor<Exception> ac = ArgumentCaptor.forClass(Exception.class);
        verify(exceptionHandlerMock).handleException(ac.capture());
        assertEquals(ac.getValue().getMessage(), exMessage);
        assertFalse(ac.getValue() instanceof SakuliValidationException);
        assertFalse(ac.getValue() instanceof NonScreenshotException);
        assertTrue(ac.getValue() instanceof SakuliActionException);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "throwException"}});
    }

    @Test
    public void testAddImagePaths() throws Exception {
        ArgumentCaptor path = ArgumentCaptor.forClass(Path.class);
        testling.addImagePathsAsString("/home");
        verify(loaderMock).addImagePaths((Path[]) path.capture());
        assertEquals(path.getValue().toString(), Paths.get("/home").normalize().toString());
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "addImagePaths"}});
    }

    @Test
    public void testAddRelativeImagePaths() throws Exception {
        Path currenPath = Paths.get(".").toAbsolutePath().normalize();
        sample.setTcFile(currenPath.resolve("tc.js"));
        ArgumentCaptor path = ArgumentCaptor.forClass(Path.class);
        String picfolderName = "my_pic_folder";
        testling.addImagePathsAsString(picfolderName);
        verify(loaderMock).addImagePaths((Path[]) path.capture());
        assertEquals(path.getValue().toString(), currenPath.toString() + File.separator + picfolderName);
        verifyTestCaseHasActions(loaderMock.getCurrentTestCase(), new Object[][]{{"TestCaseAction", "addImagePaths"}});
    }

    private void verifyTestStepHasActions(TestCaseStep testCaseStepToVerify, Object[][] expectedActions) {
        verifyTestActions(testCaseStepToVerify.getTestActions(), expectedActions);
    }

    private void verifyTestCaseHasActions(TestCase testCaseToVerify, Object[][] expectedActions) {
        verifyTestActions(testCaseToVerify.getTestActions(), expectedActions);
    }

    private void verifyTestActions(List<TestAction> testActionsToVerify, Object[][] expectedActions) {
        assertEquals(testActionsToVerify.size(), expectedActions.length);
        for (int i = 0; i < expectedActions.length; i++) {
            Object[] expectedAction = expectedActions[i];
            TestAction testAction = testActionsToVerify.get(i);
            assertEquals(testAction.getObject(), expectedAction[0]);
            assertEquals(testAction.getMethod(), expectedAction[1]);

        }
    }

}
