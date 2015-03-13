/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
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
}
