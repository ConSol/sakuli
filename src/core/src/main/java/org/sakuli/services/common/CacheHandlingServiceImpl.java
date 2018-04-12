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

package org.sakuli.services.common;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.helper.TestCaseStepHelper;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.services.TeardownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The CacheHandlingResultService will manage the cached steps definitions in the file `steps.cache`.
 * If the {@link org.sakuli.datamodel.TestSuite#state} is NOT {@link TestSuiteState#ERRORS} or {@link TestSuiteState#RUNNING} it will
 * remove all present not called step definitions and persist its.
 * This Service MUST be called as first Service before all other {@link org.sakuli.services.ResultService}s
 *
 * @author tschneck
 */
@Component
public class CacheHandlingServiceImpl implements TeardownService {

    private static Logger LOGGER = LoggerFactory.getLogger(CacheHandlingServiceImpl.class);
    @Autowired
    protected SakuliExceptionHandler exceptionHandler;

    @Override
    public int getServicePriority() {
        return 100;
    }

    @Override
    public void teardownTestSuite(@NonNull TestSuite testSuite) {
        if (testSuite.getState() != null && testSuite.getState().isFinishedWithoutErrors()) {
            removeCachedInitSteps(testSuite);
            writeCachedStepDefinitions(testSuite);
        }
    }

    @Override
    public void teardownTestCase(@NonNull TestCase testCase) {
        //Not needed
    }

    @Override
    public void teardownTestCaseStep(@NonNull TestCaseStep testCaseStep) {
        //Not needed
    }

    protected void removeCachedInitSteps(TestSuite testSuite) {
        for (TestCase tc : testSuite.getTestCases().values()) {
            List<TestCaseStep> filteredSteps = new ArrayList<>();
            for (TestCaseStep step : tc.getSteps()) {
                if (step.getState() != null && step.getState().isFinishedWithoutErrors()) {
                    filteredSteps.add(step);
                } else {
                    LOGGER.debug("remove cached and not called step '{}'", step.getId());
                }
            }
            tc.setSteps(filteredSteps);
        }
    }

    protected void writeCachedStepDefinitions(TestSuite testSuite) {
        try {
            TestCaseStepHelper.writeCachedStepDefinitions(testSuite);
        } catch (IOException e) {
            exceptionHandler.handleException(
                    new SakuliRuntimeException("Can't create cache file(s) for test case steps!", e), true);
        }
    }
}
