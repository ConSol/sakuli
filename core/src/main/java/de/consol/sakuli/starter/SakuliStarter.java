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
import de.consol.sakuli.dao.DaoTestSuite;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.exceptions.SakuliCipherException;
import de.consol.sakuli.exceptions.SakuliProxyException;
import de.consol.sakuli.starter.proxy.SahiProxy;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

import java.io.File;
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
                    //Set the path to the test suite and logger
                    String absolutePathSuiteFolder = checkTestSuiteFolder(args[1]);
                    System.setProperty(TestSuite.SUITE_FOLDER_PROPERTY, absolutePathSuiteFolder);
                    tempLogCache += "\nsystem property '" + TestSuite.SUITE_FOLDER_PROPERTY + "' has been set to \"" + absolutePathSuiteFolder + "\"";
                    System.setProperty(TestSuite.LOG_FOLDER_PROPERTY, absolutePathSuiteFolder);
                    tempLogCache += "\nsystem property '" + TestSuite.LOG_FOLDER_PROPERTY + "' has been set to \"" + absolutePathSuiteFolder + "\"";

                    //if sahi home have been set override the defaul
                    if (args.length > 3) {
                        String absolutSahiHomePath = checkFolder(args[3], "sahi home folder");
                        System.setProperty(SahiProxy.SAHI_PROXY_HOME, absolutSahiHomePath);
                        tempLogCache += "\nsystem property '" + SahiProxy.SAHI_PROXY_HOME + "' has been set to \"" + absolutSahiHomePath + "\"";
                    }
                } else {
                    //Set logger properties
                    String logFolderPath = Paths.get(args[1]).toFile().getAbsolutePath();
                    System.setProperty(TestSuite.LOG_FOLDER_PROPERTY, logFolderPath);
                    tempLogCache += "\nsystem property '" + TestSuite.LOG_FOLDER_PROPERTY + "' has been set to \"" + logFolderPath + "\"";
                }
                //because of the property, the logger must be initialized after setProperty() of the log folder!
                Logger logger = LoggerFactory.getLogger(SakuliStarter.class);
                logger.debug(tempLogCache);

                String absolutePathIncludeFolder = checkFolder(args[2], "sakuli include folder");
                System.setProperty(TestSuite.INCLUDE_FOLDER_PROPERTY, absolutePathIncludeFolder);
                logger.debug("system property '" + TestSuite.INCLUDE_FOLDER_PROPERTY + "' has been set to \"" + absolutePathIncludeFolder + "\"");


                /***
                 * SAKULI Starter to run the test suite with embedded sahi proxy
                 */

                try {
                    logger.info("Start Sakuli-Test-Suite in folder \"" + System.getProperty(TestSuite.SUITE_FOLDER_PROPERTY) + "\"");
                    SahiConnector sahiConnector = getBeanFacorty().getBean(SahiConnector.class);

                    //start the execution of the test suite
                    logger.debug("initialize SakuliStarter");
                    sahiConnector.startSahiTestSuite();
                }
                /**
                 * will be catched if the Sahi-Proxy could not shutdown;
                 */ catch (SakuliProxyException e) {
                    e.printStackTrace();
                    System.exit(99);
                } finally {
                    DaoTestSuite daoTestSuite = getBeanFacorty().getBean(DaoTestSuite.class);
                    //after the execution,save the test suite
                    daoTestSuite.updateTestSuiteResult();
                    daoTestSuite.saveTestSuiteToSahiJobs();

                    //print out the result
                    TestSuite testSuite = getBeanFacorty().getBean(TestSuite.class);
                    logger.info(testSuite.getResultString()
                            + "\n===========  SAKULI Testsuite '" + testSuite.getGuid() + "' execution FINISHED - "
                            + testSuite.getState() + " ======================\n");
                    if (testSuite.getState().equals(TestSuiteState.ERRORS)) {
                        String errorMsg = "ERROR-Summary:\n" + testSuite.getExceptionMessages();
                        logger.error(errorMsg + "\n");
                    }

                    clearProperties();
                    //return the state as system exit parameter
                    //return values are corresponding to the error codes in file "sahi_return_codes.txt"
                    System.exit(testSuite.getState().getErrorCode());
                }


            } else if (cmd.hasOption(encrypt.getOpt())) {
                String ethinterface = cmd.getOptionValue("interface");
                CipherUtil cipher = new CipherUtil();
                cipher.setInterfaceName(ethinterface);
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

    private static void clearProperties() {
        System.clearProperty(TestSuite.SUITE_FOLDER_PROPERTY);
        System.clearProperty(TestSuite.INCLUDE_FOLDER_PROPERTY);
        System.clearProperty(TestSuite.LOG_FOLDER_PROPERTY);
    }

    /**
     * build up the application context "starter"
     *
     * @return a Spring {@link BeanFactory}
     */
    private static BeanFactory getBeanFacorty() {
        BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance();
        BeanFactoryReference bf = bfl.useBeanFactory("de.consol.sakuli.app.starter");
        return bf.getFactory();
    }

    private static String checkFolder(String arg, String nameOfFolder) throws FileNotFoundException {
        Path folder = Paths.get(arg);
        if (!Files.exists(folder)) {
            throw new FileNotFoundException(nameOfFolder + " \"" + arg + "\" does not exist!");
        }
        // TODO add propertie check
        return folder.toFile().getAbsolutePath();
    }

    private static String checkTestSuiteFolder(String arg) throws FileNotFoundException {
        // TODO change to nio.Path
        File testSuiteFolder = new File(arg);
        File propertyFile = new File(arg + "/testsuite.properties");
        File suiteFile = new File(arg + "/testsuite.suite");

        if (!testSuiteFolder.exists()) {
            throw new FileNotFoundException("sakuli test suite folder \"" + arg + "\" does not exist");
        } else if (!propertyFile.exists()) {
            throw new FileNotFoundException("property file \"testsuite.properties\" does not exist in folder: " + arg);
        } else if (!suiteFile.exists()) {
            throw new FileNotFoundException("suite file \"testsuite.suite\" does not exist in folder: " + arg);
        } else {
            return testSuiteFolder.getAbsolutePath();
        }

    }

}
