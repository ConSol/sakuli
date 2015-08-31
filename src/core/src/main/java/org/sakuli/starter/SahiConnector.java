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

import net.sf.sahi.ant.Report;
import net.sf.sahi.test.TestRunner;
import net.sf.sahi.util.Utils;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.starter.helper.SahiProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class SahiConnector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected int countConnections = 0;

    @Autowired
    private SahiProxy sahiProxy;
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private SakuliProperties sakuliProperties;
    @Autowired
    private SahiProxyProperties sahiProxyProperties;

    @Autowired
    private SakuliExceptionHandler sakuliExceptionHandler;

    /**
     * Initialize method to start the sahi proxy thread, if needed
     */
    public void init() throws SakuliInitException {
        logger.info("Initialize Sahi Proxy! ");
        try {
            sahiProxy.startProxy();
        } catch (FileNotFoundException e) {
            throw new SakuliInitException(e);
        }
    }

    /**
     * starts a specific sahi test suite in sakuli
     */
    public void startSahiTestSuite() throws SakuliInitException {
        logger.info("Start Sakuli-Test-Suite from folder \""
                + testSuite.getTestSuiteFolder().toAbsolutePath().toString()
                + "\"");

        checkTestSuiteFile();
//        ConnectionTester.checkTestCaseInitURL(testSuite);
        //default sahi runner to play the sahi script
        TestRunner runner = getTestRunner();
        runner.setIsSingleSession(false);
        //config reporter
        runner.addReport(new Report("html", sakuliProperties.getLogFolder().toAbsolutePath().toString()));
        //add include folder property
        runner.setInitJS(getInitJSString());

        try { //is there to handle exceptions in the catch block from this.reconnect()
            try {
                countConnections++;
                // Script-Runner starts
                logger.info("Sahi-Script-Runner starts!\n");

                //starts the script runner with the prefilled configurations
                String output = runner.execute();

                testSuite.setStopDate(new Date());
                logger.info("test suite '" + testSuite.getId() + "' stopped at " + TestSuite.GUID_DATE_FORMATE.format(testSuite.getStopDate()));

                logger.info("Sahi-Script-Runner executed with " + output);

                //should only thrown if an exception could fetched by the backend of some reason
                if (output.equals("FAILURE")
                        && testSuite.getException() == null) {
                    throw new SakuliInitException("SAHI-Proxy returned 'FAILURE' ");
                }


            } catch (ConnectException | IllegalMonitorStateException e) {
                //Reconnect - wait for Thread "sahiRunner"
                this.reconnect(e);
            }
        } catch (Throwable e) {
            sakuliExceptionHandler.handleException(e);
        } finally {
            logger.info("test suite finished");
            //shutdown sahi proxy!
            sahiProxy.shutdown();
        }
    }

    /**
     * checks the 'testsuite.suite' file if it is walid!.
     *
     * @throws SakuliInitException
     */
    void checkTestSuiteFile() throws SakuliInitException {
        try {
            Utils.readFileAsString(testSuite.getAbsolutePathOfTestSuiteFile());
        } catch (Exception e) {
            throw new SakuliInitException(e, String.format("Error - %s - during reading in testsuite.suite file '%s'",
                    (e instanceof NullPointerException) ? "unable to read or locate file" : e.getMessage(),
                    testSuite.getAbsolutePathOfTestSuiteFile()));
        }
    }

    //TODO TS #96  refactor, write Test and document
    protected String getInitJSString() {
        return String.format("var $includeFolder = '%s'; var $testSuiteFolder = '%s';",
                getIncludeFolderJsPath(),
                getTestSuiteFolderJsPath()
        );
    }

    protected TestRunner getTestRunner() {
        //have to be always one, to prevent errors in the sikuli screen capturing parts
        String threads = "1";
        //default start URL of the sahi proxy
        final String sahiHost = "localhost";
        final String sahiPort = sahiProxyProperties.getProxyPort().toString();
        String startUrl = String.format("http://%s:%s", sahiHost, sahiPort);
        logger.info("connect Sahi-TestRunner:{}", startUrl);
        return new TestRunner(
                testSuite.getAbsolutePathOfTestSuiteFile(),  //path to the .suite file
                testSuite.getBrowserName(),   //the browser name, for example "firefox"
                startUrl,                     //the start url, for example "http://localhost:9999"
                sahiHost,                     //host on which the sahi proxy will started
                sahiPort,                     //port on which the sahi port is opend
                threads);                     //numer of parallel process in the sakuli application still 1
    }

    protected String getIncludeFolderJsPath() {
        String path = sakuliProperties.getJsLibFolder().toAbsolutePath().toString() + File.separator + "sakuli.js";
        if (path.contains("\\")) {
            //replace \ with \\
            path = path.replaceAll("\\\\", "\\\\\\\\");
        }
        return path;
    }

    protected String getTestSuiteFolderJsPath() {
        String path = testSuite.getTestSuiteFolder().toAbsolutePath().toString();
        if (path.contains("\\")) {
            //replace \ with \\
            path = path.replaceAll("\\\\", "\\\\\\\\");
        }
        return path;
    }

    /**
     * reconnect method for method {@link #startSahiTestSuite()}
     *
     * @param e the thrown ConnectException or IllegalMonitorStateException
     * @throws InterruptedException
     */
    protected void reconnect(Exception e) throws InterruptedException, SakuliException {
        logger.warn("Cannot connect to sahi proxy - start Proxy.main()");
        if (countConnections <= sahiProxyProperties.getMaxConnectTries()) {
            logger.info(
                    "RECONNECT to sahi proxy in "
                            + sahiProxyProperties.getReconnectSeconds()
                            + " seconds"
            );

            //send thread to sleep
            Thread.sleep(TimeUnit.SECONDS.toMillis(sahiProxyProperties.getReconnectSeconds()));
            this.startSahiTestSuite();
        } else {
            logger.info("Reconnect to sahi proxy unsuccessful - Connection refused");
            throw new InterruptedException(e.getMessage());
        }
    }

}
