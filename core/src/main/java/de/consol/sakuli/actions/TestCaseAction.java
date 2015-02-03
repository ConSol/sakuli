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
 * @author tschneck Date: 19.06.13
 */
@Component
public class TestCaseAction {
    public static final String ERROR_NOT_SET_THRESHOLD_VARIABLE = "the %s threshold have to be set! If the %s threshold should NOT be considered, please set it to 0!";
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
    public void initWithPaths(String testCaseID, int warningTime, int criticalTime, Path... imagePaths) {
        loader.init(testCaseID, imagePaths);
        initWarningAndCritical(warningTime, criticalTime);
    }

    private void initWarningAndCritical(int warningTime, int criticalTime) {
        if (checkWarningAndCrititicalTime(warningTime, criticalTime)) {
            //if everything is ok set the times
            loader.getCurrentTestCase().setWarningTime(warningTime);
            loader.getCurrentTestCase().setCriticalTime(criticalTime);
        }
    }

    /**
     * Check if the warning time is set correctly:
     * <ul>
     * <li>Greater or equal then 0</li>
     * <li>warning time > critical time</li>
     * </ul>
     *
     * @return true on success, on error - call {@link #handleException(String)} and return false!
     */
    private boolean checkWarningAndCrititicalTime(int warningTime, int criticalTime) {
        if (criticalTime < 0) {
            handleException(getErrorNotSetTimeVariableMessage("critical"));
        } else if (warningTime < 0) {
            handleException(getErrorNotSetTimeVariableMessage("warning"));
        } else {
            if (warningTime > criticalTime) {
                handleException("warning threshold must be less than critical threshold!");
            } else {
                return true;
            }
        }
        return false;
    }

    private String getErrorNotSetTimeVariableMessage(String thersholdName) {
        return String.format(ERROR_NOT_SET_THRESHOLD_VARIABLE, thersholdName, thersholdName);
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
     * Save a new step to a existing test case. Must be called before {@link #saveResult(String, String, String, String,
     * String)}
     *
     * @param stepName    name of this step
     * @param startTime   start time in milliseconds
     * @param stopTime    end time in milliseconds
     * @param warningTime warning threshold in seconds
     * @throws SakuliException
     */
    @LogToResult(message = "add a step to the current test case")
    public void addTestCaseStep(String stepName, String startTime, String stopTime, int warningTime) throws SakuliException {

        checkWarningAndCrititicalTime(loader.getCurrentTestCase().getWarningTime(), loader.getCurrentTestCase().getCriticalTime());
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

    /****************
     * EXCEPTION HANDLING
     *********************/

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
