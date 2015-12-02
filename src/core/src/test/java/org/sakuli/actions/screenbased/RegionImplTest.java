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

package org.sakuli.actions.screenbased;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.loader.BaseActionLoader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 12/2/15
 */
public class RegionImplTest {
    @Mock
    private BaseActionLoader loader;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testResolveTakeScreenshotFolder() throws Exception {
        TestCase tc = mock(TestCase.class);
        Path examplePath = Paths.get(BaseTest.TEST_FOLDER_PATH + File.separator + "case" + File.separator + "tc.js");
        when(tc.getTcFile()).thenReturn(examplePath);
        when(loader.getCurrentTestCase()).thenReturn(tc);
        Path result = RegionImpl.resolveTakeScreenshotFolder("test", loader);
        assertEquals(result.toString(), examplePath.toString() + File.separator + "test");
        verify(loader, never()).getTestSuite();
        verify(loader).getCurrentTestCase();
    }

    @Test
    public void testResolveTakeScreenshotFolderFallback() throws Exception {
        TestCase tc = mock(TestCase.class);
        Path examplePath = Paths.get(BaseTest.TEST_FOLDER_PATH + File.separator + "NO_tc.js");
        when(tc.getTcFile()).thenReturn(examplePath);
        when(loader.getCurrentTestCase()).thenReturn(tc);
        TestSuite ts = mock(TestSuite.class);
        when(ts.getTestSuiteFolder()).thenReturn(Paths.get(BaseTest.TEST_FOLDER_PATH));
        when(loader.getTestSuite()).thenReturn(ts);

        Path result = RegionImpl.resolveTakeScreenshotFolder("test", loader);
        assertEquals(result.toString(), Paths.get(BaseTest.TEST_FOLDER_PATH) + File.separator + "test");
        verify(loader).getTestSuite();
        verify(loader).getCurrentTestCase();
    }

    @Test
    public void testResolveTakeScreenshotFolderAbsolutPath() throws Exception {
        String filename = "/home/test";
        Path result = RegionImpl.resolveTakeScreenshotFolder(filename, loader);
        assertEquals(result.toString(), filename);
        verify(loader, never()).getTestSuite();
        verify(loader, never()).getCurrentTestCase();
    }


}