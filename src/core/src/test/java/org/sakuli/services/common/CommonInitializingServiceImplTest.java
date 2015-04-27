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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.utils.TestSuitePropertiesTestUtils;
import org.testng.annotations.Test;

import java.io.File;

import static org.mockito.Mockito.spy;
import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 22.05.14
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
    public void testInit() throws Throwable {
        testSuiteProperties = spy(TestSuitePropertiesTestUtils.getTestProps(this.getClass(), "valid", "suite_id"));
        ts = spy(new TestSuite(testSuiteProperties));
        MockitoAnnotations.initMocks(this);

        testling.initTestSuite();

        assertEquals(ts.getState(), TestSuiteState.RUNNING);
        assertNotNull(ts.getStartDate());
        assertTrue(ts.getAbsolutePathOfTestSuiteFile().endsWith("valid" + File.separator + "testsuite.suite"), "test absolut path");
        assertEquals(ts.getDbPrimaryKey(), -1);
        assertEquals(ts.getTestCases().size(), 1);
        assertEquals(ts.getId(), "suite_id");
    }

    @Test
    public void testInitWithOutTestCases() throws Throwable {
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


    @Test(expectedExceptions = SakuliInitException.class, expectedExceptionsMessageRegExp = "Cannot read testsuite.suite.*")
    public void testInitExceptionForTestCase() throws Throwable {
        testSuiteProperties = spy(TestSuitePropertiesTestUtils.getTestProps(this.getClass(), "unvalid", ""));
        ts = spy(new TestSuite(testSuiteProperties));
        MockitoAnnotations.initMocks(this);
        testling.initTestSuite();
    }

}
