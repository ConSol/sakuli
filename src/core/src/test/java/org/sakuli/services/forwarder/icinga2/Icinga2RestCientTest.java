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

package org.sakuli.services.forwarder.icinga2;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.sakuli.loader.BeanLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
public class Icinga2RestCientTest extends AbstractIcinga2ForwarderBaseTest {

    private Icinga2RestCient testling;

    @BeforeMethod
    public void setUp() throws Exception {
        testling = BeanLoader.loadBean(Icinga2RestCient.class);
    }

    @Test
    public void testInitIcingaClient() throws Exception {
        // testling.initIcingaClient() will be called on context startup
        Set<Object> registeredObjects = ((Client) ReflectionTestUtils.getField(testling, "icingaClient")).getConfiguration().getInstances();
        assertEquals(registeredObjects.size(), 2);
        registeredObjects.forEach(e -> {
            if (e instanceof HttpAuthenticationFeature) {
                assertNotNull(ReflectionTestUtils.getField(e, "basicCredentials"));
                return;
            }
            if (e instanceof Icinga2RestCient.ErrorResponseFilter) {
                return;
            }
            throw new RuntimeException("not epected class of registered Object of 'icingaClient'");
        });
    }

    @Test
    public void testGetTargetCheckResult() throws Exception {
        assertEquals(testling.getTargetCheckResult().getUri().toString(),
                "https://my-icinga-host:5665/v1/actions/process-check-result?service=sakuli-icinga-test!incinga-test-suite");

    }
}