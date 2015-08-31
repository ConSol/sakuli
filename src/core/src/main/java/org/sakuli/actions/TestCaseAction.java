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

import org.apache.commons.lang.StringUtils;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.datamodel.helper.TestCaseHelper;
import org.sakuli.datamodel.helper.TestCaseStepHelper;
import org.sakuli.datamodel.helper.TestDataEntityHelper;
import org.sakuli.exceptions.SakuliActionException;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliValidationException;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BaseActionLoaderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Date;

/**
 * @author tschneck Date: 19.06.13
 */
@Component
public class TestCaseAction {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Represents the current running TestCase. The object will be set at {@link #init(String, int, int, String...)} and
     * releases at {@link #saveResult(String, String, String, String, String)}
     */
    @Autowired
    @Qualifier(BaseActionLoaderImpl.QUALIFIER)
    private BaseActionLoader loader;

    /****************
     * Init functions for the java script engine.
     *********************/

    /**
     * Set the warning and critical Time to the specific test case.
     *
     * @param testCaseID   current ID of the test case
     * @param warningTime  warning threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param criticalTime critical threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param imagePaths   multiple paths to images
     */
    @LogToResult(message = "init a new test case")
    public void init(String testCaseID, int warningTime, int criticalTime, String... imagePaths) {
        loader.init(testCaseID, imagePaths);
        initWarningAndCritical(warningTime, criticalTime);
    }

    /**
     * Set the warning and critical Time to the specific test case.
     *
     * @param testCaseID   current ID of the test case
     * @param warningTime  warning threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param criticalTime critical threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param imagePaths   multiple paths to images
     */
    @LogToResult(message = "init a new test case")
    public void initWithPaths(String testCaseID, int warningTime, int criticalTime, Path... imagePaths) {
        loader.init(testCaseID, imagePaths);
        initWarningAndCritical(warningTime, criticalTime);
    }

    private void initWarningAndCritical(int warningTime, int criticalTime) {
        TestCase currentTestCase = loader.getCurrentTestCase();
        String errormsg = TestDataEntityHelper.checkWarningAndCriticalTime(warningTime, criticalTime, currentTestCase.toStringShort());
        if (errormsg != null) {
            handleException(errormsg);
        } else {
            //if everything is ok set the times
            currentTestCase.setWarningTime(warningTime);
            currentTestCase.setCriticalTime(criticalTime);
        }
    }


    /****************
     * TEST CASE HANDLING
     *********************/

    /**
     * save the Result of a test Case
     *
     * @param testCaseId  id of the corresponding test case
     * @param startTime   start time in milliseconds
     * @param stopTime    end time in milliseconds
     * @param lastURL     URL to the last visited page during this test case
     * @param browserInfo detail information about the used browser
     * @throws SakuliException
     */
    @LogToResult(message = "save the result of the current test case")
    public void saveResult(String testCaseId, String startTime, String stopTime, String lastURL, String browserInfo) throws SakuliException {
        if (!loader.getCurrentTestCase().getId().equals(testCaseId)) {
            handleException("testcaseID '" + testCaseId + "' to save the test case Result ist is not valid!");
        }
        TestSuite testSuite = loader.getTestSuite();
        testSuite.setBrowserInfo(browserInfo);

        //set TestCase vars
        TestCase tc = loader.getCurrentTestCase();
        tc.setLastURL(lastURL);

        try {
            tc.setStartDate(new Date(Long.parseLong(startTime)));
            tc.setStopDate(new Date(Long.parseLong(stopTime)));
            logger.debug("test case duration = " + tc.getDuration());
            tc.refreshState();
        } catch (NumberFormatException | NullPointerException e) {
            handleException("Duration could not be calculated! " +
                    "Check if the warning and critical threshold is set correctly in your test case!  " +
                    "=> START date: " + startTime
                    + "\tSTOP date: " + stopTime
                    + "\n" + e.getMessage());
        }

        //release current test case -> indicates that this case is finished
        loader.setCurrentTestCase(null);
    }

    /**
     * Wrapper for {@link #addTestCaseStep(String, String, String, int)} with warningTime '0'.
     */
    public void addTestCaseStep(String stepName, String startTime, String stopTime) throws SakuliException {
        addTestCaseStep(stepName, startTime, stopTime, 0);
    }

    /**
     * Save a new step to a existing test case. Must be called before {@link #saveResult(String, String, String, String,
     * String)}
     *
     * @param stepName    name of this step
     * @param startTime   start time in milliseconds
     * @param stopTime    end time in milliseconds
     * @param warningTime warning threshold in seconds. If the threshold is set to 0, the execution time will never exceed, so the state will be always OK!
     * @throws SakuliException
     */
    @LogToResult(message = "add a step to the current test case")
    public void addTestCaseStep(String stepName, String startTime, String stopTime, int warningTime) throws SakuliException {
        if (stepName == null || stepName.isEmpty() || stepName.equals("undefined")) {
            handleException("Please set a Name - all values of the test case step need to be set!");
        }
        String errormsg = TestCaseStepHelper.checkWarningTime(warningTime, stepName);
        if (errormsg != null) {
            handleException(errormsg);
        }

        TestCaseStep step = findStep(stepName);
        try {
            step.setStartDate(new Date(Long.parseLong(startTime)));
            step.setStopDate(new Date(Long.parseLong(stopTime)));
            step.setWarningTime(warningTime);
        } catch (NullPointerException | NumberFormatException e) {
            loader.getExceptionHandler().handleException(e);
        }
        logger.debug("duration of the step \"" + stepName + "\": " + step.getDuration() + " sec.");
        step.refreshState();
        logger.debug("result of step \"" + stepName + "\": " + step.getState());
        loader.getCurrentTestCase().addStep(step);
        logger.debug("test case step \""
                + step.getName()
                + "\" saved to test case \""
                + loader.getCurrentTestCase().getId()
                + "\"");
    }

    protected TestCaseStep findStep(String stepName) {
        TestCaseStep newStep = new TestCaseStep();
        newStep.setId(stepName);
        for (TestCaseStep step : loader.getCurrentTestCase().getSteps()) {
            if (StringUtils.equals(step.getId(), newStep.getId())) {
                return step;
            }
        }
        return newStep;
    }

    /**
     * Creates a new test case based exception with an optional screenshot at the calling time.
     * Will be called from sakuli.js or in side of 'org.sakuli.javaDSL.AbstractSakuliTest'.
     *
     * @param message    error message
     * @param screenshot enable / disable screenshot functionality
     */
    @LogToResult(level = LogLevel.ERROR)
    public void throwException(String message, boolean screenshot) {
        loader.getExceptionHandler().handleException(screenshot ? new SakuliActionException(message) : new SakuliValidationException(message));
    }

    /**
     * calls the method {@link SakuliExceptionHandler#handleException(Throwable)}
     *
     * @param e the original exception
     */
    public void handleException(Throwable e) {
        loader.getExceptionHandler().handleException(e, false);
    }

    /**
     * @param exceptionMessage String message
     */
    public void handleException(String exceptionMessage) {
        loader.getExceptionHandler().handleException(exceptionMessage, false);
    }

    @Override
    public String toString() {
        if (loader != null && loader.getCurrentTestCase() != null) {
            return "test case [" + loader.getCurrentTestCase().getActionValueString() + "]";
        }
        return "test case not initialized";
    }

    /****************
     * TEST CASE INFO FUNCTIONS
     *********************/
    /**
     * @param pathToTestCaseFile path to the test case file "_tc.js"
     * @return returns test the currentTestCase Name
     */
    @LogToResult(message = "convert the path of the test case file to a valid test case ID")
    public String getIdFromPath(String pathToTestCaseFile) {
        logger.info("Return a test-case-id for \"" + pathToTestCaseFile + "\"");
        String id = TestCaseHelper.convertTestCaseFileToID(pathToTestCaseFile);
        //check id
        if (loader.getTestSuite().checkTestCaseID(id)) {
            logger.info("test-case-id = " + id);
            return id;
        } else {
            handleException("cannot identify testcase for pathToTestCaseFile=" + pathToTestCaseFile);
            return null;
        }
    }

    /**
     * @return String value of the last URL
     */
    @LogToResult(message = "return 'lastURL'")
    public String getLastURL() {
        return loader.getCurrentTestCase().getLastURL();
    }

    /**
     * Set a new URL to the current TestCase as last visited URL.
     *
     * @param lastURL String value of the last URL
     */
    @LogToResult(message = "set 'lastURL' to new value")
    public void setLastURL(String lastURL) {
        loader.getCurrentTestCase().setLastURL(lastURL);
    }

    /**
     * @return the folder path of the current testcase as {@link String}.
     */
    @LogToResult
    public String getTestCaseFolderPath() {
        try {
            return loader.getCurrentTestCase().getTcFile().getParent().toAbsolutePath().toString();
        } catch (Exception e) {
            handleException(new SakuliException(e,
                    String.format("cannot resolve the folder path of the current testcase '%s'",
                            loader.getCurrentTestCase())));
            return null;
        }
    }

    /**
     * @return the folder path of the current testsuite as {@link String}.
     */
    @LogToResult
    public String getTestSuiteFolderPath() {
        try {
            return loader.getTestSuite().getTestSuiteFolder().toAbsolutePath().toString();
        } catch (Exception e) {
            handleException(new SakuliException(e,
                    String.format("cannot resolve the folder path of the current testsuite '%s'",
                            loader.getTestSuite())));
            return null;
        }
    }

}
