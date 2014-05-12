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

import de.consol.sakuli.starter.proxy.SahiProxy;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;

/**
 * Overrides the default {@link PropertyPlaceholderConfigurer} to dynamically load the properties files in  the {@link SakuliProperties#TEST_SUITE_FOLDER}
 * and {@link SakuliProperties#INCLUDE_FOLDER}.
 *
 * @author tschneck
 *         Date: 11.05.14
 */
public class SakuliPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    public static String TEST_SUITE_FOLDER_VALUE;
    public static String INCLUDE_FOLDER_VALUE;
    public static String SAHI_PROXY_HOME_VALUE;

    //TODO TS config SakuliStarter application context to this logic

    @Override
    protected void loadProperties(Properties props) throws IOException {
        //load properties set by command args
        props.put(SakuliProperties.TEST_SUITE_FOLDER, TEST_SUITE_FOLDER_VALUE);
        props.put(SakuliProperties.INCLUDE_FOLDER, INCLUDE_FOLDER_VALUE);

        //load common sakuli properties
        String sakuliProperties = Paths.get(INCLUDE_FOLDER_VALUE).toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER;
        addPropertiesFromFile(props, sakuliProperties);
        //load test suite properties
        String testSuitePropFile = Paths.get(TEST_SUITE_FOLDER_VALUE).toAbsolutePath().toString() + SakuliProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER;
        addPropertiesFromFile(props, testSuitePropFile);

        //override if set sahi proxy home
        if (StringUtils.isNotEmpty(SAHI_PROXY_HOME_VALUE)) {
            props.setProperty(SahiProxy.SAHI_PROXY_HOME, SAHI_PROXY_HOME_VALUE);
        }
        super.loadProperties(props);
    }

    /**
     * Reads in the properties for a specific file
     *
     * @param props
     * @param filePath
     */
    protected void addPropertiesFromFile(Properties props, String filePath) {
        System.out.println("read in properties from '" + filePath + "'");
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(filePath);
            Iterator<String> keyIt = propertiesConfiguration.getKeys();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Object value = propertiesConfiguration.getProperty(key);
                props.put(key, value);
            }
        } catch (ConfigurationException | NullPointerException e) {
            throw new RuntimeException("Error by reading the property file '" + filePath + "'");
        }

    }

}
