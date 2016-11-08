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

import org.jtwig.JtwigModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.services.forwarder.checkmk.CheckMKTemplateOutputBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createModel() {
        JtwigModel model = testling.createModel();
        assertEquals(model.get("testsuite").get().getValue(), testSuite);
    }

    @DataProvider
    public Object[][] getTemplatePathDP() {
        return new Object[][] {
                { "/tmp/template", "Check_MK", "/tmp/template/check_mk/main.twig"},
                { "/tmp/template", "CHECKmk", "/tmp/template/checkmk/main.twig"},
                { "/tmp/template", "gearman", "/tmp/template/gearman/main.twig"},
        };
    }

    @Test(dataProvider = "getTemplatePathDP")
    public void getTemplatePath(String templateFolder, String converterName, String expectedTemplatePath) {
        doReturn(templateFolder).when(sakuliProperties).getForwarderTemplateFolder();
        doReturn(converterName).when(testling).getConverterName();
        assertEquals(testling.getTemplatePath(), expectedTemplatePath);
    }

}
