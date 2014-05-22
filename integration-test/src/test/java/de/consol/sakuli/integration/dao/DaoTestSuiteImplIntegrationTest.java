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

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.integration.builder.TestSuiteBuilder;
import de.consol.sakuli.services.dao.impl.DaoTestSuiteImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 25.07.13
 */
public class DaoTestSuiteImplIntegrationTest extends DaoIntegrationTest<DaoTestSuiteImpl> {

    @Override
    protected DaoTestSuiteImpl createTestling() throws SakuliException {
        return new DaoTestSuiteImpl(dataSource);
    }

    @Test
    public void testGetTestSuitePrimaryKey() throws Exception {
        //first
        testling.setTestSuite(TestSuiteBuilder.createEmptyTestSuite());
        int key1 = testling.insertInitialTestSuiteData();
        Assert.assertTrue(key1 > 0);

        //second init
        testling.setTestSuite(TestSuiteBuilder.createEmptyTestSuite());
        int key2 = testling.insertInitialTestSuiteData();
        Assert.assertEquals(key2, key1 + 1);
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
