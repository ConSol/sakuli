/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

import jersey.repackaged.com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Helper Class to resolve environment vars to properties
 *
 * @author tschneck
 *         Date: 7/18/17
 */
public class EnvironmentPropertyConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentPropertyConfigurer.class);

    /**
     * Maps environment vars like 'SAKULI_LOG_FOLDER' to the sakuli property 'sakuli.log.folder' automatically by the name.
     * Only predefined (and known) properties of `sakuli-default.properties will be updated.
     *
     * @param properties prefilled properties, which should be checked
     * @return updated properties
     */
    public static Properties resolveDashedProperties(Properties properties) {
        final Map<String, String> envEntries = System.getenv();
        return resolveDashedProperties(properties, envEntries);
    }

    static Properties resolveDashedProperties(Properties properties, Map<String, String> envEntries) {
        //stream over all known properties
        properties.stringPropertyNames().parallelStream()
                .filter(Objects::nonNull)
                // sakuli.test.env -> SAKULI_TEST_ENV , sakuli.test.env
                .map(propKey -> Maps.immutableEntry(propKey.toUpperCase().replaceAll("\\.", "_"), propKey))
                // environment contains SAKULI_TEST_ENV ?
                .filter(keyTupel -> envEntries.containsKey(keyTupel.getKey()))
                // sakuli.test.env, <value from env>
                .map(keyTupel -> mapEnvValueToPropKey(envEntries, keyTupel))
                // update properties
                .forEach(e -> properties.put(e.getKey(), e.getValue()));
        return properties;
    }

    /**
     * Resolves from the environment map, the fitting value of the Entry {@code <environment-key, property-key>} and logs the usage.
     */
    private static Map.Entry<String, String> mapEnvValueToPropKey(Map<String, String> envEntries, Map.Entry<String, String> keyTupel) {
        final String envKey = keyTupel.getKey();
        final String propertyKey = keyTupel.getValue();
        final String finalValue = envEntries.getOrDefault(envKey, "");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("use environment variable '{}={}' value instead of property '{}'", envKey, finalValue, propertyKey);
        } else {
            LOGGER.info("use environment variable '{}' value instead of property '{}'", envKey, propertyKey);
        }
        return Maps.immutableEntry(propertyKey, finalValue);
    }

}
