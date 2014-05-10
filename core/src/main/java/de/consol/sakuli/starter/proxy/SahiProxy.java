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

package de.consol.sakuli.starter.proxy;

import de.consol.sakuli.exceptions.SakuliProxyException;
import net.sf.sahi.Proxy;
import net.sf.sahi.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Starts the proxy like <sahi-installation-folder>/bin/sakuli_proxy.sh
 */
@Component
public class SahiProxy {
    //static fields for property names
    public static final String SAHI_PROXY_HOME = "sahiproxy.homePath";
    public static final String SAHI_PROXY_CONFIG = "sahiproxy.configurationPath";
    public static final String SAHI_PROXY_PORT = "sahiproxy.port";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    //Values are defined in the the property file "sahi.properties"
    @Value("${" + SAHI_PROXY_HOME + "}")
    private String sahiHomePath;
    @Value("${" + SAHI_PROXY_CONFIG + "}")
    private String configPath;
    @Value("${" + SAHI_PROXY_PORT + "}")
    private int proxyPort;

    private Proxy sahiProxy;

    /**
     * see {@link #startProxy(boolean)} with parameter true.
     */
    public void startProxy() throws SakuliProxyException, FileNotFoundException {
        startProxy(true);
    }

    /**
     * Starts a seperate Thread for the sahi proxy.
     * You can do a shutdown with
     *
     * @param asyncron init if a separate thread should started
     * @throws SakuliProxyException if the specified files in the sahi.properties are not valid
     */
    public void startProxy(boolean asyncron) throws SakuliProxyException, FileNotFoundException {

        sahiProxy = new Proxy(proxyPort);

        //Helper to get the absolut paths
        Path sahiHome = Paths.get(sahiHomePath).normalize();
        Path sahiConfig = Paths.get(configPath).normalize();

        if (Files.exists(sahiHome) && Files.exists(sahiConfig)) {
            logger.info("START Sahi-PROXY:\nSahi-Home folder: "
                            + sahiHome.toFile().getAbsolutePath()
                            + "\nSahi-Configuration folder: "
                            + sahiConfig.toFile().getAbsolutePath()
            );

            //set the custom paths to the sahi environment
            Configuration.init(sahiHome.toFile().getAbsolutePath(), sahiConfig.toFile().getAbsolutePath());
            Configuration.setUnmodifiedTrafficLogging(true);
            //Starts the sahi proxy as asynchron Thread
            try {
                sahiProxy.start(asyncron);
                Thread.sleep(200);
            } catch (RuntimeException e) {
                logger.error("RUNTIME EXCEPTION");
                throw new SakuliProxyException(e);
            } catch (Throwable e) {
                logger.error("THROWABLE EXCEPTION");
                throw new SakuliProxyException(e.getMessage());
            }
        } else {
            throw new FileNotFoundException("the path to 'sahiproxy.homePath=" + sahiHome
                    + "' or 'sahiproxy.configurationPath=" + sahiConfig +
                    "' is not valid, please check the property file \"sahi.properties\"");
        }
    }

    public void shutdown() throws SakuliProxyException {
        logger.debug("SHUTDOWN SAHI-Proxy now!");
        sahiProxy.stop();
        if (!sahiProxy.isRunning()) {
            logger.info("SHUTDOWN SAHI-Proxy SUCCESSFULLY");
        } else {
            throw new SakuliProxyException("SAHI-Proxy failed to Shutdown!");
        }

    }
}
