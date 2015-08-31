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

import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.services.forwarder.database.ProfileJdbcDb;
import org.sakuli.services.forwarder.database.dao.DaoTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tschneck
 *         Date: 17.06.13
 */
@ProfileJdbcDb
@Component
public class DaoTestCaseImpl extends Dao implements DaoTestCase {

    @Autowired
    public DaoTestCaseImpl(DataSource dataSource) throws SakuliException {
        super(dataSource);
    }

    @Override
    public void saveTestCaseResult(final TestCase testCase) {
        logger.info("Save results for test case \"" + testCase.getId() + "\"");

        //create a map for the sql parameters
        MapSqlParameterSource tcParameters = new MapSqlParameterSource();
        tcParameters.addValue("sakuli_suites_id", testSuite.getDbPrimaryKey());
        tcParameters.addValue("caseID", testCase.getId());
        tcParameters.addValue("result", testCase.getState().getErrorCode());
        tcParameters.addValue("result_desc", testCase.getState());
        tcParameters.addValue("name", testCase.getName());
        tcParameters.addValue("guid", testSuite.getGuid());
        tcParameters.addValue("start", testCase.getStartDateAsUnixTimestamp());
        tcParameters.addValue("stop", testCase.getStopDateAsUnixTimestamp());
        int warningTime = testCase.getWarningTime();
        tcParameters.addValue("warning", (warningTime != 0) ? warningTime : null);
        int criticalTime = testCase.getCriticalTime();
        tcParameters.addValue("critical", (criticalTime != 0) ? criticalTime : null);
        tcParameters.addValue("browser", testSuite.getBrowserInfo());
        tcParameters.addValue("lastpage", testCase.getLastURL());

        //try to save the screenshot
        try {
            if (testCase.getScreenShotPath() != null) {
                final InputStream blobIs = Files.newInputStream(testCase.getScreenShotPath());
                final int length = (int) testCase.getScreenShotPath().toFile().length();
                tcParameters.addValue("screenshot", new SqlLobValue(blobIs, length, lobHandler), Types.BLOB);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tcParameters.addValue("duration", testCase.getDuration());
        tcParameters.addValue("msg", testCase.getExceptionMessages());

        //generate the sql-statement
        SimpleJdbcInsert insertTCResults = new SimpleJdbcInsert(getDataSource())
                .withTableName("sakuli_cases")
                .usingGeneratedKeyColumns("id");


        logger.info("write the following values to 'sakuli_cases': "
                + tcParameters.getValues()
                + " => now execute ....");

        int dbPrimaryKey = insertTCResults.executeAndReturnKey(tcParameters).intValue();

        logger.info("test case '" + testCase.getId()
                + "' has been written to 'sakuli_cases' with  primaryKey=" + dbPrimaryKey);
        testCase.setDbPrimaryKey(dbPrimaryKey);
    }

    @Override
    public int getCountOfSahiCases() {
        return this.getJdbcTemplate().queryForObject("select count(*) from sakuli_cases", Integer.class);
    }

    @Deprecated
    @Override
    public File getScreenShotFromDB(int dbPrimaryKey) {
        try {
            List l = getJdbcTemplate().query("select id, screenshot from sakuli_cases where id=" + dbPrimaryKey,
                    (rs, i) -> {
                        Map results = new HashMap();
                        InputStream blobBytes = lobHandler.getBlobAsBinaryStream(rs, "screenshot");
                        results.put("BLOB", blobBytes);
                        return results;
                    }
            );
            HashMap<String, InputStream> map = (HashMap<String, InputStream>) l.get(0);

            //ByteArrayInputStream in = new ByteArrayInputStream(map.get("BLOB"));
            BufferedImage picBuffer = ImageIO.read(map.get("BLOB"));
            File png = new File(testSuite.getAbsolutePathOfTestSuiteFile().substring(0, testSuite.getAbsolutePathOfTestSuiteFile().lastIndexOf(File.separator)) + File.separator + "temp_junit_test.png");
            png.createNewFile();
            ImageIO.write(picBuffer, "png", png);

            png.deleteOnExit();
            return png;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
