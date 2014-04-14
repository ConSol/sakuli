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

package de.consol.sakuli.dao.impl;

import de.consol.sakuli.exceptions.SakuliException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * @author tschneck
 *         Date: 22.07.13
 */

public class DaoTest {

    private Dao testling;
    @Mock
    private DataSource dataSource;

    @BeforeMethod
    public void init() throws SakuliException {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testCreateSqlSetStringForNamedParameter() throws Throwable {
        testling = new Dao(dataSource) {
        };
        MapSqlParameterSource source = new MapSqlParameterSource().addValue("testling2", "value").addValue("testling", "value");
        Assert.assertEquals("SET testling2=:testling2, testling=:testling ", testling.createSqlSetStringForNamedParameter(source.getValues()));
        source.addValue("nullable", null);
        Assert.assertEquals("SET testling2=:testling2, testling=:testling ", testling.createSqlSetStringForNamedParameter(source.getValues()));

    }
}
