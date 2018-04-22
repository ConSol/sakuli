/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

import org.apache.commons.lang.StringUtils;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.helper.TestDataEntityHelper;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;

import java.nio.file.Path;

/**
 * @author tschneck
 * Date: 4/25/17
 */
public abstract class AbstractTestCaseActionImpl implements TestCaseAction {
    /**
     * Represents the current running TestCase. The object will be set at {@link #init(String, int, int, String...)} and
     */

    protected BaseActionLoader loader;

    public AbstractTestCaseActionImpl() {
//        System.out.println("call??????");
        loader = BeanLoader.loadBaseActionLoader();
    }

    @Override
    @LogToResult(message = "init a new test case", logClassInstance = false)
    public void init(String testCaseID, int warningTime, int criticalTime, String... imagePaths) {
        loader.init(testCaseID, imagePaths);
        initWarningAndCritical(warningTime, criticalTime);
    }


    @LogToResult(message = "init a new test case with caseID", logClassInstance = false)
    public void initWithCaseID(String testCaseID, String newTestCaseID, int warningTime, int criticalTime, String... imagePaths) {
        loader.init(testCaseID, imagePaths);
        loader.getCurrentTestCase().setId(newTestCaseID);
        initWarningAndCritical(warningTime, criticalTime);
    }

    @Override
    @LogToResult(message = "init a new test case")
    public void initWithPaths(String testCaseID, int warningTime, int criticalTime, Path... imagePaths) {
        loader.init(testCaseID, imagePaths);
        initWarningAndCritical(warningTime, criticalTime);
    }


    @Override
    @LogToResult
    public void addImagePaths(Path... imagePaths) throws SakuliCheckedException {
        loader.addImagePaths(imagePaths);
    }

    protected void initWarningAndCritical(int warningTime, int criticalTime) {
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

    @Override
    public void handleException(Exception e) {
        loader.getExceptionHandler().handleException(e, false);
    }


    @Override
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

    @Override
    @LogToResult
    public String getTestCaseFolderPath() {
        try {
            return loader.getCurrentTestCase().getTcFolder().toAbsolutePath().toString();
        } catch (Exception e) {
            handleException(new SakuliCheckedException(e,
                    String.format("cannot resolve the folder path of the current testcase '%s'",
                            loader.getCurrentTestCase())));
            return null;
        }
    }

    @Override
    @LogToResult
    public String getTestSuiteFolderPath() {
        try {
            return loader.getTestSuite().getTestSuiteFolder().toAbsolutePath().toString();
        } catch (Exception e) {
            handleException(new SakuliCheckedException(e,
                    String.format("cannot resolve the folder path of the current testsuite '%s'",
                            loader.getTestSuite())));
            return null;
        }
    }
}
