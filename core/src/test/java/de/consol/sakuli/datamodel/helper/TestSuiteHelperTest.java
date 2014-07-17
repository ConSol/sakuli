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

package de.consol.sakuli.datamodel.helper;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import de.consol.sakuli.utils.TestSuitePropertiesTestUtils;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 17.07.13
 */
public class TestSuiteHelperTest extends TestSuitePropertiesTestUtils {

    @Test
    public void testInit() throws Throwable {
        TestSuiteProperties testProps = TestSuitePropertiesTestUtils.getTestProps(this.getClass(), "valid", "suite_id_001");

        HashMap<String, TestCase> result = TestSuiteHelper.loadTestCases(testProps);
        assertEquals(1, result.size());
        //tests if onyl the valid testcase are in the suite, with there right names
        TestCase tc = result.values().iterator().next();
        assertEquals("http://localhost:8080", tc.getStartUrl());
        assertEquals(tc.getName(), "validTestCase");
        assertEquals(tc.getId(), "validTestCase");
        assertEquals(tc.getId(), result.keySet().iterator().next());
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "test case path \".*unValidTestCase.*\" doesn't exists - check your \"testsuite.suite\" file")
    public void testInitExceptionForTestCase() throws Throwable {
        TestSuiteProperties testProps = TestSuitePropertiesTestUtils.getTestProps(this.getClass(), "unvalid", "unvalid_id");
        TestSuiteHelper.loadTestCases(testProps);
    }

}
