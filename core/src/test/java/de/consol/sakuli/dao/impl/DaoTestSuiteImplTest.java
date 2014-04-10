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

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 25.07.13
 */
public class DaoTestSuiteImplTest extends DaoTest<DaoTestSuiteImpl> {

    @BeforeMethod
    @Override
    public void init() throws SakuliException {
        super.init();
        testling = new DaoTestSuiteImpl(dataSource);
        testling.testSuite = testSuiteMock;
    }

    @Test
    public void testGetTestSuitePrimaryKey() throws Exception {

    }

    @Test
    public void testUpdateTestSuiteResult() throws Exception {

    }

    @Test
    public void testSaveTestSuiteToSahiJobs() throws Throwable {
        String guid = "JUNIT_123" + TestSuite.GUID_DATE_FORMATE.format(new Date());
        when(testSuiteMock.getGuid()).thenReturn(guid);
        Assert.assertTrue(testSuiteMock.getGuid().contains("JUNIT_123"));
        int results = testling.getCountOfSahiJobs();
        testling.saveTestSuiteToSahiJobs();
        Assert.assertEquals(results + 1, testling.getCountOfSahiJobs());

    }

    @Test
    public void testGetCountOfSahiJobs() throws Throwable {
        Assert.assertNotNull(testling.getCountOfSahiJobs());

    }
}
