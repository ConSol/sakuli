/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.consol.sakuli.javaDSL.utils;

import de.consol.sakuli.datamodel.properties.SakuliProperties;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Tobias Schneck
 */
public class SakuliJavaPropertyPlaceholderConfigurer extends SakuliPropertyPlaceholderConfigurer {
    public static String JAVA_TEST_SUITE_FOLDER;

    @Override
    protected void loadCommonSakuliProperties(Properties props) {
        super.loadCommonSakuliProperties(props);
        String sakuliJavaProperties = Paths.get(JAVA_TEST_SUITE_FOLDER).normalize().toAbsolutePath().toString() + SakuliProperties.SAKULI_PROPERTIES_FILE_APPENDER;
        if (Files.exists(Paths.get(sakuliJavaProperties))) {
            addPropertiesFromFile(props, sakuliJavaProperties, loadSakuliProperties);
        }
        props.put(TestSuiteProperties.LOAD_TEST_CASES_AUTOMATIC_PROPERTY, "false");
    }
}
