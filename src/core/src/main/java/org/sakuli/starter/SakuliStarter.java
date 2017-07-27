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
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.InitializingServiceHelper;
import org.sakuli.services.TeardownServiceHelper;
import org.sakuli.services.cipher.AesKeyHelper;
import org.sakuli.starter.helper.CipherDelegator;
import org.sakuli.starter.helper.CmdPrintHelper;
import org.sakuli.starter.helper.SakuliFolderHelper;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
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
    private final static Option masterkey = OptionBuilder
            .withArgName("base64 AES key")
            .hasArg()
            .withDescription("(optional)  AES base64 key used by command 'encrypt'")
            .isRequired(false)
            .withLongOpt("masterkey")
            .create("m");
    private final static Option create = OptionBuilder
            .withArgName("object")
            .hasArg()
            .withDescription("create a sakuli object")
            .withLongOpt("create")
            .create();
    /**
     * {@link System#exit(int)} value if the help is printed out. This value will be used in the `sakuli` starter
     */
    private static final int SYSTEM_EXIT_VALUE_HELP = 100;

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
        options.addOption(masterkey);
        options.addOption(create);
        try {
            CommandLine cmd = parser.parse(options, args);
            final String browserValue = getOptionValue(cmd, browser);
            final String testSuiteFolderPath = getOptionValue(cmd, run);
            final String sakuliMainFolderPath = getOptionValue(cmd, sakuliHome);
            final String sahiHomePath = getOptionValue(cmd, sahiHome);
            final String ethInterface = getOptionValue(cmd, anInterface);
            final String strToEncrypt = getOptionValue(cmd, encrypt);
            final String strMasterkey = getOptionValue(cmd, masterkey);
            final String strCreate = getOptionValue(cmd, create);

            checkAndAssignEncryptionOptions(strMasterkey, ethInterface);
            if (cmd.hasOption(run.getLongOpt()) || cmd.hasOption(run.getOpt())) {
                TestSuite testSuite = runTestSuite(testSuiteFolderPath, sakuliMainFolderPath, browserValue, sahiHomePath);
                //return the state as system exit parameter
                //return values are corresponding to the error codes in file "sahi_return_codes.txt"
                System.exit(testSuite.getState().getErrorCode());
            } else if (cmd.hasOption(create.getLongOpt())) {
                if (masterkey.getLongOpt().equals(strCreate)) {
                    AesKeyHelper.printRandomBase64Key();
                    System.exit(0);
                } else {
                    System.err.println("create object '" + strCreate + "' not supported - choose a correct value!");
                    System.exit(-1);
                }
            } else if (cmd.hasOption(encrypt.getLongOpt()) || cmd.hasOption(encrypt.getOpt())) {
                System.out.printf("\nString to Encrypt: %s \n...", strToEncrypt);
                final Entry<String, String> secret = CipherDelegator.encrypt(strToEncrypt);
                System.out.printf("\nEncrypted secret with '%s':\n%s", secret.getKey(), secret.getValue());
                System.out.println("\n\n... now copy the secret to your testcase!");
                System.exit(0);
            } else {
                CmdPrintHelper.printHelp(options);
                System.exit(SYSTEM_EXIT_VALUE_HELP);
            }
        } catch (ParseException e) {
            System.err.println("Parsing of command line failed: " + e.getMessage());
            CmdPrintHelper.printHelp(options);
            System.exit(SYSTEM_EXIT_VALUE_HELP);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
            System.exit(TestSuiteState.ERRORS.getErrorCode());
        }
    }

    protected static void checkAndAssignEncryptionOptions(String masterkey, String ethInterface) throws SakuliInitException {
        if (isNotEmpty(masterkey) && isNotEmpty(ethInterface)) {
            throw new SakuliInitException("Encryption setup error: Masterkey and network interface specified, please specify only one option!");
        }
        if (isNotEmpty(masterkey)) {
            LOGGER.debug("set masterkey '{}' for encryption", masterkey);
            SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = masterkey;
        }
        if (isNotEmpty(ethInterface)) {
            LOGGER.debug("set '{}' as encryption interface", ethInterface);
            SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = ethInterface;
        }
    }

    private static String getOptionValue(CommandLine cmd, Option option) {
        return option.getOpt() != null ? cmd.getOptionValue(option.getOpt()) : cmd.getOptionValue(option.getLongOpt());
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

        //if sahi home have been set overwrite the default
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
            LOGGER.info("========== TEAR-DOWN SAKULI TEST SUITE '{}' ==========", result.getId());
            TeardownServiceHelper.invokeTeardownServices();

            //finally shutdown context and return the result
            result = BeanLoader.loadBean(TestSuite.class);
            BeanLoader.releaseContext();
        }
        return result;
    }

}
