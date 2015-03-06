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

package org.sakuli.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck Date: 09.05.14
 */
@Component
public class LoggerInitializer {

    private static final String LOG_CONFIG_FILE_NAME = "sakuli-log-config.xml";
    private static Logger logger = LoggerFactory.getLogger(LoggerInitializer.class);

    @Autowired
    private SakuliProperties sakuliProperties;

    @PostConstruct
    public void initLoggerContext() throws JoranException, URISyntaxException, SakuliException {
        //start sysout forwarding to slf4j
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // override default configuration

        // set the properties of the LoggerContext
        context.putProperty(SakuliProperties.LOG_FOLDER, sakuliProperties.getLogFolder().toAbsolutePath().toString());
        context.putProperty(SakuliProperties.LOG_PATTERN, sakuliProperties.getLogPattern());

        //determine the config file
        String configFilePath = getConfigFileFromClasspath();
        if (configFilePath == null) {
            configFilePath = getConfigFile();
        }
        if (configFilePath == null) {
            throw new SakuliException("file '" + LOG_CONFIG_FILE_NAME + "'not found! Please ensure that your include folder or your classpath contains at least the file '" + LOG_CONFIG_FILE_NAME + "'.");
        }
        jc.doConfigure(configFilePath);

        //log all properties after logger is configured
        logger.info("set logback configuration file '{}'", configFilePath);
        logger.info("set '{}' to '{}'", SakuliProperties.LOG_FOLDER, sakuliProperties.getLogFolder().toAbsolutePath().toString());
        logger.info("set '{}' to '{}''", SakuliProperties.LOG_PATTERN, sakuliProperties.getLogPattern());
    }

    protected String getConfigFile() throws SakuliException {
        Path configFile = Paths.get(sakuliProperties.getConfigFolder() + File.separator + LOG_CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            return configFile.toAbsolutePath().toString();
        }
        return null;
    }

    protected String getConfigFileFromClasspath() {
        try {
            return ResourceHelper.getClasspathResource(
                    getClass(),
                    "/" + LOG_CONFIG_FILE_NAME,
                    "unexpected error by resolving the '" + LOG_CONFIG_FILE_NAME + "' from classpath, now try to resolve it from the include folder."
            )
                    .toAbsolutePath()
                    .toString();
        } catch (NoSuchFileException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
