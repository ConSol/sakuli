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

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

/**
 * @author tschneck
 *         Date: 15.05.14
 */
public class ConnectionTester {

    static Logger logger = LoggerFactory.getLogger(ConnectionTester.class);

    public static void checkTestCaseInitURL(TestSuite testSuite) throws SakuliException {
        if (testSuite != null) {
            if (testSuite.getTestCases() != null) {
                for (TestCase tc : testSuite.getTestCases().values()) {
                    pingURL(tc.getStartUrl());
                }
            }
            throw new SakuliException(String.format("no test cases for test suite '%s' have been loaded! Check the configuration in the file '%s'",
                    testSuite.getId(),
                    testSuite.getAbsolutePathOfTestSuiteFile()));
        }
        throw new SakuliException("The test suite has not be configured correctly, please check your settings!");
    }

    //TODO TS finalize
    public static int pingURL(String url) {
        HttpURLConnection connection = null;
        int code = -1;
        try {
            InetSocketAddress proxyInet = new InetSocketAddress("proxy.consol.de", 8001);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyInet);
//            URL httpsUrl = new URL("https://192.168.17.22:8443/test");
//            HttpsURLConnection httpsCon = (HttpsURLConnection) httpsUrl.openConnection(proxy);

            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection(proxy);
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(10000);
            code = connection.getResponseCode();
            logger.info("PING result for {}: {}", url, code);
            // You can determine on HTTP return code received. 200 is success.
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return code;
    }
}
