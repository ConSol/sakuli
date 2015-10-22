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

package org.sakuli.services.forwarder.database;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.database.dao.DaoTestCase;
import org.sakuli.services.forwarder.database.dao.DaoTestCaseStep;
import org.sakuli.services.forwarder.database.dao.DaoTestSuite;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class DatabaseResultServiceImplTest {

    @Mock
    private DaoTestCase daoTestCase;
    @Mock
    private DaoTestCaseStep daoTestCaseStep;
    @Mock
    private DaoTestSuite daoTestSuite;
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Spy
    @InjectMocks
    private DatabaseResultServiceImpl testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveResultsInDatabase() throws Exception {
        when(testSuite.getTestCases()).thenReturn(null);
        testling.saveAllResults();
        verify(daoTestSuite).saveTestSuiteResult();
        verify(daoTestSuite).saveTestSuiteToSahiJobs();
        verify(daoTestCase, never()).saveTestCaseResult(any(TestCase.class));
        verify(daoTestCaseStep, never()).saveTestCaseSteps(any(SortedSet.class), anyInt());
    }

    @Test
    public void testSaveResultsInDatabaseHandleException() throws Exception {
        doThrow(DataAccessException.class).when(daoTestSuite).saveTestSuiteResult();
        testling.saveAllResults();
        verify(exceptionHandler).handleException(any(SakuliForwarderException.class), anyBoolean());
    }

    @Test
    public void testSaveResultsInDatabaseWithTestcases() throws Exception {

        Integer tcPrimaryKey = 22;
        TestCase tc1 = mock(TestCase.class);
        TestCaseStep tcs1 = mock(TestCaseStep.class);
        when(tc1.getDbPrimaryKey()).thenReturn(tcPrimaryKey);
        SortedSet<TestCaseStep> tcStepList = new TreeSet<>(Collections.singleton(tcs1));
        when(tc1.getStepsAsSortedSet()).thenReturn(tcStepList);

        TestCase tc2 = mock(TestCase.class);
        when(tc2.getSteps()).thenReturn(new ArrayList<>());

        Map<String, TestCase> testCaseMap = new HashMap<>();
        testCaseMap.put("1", tc1);
        testCaseMap.put("2", tc2);

        testSuite = new TestSuite();
        testSuite.setTestCases(testCaseMap);
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);

        testling.saveAllResults();

        verify(daoTestSuite).saveTestSuiteResult();
        verify(daoTestSuite).saveTestSuiteToSahiJobs();
        verify(daoTestCase).saveTestCaseResult(tc1);
        verify(daoTestCase).saveTestCaseResult(tc2);
        verify(daoTestCase, times(2)).saveTestCaseResult(any(TestCase.class));
        verify(daoTestCaseStep).saveTestCaseSteps(tcStepList, tcPrimaryKey);
        verify(daoTestCaseStep).saveTestCaseSteps(any(SortedSet.class), anyInt());
    }

}