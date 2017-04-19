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

import org.apache.commons.lang3.StringUtils;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.helper.TestSuiteHelper;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.services.InitializingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author tschneck Date: 22.05.14
 */
@Component
public class CommonInitializingServiceImpl implements InitializingService {
    private static Logger logger = LoggerFactory.getLogger(InitializingService.class);
    @Autowired
    protected TestSuite testSuite;
    @Autowired
    protected TestSuiteProperties testSuiteProperties;
    @Autowired
    protected SakuliProperties sakuliProperties;

    static void checkConfiguration(TestSuite testSuite) throws SakuliInitException {
        if (StringUtils.isBlank(testSuite.getId())) {
            throw new SakuliInitException(String.format(
                    "The property '%s' have not been set. Please check your '%s' file",
                    TestSuiteProperties.SUITE_ID,
                    TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_NAME));
        }
    }

    @Override
    public int getServicePriority() {
        return 100;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initTestSuite() throws SakuliInitException {
        checkConfiguration(testSuite);
        logger.info("initialize test suite with id '{}'", testSuite.getId());
        testSuite.clearException();
        testSuite.setState(TestSuiteState.RUNNING);
        testSuite.setStartDate(new Date());
        testSuite.setStopDate(null);

        try {
            testSuite.setHost(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            testSuite.setHost("UNKNOWN HOST");
        }

        /**
         * Optional init actions
         */
        if (testSuiteProperties.isLoadTestCasesAutomatic()) {
            try {
                testSuite.setTestCases(null);
                testSuite.setTestCases(TestSuiteHelper.loadTestCases(testSuiteProperties));
            } catch (IOException e) {
                throw new SakuliInitException(e, String.format("Cannot read testsuite.suite '%s' file", testSuiteProperties.getTestSuiteSuiteFile().toString()));
            }
        }
        logger.info("test suite with guid '{}' has been initialized!", testSuite.getGuid());
        logger.debug("test suite data after initialization: {}", testSuite.toString());
    }
}
