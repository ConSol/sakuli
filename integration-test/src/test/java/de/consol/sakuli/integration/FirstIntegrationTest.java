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

package de.consol.sakuli.integration;

import de.consol.sakuli.integration.demo.DemoApplication;
import org.testng.annotations.Test;

/**
 * @author tschneck
 *         Date: 09.04.14
 */
@Test(groups = IntegrationTest.GROUP)
public class FirstIntegrationTest {

    @Test
    public void testTest() throws Exception {
        DemoApplication demoApplication = new DemoApplication();
        System.out.println("............................START");
        Thread thread = new Thread(demoApplication);
        thread.start();

        Thread.sleep(10000);
        System.out.println("............................STOP");
        demoApplication.stop();
    }
}
