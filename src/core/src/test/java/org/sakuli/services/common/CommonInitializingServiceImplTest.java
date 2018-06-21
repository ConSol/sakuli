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

package org.sakuli.services.common;

import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.utils.TestSuitePropertiesBuilder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author tschneck
 * Date: 22.05.14
 */
public class CommonInitializingServiceImplTest {

    @Spy
    private TestSuite ts;
    @Spy
    private TestSuiteProperties testSuiteProperties;
    @Mock
    private SakuliProperties sakuliProperties;
    @InjectMocks
    private CommonInitializingServiceImpl testling;

    @Test
    public void testInit() throws Exception {
        testSuiteProperties = spy(new TestSuitePropertiesBuilder(this.getClass(), "valid", "suite_id").build());
        ts = spy(new TestSuite(testSuiteProperties));
        MockitoAnnotations.initMocks(this);

        testling.initTestSuite();

        assertEquals(ts.getState(), TestSuiteState.RUNNING);
        assertNotNull(ts.getStartDate());
        assertTrue(ts.getAbsolutePathOfTestSuiteFile().endsWith("valid" + File.separator + "testsuite.suite"),
                "test absolut path");
        assertEquals(ts.getDbPrimaryKey(), -1);
        assertEquals(ts.getTestCases().size(), 1);
        assertEquals(ts.getId(), "suite_id");
    }

    @DataProvider(name = "filterTestCasesProvider")
    public static Object[][] filteredTestCaseDataProvider() {
        return new Object[][] { { Arrays.asList("tc1/_tc.js", "tc2/_tc.js"), 2 }, { Arrays.asList("tc1/_tc.js"), 1 } };
    }

    @Test(dataProvider = "filterTestCasesProvider")
    public void testInitWithFilter(List<String> filters, Integer amountOfTestCases) throws Exception {
        testSuiteProperties =
                spy(new TestSuitePropertiesBuilder(this.getClass(), "filtered", "suite_id").withFilters(filters)
                        .build());
        ts = spy(new TestSuite(testSuiteProperties));
        MockitoAnnotations.initMocks(this);

        testling.initTestSuite();

        assertEquals(ts.getState(), TestSuiteState.RUNNING);
        assertNotNull(ts.getStartDate());
        assertTrue(ts.getAbsolutePathOfTestSuiteFile().endsWith("filtered" + File.separator + "testsuite.suite"),
                "test absolut path");
        assertEquals(ts.getDbPrimaryKey(), -1);
        assertEquals(ts.getTestCases().size(), (int) (amountOfTestCases));
        assertEquals(ts.getId(), "suite_id");
    }

    @Test
    public void testInitWithOutTestCases() throws Exception {
        TestSuiteProperties props = new TestSuiteProperties();
        props.setTestSuiteId("suite_id");
        props.setLoadTestCasesAutomatic(false);
        testSuiteProperties = spy(props);
        ts = spy(new TestSuite(testSuiteProperties));
        MockitoAnnotations.initMocks(this);

        testling.initTestSuite();

        assertEquals(ts.getState(), TestSuiteState.RUNNING);
        assertNotNull(ts.getStartDate());
        assertNull(ts.getAbsolutePathOfTestSuiteFile());
        assertEquals(ts.getDbPrimaryKey(), -1);
        assertNull(ts.getTestCases());
        assertEquals(ts.getId(), "suite_id");
    }

    @Test(expectedExceptions = SakuliInitException.class,
            expectedExceptionsMessageRegExp = "Cannot read testsuite.suite.*")
    public void testInitExceptionForTestCase() throws Exception {
        testSuiteProperties = spy(new TestSuitePropertiesBuilder(this.getClass(), "unvalid", "").build());
        testSuiteProperties.setTestSuiteId("testid");
        ts = spy(new TestSuite(testSuiteProperties));
        MockitoAnnotations.initMocks(this);
        testling.initTestSuite();
    }

    @Test
    public void testCheckTestSuiteOk() throws Exception {
        CommonInitializingServiceImpl.checkConfiguration(new TestSuiteExampleBuilder().buildExample());
    }

    @Test(expectedExceptions = SakuliInitException.class)
    public void testCheckTestSuiteException() throws Exception {
        CommonInitializingServiceImpl.checkConfiguration(new TestSuite());
    }
}
