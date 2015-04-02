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

package org.sakuli.starter.helper;

import net.sf.sahi.Proxy;
import net.sf.sahi.config.Configuration;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.exceptions.SakuliInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Starts the proxy like <sahi-installation-folder>/bin/sakuli_proxy.sh
 */
@Component
public class SahiProxy {
    public static final String SAHI_INJECT_END = "<!--SAHI_INJECT_END-->";
    public static final String SAKULI_INJECT_SCRIPT_TAG = "<script src='/_s_/spr/sakuli/inject.js'></script>";
    //static fields for property names

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SahiProxyProperties props;

    private Proxy sahiProxy;

    /**
     * see {@link #startProxy(boolean)} with parameter true.
     */
    public void startProxy() throws SakuliInitException, FileNotFoundException {
        startProxy(true);
    }

    /**
     * Starts a seperate Thread for the sahi proxy.
     * You can do a shutdown with
     *
     * @param asyncron init if a separate thread should started
     * @throws SakuliInitException if the specified files in the sahi.properties are not valid
     */
    public void startProxy(boolean asyncron) throws SakuliInitException, FileNotFoundException {

        sahiProxy = new Proxy(props.getProxyPort());

        if (Files.exists(props.getSahiHomeFolder()) && Files.exists(props.getSahiConfigFolder())) {
            logger.info("START Sahi-PROXY: Sahi-Home folder '{}', Sahi-Configuration folder '{}'",
                    props.getSahiHomeFolder().toAbsolutePath().toString(),
                    props.getSahiConfigFolder().toAbsolutePath().toString()
            );

            try {
                injectCustomJavaScriptFiles();
                //set the custom paths to the sahi environment
                Configuration.init(props.getSahiHomeFolder().toAbsolutePath().toString(), props.getSahiConfigFolder().toAbsolutePath().toString());
                Configuration.setUnmodifiedTrafficLogging(false);
                Configuration.setModifiedTrafficLogging(false);
                //Starts the sahi proxy as asynchron Thread

                sahiProxy.start(asyncron);
                Thread.sleep(200);
            } catch (RuntimeException e) {
                logger.error("RUNTIME EXCEPTION");
                throw new SakuliInitException(e);
            } catch (Throwable e) {
                logger.error("THROWABLE EXCEPTION");
                throw new SakuliInitException(e.getMessage());
            }
        } else {
            throw new FileNotFoundException("the path to '" + SahiProxyProperties.PROXY_HOME_FOLDER + "=" + props.getSahiHomeFolder().toAbsolutePath().toString()
                    + "' or '" + SahiProxyProperties.PROXY_CONFIG_FOLDER + "=" + props.getSahiConfigFolder().toAbsolutePath().toString() +
                    "' is not valid, please check the properties files!");
        }
    }

    protected void injectCustomJavaScriptFiles() throws IOException {
        File injectFile = props.getSahiJSInjectConfigFile().toFile();
        String injectFileString = FileUtils.readFileToString(injectFile, Charsets.UTF_8);
        if (!injectFileString.contains(SAKULI_INJECT_SCRIPT_TAG)) {
            injectFileString = StringUtils.replace(injectFileString, SAHI_INJECT_END, SAKULI_INJECT_SCRIPT_TAG + "\r\n" + SAHI_INJECT_END);
            FileUtils.writeStringToFile(injectFile, injectFileString, Charsets.UTF_8);
            logger.info("added '{}' to Sahi inject config file '{}'", SAKULI_INJECT_SCRIPT_TAG, props.getSahiJSInjectConfigFile().toString());
        }

        Path source = props.getSahiJSInjectSourceFile();
        Path target = props.getSahiJSInjectTargetFile();
        if (isNewer(source, target)) {
            FileUtils.copyFile(source.toFile(), target.toFile(), false);
            logger.info("copied file '{}' to target '{}'", source.toString(), target.toString());
        }
    }

    private boolean isNewer(Path source, Path target) {
        return !Files.exists(target)
                || FileUtils.isFileNewer(source.toFile(), target.toFile());
    }

    public void shutdown() throws SakuliInitException {
        logger.debug("SHUTDOWN SAHI-Proxy now!");
        sahiProxy.stop();
        if (!sahiProxy.isRunning()) {
            logger.info("SHUTDOWN SAHI-Proxy SUCCESSFULLY");
        } else {
            throw new SakuliInitException("SAHI-Proxy failed to Shutdown!");
        }

    }
}
