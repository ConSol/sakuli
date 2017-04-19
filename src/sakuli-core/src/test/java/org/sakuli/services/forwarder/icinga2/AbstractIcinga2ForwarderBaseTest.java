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

import org.sakuli.services.AbstractServiceBaseTest;
import org.testng.annotations.AfterClass;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
public abstract class AbstractIcinga2ForwarderBaseTest extends AbstractServiceBaseTest {
    public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Override
    protected String getTestContextPath() {
        System.setProperty(SPRING_PROFILES_ACTIVE, "ICINGA2");
        return "icinga2Test-beanRefFactory.xml";
    }

    @Override
    protected String getTestFolderPath() {
        return getResource("icinga2-testsuite", this.getClass());
    }

    @Override
    protected void initBaseActionLoader() {
        //not needed
    }

    @AfterClass
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        System.clearProperty(SPRING_PROFILES_ACTIVE);
    }
}
