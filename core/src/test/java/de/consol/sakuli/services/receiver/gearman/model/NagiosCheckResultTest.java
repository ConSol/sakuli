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

package de.consol.sakuli.services.receiver.gearman.model;

import de.consol.sakuli.builder.TestSuiteExampleBuilder;
import de.consol.sakuli.services.receiver.gearman.GearmanProperties;
import de.consol.sakuli.services.receiver.gearman.model.builder.NagiosCheckResultBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.SortedMap;

public class NagiosCheckResultTest {

    @Test
    public void testGetPayload() throws Exception {
        NagiosCheckResult checkResult = new NagiosCheckResultBuilder()
                .withTestSuite(new TestSuiteExampleBuilder().buildExample(), new GearmanProperties())
                .build();

        SortedMap<PayLoadFields, String> map = checkResult.getPayload();
        Assert.assertEquals(map.firstKey(), PayLoadFields.TYPE);
        Assert.assertEquals(map.lastKey(), PayLoadFields.OUTPUT);
    }

    @Test
    public void testGetPayloadString() throws Exception {
        NagiosCheckResult checkResult = new NagiosCheckResultBuilder()
                .withTestSuite(new TestSuiteExampleBuilder().buildExample(), new GearmanProperties())
                .build();

        Assert.assertTrue(checkResult.getPayloadString().matches("type=passive\n" +
                "host_name=localhost\n" +
                "start_time=.*\n" +
                "finish_time=.*\n" +
                "return_code=\\d\n" +
                "service_description=UnitTest_.*\n" +
                "output=.*"));
    }
}