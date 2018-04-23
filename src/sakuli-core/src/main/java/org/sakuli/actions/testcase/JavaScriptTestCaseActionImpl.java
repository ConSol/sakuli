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
import org.sakuli.actions.logging.LogToResultClassName;
import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.datamodel.helper.TestCaseHelper;
import org.sakuli.datamodel.helper.TestDataEntityHelper;
import org.sakuli.exceptions.SakuliActionException;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.exceptions.SakuliValidationException;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.TeardownServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * @author tschneck Date: 19.06.13
 */
//TODO TS move to package `sahi-setup`
@LogToResultClassName("TestCaseAction")
public class JavaScriptTestCaseActionImpl extends AbstractTestCaseActionImpl {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * save the Result of a test Case
     *
     * @param testCaseId  id of the corresponding test case
     * @param startTime   start time in milliseconds
     * @param stopTime    end time in milliseconds
     * @param lastURL     URL to the last visited page during this test case
     * @param browserInfo detail information about the used browser
     * @throws SakuliCheckedException
     */
    @LogToResult
    public void saveResult(String testCaseId, String startTime, String stopTime, String lastURL, String browserInfo, boolean forward) throws SakuliCheckedException {
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
        if (forward) {
            forwardTestDataEntity(tc);
        }
    }

    /**
     * Wrapper for {@link #addTestCaseStep(String, String, String, int, int, boolean)} with warningTime '0'.
     */
    public void addTestCaseStep(String stepName, String startTime, String stopTime) throws SakuliCheckedException {
        addTestCaseStep(stepName, startTime, stopTime, 0, 0, false);
    }

    /**
     * Save a new step to a existing test case. Must be called before {@link #saveResult(String, String, String, String, String, boolean)}
     *
     * @param stepName     name of this step
     * @param startTime    start time in milliseconds
     * @param stopTime     end time in milliseconds
     * @param warningTime  warning threshold in seconds. If the threshold is set to 0, the execution time will never exceed, so the state will be always OK!
     * @param criticalTime critical threshold in seconds. If the threshold is set to 0, the execution time will never exceed, so the state will be always OK!
     * @param forward      boolean flag indicating whether the result of the test case shall be immediately processed by the enabled forwarders. This means before the test suite has been executed to the end. If not specified in another way, this option is disabled!
     * @throws SakuliCheckedException
     */
    @LogToResult(message = "TestCase.endOfStep()", logClassInstance = false)
    public void addTestCaseStep(String stepName, String startTime, String stopTime, int warningTime, int criticalTime, boolean forward) throws SakuliCheckedException {
        if (stepName == null || stepName.isEmpty() || stepName.equals("undefined")) {
            handleException("Please set a Name - all values of the test case step need to be set!");
        }
        String errormsg = TestDataEntityHelper.checkWarningAndCriticalTime(warningTime, criticalTime, String.format("TestCaseStep [name = %s]", stepName));
        if (errormsg != null) {
            handleException(errormsg);
        }

        TestCaseStep step = findStep(stepName);
        try {
            step.setStartDate(new Date(Long.parseLong(startTime)));
            step.setStopDate(new Date(Long.parseLong(stopTime)));
            step.setWarningTime(warningTime);
            step.setCriticalTime(criticalTime);

            step.addActions(loader.getCurrentTestCase().getAndResetTestActions());
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
        if (forward) {
            forwardTestDataEntity(step);
        }
    }

    //TODO TS maybe this also for JavaDSL needed
    private void forwardTestDataEntity(AbstractTestDataEntity abstractTestDataEntity) {
        BeanLoader.loadBean(ExecutorService.class).submit(() -> {
            logger.info("======= TRIGGER ASYNC teardown of: {} =======", abstractTestDataEntity.toStringShort());
            TeardownServiceHelper.invokeTeardownServices(abstractTestDataEntity, true);
        });
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
    @LogToResult(message = "determine a valid test case ID from the test case file path", logClassInstance = false)
    public String getIdFromPath(String pathToTestCaseFile) {
        logger.debug("determine a ID for testcase for file path '{}'", pathToTestCaseFile);
        String id = TestCaseHelper.convertTestCaseFileToID(pathToTestCaseFile);
        //check id
        if (loader.getTestSuite().checkTestCaseID(id)) {
            logger.debug("determined testcase id: {}", id);
            return id;
        } else {
            handleException("cannot determine testcase id for path of testcase file '" + pathToTestCaseFile + "'");
            return null;
        }
    }

    /**
     * @return String value of the last URL
     */
    @LogToResult
    public String getLastURL() {
        return loader.getCurrentTestCase().getLastURL();
    }

    /**
     * Set a new URL to the current TestCase as last visited URL.
     *
     * @param lastURL String value of the last URL
     */
    @LogToResult
    public void setLastURL(String lastURL) {
        loader.getCurrentTestCase().setLastURL(lastURL);
    }


    @Override
    public void addImagePathsAsString(String... imagePaths) throws SakuliCheckedException {
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
