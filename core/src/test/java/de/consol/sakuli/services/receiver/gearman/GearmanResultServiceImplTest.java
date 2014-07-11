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

package de.consol.sakuli.services.receiver.gearman;

import de.consol.sakuli.builder.TestSuiteBuilder;
import de.consol.sakuli.datamodel.TestSuite;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static de.consol.sakuli.datamodel.state.TestSuiteState.OK;
import static org.mockito.Mockito.when;

public class GearmanResultServiceImplTest {

    @InjectMocks
    private GearmanResultServiceImpl testling;
    @Mock
    private GearmanProperties properties;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(enabled = false)
    public void testSaveAllResults() throws Exception {
        when(properties.getServiceType()).thenReturn("passive");
        when(properties.getServerQueue()).thenReturn("check_results");
        when(properties.getServerHost()).thenReturn("99.99.99.20");
        when(properties.getServerPort()).thenReturn(4730);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");
        TestSuite testSuite = new TestSuiteBuilder()
                .withHost("win7sakuli")
                .withId("sakuli_demo")
                .withState(OK)
                .buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);

        testling.saveAllResults();
    }
}