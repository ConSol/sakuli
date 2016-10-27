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

package org.sakuli.services;

import org.joda.time.DateTime;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.forwarder.database.DatabaseInitializingServiceImpl;
import org.sakuli.services.forwarder.gearman.GearmanInitializingServiceImpl;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 09.04.15
 */
public class InitializingServiceHelperTest extends AbstractServiceBaseTest {

    @Test
    public void testInvokeAllResultServices() throws Exception {
        assertEquals(BeanLoader.loadMultipleBeans(InitializingService.class).size(), 3);
        DatabaseInitializingServiceImpl databaseService = BeanLoader.loadBean(DatabaseInitializingServiceImpl.class);
        doNothing().when(databaseService).initTestSuite();
        GearmanInitializingServiceImpl gearmanService = BeanLoader.loadBean(GearmanInitializingServiceImpl.class);
        doNothing().when(gearmanService).initTestSuite();

        TestSuite testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.setState(null);
        testSuite.setStartDate(null);
        InitializingServiceHelper.invokeInitializingServcies();
        assertEquals(testSuite.getState(), TestSuiteState.RUNNING);
        assertTrue(testSuite.getStartDate().before(new DateTime().plusMillis(100).toDate()));
        verify(databaseService).initTestSuite();
        verify(gearmanService).initTestSuite();
    }
}