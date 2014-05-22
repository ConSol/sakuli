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

import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.integration.builder.TestCaseBuilder;
import de.consol.sakuli.services.dao.impl.DaoTestCaseImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author tschneck
 *         Date: 25.07.13
 */
public class DaoTestCaseImplIntegrationTest extends DaoIntegrationTest<DaoTestCaseImpl> {


    @Override
    protected DaoTestCaseImpl createTestling() throws SakuliException {
        return new DaoTestCaseImpl(dataSource);
    }

    @Test
    public void testSaveTestCaseResult() throws Exception {
        //TODO Implement
//        when(testSuiteMock.getDbJobPrimaryKey()).thenReturn(0);
//        TestCase tester = new TestCase("unit_test", "1253");


    }

    @Test
    public void testGetCountOfSahiCases() throws Throwable {
        int countOfSahiCases = testling.getCountOfSahiCases();
        Assert.assertTrue(countOfSahiCases >= 0);

        //save new testcase
        initDeafultTestSuiteMock();
        testling.saveTestCaseResult(TestCaseBuilder.createEmptyTestCase());
        Assert.assertEquals(testling.getCountOfSahiCases(), countOfSahiCases + 1);
    }


    @Test
    public void testGetScreenShotFromDB() throws Exception {
        // TODO implement test
    }

}
