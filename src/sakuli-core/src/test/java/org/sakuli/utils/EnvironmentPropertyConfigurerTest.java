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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.util.Map;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 7/18/17
 */
public class EnvironmentPropertyConfigurerTest {
    @DataProvider(name = "envPropMap")
    public static Object[][] envPropMap() {
        return new Object[][]{
                //prob key, prob value, env key, env value
                {"sakuli.test.env", "my-val", "SAKULI_TEST_ENV", "env-val"},
                {"sakuli.myTestProp.key", "", "SAKULI_MYTESTPROP_KEY", "env-val"},
                {"sakuli.MY_Prop.key", "PROPSVAL", "SAKULI_MY_PROP_KEY", "env"},
                {"sakuliempty", "PROPSVAL", "SAKULIEMPTY", ""}
        };
    }

    @Test
    public void testResolveDashedPropertiesEmpty() throws Exception {
        final Properties properties = EnvironmentPropertyConfigurer.resolveDashedProperties(new Properties());
        assertEquals(properties.size(), 0);
    }

    @Test(dataProvider = "envPropMap")
    public void testResolveDashedProperties(String key, String value, String keyENV, String valueENV) throws Exception {
        Properties props = new Properties();
        props.put(key, value);
        props = EnvironmentPropertyConfigurer.resolveDashedProperties(props);
        assertEquals(props.size(), 1);
        assertEquals(props.get(key), value);

        Map<String, String> envMap = Maps.newHashMap(System.getenv());
        envMap.put(keyENV, valueENV);
        props = EnvironmentPropertyConfigurer.resolveDashedProperties(props, envMap);
        assertEquals(props.size(), 1);
        assertEquals(props.get(key), valueENV);
    }

}