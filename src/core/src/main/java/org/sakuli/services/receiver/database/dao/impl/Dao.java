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

package org.sakuli.services.receiver.database.dao.impl;

import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobHandler;

import javax.sql.DataSource;
import java.net.ConnectException;
import java.util.Map;


/**
 * @author tschneck
 *         Date: 12.07.13
 */

public abstract class Dao extends NamedParameterJdbcDaoSupport {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected TestSuite testSuite;
    @Autowired
    protected LobHandler lobHandler;

    public Dao(DataSource dataSource) throws SakuliException {

        try {
            if (dataSource == null) {
                throw new ConnectException("Cannot get a connection to the Database!");
            }
            setDataSource(dataSource);
        } catch (Throwable e) {
            logger.debug("Suppressed Exception for missing DB connection: ", e);
            throw new RuntimeException("Database is not reachable, please check your 'db.properties' !!!");
        }
    }


    public String createSqlSetStringForNamedParameter(Map<String, ?> paramMap) {
        String sql = "SET ";
        for (String key : paramMap.keySet()) {
            if (paramMap.get(key) != null) {
                sql = sql + key + "=:" + key + ", ";
            }
        }

        return sql.substring(0, sql.lastIndexOf(",")) + " ";
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }
}
