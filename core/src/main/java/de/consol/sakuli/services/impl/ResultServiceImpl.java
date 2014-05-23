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

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.services.ResultService;
import de.consol.sakuli.services.dao.DaoTestCase;
import de.consol.sakuli.services.dao.DaoTestCaseStep;
import de.consol.sakuli.services.gearman.GearmanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


/**
 * @author tschneck
 *         Date: 23.05.14
 */
@Component
public class ResultServiceImpl extends AbstractServiceImpl implements ResultService {

    private static Logger logger = LoggerFactory.getLogger(ResultService.class);

    @Autowired
    private DaoTestCase daoTestCase;
    @Autowired
    private DaoTestCaseStep daoTestCaseStep;

    @Autowired
    private GearmanService gearmanService;

    @Override
    public void saveAllResults() {
        //TODO TS write tests,
        // TODO TS then implement gearman service
        if (sakuliProperties.isSendToGearmanQueueEnabled()) {
            gearmanService.sendToNagios(testSuite);
        }
        if (sakuliProperties.isPersistInDatabaseEnabled()) {
            saveResultsInDatabase();
        }
    }

    /**
     * saves all results into the configured database
     */
    protected void saveResultsInDatabase() {
        //after the execution,save the test suite
        daoTestSuite.updateTestSuiteResult();
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
