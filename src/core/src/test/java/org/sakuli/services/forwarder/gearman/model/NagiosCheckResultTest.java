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
import org.sakuli.services.forwarder.gearman.GearmanTemplateOutputBuilder;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosCheckResultBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

public class NagiosCheckResultTest {

    @Spy
    private TestSuite testSuite = new TestSuiteExampleBuilder().buildExample();
    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private GearmanTemplateOutputBuilder outputBuilder;
    @InjectMocks
    private NagiosCheckResultBuilder testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        MonitoringPropertiesTestHelper.initMock(gearmanProperties);
        //represents the property replace mechanism of the `.properties` file
        when(gearmanProperties.getServerQueue()).then(a -> testSuite.getId());
    }

    @Test
    public void testGetPayload() throws Exception {
        String gearmanPayload="test_payload";
        when(outputBuilder.createOutput()).thenReturn(gearmanPayload);

        NagiosCheckResult checkResult = testling.build();
        Assert.assertEquals(checkResult.getPayload(), gearmanPayload);
        Assert.assertNotNull(checkResult.getQueueName());
        Assert.assertEquals(checkResult.getQueueName(), gearmanProperties.getServerQueue());
        Assert.assertNotNull(checkResult.getUuid());
        Assert.assertEquals(checkResult.getUuid(), testSuite.getGuid());
    }

}
