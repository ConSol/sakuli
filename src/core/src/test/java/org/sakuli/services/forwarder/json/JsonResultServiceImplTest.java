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

package org.sakuli.services.forwarder.json;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliForwarderException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertTrue;

/**
 * Created by georgi on 28/09/17.
 */
public class JsonResultServiceImplTest {

    @Spy
    @InjectMocks
    private JsonResultServiceImpl testling;
    @Mock
    private JsonProperties jsonProperties;
    @Mock
    private TestSuite testSuite;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] createJsonFilePathDP() {
        return new Object[][]{
                {"tmp" + File.separator + "_json", "docker_test", "tmp\\" + File.separator + "_json\\" + File.separator + "docker_test_.*\\.json"},
                {"tmp" + File.separator + "blub", "test_suite", "tmp\\" + File.separator + "blub\\" + File.separator + "test_suite_.*\\.json"},
        };
    }

    @Test(dataProvider = "createJsonFilePathDP")
    public void createJsonFilePath(String outputJsonDir, String testSuiteId, String expectedFilePathRegEx) throws SakuliForwarderException {
        doReturn(testSuiteId).when(testSuite).getId();
        doReturn(Paths.get(outputJsonDir)).when(jsonProperties).getOutputJsonDir();
        assertTrue(testling.createJsonFilePath().toString().matches(expectedFilePathRegEx));
    }

}
