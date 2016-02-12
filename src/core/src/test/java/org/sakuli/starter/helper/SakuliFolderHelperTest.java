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

package org.sakuli.starter.helper;

import org.sakuli.BaseTest;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

public class SakuliFolderHelperTest extends BaseTest {

    @DataProvider(name = "windowsPaths")
    public static Object[][] windowsPaths() {
        return new Object[][]{
                {"C:\\Program Files (x86)"},
                {"\"C:\\Program Files (x86)\""},
                {"C:\\Program Files (x86)\\sakuli"},
                {"\"C:\\Program Files (x86)\"\\sakuli"},
                {"C:\\PROGRA~1\\."},
                {"\"C:\\PROGRA~1\\.\""},
                {"\'C:\\PROGRA~1\\.\'"},
        };

    }

    @Test
    public void testCheckSahiProxyHomeAndSetContextVariables() throws Exception {
        //test with test suite folder to ensure that file is present
        final String absoluteSahiPath = Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString();
        String log = "";
        log = SakuliFolderHelper.checkSahiProxyHomeAndSetContextVariables(TEST_FOLDER_PATH, log);
        assertEquals(SakuliPropertyPlaceholderConfigurer.SAHI_HOME_VALUE, absoluteSahiPath);
        assertEquals(log, "\nset property '" + SahiProxyProperties.PROXY_HOME_FOLDER + "' to \"" + absoluteSahiPath + "\"");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "sahi folder .* does not exist!")
    public void testCheckSahiProxyHomeAndSetContextVariablesFolderMissing() throws Exception {
        SakuliFolderHelper.checkSahiProxyHomeAndSetContextVariables("not-valid-path/to-sahi-home", "");
    }

    @Test
    public void testCheckTestSuiteFolderAndSetContextVariables() throws Exception {
        final String absoluteTestSuitePath = Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString();
        String log = "";
        log = SakuliFolderHelper.checkTestSuiteFolderAndSetContextVariables(TEST_FOLDER_PATH, log);
        assertEquals(SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE, absoluteTestSuitePath);
        assertEquals(log, "\nset property '" + TestSuiteProperties.TEST_SUITE_FOLDER + "' to \"" + absoluteTestSuitePath + "\"");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "sakuli test suite folder .* does not exist!")
    public void testCheckTestSuiteFolderAndSetContextVariablesFolderMissing() throws Exception {
        SakuliFolderHelper.checkTestSuiteFolderAndSetContextVariables("not-valid-path/to-suite", "");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "property file \"testsuite.properties\" does not exist in folder.*")
    public void testCheckTestSuiteFolderAndSetContextVariablesSuitePropertyMissing() throws Exception {
        Path path = Paths.get(getClass().getResource("unvalid").toURI());
        assertTrue(Files.exists(path));
        SakuliFolderHelper.checkTestSuiteFolderAndSetContextVariables(path.toString(), "");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "suite file \"testsuite.suite\" does not exist in folder.*")
    public void testCheckTestSuiteFolderAndSetContextVariablesSuiteFileMissing() throws Exception {
        Path path = Paths.get(getClass().getResource("unvalid2").toURI());
        assertTrue(Files.exists(path));
        SakuliFolderHelper.checkTestSuiteFolderAndSetContextVariables(path.toString(), "");
    }

    @Test
    public void testCheckHomeFolder() throws Exception {
        Path path = Paths.get(BaseTest.SAKULI_HOME_FOLDER_PATH);
        assertTrue(Files.exists(path));
        SakuliFolderHelper.checkSakuliHomeFolderAndSetContextVariables(path.toString(), "");
        assertEquals(SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE, path.normalize().toAbsolutePath().toString());

    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = ".*not have a valid file structure! Please use the correct folder!")
    public void testCheckHomeFolderNotValid() throws Exception {
        Path path = Paths.get("./src");
        assertTrue(Files.exists(path));
        SakuliFolderHelper.checkSakuliHomeFolderAndSetContextVariables(path.toString(), "");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "no valid SAKULI HOME folder specified - please configure one!")
    public void testCheckHomeFolderNotDefined() throws Exception {
        System.clearProperty("SAKULI_HOME");
        String envSakuliHome = System.getenv("SAKULI_HOME");
        assertTrue(envSakuliHome == null || envSakuliHome.equalsIgnoreCase("null"));
        SakuliFolderHelper.checkSakuliHomeFolderAndSetContextVariables(null, "");
    }

    @Test(dataProvider = "windowsPaths")
    public void testWindowsPaths(String path) throws Exception {
        Path p = Paths.get(SakuliFolderHelper.removeWindowsEscapeCharacters(path)).normalize();
        assertNotNull(p);
    }
}