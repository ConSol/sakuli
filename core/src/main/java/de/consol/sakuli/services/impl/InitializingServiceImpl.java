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

package de.consol.sakuli.services.impl;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.helper.TestSuiteHelper;
import de.consol.sakuli.datamodel.properties.SakuliProperties;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.services.InitializingService;
import de.consol.sakuli.services.dao.DaoTestSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author tschneck
 *         Date: 22.05.14
 */
@Service
public class InitializingServiceImpl implements InitializingService {

    @Autowired
    private TestSuite testSuite;
    @Autowired
    private TestSuiteProperties testSuiteProperties;
    @Autowired
    private SakuliProperties sakuliProperties;
    @Autowired
    private DaoTestSuite daoTestSuite;


    /**
     * {@inheritDoc}
     */
    @Override
    public void initTestSuite() throws FileNotFoundException {
        logger.info("initialize test suite with id '{}'", testSuite.getId());
        testSuite.setState(TestSuiteState.RUNNING);
        testSuite.setStartDate(new Date());

        try {
            testSuite.setHost(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            testSuite.setHost("UNKNOWN HOST");
        }

        /**
         * Optional init actions
         */
        if (testSuiteProperties.isLoadTestCasesAutomatic()) {
            testSuite.setTestCases(TestSuiteHelper.loadTestCases(testSuiteProperties));
        }
        if (sakuliProperties.isPersistInDatabaseEnabled()) {
            testSuite.setDbPrimaryKey(daoTestSuite.insertInitialTestSuiteData());
        }
        logger.info("test suite with guid '{}' has been initialized!", testSuite.getGuid());
        logger.debug("test suite data after initialization: {}", testSuite.toString());
    }
}
