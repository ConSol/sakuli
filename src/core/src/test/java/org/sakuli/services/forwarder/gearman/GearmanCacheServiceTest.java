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

package org.sakuli.services.forwarder.gearman;

import org.mockito.*;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.services.forwarder.MonitoringPropertiesTestHelper;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosCheckResultBuilder;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosOutputBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class GearmanCacheServiceTest extends BaseTest {

    @InjectMocks
    private GearmanCacheService testling;

    @Mock
    private GearmanProperties gearmanProperties;
    @Mock
    private TestSuiteProperties testSuiteProperties;

    @Mock
    private TestSuite testSuite;
    @Mock
    private NagiosOutputBuilder nagiosOutputBuilder;
    @InjectMocks
    private NagiosCheckResultBuilder checkResultBuilder;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(checkResultBuilder, "testSuite", new TestSuiteExampleBuilder().buildExample());
        when(nagiosOutputBuilder.build()).thenReturn(new NagiosOutput());
        MonitoringPropertiesTestHelper.initMock(gearmanProperties);
    }

    @Test
    public void testCacheResultsEmptyAndNew() throws Exception {
        when(testSuiteProperties.getTestSuiteFolder()).thenReturn(Paths.get("target"));
        when(gearmanProperties.getServerQueue()).thenReturn("check_results");

        List< NagiosCheckResult> results = new ArrayList<>();
        testling.cacheResults(results);
        Assert.assertEquals(testling.getCachedResults().size(), 0L);

        NagiosCheckResult newResult = checkResultBuilder.build();
        results.add(newResult);

        testling.cacheResults(results);
        results = testling.getCachedResults();
        Assert.assertEquals(results.size(), 1L);
        Assert.assertEquals(results.get(0).getQueueName(), "check_results");
        Assert.assertEquals(results.get(0).getUuid(), newResult.getUuid());
        Assert.assertEquals(results.get(0).getPayloadString().trim(), newResult.getPayloadString().trim());

        NagiosCheckResult newResult2 = checkResultBuilder.build();
        results.add(newResult2);

        testling.cacheResults(results);
        results = testling.getCachedResults();
        Assert.assertEquals(results.size(), 2L);
        Assert.assertEquals(results.get(0).getQueueName(), "check_results");
        Assert.assertEquals(results.get(0).getUuid(), newResult.getUuid());
        Assert.assertEquals(results.get(0).getPayloadString().trim(), newResult.getPayloadString().trim());
        Assert.assertEquals(results.get(1).getQueueName(), "check_results");
        Assert.assertEquals(results.get(1).getUuid(), newResult2.getUuid());
        Assert.assertEquals(results.get(1).getPayloadString().trim(), newResult2.getPayloadString().trim());
    }

    @Test
    public void testCacheResultsRoundtrip() throws Exception {
        when(testSuiteProperties.getTestSuiteFolder()).thenReturn(Paths.get(getTestFolderPath()));
        when(gearmanProperties.getServerQueue()).thenReturn("check_results");

        List< NagiosCheckResult> originalResults = testling.getCachedResults();
        try {
            Assert.assertEquals(originalResults.size(), 2L);
            Assert.assertEquals(originalResults.get(0).getQueueName(), "check_results");
            Assert.assertEquals(originalResults.get(0).getUuid(), "example_xfce__2016_03_23_14_00_00_000");
            Assert.assertTrue(originalResults.get(0).getPayloadString().contains("[OK] case \"case1\""));
            Assert.assertEquals(originalResults.get(1).getQueueName(), "check_results");
            Assert.assertEquals(originalResults.get(1).getUuid(), "example_xfce__2016_03_23_15_00_00_000");
            Assert.assertTrue(originalResults.get(1).getPayloadString().contains("[OK] case \"case2\""));

            testling.cacheResults(originalResults);
            List<NagiosCheckResult> results = testling.getCachedResults();
            Assert.assertEquals(results.size(), 2L);
            Assert.assertEquals(results.get(0).getQueueName(), "check_results");
            Assert.assertEquals(results.get(0).getUuid(), "example_xfce__2016_03_23_14_00_00_000");
            Assert.assertTrue(results.get(0).getPayloadString().contains("[OK] case \"case1\""));
            Assert.assertEquals(results.get(1).getQueueName(), "check_results");
            Assert.assertEquals(results.get(1).getUuid(), "example_xfce__2016_03_23_15_00_00_000");
            Assert.assertTrue(results.get(1).getPayloadString().contains("[OK] case \"case2\""));

            NagiosCheckResult newResult = checkResultBuilder.build();
            results.add(0, newResult);

            testling.cacheResults(results);
            results = testling.getCachedResults();
            Assert.assertEquals(results.size(), 3L);
            Assert.assertEquals(results.get(0).getQueueName(), "check_results");
            Assert.assertEquals(results.get(0).getUuid(), newResult.getUuid());
            Assert.assertEquals(results.get(0).getPayloadString().trim(), newResult.getPayloadString().trim());
            Assert.assertEquals(results.get(1).getQueueName(), "check_results");
            Assert.assertEquals(results.get(1).getUuid(), "example_xfce__2016_03_23_14_00_00_000");
            Assert.assertTrue(results.get(1).getPayloadString().contains("[OK] case \"case1\""));
            Assert.assertEquals(results.get(2).getQueueName(), "check_results");
            Assert.assertEquals(results.get(2).getUuid(), "example_xfce__2016_03_23_15_00_00_000");
            Assert.assertTrue(results.get(2).getPayloadString().contains("[OK] case \"case2\""));
        } finally {
            testling.cacheResults(originalResults);
        }
    }
}