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

package de.consol.sakuli.utils;

import de.consol.sakuli.BaseTest;
import de.consol.sakuli.starter.proxy.SahiProxy;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SakuliPropertyPlaceholderConfigurerTest extends BaseTest {
    @Spy
    private SakuliPropertyPlaceholderConfigurer testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadProperties() throws Exception {
        Properties props = spy(new Properties());
        testling.loadProperties(props);
        verify(props).put(SakuliProperties.TEST_SUITE_FOLDER, TEST_FOLDER_PATH);
        verify(props).put(SakuliProperties.INCLUDE_FOLDER, INCLUDE_FOLDER_PATH);
        verify(props).put(SahiProxy.SAHI_PROXY_HOME, SAHI_FOLDER_PATH);
        verify(testling).addPropertiesFromFile(props, Paths.get(INCLUDE_FOLDER_PATH).toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER);
        verify(testling).addPropertiesFromFile(props, Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString() + SakuliProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER);

        assertEquals(props.getProperty(SakuliProperties.ENCRYPTION_INTERFACE_TEST_MODE), "true");
        assertEquals(props.getProperty(SakuliProperties.TEST_SUITE_ID), "0001_testsuite_example");
    }

    @Test
    public void testLoadPropertiesSahiHomeNotset() throws Exception {
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = "";
        Properties props = spy(new Properties());
        testling.loadProperties(props);
        verify(props, never()).put(SahiProxy.SAHI_PROXY_HOME, "");
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = SAHI_FOLDER_PATH;
    }

    @Test
    public void testAddPropertiesFromFile() throws Exception {
        Path sakuliPropFile = Paths.get(getClass().getResource("/JUnit-sakuli.properties").toURI());
        assertTrue(Files.exists(sakuliPropFile));
        Properties props = new Properties();
        testling.addPropertiesFromFile(props, sakuliPropFile.toAbsolutePath().toString());
        assertTrue(props.size() > 0);
        assertEquals(props.getProperty(SakuliProperties.ENCRYPTION_INTERFACE_TEST_MODE), "true");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error by reading the property file '.*invalid.properties'")
    public void testAddPropertiesFromFileRuntimeException() throws Exception {
        Properties props = new Properties();
        testling.addPropertiesFromFile(props, "invalid.properties");
    }
}