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

package org.sakuli.integration.dao;

import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.integration.builder.TestCaseBuilder;
import org.sakuli.services.forwarder.database.dao.impl.DaoTestCaseImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.sakuli.integration.IntegrationTest.GROUP;

/**
 * @author tschneck
 * Date: 25.07.13
 */
@Test(groups = GROUP)
public class DaoTestCaseImplIntegrationTest extends DaoIntegrationTest<DaoTestCaseImpl> {


    @Override
    protected DaoTestCaseImpl createTestling() throws SakuliCheckedException {
        return new DaoTestCaseImpl(dataSource);
    }

    @Test
    public void testGetCountOfSahiCases() throws Exception {
        int countOfSahiCases = testling.getCountOfSahiCases();
        Assert.assertTrue(countOfSahiCases >= 0);

        //save new testcase
        initDeafultTestSuiteMock();
        testling.saveTestCaseResult(TestCaseBuilder.createEmptyTestCase());
        Assert.assertEquals(testling.getCountOfSahiCases(), countOfSahiCases + 1);
    }

}
