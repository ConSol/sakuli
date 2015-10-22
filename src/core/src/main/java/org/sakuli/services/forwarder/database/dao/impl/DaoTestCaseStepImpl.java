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

package org.sakuli.services.forwarder.database.dao.impl;

import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.services.forwarder.database.ProfileJdbcDb;
import org.sakuli.services.forwarder.database.dao.DaoTestCaseStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.SortedSet;

/**
 * @author tschneck
 *         Date: 12.07.13
 */
@ProfileJdbcDb
@Component
public class DaoTestCaseStepImpl extends Dao implements DaoTestCaseStep {

    @Autowired
    public DaoTestCaseStepImpl(DataSource dataSource) throws SakuliException {
        super(dataSource);
    }

    @Override
    public void saveTestCaseSteps(SortedSet<TestCaseStep> steps, int primaryKeyOfTestCase) {
        for (TestCaseStep step : steps) {
            logger.info("============== save STEP \"" + step.getName() + "\" ==============");
            MapSqlParameterSource stepParameters = new MapSqlParameterSource();
            stepParameters.addValue("sakuli_cases_id", primaryKeyOfTestCase);
            stepParameters.addValue("result", step.getState().getErrorCode());
            stepParameters.addValue("result_desc", step.getState());
            stepParameters.addValue("name", step.getName());
            stepParameters.addValue("start", step.getStartDateAsUnixTimestamp());
            stepParameters.addValue("stop", step.getStopDateAsUnixTimestamp());
            int warningTime = step.getWarningTime();
            stepParameters.addValue("warning", (warningTime != 0) ? warningTime : null);
            stepParameters.addValue("duration", step.getDuration());

            logger.info("write the following values to 'sakuli_steps': "
                    + stepParameters.getValues()
                    + "\n now execute ....");

            //generate the sql-statement
            SimpleJdbcInsert insertStepResults = new SimpleJdbcInsert(getDataSource())
                    .withTableName("sakuli_steps")
                    .usingGeneratedKeyColumns("id");

            //execute the sql-statement and save the primary key
            int dbPrimaryKey = insertStepResults.executeAndReturnKey(stepParameters).intValue();
            logger.info("test case step '" + step.getName()
                    + "' has been written to 'sakuli_steps' with  primaryKey=" + dbPrimaryKey);
            step.setDbPrimaryKey(dbPrimaryKey);
        }
    }
}
