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

package de.consol.sakuli.services.database;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.services.ResultService;
import de.consol.sakuli.services.database.dao.DaoTestCase;
import de.consol.sakuli.services.database.dao.DaoTestCaseStep;
import de.consol.sakuli.services.database.dao.DaoTestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author tschneck
 *         Date: 09.07.14
 */
@Profile("jdbc-db")
@Component
public class DatabaseResultServiceImpl implements ResultService {
    private static Logger logger = LoggerFactory.getLogger(DatabaseResultServiceImpl.class);

    @Autowired
    private DaoTestCase daoTestCase;
    @Autowired
    private DaoTestCaseStep daoTestCaseStep;
    @Autowired
    private DaoTestSuite daoTestSuite;
    @Autowired
    private TestSuite testSuite;

    @Override
    public void saveAllResults() {
        logger.info("try to save all results to the database");
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
    }
}
