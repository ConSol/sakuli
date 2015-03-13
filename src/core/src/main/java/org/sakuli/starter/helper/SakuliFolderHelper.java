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

import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author tschneck
 *         Date: 13.03.15
 */
public class SakuliFolderHelper {


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
        Path testSuiteFolder = Paths.get(testSuiteFolderPath);
        Path propertyFile = Paths.get(testSuiteFolderPath + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_APPENDER);
        Path suiteFile = Paths.get(testSuiteFolderPath + TestSuiteProperties.TEST_SUITE_SUITE_FILE_APPENDER);

        if (!Files.exists(testSuiteFolder)) {
            throw new FileNotFoundException("sakuli test suite folder \"" + testSuiteFolderPath + "\" does not exist!");
        } else if (!Files.exists(propertyFile)) {
            throw new FileNotFoundException("property file \"" + TestSuiteProperties.TEST_SUITE_PROPERTIES_FILE_NAME + "\" does not exist in folder: " + testSuiteFolderPath);
        } else if (!Files.exists(suiteFile)) {
            throw new FileNotFoundException("suite file \"" + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + "\" does not exist in folder: " + testSuiteFolderPath);
        }
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = testSuiteFolder.normalize().toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + TestSuiteProperties.TEST_SUITE_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE + "\"";
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
        Path mainFolderPath;
        if (sakuliMainFolderPath != null) {
            mainFolderPath = Paths.get(sakuliMainFolderPath);
        } else {
            mainFolderPath = getAlternativeSakuliMainFolder();
        }
        if (!Files.exists(mainFolderPath)) {
            throw new FileNotFoundException("SAKULI HOME folder \"" + sakuliMainFolderPath + "\" does not exist!");
        }
        if (!checkSubMainFolder(mainFolderPath)) {
            throw new FileNotFoundException("the assigned SAKULI HOME foler \"" + sakuliMainFolderPath + "\" does not have a valid file structure! Please use the correct folder!");
        }
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = mainFolderPath.normalize().toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + SakuliProperties.SAKULI_HOME_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE + "\"";
    }

    private static Path getAlternativeSakuliMainFolder() throws FileNotFoundException {
        Path sakuli_home = Paths.get(System.getProperty("SAKULI_HOME", "."));
        if (Files.exists(sakuli_home) && checkSubMainFolder(sakuli_home)) {
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
        Path sahiFolder = Paths.get(sahiProxyHomePath);
        if (!Files.exists(sahiFolder)) {
            throw new FileNotFoundException("sahi folder \"" + sahiProxyHomePath + "\" does not exist!");
        }
        SakuliPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = sahiFolder.normalize().toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + SahiProxyProperties.PROXY_HOME_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.SAHI_HOME_VALUE + "\"";
    }

}
