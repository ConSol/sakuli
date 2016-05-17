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

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.utils.CleanUpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * The common result service will be called every time after the test suite execution.
 * Currently the result will be only logged.
 *
 * @author tschneck
 */
@Component
public class CommonResultServiceImpl extends AbstractResultService {
    private static Logger LOGGER = LoggerFactory.getLogger(CommonResultServiceImpl.class);

    private SakuliProperties sakuliProperties;

    @Autowired
    public CommonResultServiceImpl(SakuliProperties sakuliProperties) {
        this.sakuliProperties = sakuliProperties;
    }

    @Override
    public int getServicePriority() {
        return 0;
    }

    @Override
    public void saveAllResults() {
        cleanUp();
        LOGGER.info(testSuite.getResultString(sakuliProperties)
                + "\n===========  SAKULI Testsuite \"" + testSuite.getId() + "\" execution FINISHED - "
                + testSuite.getState() + " ======================\n");
        switch (testSuite.getState()) {
            case WARNING_IN_CASE:
                logTestCaseStateDetailInfo(tc -> tc.getState().isWarning());
                break;
            case WARNING_IN_STEP:
                logTestCaseStateDetailInfo(tc -> tc.getState().isWarningInStep());
                break;
            case CRITICAL_IN_CASE:
                logTestCaseStateDetailInfo(tc -> tc.getState().isCritical());
                break;
            case ERRORS:
                String errorMsg = "ERROR:\n" + testSuite.getExceptionMessages(false, sakuliProperties.getLogExceptionFormatMappings());
                LOGGER.error(errorMsg + "\n");
                break;
        }
    }

    private void logTestCaseStateDetailInfo(Predicate<TestCase> predicate) {
        testSuite.getTestCases().values().stream()
                .filter(predicate)
                .forEach(tc -> {
                    if (tc.getState().isWarningInStep()) {
                        tc.getSteps().stream()
                                .filter(s -> s.getState().isWarning())
                                .forEach(s -> LOGGER.warn("{}: {}", testSuite.getState(), tc.getName() + " -> " + s.getName()));
                    } else {
                        LOGGER.warn("{}: {}", testSuite.getState(), tc.getName());
                    }
                });
    }

    public void cleanUp() {
        CleanUpHelper.cleanClipboard();
        CleanUpHelper.releaseAllModifiers();
    }
}
