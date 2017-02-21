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

package org.sakuli.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.LogConfigurationException;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

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
    public void initLoggerContext() {
        // properties of the LoggerContext
        List<ImmutablePair<String, String>> properties = Arrays.asList(
                new ImmutablePair<>(SakuliProperties.LOG_FOLDER, sakuliProperties.getLogFolder().toAbsolutePath().toString()),
                new ImmutablePair<>(SakuliProperties.LOG_PATTERN, sakuliProperties.getLogPattern()),
                new ImmutablePair<>(SakuliProperties.LOG_LEVEL_SAKULI, sakuliProperties.getLogLevelSakuli()),
                new ImmutablePair<>(SakuliProperties.LOG_LEVEL_SAHI, sakuliProperties.getLogLevelSahi()),
                new ImmutablePair<>(SakuliProperties.LOG_LEVEL_SIKULI, sakuliProperties.getLogLevelSikuli()),
                new ImmutablePair<>(SakuliProperties.LOG_LEVEL_SPRING, sakuliProperties.getLogLevelSpring()),
                new ImmutablePair<>(SakuliProperties.LOG_LEVEL_ROOT, sakuliProperties.getLogLevelRoot())
        );
        //log new properties before configuring
        properties.stream().filter(p -> isNotEmpty(p.getValue()))
                .forEach(p -> logger.info("set '{}' to '{}'", p.getKey(), p.getValue()));

        //start sysout forwarding to slf4j
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset(); // overwrite default configuration

        //put properties into the context
        properties.stream().filter(p -> isNotEmpty(p.getValue()))
                .forEach(p -> context.putProperty(p.getKey(), p.getValue()));

        //determine the config file
        String configFilePath = getConfigFileFromClasspath();
        if (configFilePath == null) {
            configFilePath = getConfigFile();
        }
        if (configFilePath == null) {
            throw new LogConfigurationException("Log configuration file '" + LOG_CONFIG_FILE_NAME + "' not found! Please ensure that your config folder or your classpath contains the file.");
        }
        try {
            jc.doConfigure(configFilePath);
        } catch (JoranException e) {
            throw new LogConfigurationException("unable to run the LoggerIntializer, pleae check your config!", e);
        }

        logger.info("set logback configuration file '{}'", configFilePath);
        // Jersey uses java.util.logging - bridge to slf4
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    protected String getConfigFile() {
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
