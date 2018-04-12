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
import org.sakuli.services.common.CacheHandlingServiceImpl;
import org.sakuli.services.common.LogCleanUpServiceImpl;
import org.sakuli.services.forwarder.database.DatabaseResultServiceImpl;
import org.sakuli.services.forwarder.gearman.GearmanResultServiceImpl;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 * Date: 09.04.15
 */
public class TeardownServiceHelperTest extends AbstractServiceBaseTest {

    @Test
    public void testInvokeAllTeardwonServices() {
        assertEquals(BeanLoader.loadMultipleBeans(TeardownService.class).size(), 5);
        DatabaseResultServiceImpl databaseResultService = mockDatabaseResultService();
        GearmanResultServiceImpl gearmanResultService = mockGearmanResultService();
        CacheHandlingServiceImpl cacheHandlingResultService = mockCacheHandlingResultService();
        LogCleanUpServiceImpl logCleanUpResultService = mockLogCleanUpResultService();
        TestSuite testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.setState(TestSuiteState.RUNNING);

        TeardownServiceHelper.invokeTeardownServices(testSuite);
        assertEquals(testSuite.getState(), TestSuiteState.OK);
        assertTrue(testSuite.getStopDate().after(testSuite.getStartDate()));
        verify(databaseResultService).teardownTestSuite(eq(testSuite));
        verify(gearmanResultService).tearDown(eq(Optional.of(testSuite)));
        verify(cacheHandlingResultService).teardownTestSuite(eq(testSuite));
        verify(logCleanUpResultService).teardownTestSuite(eq(testSuite));
    }

    private LogCleanUpServiceImpl mockLogCleanUpResultService() {
        LogCleanUpServiceImpl logCleanUpResultService = BeanLoader.loadBean(LogCleanUpServiceImpl.class);
        doNothing().when(logCleanUpResultService).teardownTestSuite(any());
        return logCleanUpResultService;
    }

    private GearmanResultServiceImpl mockGearmanResultService() {
        GearmanResultServiceImpl gearmanResultService = BeanLoader.loadBean(GearmanResultServiceImpl.class);
        doNothing().when(gearmanResultService).tearDown(any());
        return gearmanResultService;
    }

    private DatabaseResultServiceImpl mockDatabaseResultService() {
        DatabaseResultServiceImpl databaseResultService = BeanLoader.loadBean(DatabaseResultServiceImpl.class);
        doNothing().when(databaseResultService).teardownTestSuite(any());
        return databaseResultService;
    }

    private CacheHandlingServiceImpl mockCacheHandlingResultService() {
        CacheHandlingServiceImpl cacheHandlingResultService = BeanLoader.loadBean(CacheHandlingServiceImpl.class);
        doNothing().when(cacheHandlingResultService).teardownTestSuite(any());
        return cacheHandlingResultService;
    }
}