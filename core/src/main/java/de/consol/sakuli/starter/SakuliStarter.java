/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.starter;

import de.consol.sakuli.actions.environment.CipherUtil;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.properties.ActionProperties;
import de.consol.sakuli.datamodel.properties.SahiProxyProperties;
import de.consol.sakuli.datamodel.properties.SakuliProperties;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.exceptions.SakuliCipherException;
import de.consol.sakuli.exceptions.SakuliProxyException;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.services.InitializingService;
import de.consol.sakuli.services.ResultService;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SakuliStarter {


    /**
     * The Sakuli-Starter executes a specific sakuli-testsuite.
     * A test suite has to contain as minimum following files:
     * - testsuite.suite  => specifies the testcases
     * - testsuite.properties  => specifies the runtime settings like the browser for the test suite.
     *
     * @param args relative or absolute path to the folder of your test suite
     */
    public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        Option help = new Option("help", "Display help");
        Option run = new Option("run", "stats to run a sakuli suite with the arguments [test suite folder] [include folder] [sahi folder]");
        Option r = new Option("r", "stats to run a sakuli suite with the arguments [test suite folder] [include folder]  [sahi folder]");
        Option encrypt = new Option("encrypt", true, "encrypt your secret");
        Option anInterface = new Option("interface", true, "interface encrypt your secret");
        options.addOption(help);
        options.addOption(run);
        options.addOption(r);
        options.addOption(encrypt);
        options.addOption(anInterface);

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(run.getOpt()) || cmd.hasOption(r.getOpt())) {

                /**
                 * initialize the property environment
                 */
                String tempLogCache = "";
                if (cmd.hasOption(run.getOpt()) || cmd.hasOption(r.getOpt())) {
                    //check and set the path to the test suite
                    tempLogCache = checkTestSuiteFolderAndSetContextVariables(args[1], tempLogCache);

                    //if sahi home have been set override the default
                    if (args.length > 3) {
                        tempLogCache = checkSahiProxyHomeAndSetContextVariables(args[3], tempLogCache);
                    }
                }
                //check and set the include folder
                tempLogCache = checkIncludeFolderAndSetContextVariables(args[2], tempLogCache);

                //because of the property loading strategy, the logger must be initialized through the Spring Context!
                SahiConnector sahiConnector = BeanLoader.loadBean(SahiConnector.class);
                Logger logger = LoggerFactory.getLogger(SakuliStarter.class);
                logger.debug(tempLogCache);

                // Init the test suite data
                BeanLoader.loadBean(InitializingService.class).initTestSuite();

                /***
                 * SAKULI Starter to run the test suite with embedded sahi proxy
                 */
                try {
                    sahiConnector.init();

                    //start the execution of the test suite
                    logger.debug("start new sakuli test suite");
                    sahiConnector.startSahiTestSuite();
                }
                /**
                 * will be catched if the Sahi-Proxy could not shutdown;
                 */ catch (SakuliProxyException e) {
                    e.printStackTrace();
                    System.exit(99);
                } finally {
                    ResultService resultService = BeanLoader.loadBean(ResultService.class);
                    resultService.saveAllResults();

                    //fina log and shutdown context
                    final TestSuite testSuite = BeanLoader.loadBean(TestSuite.class);
                    BeanLoader.releaseContext();
                    logger.info(testSuite.getResultString()
                            + "\n===========  SAKULI Testsuite '" + testSuite.getGuid() + "' execution FINISHED - "
                            + testSuite.getState() + " ======================\n");
                    if (testSuite.getState().equals(TestSuiteState.ERRORS)) {
                        String errorMsg = "ERROR-Summary:\n" + testSuite.getExceptionMessages();
                        logger.error(errorMsg + "\n");
                    }

                    //return the state as system exit parameter
                    //return values are corresponding to the error codes in file "sahi_return_codes.txt"
                    System.exit(testSuite.getState().getErrorCode());
                }


            } else if (cmd.hasOption(encrypt.getOpt())) {
                String ethinterface = cmd.getOptionValue("interface");
                ActionProperties cipherProps = new ActionProperties();
                cipherProps.setEncryptionInterface(ethinterface);
                cipherProps.setEncryptionInterfaceTestMode(false);
                CipherUtil cipher = new CipherUtil(cipherProps);
                cipher.getNetworkInterfaceNames();
                final String strToEncrypt = cmd.getOptionValue("encrypt");
                final String encryptedStr = cipher.encrypt(strToEncrypt);
                System.out.println("String to Encrypt : " + strToEncrypt);
                System.out.println("Encrypted : " + encryptedStr);
                System.exit(0);
            } else {
                System.out.println("ERROR: print help");
                // TODO: Print out help
            }
        } catch (SakuliCipherException e) {
            e.printStackTrace();
            System.out.println("CHIPHER ERROR: " + e.getMessage());
        } catch (ParseException | FileNotFoundException e) {
            e.printStackTrace();
            // TODO: Print out help
            System.exit(-1);
        }
    }


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
    protected static String checkTestSuiteFolderAndSetContextVariables(String testSuiteFolderPath, String tempLogCache) throws FileNotFoundException {
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
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = testSuiteFolder.toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + TestSuiteProperties.TEST_SUITE_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE + "\"";
    }

    /**
     * Validates the path to the include folder and set the path to {@link SakuliPropertyPlaceholderConfigurer}
     *
     * @param includeFolderPath path to the include folder
     * @param tempLogCache      temporary string for later logging
     * @return the updated tempLogCache String.
     * @throws FileNotFoundException if the folde doesn't exist
     */
    protected static String checkIncludeFolderAndSetContextVariables(String includeFolderPath, String tempLogCache) throws FileNotFoundException {
        Path includeFolder = Paths.get(includeFolderPath);
        if (!Files.exists(includeFolder)) {
            throw new FileNotFoundException("sakuli include folder \"" + includeFolderPath + "\" does not exist!");
        }
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = includeFolder.toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + SakuliProperties.INCLUDE_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE + "\"";
    }

    /**
     * Validates the path to the sahi home folder and set the path to {@link SakuliPropertyPlaceholderConfigurer}
     *
     * @param sahiProxyHomePath path to the sahi home folder
     * @param tempLogCache      temporary string for later logging
     * @return the updated tempLogCache String.
     * @throws FileNotFoundException if the folde doesn't exist
     */
    protected static String checkSahiProxyHomeAndSetContextVariables(String sahiProxyHomePath, String tempLogCache) throws FileNotFoundException {
        Path sahiFolder = Paths.get(sahiProxyHomePath);
        if (!Files.exists(sahiFolder)) {
            throw new FileNotFoundException("sahi folder \"" + sahiProxyHomePath + "\" does not exist!");
        }
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = sahiFolder.toAbsolutePath().toString();
        return tempLogCache + "\nset property '" + SahiProxyProperties.PROXY_HOME_FOLDER + "' to \"" + SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE + "\"";
    }


}
