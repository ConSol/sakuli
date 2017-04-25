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

package org.sakuli.actions.testcase;

import org.sakuli.actions.logging.LogToResult;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.datamodel.helper.TestCaseHelper;
import org.sakuli.datamodel.helper.TestCaseStepHelper;
import org.sakuli.exceptions.SakuliActionException;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Date;

/**
 * @author tschneck Date: 19.06.13
 */
//TODO move to package `sahi-setup`
//@Component
public class JavaScriptTestCaseActionImpl extends AbstractTestCaseActionImpl {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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


    @Override
    public void addImagePathsAsString(String... imagePaths) throws SakuliException {
        for (String path : imagePaths) {
            //check if absolute path
            if (!path.matches("(\\/\\S*|\\w:\\\\\\S*)")) {
                addImagePaths(loader.getCurrentTestCase().getTcFile().getParent().resolve(path));
            } else {
                addImagePaths(Paths.get(path));
            }
        }
    }
}
