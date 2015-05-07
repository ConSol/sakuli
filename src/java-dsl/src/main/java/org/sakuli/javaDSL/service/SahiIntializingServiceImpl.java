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
