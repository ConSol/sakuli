/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services;

import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.common.CacheHandlingResultServiceImpl;
import org.sakuli.services.common.LogCleanUpResultServiceImpl;
import org.sakuli.services.forwarder.database.DatabaseResultServiceImpl;
import org.sakuli.services.forwarder.gearman.GearmanResultServiceImpl;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 09.04.15
 */
public class TeardownServiceHelperTest extends AbstractServiceBaseTest {

    @Test
    public void testInvokeAllTeardwonServices() throws Exception {
        assertEquals(BeanLoader.loadMultipleBeans(TeardownService.class).size(), 5);
        DatabaseResultServiceImpl databaseResultService = mockDatabaseResultService();
        GearmanResultServiceImpl gearmanResultService = mockGearmanResultService();
        CacheHandlingResultServiceImpl cacheHandlingResultService = mockCacheHandlingResultService();
        LogCleanUpResultServiceImpl logCleanUpResultService = mockLogCleanUpResultService();
        TestSuite testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.setState(TestSuiteState.RUNNING);

        TeardownServiceHelper.invokeTeardownServices();
        assertEquals(testSuite.getState(), TestSuiteState.OK);
        assertTrue(testSuite.getStopDate().after(testSuite.getStartDate()));
        verify(databaseResultService).saveAllResults();
        verify(databaseResultService).refreshStates();
        verify(gearmanResultService).saveAllResults();
        verify(gearmanResultService).refreshStates();
        verify(cacheHandlingResultService).saveAllResults();
        verify(cacheHandlingResultService).refreshStates();
        verify(logCleanUpResultService).triggerAction();
    }

    private LogCleanUpResultServiceImpl mockLogCleanUpResultService() {
        LogCleanUpResultServiceImpl logCleanUpResultService = BeanLoader.loadBean(LogCleanUpResultServiceImpl.class);
        doNothing().when(logCleanUpResultService).triggerAction();
        return logCleanUpResultService;
    }

    private GearmanResultServiceImpl mockGearmanResultService() {
        GearmanResultServiceImpl gearmanResultService = BeanLoader.loadBean(GearmanResultServiceImpl.class);
        doNothing().when(gearmanResultService).refreshStates();
        doNothing().when(gearmanResultService).saveAllResults();
        return gearmanResultService;
    }

    private DatabaseResultServiceImpl mockDatabaseResultService() {
        DatabaseResultServiceImpl databaseResultService = BeanLoader.loadBean(DatabaseResultServiceImpl.class);
        doNothing().when(databaseResultService).refreshStates();
        doNothing().when(databaseResultService).saveAllResults();
        return databaseResultService;
    }

    private CacheHandlingResultServiceImpl mockCacheHandlingResultService() {
        CacheHandlingResultServiceImpl cacheHandlingResultService = BeanLoader.loadBean(CacheHandlingResultServiceImpl.class);
        doNothing().when(cacheHandlingResultService).refreshStates();
        doNothing().when(cacheHandlingResultService).saveAllResults();
        return cacheHandlingResultService;
    }
}