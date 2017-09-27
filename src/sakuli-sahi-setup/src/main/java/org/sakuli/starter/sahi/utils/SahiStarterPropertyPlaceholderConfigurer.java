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

package org.sakuli.starter.sahi.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Overrides the default {@link PropertyPlaceholderConfigurer} to dynamically load the properties files in  the {@link
 * TestSuiteProperties#TEST_SUITE_FOLDER} and {@link SakuliProperties#SAKULI_HOME_FOLDER}.
 *
 * @author tschneck Date: 11.05.14
 */
public class SahiStarterPropertyPlaceholderConfigurer extends SakuliPropertyPlaceholderConfigurer {

    public static String SAHI_HOME_VALUE;
    private static Logger LOGGER = LoggerFactory.getLogger(SahiStarterPropertyPlaceholderConfigurer.class);
    protected boolean writePropertiesToSahiConfig = true;
    private Map<String, Map<String, Object>> modifiedSahiConfigProps;

    public SahiStarterPropertyPlaceholderConfigurer() {
        modifiedSahiConfigProps = new HashMap<>();
    }

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
        super.loadProperties(props);
        loadEnvironmentVariablesToProperties(props);
        //overwrite if set sahi proxy home
        if (isNotEmpty(SAHI_HOME_VALUE)) {
            props.setProperty(SahiProxyProperties.PROXY_HOME_FOLDER, SAHI_HOME_VALUE);
            props.setProperty(SahiProxyProperties.PROXY_CONFIG_FOLDER, SAHI_HOME_VALUE + File.separator + "userdata");
        }
        if (isNotEmpty(TEST_SUITE_BROWSER)) {
            props.setProperty(TestSuiteProperties.BROWSER_NAME, TEST_SUITE_BROWSER);
        }
        assignEncryptionProperties(props);

        modifySahiProperties(props);
    }

    protected void loadEnvironmentVariablesToProperties(Properties props) {
        EnvironmentPropertyConfigurer.resolveDashedProperties(props);
    }

    @PreDestroy
    public void restoreProperties() {
        try {
            for (Map.Entry<String, Map<String, Object>> entry : modifiedSahiConfigProps.entrySet()) {
                String propFile = entry.getKey();
                LOGGER.debug("restore properties file '{}' with properties '{}'", propFile, entry.getValue());
                PropertiesConfiguration propConfig = new PropertiesConfiguration(propFile);
                propConfig.setAutoSave(true);
                for (Map.Entry<String, Object> propEntry : entry.getValue().entrySet()) {
                    String propKey = propEntry.getKey();
                    if (propConfig.containsKey(propKey)) {
                        propConfig.clearProperty(propKey);
                    }
                    propConfig.addProperty(propKey, propEntry.getValue());
                }
            }
        } catch (ConfigurationException e) {
            LOGGER.error("Error in restore sahi config properties", e);
        }
    }

    protected void modifySahiProperties(Properties props) {
        if (writePropertiesToSahiConfig) {
            String sahiConfigFolderPath = resolve(props.getProperty(SahiProxyProperties.PROXY_CONFIG_FOLDER), props);

            String sahiPropConfig = Paths.get(sahiConfigFolderPath + SahiProxyProperties.SAHI_PROPERTY_FILE_APPENDER).normalize().toAbsolutePath().toString();
            modifyPropertiesConfiguration(sahiPropConfig, SahiProxyProperties.userdataPropertyNames, props);
            modifySahiProxyPortPropertiesConfiguration(sahiPropConfig, props);
            String sahiLogPropConfig = Paths.get(sahiConfigFolderPath + SahiProxyProperties.SAHI_LOG_PROPERTY_FILE_APPENDER).normalize().toAbsolutePath().toString();
            modifyPropertiesConfiguration(sahiLogPropConfig, SahiProxyProperties.logPropertyNames, props);
        }
    }


    /**
     * Modifies the properties file 'propFilePathToConfig' with assigned key from the resource properties.
     */
    protected void modifyPropertiesConfiguration(String propFilePathToConfig, List<String> updateKeys, Properties resourceProps) {
        try {
            PropertiesConfiguration propConfig = new PropertiesConfiguration(propFilePathToConfig);
            propConfig.setAutoSave(true);
            Properties temProps = new Properties();
            for (String propKey : updateKeys) {
                String resolve = resolve(resourceProps.getProperty(propKey), resourceProps);
                if (resolve != null) {
                    if (propConfig.containsKey(propKey)) {
                        addToModifiedPropertiesMap(propFilePathToConfig, propKey, propConfig.getProperty(propKey));
                        propConfig.clearProperty(propKey);
                    }
                    temProps.put(propKey, resolve);
                    propConfig.addProperty(propKey, resolve);
                }
            }
            LOGGER.debug("modify properties file '{}' with '{}'", propFilePathToConfig, temProps.toString());
        } catch (ConfigurationException e) {
            LOGGER.error("modify sahi properties went wrong", e);
        }

    }

    /**
     * writes the {@link SahiProxyProperties#PROXY_PORT} value as {@link SahiProxyProperties#SAHI_PROPERTY_PROXY_PORT_MAPPING}
     * property to sahiConfigPropertyFilePath!
     */
    protected void modifySahiProxyPortPropertiesConfiguration(String sahiConfigPropertyFilePath, Properties props) {
        final String sahiProxyPort = props.getProperty(SahiProxyProperties.PROXY_PORT);
        if (sahiProxyPort != null) {
            try {
                PropertiesConfiguration propConfig = new PropertiesConfiguration(sahiConfigPropertyFilePath);
                propConfig.setAutoSave(true);
                final String sahiMappingPropertyProxyPort = SahiProxyProperties.SAHI_PROPERTY_PROXY_PORT_MAPPING;

                if (propConfig.containsKey(sahiMappingPropertyProxyPort)) {
                    propConfig.clearProperty(sahiMappingPropertyProxyPort);
                }
                //remove property after the test execution, so that the installation can't break
                addToModifiedPropertiesMap(sahiConfigPropertyFilePath, sahiMappingPropertyProxyPort, null);
                propConfig.addProperty(sahiMappingPropertyProxyPort, sahiProxyPort);
                LOGGER.debug("modify properties file '{}' with '{}={}'", sahiConfigPropertyFilePath, sahiMappingPropertyProxyPort, sahiProxyPort);
            } catch (ConfigurationException e) {
                LOGGER.error("modify sahi properties went wrong", e);
            }
        }
    }

    protected void addToModifiedPropertiesMap(String propFilePath, String propKey, Object propertyValue) {
        Map<String, Object> propMap = modifiedSahiConfigProps.get(propFilePath);
        if (propMap == null) {
            propMap = new HashMap<>();
        }
        propMap.put(propKey, propertyValue);
        modifiedSahiConfigProps.put(propFilePath, propMap);
    }

    public boolean isWritePropertiesToSahiConfig() {
        return writePropertiesToSahiConfig;
    }

    public void setWritePropertiesToSahiConfig(boolean writePropertiesToSahiConfig) {
        this.writePropertiesToSahiConfig = writePropertiesToSahiConfig;
    }
}
