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

package org.sakuli.javaDSL.utils;

import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * @author Tobias Schneck
 */
public class SakuliJavaPropertyPlaceholderConfigurer extends SakuliPropertyPlaceholderConfigurer {

    @Override
    protected void loadSakuliDefaultProperties(Properties props) {
        super.loadSakuliDefaultProperties(props);
        props.put(TestSuiteProperties.LOAD_TEST_CASES_AUTOMATIC_PROPERTY, "false");
    }

    @Override
    protected void modifySahiProperties(Properties props) {
        String isUiTest = props.getProperty(TestSuiteProperties.TEST_SUITE_IS_UI_TEST);
        writePropertiesToSahiConfig = !Boolean.valueOf(isUiTest);
        super.modifySahiProperties(props);
    }
}
