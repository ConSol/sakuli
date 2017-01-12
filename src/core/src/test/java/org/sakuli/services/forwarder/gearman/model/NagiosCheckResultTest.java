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

package org.sakuli.services.forwarder.gearman.model;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.forwarder.MonitoringPropertiesTestHelper;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosCheckResultBuilder;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosOutputBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.SortedMap;

import static org.mockito.Mockito.when;

public class NagiosCheckResultTest {

    @Spy
    private TestSuite testSuite = new TestSuiteExampleBuilder().buildExample();
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private NagiosOutputBuilder nagiosOutputBuilder;
    @InjectMocks
    private NagiosCheckResultBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(nagiosOutputBuilder.build()).thenReturn(new NagiosOutput());
        MonitoringPropertiesTestHelper.initMock(gearmanProperties);
        //represents the property replace mechanism of the `.properties` file
        when(gearmanProperties.getNagiosServiceDescription()).then(a -> testSuite.getId());
    }

    @Test
    public void testGetPayload() throws Exception {

        NagiosCheckResult checkResult = testling.build();

        SortedMap<PayLoadFields, String> map = checkResult.getPayload();
        Assert.assertEquals(map.firstKey(), PayLoadFields.TYPE);
        Assert.assertEquals(map.lastKey(), PayLoadFields.OUTPUT);
    }

    @Test
    public void testGetPayloadString() throws Exception {
        NagiosCheckResult checkResult = testling.build();

        String regex = "type=passive\n" +
                "host_name=localhost\n" +
                "start_time=.*\n" +
                "finish_time=.*\n" +
                "return_code=\\d\n" +
                "service_description=UnitTest_.*\n" +
                "output=.*";
        Assert.assertTrue(checkResult.getPayloadString().matches(regex), checkResult.getPayloadString() + "\nwon't match to:\n" + regex);
    }
}