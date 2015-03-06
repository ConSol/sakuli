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

package org.sakuli.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.PropertyHolder;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.loader.BeanLoader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SakuliPropertyPlaceholderConfigurerTest extends BaseTest {
    @Spy
    private SakuliPropertyPlaceholderConfigurer testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testling.setWritePropertiesToSahiConfig(false);
        testling.setLoadTestSuiteProperties(true);
        testling.setLoadSakuliProperties(true);
    }

    @Test
    public void testLoadPropertiesTestSuiteFolder() throws Exception {
        Properties props = spy(new Properties());
        testling.setLoadSakuliProperties(false);
        testling.loadProperties(props);
        verify(props).put(TestSuiteProperties.TEST_SUITE_FOLDER, TEST_FOLDER_PATH);
        verify(props).put(SakuliProperties.INCLUDE_FOLDER, INCLUDE_FOLDER_PATH);
        verify(props).put(SahiProxyProperties.PROXY_HOME_FOLDER, SAHI_FOLDER_PATH);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(INCLUDE_FOLDER_PATH).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER, true);
        verify(testling).addPropertiesFromFile(props,
                Paths.get(TEST_FOLDER_PATH).normalize().toAbsolutePath().toString() + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).modifyPropertiesConfiguration(anyString(), anyListOf(String.class), any(Properties.class));
        assertNull(props.getProperty(ActionProperties.ENCRYPTION_INTERFACE_AUTODETECT), null);
        assertEquals(props.getProperty(TestSuiteProperties.SUITE_ID), "0001_testsuite_example");
    }

    @Test
    public void testLoadPropertiesIncludeFolder() throws Exception {
        Properties props = spy(new Properties());
        testling.setLoadTestSuiteProperties(false);
        testling.loadProperties(props);
        verify(props).put(TestSuiteProperties.TEST_SUITE_FOLDER, TEST_FOLDER_PATH);
        verify(props).put(SakuliProperties.INCLUDE_FOLDER, INCLUDE_FOLDER_PATH);
        verify(testling).addPropertiesFromFile(props,
                Paths.get(INCLUDE_FOLDER_PATH).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(TEST_FOLDER_PATH).normalize().toAbsolutePath().toString() + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER, true);

        assertNotNull(props.getProperty(SakuliProperties.INCLUDE_FOLDER));
        assertNull(props.getProperty(TestSuiteProperties.SUITE_ID));
    }

    @Test
    public void testLoadPropertiesSahiHomeNotset() throws Exception {
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = "";
        Properties props = spy(new Properties());
        testling.loadProperties(props);
        verify(props, never()).put(SahiProxyProperties.PROXY_HOME_FOLDER, "");
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = SAHI_FOLDER_PATH;
    }

    @Test
    public void testAddPropertiesFromFile() throws Exception {
        Path sakuliPropFile = Paths.get(getClass().getResource("/JUnit-sakuli.properties").toURI());
        assertTrue(Files.exists(sakuliPropFile));
        Properties props = new Properties();
        testling.addPropertiesFromFile(props, sakuliPropFile.toAbsolutePath().toString(), true);
        assertTrue(props.size() > 0);
        assertEquals(props.getProperty(ActionProperties.TAKE_SCREENSHOTS), "true", "assert of property " + ActionProperties.TAKE_SCREENSHOTS);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error by reading the property file '.*invalid.properties'")
    public void testAddPropertiesFromFileRuntimeException() throws Exception {
        Properties props = new Properties();
        testling.addPropertiesFromFile(props, "invalid.properties", true);
    }

    @Test
    public void testTestSuiteFolder() throws IOException {
        PropertyHolder properties = BeanLoader.loadBean(PropertyHolder.class);
        Path tsFolder = Paths.get(properties.getTestSuiteFolder());

        assertTrue(Files.exists(tsFolder), "test suite folder doesn't exists or have not been set correctly");
        System.out.println(tsFolder.toFile().getAbsolutePath());
        assertTrue(tsFolder.toFile().getAbsolutePath().contains(TEST_FOLDER_PATH));
    }

    @Test
    public void testIncludeFolder() throws IOException {
        SakuliProperties properties = BeanLoader.loadBean(SakuliProperties.class);

        assertTrue(Files.exists(properties.getIncludeFolder()), "include folder doesn't exists");
        assertTrue(properties.getIncludeFolder().toString().contains(INCLUDE_FOLDER_PATH.substring(2)));

        assertNotNull(properties.getLogPattern());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModifySahiProperties() throws Exception {
        Properties props = new Properties();
        doNothing().when(testling).modifyPropertiesConfiguration(anyString(), anyListOf(String.class), any(Properties.class));
        testling.setWritePropertiesToSahiConfig(true);
        testling.modifySahiProperties(props);

        ArgumentCaptor<List> argumentCaptorSahiProp = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> argumentCaptorLogProp = ArgumentCaptor.forClass(List.class);
        verify(testling).modifyPropertiesConfiguration(contains(SahiProxyProperties.SAHI_PROPERTY_FILE_APPENDER), argumentCaptorSahiProp.capture(), eq(props));
        verify(testling).modifyPropertiesConfiguration(contains(SahiProxyProperties.SAHI_LOG_PROPERTY_FILE_APPENDER), argumentCaptorLogProp.capture(), eq(props));

        assertTrue(argumentCaptorSahiProp.getValue().containsAll(
                        Arrays.asList("logs.dir", "ext.http.proxy.enable", "ext.http.proxy.host", "ext.http.proxy.port",
                                "ext.http.proxy.auth.enable", "ext.http.proxy.auth.name", "ext.http.proxy.auth.password",
                                "ext.https.proxy.enable", "ext.https.proxy.host", "ext.https.proxy.port",
                                "ext.https.proxy.auth.enable", "ext.https.proxy.auth.name", "ext.https.proxy.auth.password",
                                "ext.http.both.proxy.bypass_hosts")),
                "currently contains: " + argumentCaptorSahiProp.getValue().toString());
        assertTrue(argumentCaptorLogProp.getValue().containsAll(
                        Arrays.asList("handlers", "java.util.logging.ConsoleHandler.level", "java.util.logging.FileHandler.level",
                                "java.util.logging.ConsoleHandler.formatter", "java.util.logging.FileHandler.formatter",
                                "java.util.logging.FileHandler.limit", "java.util.logging.FileHandler.count",
                                "java.util.logging.FileHandler.pattern")),
                "currently contains: " + argumentCaptorLogProp.getValue().toString());
    }

    @Test
    public void testModifyPropertyFile() throws Exception {
        Path targetProps = Paths.get(this.getClass().getResource("properties-test/target.properties").toURI());

        Properties basicProps = new Properties();
        basicProps.put("test.prop.1", "test-value-1");
        basicProps.put("test.prop.2", "test-value-2");
        testling.modifyPropertiesConfiguration(targetProps.toAbsolutePath().toString(), Arrays.asList("test.prop.1", "test.prop.2"), basicProps);
        PropertiesConfiguration targetProfConf = new PropertiesConfiguration(targetProps.toFile());
        assertEquals(targetProfConf.getString("test.prop.1"), "test-value-1");
        assertEquals(targetProfConf.getString("test.prop.2"), "test-value-2");

        testling.restoreProperties();
        targetProfConf = new PropertiesConfiguration(targetProps.toFile());
        assertEquals(targetProfConf.getString("test.prop.1"), "xyz");
        assertEquals(targetProfConf.getString("test.prop.2"), "zyx");
    }
}