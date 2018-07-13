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

package org.sakuli.aop;

import net.sf.sahi.request.HttpRequest;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.loader.BeanLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Test for {@link SahiHeaderAspect}
 *
 * @author tschneck
 * Date: 2/25/16
 */
public class SahiHeaderAspectTest extends AopBaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(SahiHeaderAspect.class);
    private HttpRequest testling;

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        initMocks();
        InputStream input = new ByteArrayInputStream(new byte[]{});
        testling = new HttpRequest(input);
    }

    @Test
    public void testRemoveHeaderDisabeld() throws Exception {
        final SahiHeaderAspect sahiHeaderAspect = BeanLoader.loadBean(SahiHeaderAspect.class);
        ReflectionTestUtils.setField(sahiHeaderAspect, "removeAuthorizationHeader", false);
        LOGGER.debug("SAHI this is not the correct line!");
        testling.removeHeader("Authorization");
        assertLastLine(logFile, "SAHI", LogLevel.DEBUG, "SAHI skip remove Header 'Authorization'");
    }

    @Test
    public void testRemoveHeaderNotModiefied() throws Exception {
        final SahiHeaderAspect sahiHeaderAspect = BeanLoader.loadBean(SahiHeaderAspect.class);
        ReflectionTestUtils.setField(sahiHeaderAspect, "removeAuthorizationHeader", true);
        final String controllMessage = "SAHI this is the expected line!";
        LOGGER.debug(controllMessage);
        testling.removeHeader("Authorization");
        assertLastLine(logFile, "SAHI", LogLevel.DEBUG, controllMessage);
    }

}