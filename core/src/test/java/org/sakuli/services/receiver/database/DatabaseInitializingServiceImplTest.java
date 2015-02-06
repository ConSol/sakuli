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

package org.sakuli.services.receiver.database;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliReceiverException;
import org.sakuli.services.receiver.database.dao.DaoTestSuite;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DatabaseInitializingServiceImplTest {

    @Mock
    private TestSuite testSuite;
    @Mock
    private DaoTestSuite daoTestSuite;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @InjectMocks
    private DatabaseInitializingServiceImpl testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitTestSuiteInDatabase() throws Exception {
        int dbKey = 11;
        when(daoTestSuite.insertInitialTestSuiteData()).thenReturn(dbKey);
        testling.initTestSuite();
        verify(testSuite).setDbPrimaryKey(eq(dbKey));
    }

    @Test
    public void testSaveResultsInDatabaseHandleException() throws Exception {
        doThrow(DataAccessException.class).when(daoTestSuite).insertInitialTestSuiteData();
        testling.initTestSuite();
        verify(exceptionHandler).handleException(any(SakuliReceiverException.class), anyBoolean());
    }
}