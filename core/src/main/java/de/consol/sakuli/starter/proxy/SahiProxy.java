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

import de.consol.sakuli.datamodel.properties.SahiProxyProperties;
import de.consol.sakuli.exceptions.SakuliProxyException;
import net.sf.sahi.Proxy;
import net.sf.sahi.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Files;


/**
 * Starts the proxy like <sahi-installation-folder>/bin/sakuli_proxy.sh
 */
@Component
public class SahiProxy {
    //static fields for property names

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SahiProxyProperties props;

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

        sahiProxy = new Proxy(props.getProxyPort());

        if (Files.exists(props.getSahiHomeFolder()) && Files.exists(props.getSahiConfigFolder())) {
            logger.info("START Sahi-PROXY:\nSahi-Home folder: "
                            + props.getSahiHomeFolder().toAbsolutePath().toString()
                            + "\nSahi-Configuration folder: "
                            + props.getSahiConfigFolder().toAbsolutePath().toString()
            );

            //set the custom paths to the sahi environment
            Configuration.init(props.getSahiHomeFolder().toAbsolutePath().toString(), props.getSahiConfigFolder().toAbsolutePath().toString());
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
            throw new FileNotFoundException("the path to '" + SahiProxyProperties.PROXY_HOME_FOLDER + "=" + props.getSahiHomeFolder().toAbsolutePath().toString()
                    + "' or '" + SahiProxyProperties.PROXY_CONFIG_FOLDER + "=" + props.getSahiConfigFolder().toAbsolutePath().toString() +
                    "' is not valid, please check the properties files!");
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
