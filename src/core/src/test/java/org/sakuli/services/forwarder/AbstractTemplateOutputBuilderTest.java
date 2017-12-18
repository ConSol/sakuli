/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.forwarder;

import org.apache.commons.io.FileUtils;
import org.jtwig.JtwigModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.services.forwarder.checkmk.CheckMKProperties;
import org.sakuli.services.forwarder.checkmk.CheckMKTemplateOutputBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertEquals;

/**
 * @author Georgi Todorov
 */
public class AbstractTemplateOutputBuilderTest extends BaseTest {

    @Spy
    @InjectMocks
    private CheckMKTemplateOutputBuilder testling;
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliProperties sakuliProperties;
    @Mock
    private CheckMKProperties checkMKProperties;
    private Path tmp_output_builder_test;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tmp_output_builder_test = Files.createTempDirectory("tmp_output_builder_test");
    }

    @AfterMethod
    public void clean() throws Exception {
        FileUtils.deleteDirectory(tmp_output_builder_test.toFile());
    }

    @Test
    public void createModel() {
        JtwigModel model = testling.createModel();
        assertEquals(model.get("testsuite").get().getValue(), testSuite);
        assertEquals(model.get("sakuli").get().getValue(), sakuliProperties);
        assertEquals(model.get("checkmk").get().getValue(), checkMKProperties);
    }

    @DataProvider
    public Object[][] getTemplatePathDP() {
        return new Object[][]{
                {"Check_MK", "check_mk" + File.separator + "main.twig"},
                {"CHECKmk", "checkmk" + File.separator + "main.twig"},
                {"gearman", "gearman" + File.separator + "main.twig"},
                {"gearMAN", "gearman" + File.separator + "main.twig"},
        };
    }

    @Test(dataProvider = "getTemplatePathDP")
    public void getTemplatePath(String converterName, String expectedTemplate) throws Exception {
        // to ensure file exists
        String expectedTemplatePath = tmp_output_builder_test.toString() + File.separator + expectedTemplate;
        Path expectedFile = Paths.get(expectedTemplatePath);
        Files.createDirectory(expectedFile.getParent());
        Files.createFile(expectedFile);

        doReturn(tmp_output_builder_test.toString()).when(sakuliProperties).getForwarderTemplateFolder();
        doReturn(converterName).when(testling).getConverterName();
        assertEquals(testling.getTemplatePath().toString(), expectedTemplatePath);
    }

    @Test(expectedExceptions = FileNotFoundException.class,
            expectedExceptionsMessageRegExp = "JTwig template folder for check_MK could not be found under '.*tmp_output_builder_.*check_mk.main.twig'")
    public void testGetTemplatePathFileNotExists() throws Exception {
        doReturn(tmp_output_builder_test.toString()).when(sakuliProperties).getForwarderTemplateFolder();
        doReturn("check_MK").when(testling).getConverterName();
        testling.getTemplatePath();
    }
}
