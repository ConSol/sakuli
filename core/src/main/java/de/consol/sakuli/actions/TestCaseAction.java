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

package de.consol.sakuli.actions;

import de.consol.sakuli.actions.logging.LogToResult;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestCaseStep;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.helper.TestCaseHelper;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.loader.BaseActionLoader;
import de.consol.sakuli.loader.BaseActionLoaderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Date;

/**
 * @author tschneck
 *         Date: 19.06.13
 */
@Component
public class TestCaseAction {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Represents the current running TestCase.
     * The object will be set at {@link #init(String, int, int, String...)}
     * and releases at {@link #saveResult(String, String, String, String, String)}
     */
    @Autowired
    @Qualifier(BaseActionLoaderImpl.QUALIFIER)
    private BaseActionLoader loader;

    /****************
     * Init functions for the java script engine.
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
     * Set the warning and critical Time to the specific test case.
     *
     * @param testCaseID   current ID of the test case
     * @param warningTime  warning threshold in seconds
     * @param criticalTime critical threshold in seconds
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
     * @param warningTime  warning threshold in seconds
     * @param criticalTime critical threshold in seconds
     * @param imagePaths   multiple paths to images
     */
    @LogToResult(message = "init a new test case")
    public void init(String testCaseID, int warningTime, int criticalTime, Path... imagePaths) {
        loader.init(testCaseID, imagePaths);
        initWarningAndCritical(warningTime, criticalTime);
    }


    private void initWarningAndCritical(int warningTime, int criticalTime) {
        //check some stuff then set the times
        if (warningTime <= 0 || criticalTime <= 0) {
            handleException("waring time and critical time must be greater then 0!");
        } else {
            if (warningTime > criticalTime) {
                handleException("warning threshold must be less than critical threshold!");
            } else {
                //if everything is ok set the times
                loader.getCurrentTestCase().setWarningTime(warningTime);
                loader.getCurrentTestCase().setCriticalTime(criticalTime);
            }
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
                    "Check if method \"backend.initializeNagios(warningTime, criticalTime)\"" +
                    " have been called in your test case.  " +
                    "=> START date: " + startTime
                    + "\tSTOP date: " + stopTime
                    + "\n" + e.getMessage());
        }

        //release current test case -> indicates that this case is finished
        loader.setCurrentTestCase(null);
    }

    /**
     * Save a new step to a existing test case.
     * Must be called before {@link #saveResult(String, String, String, String, String)}
     *
     * @param stepName    name of this step
     * @param startTime   start time in milliseconds
     * @param stopTime    end time in milliseconds
     * @param warningTime warning threshold in seconds
     * @throws SakuliException
     */
    @LogToResult(message = "add a step to the current test case")
    public void addTestCaseStep(String stepName, String startTime, String stopTime, int warningTime) throws SakuliException {

        /***
         * check if {@link #initTestCaseWarningAndCritical(String, int, int)}  has been executed
         *
         */
        if (loader.getCurrentTestCase().getWarningTime() < 0
                || loader.getCurrentTestCase().getCriticalTime() < 0) {
            handleException("warning and critical time have not been set!!! " +
                    "Please add the function \"initializeNagios(warningTime,criticalTime)\" to your test case !!!" +
                    "\t If warning and critical times should be  NOT considered, please set it to 0");
        }

        if (stepName.isEmpty() || stepName.equals("undefined")) {
            handleException("Please set a Name - all values of the test case step need to be set!");
        }
        if (warningTime < 0) {
            handleException("warning time of test case step need to be greater or equal then 0!");
        }

        //create a new step
        TestCaseStep step = new TestCaseStep();
        try {

            step.setName(stepName.replace(" ", "_"));
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


    /**
     * ******** EXCEPTION HANDLING ******************
     */

    /**
     * calls the method {@link SakuliExceptionHandler#handleException(Throwable)}
     *
     * @param e the original exception
     */
    public void handleException(Throwable e) {
//        logger.debug("Java Backend - handle Throwable WITH the testcase id '" + loader.getCurrentTestCase().getId() + "'");
        loader.getExceptionHandler().handleException(e, false);
    }

    /**
     * @param exceptionMessage String message
     */
    public void handleException(String exceptionMessage) {
//        logger.debug("Java Backend - handle exception from String input WITH the testcase id '" + loader.getCurrentTestCase().getId() + "' and message: " + exceptionMessage);
        loader.getExceptionHandler().handleException(exceptionMessage, false);
    }

    @Override
    public String toString() {
        if (loader != null && loader.getCurrentTestCase() != null) {
            return "test case [" + loader.getCurrentTestCase().getActionValueString() + "]";
        }
        return "test case not initialized";
    }
}
