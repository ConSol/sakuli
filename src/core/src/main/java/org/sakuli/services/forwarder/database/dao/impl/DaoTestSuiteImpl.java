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

import org.sakuli.exceptions.SakuliException;
import org.sakuli.services.forwarder.database.ProfileJdbcDb;
import org.sakuli.services.forwarder.database.dao.DaoTestSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Types;

/**
 * @author tschneck
 *         Date: 12.07.13
 */
@ProfileJdbcDb
@Component
public class DaoTestSuiteImpl extends Dao implements DaoTestSuite {

    @Autowired
    public DaoTestSuiteImpl(DataSource dataSource) throws SakuliException {
        super(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertInitialTestSuiteData() {
        logger.info("Build SQL query for new primary key in table 'sakuli_suites'");

        testSuite.refreshState();
        MapSqlParameterSource tcParameters = getInitialDataParameters();
        logger.info("write the following values to 'sakuli_suites': "
                + tcParameters.getValues()
                + " ==>  now execute ....");
        SimpleJdbcInsert insertInitialSuiteData = new SimpleJdbcInsert(getDataSource())
                .withTableName("sakuli_suites")
                .usingGeneratedKeyColumns("id");

        int dbPrimaryKey = insertInitialSuiteData.executeAndReturnKey(tcParameters).intValue();

        logger.info("test suite \"" + testSuite.getId()
                + "\" has been written to 'sakuli_suites' with  primaryKey=" + dbPrimaryKey);
        return dbPrimaryKey;
    }

    @Override
    public void saveTestSuiteResult() {
        testSuite.refreshState();
        logger.info("save the results of the test suite to the table 'sakuli_suites'");


        MapSqlParameterSource tcParameters = getCompleteParameters();
        logger.info("write the following values to 'sakuli_suites': "
                + tcParameters.getValues()
                + " ==>  now execute ....");
        String sqlStatement =
                "UPDATE sakuli_suites "
                        + createSqlSetStringForNamedParameter(tcParameters.getValues())
                        + " where id=:id";

        logger.debug("SQL-Statement for update 'sakuli_suites': " + sqlStatement);
        int affectedRows = getNamedParameterJdbcTemplate().update(sqlStatement, tcParameters);
        logger.info("update 'sakuli_suites' affected " + affectedRows + " rows");
    }

    @Override
    public int saveTestSuiteToSahiJobs() {
        logger.info("save the guid to the table 'sakuli_jobs'");
        //build up the statement
        MapSqlParameterSource tcParameters = getGuidParameter();
        logger.info("write the following values to 'sakuli_jobs': "
                + tcParameters.getValues()
                + " ==>  now execute ....");
        SimpleJdbcInsert insertTS = new SimpleJdbcInsert(getDataSource())
                .withTableName("sakuli_jobs")
                .usingGeneratedKeyColumns("id");
        testSuite.setDbJobPrimaryKey(insertTS.executeAndReturnKey(tcParameters).intValue());
        logger.info("the test suite \"" + testSuite.getId() + "\""
                + "with the guid \"" + testSuite.getGuid()
                + "\" has been written to 'sakuli_jobs' with  primaryKey=" + testSuite.getDbJobPrimaryKey());
        return testSuite.getDbJobPrimaryKey();
    }

    @Override
    public int getCountOfSahiJobs() {
        return this.getJdbcTemplate().queryForObject("select count(*) from sakuli_jobs", Integer.class);
    }

    private MapSqlParameterSource getInitialDataParameters() {
        MapSqlParameterSource tcParameters = getGuidParameter();
        tcParameters.addValue("id", testSuite.getDbPrimaryKey());
        tcParameters.addValue("suiteID", testSuite.getId());
        if (testSuite.getState() != null) {
            tcParameters.addValue("result", testSuite.getState().getErrorCode());
            tcParameters.addValue("result_desc", testSuite.getState().toString());
        }
        tcParameters.addValue("name", testSuite.getName());
        tcParameters.addValue("browser", testSuite.getBrowserInfo());
        tcParameters.addValue("host", testSuite.getHost());
        tcParameters.addValue("start", testSuite.getStartDateAsUnixTimestamp());
        return tcParameters;
    }

    private MapSqlParameterSource getGuidParameter() {
        return new MapSqlParameterSource().addValue("guid", testSuite.getGuid());
    }

    private MapSqlParameterSource getCompleteParameters() {
        MapSqlParameterSource tcParameters = getInitialDataParameters();
        tcParameters.addValues(getGuidParameter().getValues());
        tcParameters.addValue("stop", testSuite.getStopDateAsUnixTimestamp());
        int warningTime = testSuite.getWarningTime();
        tcParameters.addValue("warning", (warningTime != 0) ? warningTime : null);
        int criticalTime = testSuite.getCriticalTime();
        tcParameters.addValue("critical", (criticalTime != 0) ? criticalTime : null);
        tcParameters.addValue("duration", testSuite.getDuration());
        //try to save the screenshot
        try {
            if (testSuite.getScreenShotPath() != null) {
                final InputStream blobIs = Files.newInputStream(testSuite.getScreenShotPath());
                final int length = (int) testSuite.getScreenShotPath().toFile().length();
                tcParameters.addValue("screenshot", new SqlLobValue(blobIs, length, lobHandler), Types.BLOB);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tcParameters.addValue("msg", testSuite.getExceptionMessages());
        return tcParameters;
    }
}
