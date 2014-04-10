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

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliProxyException;
import de.consol.sakuli.starter.proxy.SahiProxy;
import net.sf.sahi.ant.Report;
import net.sf.sahi.test.TestRunner;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class SahiConnector {
    private final Logger logger = Logger.getLogger(this.getClass());
    private int countConnections = 0;
    /**
     * autowired variables **
     */
    @Autowired
    private SahiProxy sahiProxy;
    @Autowired
    private TestSuite testSuite;
    /**
     * ** Values from the the property file "sahi.properties" **
     */
    @Value("${sahiproxy.maxConnectTries}")
    private int maxConnectTries;
    @Value("${sahiproxy.reconnectSeconds}")
    private int reconnectSeconds;
    @Value("${sahiproxy.configurationPath}")
    private String sahiUserdata;
    /**
     * ** Values form the property file "log4j.properties" **
     */
    @Value("${log.folder}")
    private String logFolder;
    @Autowired
    private SakuliExceptionHandler sakuliExceptionHandler;

    /**
     * Initialize method to start the sahi proxy thread, if needed
     */
    @PostConstruct
    public void init() throws SakuliProxyException, FileNotFoundException {
        logger.info("Initialize Proxy! ");
        sahiProxy.startProxy();
    }

    /**
     * starts a specific sahi test suite in sakuli
     */
    public void startSahiTestSuite() throws SakuliProxyException {
        //have to be allways one, to prevent errors in the sikuli screen capturing parts
        String threads = "1";
        //default start URL of the sahi proxy
        String startUrl = "http://localhost:9999";

        //default sahi runner to play the sahi script
        TestRunner runner = new TestRunner(
                testSuite.getAbsolutePathOfTestSuiteFile(),  //path to the .suite file
                testSuite.getBrowserName(),   //the browser name, for example "firefox"
                startUrl,                     //the start url, for example "http://localhost:9999"
                threads);                     //numer of parallel process in the sakuli application still 1

        //config reporter
        runner.addReport(new Report("html", logFolder));
        //add include folder property
        runner.setInitJS("var $includeFolder = \"" + getIncludeFolderJsPath() + "\";");

        try {
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
                    throw new SakuliProxyException("SAHI-Proxy returned 'FAILURE' ");
                }


            } catch (ConnectException | IllegalMonitorStateException e) {
                //Reconnect - wait for Thread "sahiRunner"
                this.reconnect(e);
            }
        } catch (Throwable e) {
            sakuliExceptionHandler.handleException(e);
        } finally {
            logger.info("test suite finisched");
            //shutdown sahi proxy!
            sahiProxy.shutdown();
        }
    }

    protected String getIncludeFolderJsPath() {
        String path = testSuite.getIncludeFolderPath() + File.separator + "sakuli.inc";
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
    private void reconnect(Exception e) throws InterruptedException, SakuliException {
        logger.warn("Cannot connect to sahi proxy - start Proxy.main()");
        if (countConnections < maxConnectTries) {
            logger.info(
                    "RECONNECT to sahi proxy in "
                            + reconnectSeconds
                            + " seconds"
            );

            //send thread to sleep
            Thread.sleep(TimeUnit.SECONDS.toMillis(reconnectSeconds));
            this.startSahiTestSuite();
        } else {
            logger.info("Reconnect to sahi proxy unsuccessful - Connection refused");
            throw new InterruptedException(e.getMessage());
        }
    }

}
