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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

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
        Path folderPath = setTestSuiteFolderAndScreenshotfolder("valid");
        Assert.assertTrue(Files.exists(folderPath));
        Assert.assertTrue(Files.isDirectory(folderPath));

        ts.init();
        assertEquals(TestSuiteState.RUNNING, ts.getState());
        Assert.assertNotNull(ts.getStartDate());
        Assert.assertTrue(ts.getAbsolutePathOfTestSuiteFile().endsWith("valid" + File.separator + "testsuite.suite"), "test absolut path");
        assertEquals(999, ts.getDbPrimaryKey());
        assertEquals(1, ts.getTestCases().size());
        //tests if onyl the valid two testcases are in the suite, with there right names
        for (TestCase tc : ts.getTestCases().values()) {
            assertEquals("http://localhost:8080", tc.getStartUrl());
            if (tc.getName().equals("validTestCase") || tc.getName().equals("nestedFolderTestCase")) {
                Assert.assertTrue(true, "name of tc is valid?");
            } else {
                Assert.assertTrue(false, "name of tc is valid?");
            }
        }

    }

    private Path setTestSuiteFolderAndScreenshotfolder(String resourcePath) throws URISyntaxException {
        Path folderPath = Paths.get(getClass().getResource(resourcePath).toURI());

        ReflectionTestUtils.setField(ts, "folder", folderPath.toString());
        ReflectionTestUtils.setField(ts, "screenShotFolderPath", folderPath.toString() + File.separator + "_screenshot");
        return folderPath;
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void testInitException() throws Throwable {
        String folder = "datamodelXYZ" + File.separator + "testSuiteNotExist";
        ReflectionTestUtils.setField(ts, "folder", folder);
        ReflectionTestUtils.setField(ts, "screenShotFolderPath", folder + File.separator + "_screenshot");
        ts.init();
    }

    @Test(expectedExceptions = SakuliException.class, expectedExceptionsMessageRegExp = "test case path \".*\" doesn't exists - check your \"testsuite.suite\" file")
    public void testInitExceptionForTestCase() throws Throwable {
        Path folderPath = setTestSuiteFolderAndScreenshotfolder("unvalid");
        Assert.assertTrue(Files.exists(folderPath));
        Assert.assertTrue(Files.isDirectory(folderPath));

        ts.init();
    }

    @Test
    public void testRefreshState() throws Exception {
        TestSuite testling = new TestSuite();
        testling.refreshState();
        assertEquals(TestSuiteState.ERRORS, testling.getState());

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
        assertEquals(TestSuiteState.OK, testling.getState());

        tcTestling.setState(TestCaseState.WARNING_IN_STEP);
        testling.refreshState();
        assertEquals(TestSuiteState.WARNING_IN_STEP, testling.getState());

        tcTestling.setState(TestCaseState.WARNING);
        testling.refreshState();
        assertEquals(TestSuiteState.WARNING_IN_CASE, testling.getState());

        tcTestling.setState(TestCaseState.CRITICAL);
        testling.refreshState();
        assertEquals(TestSuiteState.CRITICAL_IN_CASE, testling.getState());

        tcTestling.setState(TestCaseState.ERRORS);
        testling.refreshState();
        assertEquals(TestSuiteState.ERRORS, testling.getState());

        //prepare dates for warning and critical test
        testling.setState(TestSuiteState.RUNNING);
        tcTestling.setState(TestCaseState.OK);
        Date currentDate = new Date();
        ReflectionTestUtils.setField(testling, "startDate", new Date(currentDate.getTime() - TimeUnit.SECONDS.toMillis(5)));
        testling.stopDate = currentDate;

        testling.setCriticalTime(10);
        testling.setWarningTime(8);
        testling.refreshState();
        assertEquals(TestSuiteState.OK, testling.getState());

        testling.setWarningTime(4);
        testling.refreshState();
        assertEquals(TestSuiteState.WARNING_IN_SUITE, testling.getState());

        testling.setCriticalTime(4);
        testling.refreshState();
        assertEquals(TestSuiteState.CRITICAL_IN_SUITE, testling.getState());
    }

    @Test
    public void testGetGuid() throws Exception {
        ReflectionTestUtils.setField(ts, "id", "001");
        ReflectionTestUtils.setField(ts, "startDate", new Date());
        assertNotNull(ts.getGuid());

        TestSuite ts2 = new TestSuite();
        ReflectionTestUtils.setField(ts2, "id", "001");
        ReflectionTestUtils.setField(ts2, "startDate", new Date());
        assertNotNull(ts2.getGuid());

        assertNotEquals(ts.getGuid(), ts2.getGuid());
    }
}
