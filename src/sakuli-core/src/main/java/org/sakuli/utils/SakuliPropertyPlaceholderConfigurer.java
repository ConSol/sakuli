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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Overrides the default {@link PropertyPlaceholderConfigurer} to dynamically load the properties files in  the {@link
 * TestSuiteProperties#TEST_SUITE_FOLDER} and {@link SakuliProperties#SAKULI_HOME_FOLDER}.
 *
 * @author tschneck Date: 11.05.14
 */
public class SakuliPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    public static String ENCRYPTION_KEY_VALUE;
    public static String TEST_SUITE_FOLDER_VALUE;
    public static String SAKULI_HOME_FOLDER_VALUE;
    public static String TEST_SUITE_BROWSER;
    public static String ENCRYPTION_INTERFACE_VALUE;
    private static Logger LOGGER = LoggerFactory.getLogger(SakuliPropertyPlaceholderConfigurer.class);
    protected boolean loadSakuliProperties = true;
    protected boolean loadSakuliDefaultProperties = true;
    protected boolean loadTestSuiteProperties = true;

    /**
     * Determines the encryption mode of the CLI based values and set it to the assigned 'props'
     */
    public static void assignEncryptionProperties(Properties props) {
        if (isNotEmpty(ENCRYPTION_INTERFACE_VALUE)) {
            props.setProperty(CipherProperties.ENCRYPTION_MODE, CipherProperties.ENCRYPTION_MODE_INTERFACE);
            if (ENCRYPTION_INTERFACE_VALUE.equals("auto")) {
                props.setProperty(CipherProperties.ENCRYPTION_INTERFACE, "");
                props.setProperty(CipherProperties.ENCRYPTION_INTERFACE_AUTODETECT, "true");
            } else {
                props.setProperty(CipherProperties.ENCRYPTION_INTERFACE, ENCRYPTION_INTERFACE_VALUE);
                props.setProperty(CipherProperties.ENCRYPTION_INTERFACE_AUTODETECT, "false");
            }
        }
        if (isNotEmpty(ENCRYPTION_KEY_VALUE)) {
            props.setProperty(CipherProperties.ENCRYPTION_MODE, CipherProperties.ENCRYPTION_MODE_ENVIRONMENT);
            props.setProperty(CipherProperties.ENCRYPTION_KEY, ENCRYPTION_KEY_VALUE);
        }
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {
        //load properties set by command args
        props.put(TestSuiteProperties.TEST_SUITE_FOLDER, TEST_SUITE_FOLDER_VALUE);
        props.put(SakuliProperties.SAKULI_HOME_FOLDER, SAKULI_HOME_FOLDER_VALUE);

        //load common sakuli properties
        loadSakuliDefaultProperties(props);
        loadSakuliProperties(props);
        loadTestSuiteProperties(props);
        loadEnvironmentVariablesToProperties(props);

        if (isNotEmpty(TEST_SUITE_BROWSER)) {
            props.setProperty(TestSuiteProperties.BROWSER_NAME, TEST_SUITE_BROWSER);
        }
        assignEncryptionProperties(props);

        super.loadProperties(props);
    }

    protected void loadSakuliDefaultProperties(Properties props) {
        String sakuliDefaultProperties = Paths.get(SAKULI_HOME_FOLDER_VALUE + SakuliProperties.CONFIG_FOLDER_APPEDER).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_DEFAULT_PROPERTIES_FILE_APPENDER;
        addPropertiesFromFile(props, sakuliDefaultProperties, loadSakuliDefaultProperties);
    }

    protected void loadSakuliProperties(Properties props) {
        String sakuliProperties = Paths.get(TEST_SUITE_FOLDER_VALUE).getParent().normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER;
        if (Files.exists(Paths.get(sakuliProperties))) {
            addPropertiesFromFile(props, sakuliProperties, loadSakuliProperties);
        }
    }

    protected void loadTestSuiteProperties(Properties props) {
        String testSuitePropFile = Paths.get(TEST_SUITE_FOLDER_VALUE).normalize().toAbsolutePath().toString() + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER;
        addPropertiesFromFile(props, testSuitePropFile, loadTestSuiteProperties);
    }

    /**
     * Reads in the properties for a specific file
     *
     * @param props    Properties to update
     * @param filePath path to readable properties file
     * @param active   activate or deactivate the  function
     */
    public void addPropertiesFromFile(Properties props, String filePath, boolean active) {
        if (active) {
            LOGGER.info("read in properties from '{}'", filePath);
            try {
                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(filePath);
                Iterator<String> keyIt = propertiesConfiguration.getKeys();
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    Object value = propertiesConfiguration.getProperty(key);
                    props.put(key, value);
                }
            } catch (ConfigurationException | NullPointerException e) {
                throw new RuntimeException("Error by reading the property file '" + filePath + "'", e);
            }
        }
    }

    protected String resolve(String string, Properties props) {
        if (string != null) {
            int i = string.indexOf("${");
            if (i >= 0) {
                String result = string.substring(0, i);
                int iLast = string.indexOf("}");
                String key = string.substring(i + 2, iLast);
                result += props.get(key) + string.substring(iLast + 1);
                return resolve(result, props);
            }
        }
        return string;
    }

    public boolean isLoadSakuliDefaultProperties() {
        return loadSakuliDefaultProperties;
    }

    public void setLoadSakuliDefaultProperties(boolean loadSakuliDefaultProperties) {
        this.loadSakuliDefaultProperties = loadSakuliDefaultProperties;
    }

    public boolean isLoadSakuliProperties() {
        return loadSakuliProperties;
    }

    public void setLoadSakuliProperties(boolean loadSakuliProperties) {
        this.loadSakuliProperties = loadSakuliProperties;
    }

    public boolean isLoadTestSuiteProperties() {
        return loadTestSuiteProperties;
    }

    public void setLoadTestSuiteProperties(boolean loadTestSuiteProperties) {
        this.loadTestSuiteProperties = loadTestSuiteProperties;
    }

    protected void loadEnvironmentVariablesToProperties(Properties props) {
        EnvironmentPropertyConfigurer.resolveDashedProperties(props);
    }
}
