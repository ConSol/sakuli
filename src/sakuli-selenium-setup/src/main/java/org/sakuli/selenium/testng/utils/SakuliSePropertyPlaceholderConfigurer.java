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

package org.sakuli.selenium.testng.utils;

import org.apache.commons.io.FileUtils;
import org.sakuli.SakuliCommonConfigExtractor;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.ocr.tessdata.OcrTessdataLibExtractor;
import org.sakuli.utils.ResourceHelper;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Properties;

import static org.sakuli.datamodel.properties.SakuliProperties.SAKULI_PROPERTIES_FILE_NAME;
import static org.sakuli.datamodel.properties.TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_NAME;

/**
 * @author Tobias Schneck
 */
public class SakuliSePropertyPlaceholderConfigurer extends SakuliPropertyPlaceholderConfigurer {
    public static final String SAKULI_TEMPFOLDER_NAME = ".sakuli";
    private static Logger LOGGER = LoggerFactory.getLogger(SakuliSePropertyPlaceholderConfigurer.class);
    private static Properties testsuiteJavaProps = new Properties();
    private static Properties sakuliJavaProps = new Properties();

    public static void setTestSuiteProperty(String key, String value) {
        testsuiteJavaProps.setProperty(key, value);
    }

    public static void setSakuliProperty(String key, String value) {
        sakuliJavaProps.setProperty(key, value);
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {
        SAKULI_HOME_FOLDER_VALUE = extractSakuliDefaultConfigFiles().toString();
        SakuliProperties.DEFAULT_TESSDATA_LIB_FOLDER = extractOcrLibFiles().toString();
        super.loadProperties(props);
    }

    protected Path extractSakuliDefaultConfigFiles() throws IOException {
        return SakuliCommonConfigExtractor.extract(getSakuliTempRoot()).getParent();
    }

    protected Path extractOcrLibFiles() throws IOException {
        return OcrTessdataLibExtractor.extract(getSakuliTempRoot());
    }

    public Path getSakuliTempRoot() throws IOException {
        final Path tmpRoot = FileUtils.getTempDirectory().toPath().resolve(SAKULI_TEMPFOLDER_NAME);
        if (!Files.exists(tmpRoot)) {
            FileUtils.forceMkdir(tmpRoot.toFile());
        }
        return tmpRoot;
    }

    @Override
    protected void loadSakuliDefaultProperties(Properties props) {
        super.loadSakuliDefaultProperties(props);
        props.put(TestSuiteProperties.LOAD_TEST_CASES_AUTOMATIC_PROPERTY, "false");
    }

    @Override
    protected void loadSakuliProperties(Properties props) {
        try {
            final Path sakuliProperties = ResourceHelper.getClasspathResource(this.getClass(), "/" + SAKULI_PROPERTIES_FILE_NAME, "");
            addPropertiesFromFile(props, sakuliProperties.toAbsolutePath().toString(), isLoadSakuliProperties());
        } catch (NoSuchFileException e) {
            LOGGER.info("'" + SAKULI_PROPERTIES_FILE_NAME + "' file not present ... skip loading");
        }
        props.putAll(sakuliJavaProps);
    }

    @Override
    protected void loadTestSuiteProperties(Properties props) {
        try {
            final Path testsuiteProperties = ResourceHelper.getClasspathResource(this.getClass(), "/" + TEST_SUITE_PROPERTIES_FILE_NAME, "");
            addPropertiesFromFile(props, testsuiteProperties.toAbsolutePath().toString(), isLoadTestSuiteProperties());
        } catch (NoSuchFileException e) {
            LOGGER.info("'" + TEST_SUITE_PROPERTIES_FILE_NAME + "' file not present ... skip loading");
        }
        props.putAll(testsuiteJavaProps);
    }

}
