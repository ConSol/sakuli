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

package org.sakuli.starter.sahi.helper;

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.sakuli.starter.sahi.utils.SahiStarterPropertyPlaceholderConfigurer;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author tschneck
 *         Date: 13.03.15
 */
public class SakuliFolderHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SakuliFolderHelper.class);

    /**
     * Validates the path to the test suite folder and ensure that it contains the files:
     * <ul>
     * <li>testsuite.properties</li>
     * <li>testsuite.suite</li>
     * </ul>
     * After all checks were succefull,the values will be set to the {@link SakuliPropertyPlaceholderConfigurer}.
     *
     * @param testSuiteFolderPath path to test suite folder
     * @param tempLogCache        temporary string for later logging
     * @return the updated tempLogCache String.
     * @throws FileNotFoundException if some of the above files are missing
     */
    public static String checkTestSuiteFolderAndSetContextVariables(String testSuiteFolderPath, String tempLogCache) throws FileNotFoundException {
        Path testSuiteFolder = normalizePath(testSuiteFolderPath);
        Path propertyFile = Paths.get(testSuiteFolder + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER);
        Path suiteFile = Paths.get(testSuiteFolder + TestSuiteProperties.TEST_SUITE_SUITE_FILE_APPENDER);

        if (testSuiteFolder == null || !Files.exists(testSuiteFolder)) {
            throw new FileNotFoundException("sakuli test suite folder \"" + testSuiteFolderPath + "\" does not exist!");
        } else if (!Files.exists(propertyFile)) {
            throw new FileNotFoundException("property file \"" + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_NAME + "\" does not exist in folder: " + testSuiteFolderPath);
        } else if (!Files.exists(suiteFile)) {
            throw new FileNotFoundException("suite file \"" + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + "\" does not exist in folder: " + testSuiteFolderPath);
        }
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = testSuiteFolder.toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + TestSuiteProperties.TEST_SUITE_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE + "\"";
    }

    /**
     * Method remove some not valid windows escape chars like ' or " . To prevent a {@link InvalidPathException}.
     * <p/>
     * {@code 'C:\PROGRA~1\.'  => C:\PROGRA~1\.}
     * <br/>
     * {@code "C:\PROGRA~1\."  => C:\PROGRA~1\. }
     *
     * @param path as {@link String}
     * @return modified path as {@link String}
     */
    public static Path normalizePath(String path) {
        String oldPath = path;
        for (char c : Arrays.asList('\'', '\"')) {
            path = StringUtils.remove(path, c);
        }
        if (!StringUtils.equals(oldPath, path)) {
            LOGGER.debug("Path [{}] modified to [{}]", oldPath, path);
        }
        return path == null ? null : Paths.get(path).toAbsolutePath().normalize();
    }

    /**
     * Validates the path to the sakuli main folder and set the path to {@link SakuliPropertyPlaceholderConfigurer}
     *
     * @param sakuliMainFolderPath path to the sakuli main folder
     * @param tempLogCache         temporary string for later logging
     * @return the updated tempLogCache String.
     * @throws FileNotFoundException if the folder doesn't exist
     */
    public static String checkSakuliHomeFolderAndSetContextVariables(String sakuliMainFolderPath, String tempLogCache) throws FileNotFoundException {
        Path normalizedPath = normalizePath(sakuliMainFolderPath);
        Path mainFolderPath = normalizedPath != null ? normalizedPath : getAlternativeSakuliMainFolder();
        if (!Files.exists(mainFolderPath)) {
            throw new FileNotFoundException("SAKULI HOME folder \"" + mainFolderPath + "\" does not exist!");
        }
        if (!checkSubMainFolder(mainFolderPath)) {
            throw new FileNotFoundException("the assigned SAKULI HOME folder \"" + mainFolderPath + "\" does not have a valid file structure! Please use the correct folder!");
        }
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = mainFolderPath.normalize().toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + SakuliProperties.SAKULI_HOME_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE + "\"";
    }

    private static Path getAlternativeSakuliMainFolder() throws FileNotFoundException {
        String envSakuliHome = System.getenv("SAKULI_HOME");
        Path sakuli_home = StringUtils.isNotEmpty(envSakuliHome) ? normalizePath(envSakuliHome) : Paths.get(".");
        if (sakuli_home != null && Files.exists(sakuli_home) && checkSubMainFolder(sakuli_home)) {
            return sakuli_home;
        }
        throw new FileNotFoundException("no valid SAKULI HOME folder specified - please configure one!");
    }

    private static boolean checkSubMainFolder(Path mainFolder) {
        List<String> subs = Arrays.asList("bin", "config", "libs", "setup");
        return subs.stream().filter(s -> !Files.exists(mainFolder.resolve(s))).count() == 0;
    }

    /**
     * Validates the path to the sahi home folder and set the path to {@link SakuliPropertyPlaceholderConfigurer}
     *
     * @param sahiProxyHomePath path to the sahi home folder
     * @param tempLogCache      temporary string for later logging
     * @return the updated tempLogCache String.
     * @throws FileNotFoundException if the folde doesn't exist
     */
    public static String checkSahiProxyHomeAndSetContextVariables(String sahiProxyHomePath, String tempLogCache) throws FileNotFoundException {
        Path sahiFolder = normalizePath(sahiProxyHomePath);
        if (sahiFolder == null || !Files.exists(sahiFolder)) {
            throw new FileNotFoundException("sahi folder \"" + sahiProxyHomePath + "\" does not exist!");
        }
        SahiStarterPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = sahiFolder.toString();
        return tempLogCache + "\nset property '" + SahiProxyProperties.PROXY_HOME_FOLDER + "' to \"" + SahiStarterPropertyPlaceholderConfigurer.SAHI_HOME_VALUE + "\"";
    }

    /**
     * set the {@link TestSuiteProperties#BROWSER_NAME} to the assigned testSuiteBrowserName.
     *
     * @param testSuiteBrowserName name of a configured browser for the test suite
     * @param tempLogCache         temporary string for later logging
     * @return the updated tempLogCache String.
     */
    public static String setTestSuiteBrowserContextVariable(String testSuiteBrowserName, String tempLogCache) {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_BROWSER = StringUtils.trimToNull(testSuiteBrowserName);
        return tempLogCache + "\nset property '" + TestSuiteProperties.BROWSER_NAME + "' to \"" + SakuliPropertyPlaceholderConfigurer.TEST_SUITE_BROWSER + "\"";
    }

}
