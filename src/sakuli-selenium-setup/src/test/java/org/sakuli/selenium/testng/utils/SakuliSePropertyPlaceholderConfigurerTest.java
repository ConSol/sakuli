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

package org.sakuli.selenium.testng.utils;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.SakuliCommonConfigExtractor;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.ocr.tessdata.OcrTessdataLibExtractor;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 4/25/17
 */
public class SakuliSePropertyPlaceholderConfigurerTest {

    @Spy
    private SakuliSePropertyPlaceholderConfigurer testling;
    private Path tmpFolder;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tmpFolder = Files.createTempDirectory(Paths.get("target"), ".sakuli-tmp");
        when(testling.getSakuliTempRoot()).thenReturn(tmpFolder);
        doNothing().when(testling).loadSakuliDefaultProperties(any());
        doNothing().when(testling).loadSakuliProperties(any());
        doNothing().when(testling).loadTestSuiteProperties(any());
    }

    @Test
    public void testExtractSakuliDefaultConfigFiles() throws Exception {
        final Path target = testling.extractSakuliDefaultConfigFiles();
        assertNotNull(target);
        assertTrue(Files.exists(target));
        SakuliCommonConfigExtractor.checkTestFilePresent(target.resolve("config"));
        testling.loadProperties(mock(Properties.class));
        assertEquals(SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE, target.toString());
    }

    @Test
    public void testExtractSakuliOcrLibFiles() throws Exception {
        final Path target = testling.extractOcrLibFiles();
        assertNotNull(target);
        assertTrue(Files.exists(target));
        OcrTessdataLibExtractor.checkTessdataLibFolderContent(target);
        testling.loadProperties(mock(Properties.class));
        assertEquals(SakuliProperties.DEFAULT_TESSDATA_LIB_FOLDER, target.toString());
    }

}