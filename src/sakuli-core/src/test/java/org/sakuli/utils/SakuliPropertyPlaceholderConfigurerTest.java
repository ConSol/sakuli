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

package org.sakuli.utils;

import org.apache.commons.lang3.StringUtils;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.PropertyHolder;
import org.sakuli.datamodel.properties.*;
import org.sakuli.loader.BeanLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SakuliPropertyPlaceholderConfigurerTest {

    public static final String PPROPERTY_TEST_FOLDER_PATH = BaseTest.getResource("/property_test/suite_folder");
    @Spy
    private SakuliPropertyPlaceholderConfigurer testling;

    @BeforeClass(alwaysRun = true)
    public void setContextProperties() {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = PPROPERTY_TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = BaseTest.SAKULI_HOME_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
        BeanLoader.refreshContext();
    }

    @AfterMethod
    @BeforeMethod
    public void setUpEncryptionVals() throws Exception {
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = null;
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = null;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testling.setLoadSakuliDefaultProperties(true);
        testling.setLoadSakuliProperties(true);
        testling.setLoadTestSuiteProperties(true);
    }

    @Test
    public void testLoadPropertiesTestSuiteFolder() throws Exception {
        Properties props = spy(new Properties());
        testling.setLoadSakuliProperties(false);
        testling.setLoadSakuliDefaultProperties(false);
        testling.loadProperties(props);
        verify(props).put(TestSuiteProperties.TEST_SUITE_FOLDER, PPROPERTY_TEST_FOLDER_PATH);
        verify(props).put(SakuliProperties.SAKULI_HOME_FOLDER, BaseTest.SAKULI_HOME_FOLDER_PATH);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(BaseTest.SAKULI_HOME_FOLDER_PATH).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_DEFAULT_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(PPROPERTY_TEST_FOLDER_PATH).getParent().normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER, true);
        verify(testling).addPropertiesFromFile(props,
                Paths.get(PPROPERTY_TEST_FOLDER_PATH).normalize().toAbsolutePath().toString() + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER, true);
        assertNull(props.getProperty(CipherProperties.ENCRYPTION_INTERFACE_AUTODETECT), null);
        assertEquals(props.getProperty(TestSuiteProperties.SUITE_ID), "0001_testsuite_example");
    }

    @Test
    public void testLoadSakuliDefaultPropertiesFolder() throws Exception {
        Properties props = spy(new Properties());
        testling.setLoadTestSuiteProperties(false);
        testling.setLoadSakuliProperties(false);
        testling.loadProperties(props);
        verify(props).put(TestSuiteProperties.TEST_SUITE_FOLDER, PPROPERTY_TEST_FOLDER_PATH);
        verify(props).put(SakuliProperties.SAKULI_HOME_FOLDER, BaseTest.SAKULI_HOME_FOLDER_PATH);
        verify(testling).addPropertiesFromFile(props,
                Paths.get(BaseTest.SAKULI_HOME_FOLDER_PATH + SakuliProperties.CONFIG_FOLDER_APPEDER).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_DEFAULT_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(PPROPERTY_TEST_FOLDER_PATH).normalize().toAbsolutePath().toString() + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(PPROPERTY_TEST_FOLDER_PATH).getParent().normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER, true);

        assertNotNull(props.getProperty(SakuliProperties.SAKULI_HOME_FOLDER));
        assertTrue(StringUtils.isBlank(props.getProperty(TestSuiteProperties.SUITE_ID)));
    }

    @Test
    public void testLoadSakuliPropertiesFolder() throws Exception {
        Properties props = spy(new Properties());
        testling.setLoadTestSuiteProperties(false);
        testling.setLoadSakuliDefaultProperties(false);
        testling.loadProperties(props);
        verify(props).put(TestSuiteProperties.TEST_SUITE_FOLDER, PPROPERTY_TEST_FOLDER_PATH);
        verify(props).put(SakuliProperties.SAKULI_HOME_FOLDER, BaseTest.SAKULI_HOME_FOLDER_PATH);
        verify(testling).addPropertiesFromFile(props,
                Paths.get(PPROPERTY_TEST_FOLDER_PATH).getParent().normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(BaseTest.SAKULI_HOME_FOLDER_PATH + SakuliProperties.CONFIG_FOLDER_APPEDER).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_DEFAULT_PROPERTIES_FILE_APPENDER, true);
        verify(testling, never()).addPropertiesFromFile(props,
                Paths.get(PPROPERTY_TEST_FOLDER_PATH).normalize().toAbsolutePath().toString() + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER, true);

        assertNotNull(props.getProperty(SakuliProperties.SAKULI_HOME_FOLDER));
        assertNull(props.getProperty(TestSuiteProperties.SUITE_ID));
        assertEquals(props.getProperty("sakuli.forwarder.gearman.server.host"), "test-gearman-host");
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

        assertTrue(Files.exists(tsFolder), "test suite folder doesn't exists or is not set correctly");
        BaseTest.assertContains(tsFolder.toAbsolutePath().toString(), PPROPERTY_TEST_FOLDER_PATH);
    }

    @Test
    public void testSakuliHomeFolder() throws IOException {
        SakuliProperties properties = BeanLoader.loadBean(SakuliProperties.class);

        assertTrue(Files.exists(properties.getSakuliHomeFolder()), "include folder doesn't exists");
        assertTrue(properties.getSakuliHomeFolder().toString().contains(BaseTest.SAKULI_HOME_FOLDER_PATH.substring(2)));

        assertNotNull(properties.getLogPattern());
    }

    @Test
    public void testTestSuiteBrowser() throws IOException {
        final String browserName = "chrome";
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_BROWSER = browserName;
        Properties props = new Properties();
        testling.loadProperties(props);
        assertEquals(props.get(TestSuiteProperties.BROWSER_NAME), browserName);
    }

    @Test
    public void testBrowserEnvironmentVariableOnly() throws IOException {
        final String val = "my-new-browser";
        doAnswer(a -> ((Properties) a.getArguments()[0]).put(TestSuiteProperties.BROWSER_NAME, val))
                .when(testling).loadEnvironmentVariablesToProperties(any());
        Properties props = new Properties();
        testling.loadProperties(props);
        assertEquals(props.get(TestSuiteProperties.BROWSER_NAME), val);
    }

    @Test
    public void testEncryptionEnvironment() throws IOException {
        final String key = "my-key-val";
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = key;
        Properties props = new Properties();
        testling.loadProperties(props);
        assertEquals(props.get(CipherProperties.ENCRYPTION_KEY), key);
        assertEquals(props.get(CipherProperties.ENCRYPTION_MODE), CipherProperties.ENCRYPTION_MODE_ENVIRONMENT);
    }

    @Test
    public void testEncryptionEnvironmentVariable() throws IOException {
        doAnswer(a -> ((Properties) a.getArguments()[0]).put(CipherProperties.ENCRYPTION_KEY, "some-other-key-val"))
                .when(testling).loadEnvironmentVariablesToProperties(any());
        final String key = "my-key-val";
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = key;
        Properties props = new Properties();
        testling.loadProperties(props);
        assertEquals(props.get(CipherProperties.ENCRYPTION_KEY), key);
        assertEquals(props.get(CipherProperties.ENCRYPTION_MODE), CipherProperties.ENCRYPTION_MODE_ENVIRONMENT);
    }

    @Test
    public void testEncryptionInterface() throws IOException {
        final String iname = "eth1";
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = iname;
        Properties props = new Properties();
        testling.loadProperties(props);
        assertEquals(props.get(CipherProperties.ENCRYPTION_INTERFACE), iname);
        assertEquals(props.get(CipherProperties.ENCRYPTION_MODE), CipherProperties.ENCRYPTION_MODE_INTERFACE);
        assertEquals(props.get(CipherProperties.ENCRYPTION_INTERFACE_AUTODETECT), "false");
    }

    @Test
    public void testEncryptionInterfaceAuto() throws IOException {
        final String iname = "auto";
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = iname;
        Properties props = new Properties();
        testling.loadProperties(props);
        assertEquals(props.get(CipherProperties.ENCRYPTION_INTERFACE), "");
        assertEquals(props.get(CipherProperties.ENCRYPTION_MODE), CipherProperties.ENCRYPTION_MODE_INTERFACE);
        assertEquals(props.get(CipherProperties.ENCRYPTION_INTERFACE_AUTODETECT), "true");
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
                        "ext.http.both.proxy.bypass_hosts", "ssl.client.keystore.type", "ssl.client.cert.path", "ssl.client.cert.password")),
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

    @Test
    public void testModifySahiProxyPortInPropertyFile() throws Exception {
        Path targetProps = Paths.get(this.getClass().getResource("properties-test/target.properties").toURI());

        Properties basicProps = new Properties();
        basicProps.put(SahiProxyProperties.PROXY_PORT, "9000");
        testling.modifySahiProxyPortPropertiesConfiguration(targetProps.toAbsolutePath().toString(), basicProps);
        PropertiesConfiguration targetProfConf = new PropertiesConfiguration(targetProps.toFile());
        assertEquals(targetProfConf.getString(SahiProxyProperties.SAHI_PROPERTY_PROXY_PORT_MAPPING), "9000");

        testling.restoreProperties();
        targetProfConf = new PropertiesConfiguration(targetProps.toFile());
        assertEquals(targetProfConf.getString(SahiProxyProperties.SAHI_PROPERTY_PROXY_PORT_MAPPING), null);
    }
}
