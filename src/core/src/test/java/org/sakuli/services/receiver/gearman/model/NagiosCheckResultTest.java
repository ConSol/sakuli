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

package org.sakuli.services.receiver.gearman.model;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.receiver.gearman.GearmanProperties;
import org.sakuli.services.receiver.gearman.GearmanPropertiesTestHelper;
import org.sakuli.services.receiver.gearman.model.builder.NagiosCheckResultBuilder;
import org.sakuli.services.receiver.gearman.model.builder.OutputBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.SortedMap;

import static org.mockito.Mockito.when;

public class NagiosCheckResultTest {

    @Mock
    private TestSuite testSuite;
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private OutputBuilder outputBuilder;
    @InjectMocks
    private NagiosCheckResultBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(testling, "testSuite", new TestSuiteExampleBuilder().buildExample());
//        when(testSuite.getState()).thenReturn(TestSuiteState.OK);
        when(outputBuilder.build()).thenReturn(new NagiosOutput());
        GearmanPropertiesTestHelper.initMock(gearmanProperties);
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