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

package org.sakuli.services.forwarder.checkmk;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertEquals;

/**
 * @author Georgi Todorov
 */
public class CheckMKResultServiceImplTest extends BaseTest {

    @InjectMocks
    private CheckMKResultServiceImpl testling;
    @Mock
    private CheckMKProperties checkMKProperties;
    @Mock
    private TestSuite testSuite;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] createSpoolFilePathDP() {
        return new Object[][] {
                {"/tmp/blub", "600", "sakuli_suite", "docker_test", "/tmp/blub/600_sakuli_suite_docker_test"},
                {"/tmp/blub", "300", null, "test_suite", "/tmp/blub/300_test_suite"},
        };
    }

    @Test(dataProvider = "createSpoolFilePathDP")
    public void createSpoolFilePath(String spoolDir, String freshness, String spoolFileNamePrefix, String testSuiteName, String expectedFilePath) {
        doReturn(testSuiteName).when(testSuite).getId();
        doReturn(spoolDir).when(checkMKProperties).getSpoolDir();
        doReturn(freshness).when(checkMKProperties).getFreshness();
        doReturn(spoolFileNamePrefix).when(checkMKProperties).getSpoolFilePrefix();
        assertEquals(testling.createSpoolFilePath(), expectedFilePath);
    }

}
