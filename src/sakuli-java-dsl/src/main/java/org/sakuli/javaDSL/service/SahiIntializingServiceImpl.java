/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.javaDSL.service;

import net.sf.sahi.client.Browser;
import net.sf.sahi.config.Configuration;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.services.InitializingService;
import org.sakuli.starter.SahiConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 07.05.15
 */
@Component
public class SahiIntializingServiceImpl implements InitializingService, SahiInitializingService {

    private static final Logger logger = LoggerFactory.getLogger(SahiIntializingServiceImpl.class);
    @Autowired
    private SahiProxyProperties sahiProxyProperties;
    @Autowired
    private SahiConnector sahiConnector;
    @Autowired
    private TestSuiteProperties testSuiteProperties;

    @Override
    public void initTestSuite() throws SakuliInitException {
        if (!testSuiteProperties.isUiTest()) {
            sahiConnector.init();
            Configuration.initJava(
                    sahiProxyProperties.getSahiHomeFolder().toString(),
                    sahiProxyProperties.getSahiConfigFolder().toString()
            );
        }
    }

    @Override
    public Browser getBrowser() {
        if (testSuiteProperties.isUiTest()) {
            return null;
        }
        logger.info("start sahi controlled browser '{}'", testSuiteProperties.getBrowserName());
        Browser browser = new Browser(testSuiteProperties.getBrowserName());
        browser.open();
        browser.navigateTo("http://sahi.example.com/_s_/dyn/Driver_initialized");
        return browser;
    }

    @Override
    public int getServicePriority() {
        return 50;
    }
}
