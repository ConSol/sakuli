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

package de.consol.sakuli.services.receiver.database;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliReceiverException;
import de.consol.sakuli.services.common.AbstractResultService;
import de.consol.sakuli.services.receiver.database.dao.DaoTestCase;
import de.consol.sakuli.services.receiver.database.dao.DaoTestCaseStep;
import de.consol.sakuli.services.receiver.database.dao.DaoTestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author tschneck
 *         Date: 09.07.14
 */
@ProfileJdbcDb
@Component
public class DatabaseResultServiceImpl extends AbstractResultService {
    private static Logger logger = LoggerFactory.getLogger(DatabaseResultServiceImpl.class);

    @Autowired
    private DaoTestCase daoTestCase;
    @Autowired
    private DaoTestCaseStep daoTestCaseStep;
    @Autowired
    private DaoTestSuite daoTestSuite;
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private SakuliExceptionHandler exceptionHandler;

    @Override
    public int getServicePriority() {
        return 10;
    }

    @Override
    public void saveAllResults() {
        logger.info("try to save all results to the database");
        try {
            daoTestSuite.saveTestSuiteResult();
            daoTestSuite.saveTestSuiteToSahiJobs();

            if (!CollectionUtils.isEmpty(testSuite.getTestCases())) {
                for (TestCase tc : testSuite.getTestCases().values()) {
                    //write testcase and steps to DB
                    daoTestCase.saveTestCaseResult(tc);

                    logger.info("... try to save all STEPS for test case '" + tc.getId() + "'!");
                    if (!CollectionUtils.isEmpty(tc.getSteps())) {
                        daoTestCaseStep.saveTestCaseSteps(tc.getSteps(), tc.getDbPrimaryKey());
                        logger.info("all STEPS for '" + tc.getId() + "' saved!");
                    } else {
                        logger.info("no STEPS for '\" + tc.getId() +\"'found => no STEPS saved in DB!");
                    }
                }
            }
        } catch (Throwable e) {
            exceptionHandler.handleException(
                    new SakuliReceiverException(e,
                            String.format("error by saving the results to the database [%s]", testSuite.toString())),
                    true);
        }
    }
}
