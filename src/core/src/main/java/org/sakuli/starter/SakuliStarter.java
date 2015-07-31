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

package org.sakuli.starter;

import org.apache.commons.cli.*;
import org.sakuli.actions.environment.CipherUtil;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliCipherException;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.InitializingServiceHelper;
import org.sakuli.services.ResultServiceHelper;
import org.sakuli.starter.helper.SakuliFolderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.Map.Entry;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@SuppressWarnings("AccessStaticViaInstance")
public class SakuliStarter {

    public static final Logger LOGGER = LoggerFactory.getLogger(SakuliStarter.class);
    private final static Option help = OptionBuilder.withDescription("display help").withLongOpt("help").create("h");
    private final static Option run = OptionBuilder
            .withArgName("test-suite-folder")
            .hasArg()
            .withDescription("run a sakuli test suite")
            .withLongOpt("run")
            .create("r");
    private final static Option browser = OptionBuilder
            .withArgName("browser")
            .hasArg()
            .withDescription("(optional) browser for the test execution, \ndefault: property 'testsuite.browser'")
            .withLongOpt("browser")
            .isRequired(false)
            .create("b");
    private final static Option sakuliHome = OptionBuilder
            .withArgName("sakuli-folder")
            .hasArg()
            .withDescription("(optional) SAKULI_HOME folder, \ndefault: environment variable 'SAKULI_HOME'")
            .isRequired(false)
            .withLongOpt("sakuli_home")
            .create();
    private final static Option sahiHome = OptionBuilder
            .withArgName("sahi-folder")
            .hasArg()
            .withDescription("(optional) Sahi installation folder, \ndefault: property 'sahi.proxy.homePath'")
            .isRequired(false)
            .withLongOpt("sahi_home")
            .create();
    private final static Option encrypt = OptionBuilder
            .withArgName("secret")
            .hasArg()
            .withDescription("encrypt a secret")
            .withLongOpt("encrypt")
            .create("e");
    private final static Option anInterface = OptionBuilder
            .withArgName("interface-name")
            .hasArg()
            .withDescription("(optional) network interface used for encryption, default: auto-selection")
            .isRequired(false)
            .withLongOpt("interface")
            .create("i");

    /**
     * The Sakuli-Starter executes a specific sakuli-testsuite. A test suite has to contain as minimum following files:
     * <ul>
     * <li>testsuite.suite  - specifies the testcases</li>
     * <li>testsuite.properties  - specifies the runtime settings like the browser for the test suite.</li>
     * </ul>
     *
     * @param args relative or absolute path to the folder of your test suite
     */
    public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption(help);
        options.addOption(run);
        options.addOption(browser);
        options.addOption(sakuliHome);
        options.addOption(sahiHome);
        options.addOption(encrypt);
        options.addOption(anInterface);
        try {
            CommandLine cmd = parser.parse(options, args);
            final String browserValue = getOptionValue(cmd, browser);
            final String testSuiteFolderPath = getOptionValue(cmd, run);
            final String sakuliMainFolderPath = getOptionValue(cmd, sakuliHome);
            final String sahiHomePath = getOptionValue(cmd, sahiHome);
            final String ethInterface = getOptionValue(cmd, anInterface);
            final String strToEncrypt = getOptionValue(cmd, encrypt);

            if (cmd.hasOption(run.getLongOpt()) || cmd.hasOption(run.getOpt())) {
                TestSuite testSuite = runTestSuite(testSuiteFolderPath, sakuliMainFolderPath, browserValue, sahiHomePath);
                //return the state as system exit parameter
                //return values are corresponding to the error codes in file "sahi_return_codes.txt"
                System.exit(testSuite.getState().getErrorCode());

            } else if (cmd.hasOption(encrypt.getLongOpt()) || cmd.hasOption(encrypt.getOpt())) {
                System.out.printf("\nString to Encrypt: %s \n...", strToEncrypt);
                final Entry<String, String> secret = encryptSecret(strToEncrypt, ethInterface);
                System.out.printf("\nEncrypted secret with interface '%s': %s", secret.getKey(), secret.getValue());
                System.out.println("\n\n... now copy the secret to your testcase!");
                System.exit(0);
            } else {
                printHelp(options);
            }
        } catch (SakuliCipherException e) {
            e.printStackTrace();
            System.out.println("CIPHER ERROR: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Parsing of command line failed: " + e.getMessage());
            printHelp(options);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String getOptionValue(CommandLine cmd, Option option) {
        return option.getOpt() != null ? cmd.getOptionValue(option.getOpt()) : cmd.getOptionValue(option.getLongOpt());
    }

    private static void printHelp(Options options) {
        System.out.println();
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(80);
        helpFormatter.printHelp("sakuli [options]", options);
    }

    /**
     * wrapper for {@link #runTestSuite(String, String, String)} with usage of the default sahi-proxy configured in the
     * 'sakuli.properties' and the configured browser 'testsuite.browser'.
     */
    public static TestSuite runTestSuite(String testSuiteFolderPath, String sakuliMainFolder) throws FileNotFoundException {
        return runTestSuite(testSuiteFolderPath, sakuliMainFolder, null);
    }

    /**
     * wrapper for {@link #runTestSuite(String, String, String, String)} with usage of the default sahi-proxy configured in the
     * 'sakuli.properties'.
     */
    public static TestSuite runTestSuite(String testSuiteFolderPath, String sakuliMainFolder, String browser) throws FileNotFoundException {
        return runTestSuite(testSuiteFolderPath, sakuliMainFolder, browser, null);
    }

    /**
     * Executes a specific Sakuli test suite in the assigend 'testSuiteFolder'. A test suite has to contain as minimum
     * following files:
     * <ul>
     * <li>testsuite.suite  = specifies the testcases</li>
     * <li>testsuite.properties  = specifies the runtime settings like the browser for the test suite.</li>
     * </ul>
     *
     * @param testSuiteFolderPath  path to the Sakuli test suite
     * @param sakuliHomeFolderPath path to the folder which contains 'config' and the 'libs' folder
     * @param browser              (optional) browser for the test execution, default: property 'testsuite.browser'
     * @param sahiHomeFolder       (optional) specifies a different sahi proxy as in the 'sakuli.properties' file
     * @return the {@link TestSuiteState} of the Sakuli test execution.
     * @throws FileNotFoundException
     */
    public static TestSuite runTestSuite(String testSuiteFolderPath, String sakuliHomeFolderPath, String browser, String sahiHomeFolder) throws FileNotFoundException {
        LOGGER.info(String.format("\n\n=========== START new SAKULI Testsuite from '%s' =================", testSuiteFolderPath));

        //temp log cache for init stuff -> should be in the log file
        String tempLogCache = "";
        //check and set the path to the test suite
        tempLogCache = SakuliFolderHelper.checkTestSuiteFolderAndSetContextVariables(testSuiteFolderPath, tempLogCache);
        //check and set the sakuli main folder
        tempLogCache = SakuliFolderHelper.checkSakuliHomeFolderAndSetContextVariables(sakuliHomeFolderPath, tempLogCache);

        //if sahi home have been set override the default
        if (isNotEmpty(sahiHomeFolder)) {
            tempLogCache = SakuliFolderHelper.checkSahiProxyHomeAndSetContextVariables(sahiHomeFolder, tempLogCache);
        }

        //set browser for test execution if not empty
        if (isNotEmpty(browser)) {
            tempLogCache = SakuliFolderHelper.setTestSuiteBrowserContextVariable(browser, tempLogCache);
        }

        //because of the property loading strategy, the logger must be initialized through the Spring Context!
        SahiConnector sahiConnector = BeanLoader.loadBean(SahiConnector.class);
        LOGGER.debug(tempLogCache);

        //Call init services
        InitializingServiceHelper.invokeInitializingServcies();
        TestSuite result = BeanLoader.loadBean(TestSuite.class);
        /***
         * SAKULI Starter to run the test suite with embedded sahi proxy
         */
        try {
            sahiConnector.init();

            //start the execution of the test suite
            LOGGER.debug("start new sakuli test suite");
            sahiConnector.startSahiTestSuite();
        }
        /**
         * will be catched if the Sahi-Proxy could not shutdown;
         */ catch (SakuliInitException e) {
            LOGGER.error("Unexpected error occurred:", e);
            System.exit(99);
        } finally {
            ResultServiceHelper.invokeResultServices();

            //finally shutdown context and return the result
            result = BeanLoader.loadBean(TestSuite.class);
            BeanLoader.releaseContext();
        }
        return result;
    }

    /**
     * Encrypt a secret based on the assigned interface.
     *
     * @param strToEncrypt secret to encrypt
     * @param ethInterface name of network interface, if NULL use the auto-detection
     * @return a Key-Value Pair of used interface for the encryption and the encrypted secret as strings.
     * @throws SakuliCipherException
     */
    public static Entry<String, String> encryptSecret(String strToEncrypt, String ethInterface) throws SakuliCipherException {
        ActionProperties cipherProps = new ActionProperties();
        if (isNotEmpty(ethInterface)) {
            cipherProps.setEncryptionInterface(ethInterface);
            cipherProps.setEncryptionInterfaceAutodetect(false);
        } else {
            cipherProps.setEncryptionInterfaceAutodetect(true);
        }
        CipherUtil cipher = new CipherUtil(cipherProps);
        cipher.scanNetworkInterfaces();
        return new AbstractMap.SimpleEntry<>(cipher.getInterfaceName(), cipher.encrypt(strToEncrypt));
    }

}
