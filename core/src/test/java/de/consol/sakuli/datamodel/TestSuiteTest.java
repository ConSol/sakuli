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

package de.consol.sakuli.datamodel;

import de.consol.sakuli.dao.DaoTestSuite;
import de.consol.sakuli.datamodel.state.TestCaseState;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.exceptions.SakuliException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 17.07.13
 */
public class TestSuiteTest {

    @InjectMocks
    private TestSuite ts = new TestSuite();
    @Mock
    private DaoTestSuite tsDaoMock;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Throwable {
        when(tsDaoMock.getTestSuitePrimaryKey()).thenReturn(999);
        String folderMock = "src/test/resources/de/consol/sakuli/datamodel/valid";
        String screenshotFolderMock = "src/test/resources/de/consol/sakuli/datamodel/valid/_screnshot";
        ReflectionTestUtils.setField(ts, "folder", folderMock);
        ReflectionTestUtils.setField(ts, "screenShotFolderPath", screenshotFolderMock);
        ts.init();
        Assert.assertEquals(TestSuiteState.RUNNING, ts.getState());
        Assert.assertNotNull(ts.getStartDate());
        Assert.assertTrue(ts.getAbsolutePathOfTestSuiteFile().endsWith("valid" + File.separator + "testsuite.suite"), "test absolut path");
        Assert.assertEquals(999, ts.getDbPrimaryKey());
        Assert.assertEquals(1, ts.getTestCases().size());
        //tests if onyl the valid two testcases are in the suite, with there right names
        for (TestCase tc : ts.getTestCases().values()) {
            Assert.assertEquals("http://localhost:8080", tc.getStartUrl());
            if (tc.getName().equals("validTestCase") || tc.getName().equals("nestedFolderTestCase")) {
                Assert.assertTrue(true, "name of tc is valid?");
            } else {
                Assert.assertTrue(false, "name of tc is valid?");
            }
        }

    }

    @Test
    public void testInitException() throws Throwable {
        String folderMock = "src/test/resources/de/consol/sakuli/datamodelXYZ";
        String screenshotFolderMock = "src/test/resources/de/consol/sakuli/xds/_screnshot";
        ReflectionTestUtils.setField(ts, "folder", folderMock);
        ReflectionTestUtils.setField(ts, "screenShotFolderPath", screenshotFolderMock);
        try {
            ts.init();
            Assert.assertTrue(false, "catch exception");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(true, "catch exception");
        }

    }

    @Test
    public void testInitExceptionForTestCase() throws Throwable {
        String folderMock = "src/test/resources/de/consol/sakuli/datamodel/unvalid";
        String screenshotFolderMock = "src/test/resources/de/consol/sakuli/datamodel/unvalid/_screnshot";
        ReflectionTestUtils.setField(ts, "folder", folderMock);
        ReflectionTestUtils.setField(ts, "screenShotFolderPath", screenshotFolderMock);
        try {
            ts.init();
            Assert.assertTrue(false, "wrong catch exception");
        } catch (SakuliException e) {
            Assert.assertTrue(e.getMessage().startsWith("test case path "));
            Assert.assertTrue(e.getMessage().endsWith("doesn't exists - check your \"testsuite.suite\" file"));
        }

    }

    @Test
    public void testRefreshState() throws Exception {
        TestSuite testling = new TestSuite();
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.ERRORS, testling.getState());

        testling = new TestSuite();
        testling.setState(TestSuiteState.RUNNING);
        testling.setCriticalTime(0);
        testling.setWarningTime(0);
        TestCase tcTestling = new TestCase("testRefreshStat", testling.getId());
        tcTestling.setState(TestCaseState.OK);
        Map<String, TestCase> testCaseMap = new HashMap<>();
        testCaseMap.put(tcTestling.getId(), tcTestling);
        ReflectionTestUtils.setField(testling, "testCases", testCaseMap);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.OK, testling.getState());

        tcTestling.setState(TestCaseState.WARNING_IN_STEP);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.WARNING_IN_STEP, testling.getState());

        tcTestling.setState(TestCaseState.WARNING);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.WARNING_IN_CASE, testling.getState());

        tcTestling.setState(TestCaseState.CRITICAL);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.CRITICAL_IN_CASE, testling.getState());

        tcTestling.setState(TestCaseState.ERRORS);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.ERRORS, testling.getState());

        //prepare dates for warning and critical test
        testling.setState(TestSuiteState.RUNNING);
        tcTestling.setState(TestCaseState.OK);
        Date currentDate = new Date();
        ReflectionTestUtils.setField(testling, "startDate", new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;

        testling.setCriticalTime(10);
        testling.setWarningTime(8);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.OK, testling.getState());

        testling.setWarningTime(4);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.WARNING_IN_SUITE, testling.getState());

        testling.setCriticalTime(4);
        testling.refreshState();
        Assert.assertEquals(TestSuiteState.CRITICAL_IN_SUITE, testling.getState());
    }

    @Test
    public void testGetGuid() throws Exception {

    }
}
