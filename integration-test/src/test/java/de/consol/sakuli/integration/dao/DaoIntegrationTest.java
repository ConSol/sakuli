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

package de.consol.sakuli.integration.dao;

import de.consol.sakuli.dao.impl.Dao;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.integration.IntegrationTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * @author tschneck Date: 22.07.13
 */
@Test(groups = IntegrationTest.GROUP)
@ContextConfiguration(locations = {"classpath*:db-applicationContext.xml"})
public abstract class DaoIntegrationTest<D extends Dao> extends AbstractTestNGSpringContextTests {

    @InjectMocks
    protected D testling;

    @Autowired
    protected DataSource dataSource;

    @Mock
    protected TestSuite testSuiteMock;
    protected SakuliExceptionHandler sakuliExceptionHandlerMock;

    protected abstract D createTestling() throws SakuliException;

    @BeforeMethod
    public void init() throws SakuliException {
        testling = createTestling();
        MockitoAnnotations.initMocks(this);
        testling.setDataSource(dataSource);
    }

}
