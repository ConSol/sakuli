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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.loader.BeanLoader;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class LoggerInitializerTest extends BaseTest {

    private final String logFolder = BaseTest.TEST_FOLDER_PATH + File.separator + "_logs4test";
    @Mock
    private SakuliProperties sakuliProperties;
    @Spy
    @InjectMocks
    private LoggerInitializer testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        when(sakuliProperties.getLogFolder()).thenReturn(Paths.get(logFolder));
        when(sakuliProperties.getLogPattern()).thenReturn("%-5level [%d{YYYY-MM-dd HH:mm:ss}] - %msg%n");
    }

    @AfterClass
    public void cleanUp() throws Throwable {
        //revert to default config
        BeanLoader.loadBean(LoggerInitializer.class).initLoggerContext();
        deleteFile(Paths.get(logFolder + File.separator + "_sakuli.log"));
        deleteFile(Paths.get(logFolder));
    }

    @Test
    public void testInitLoggerContextFromIncludeFolder() throws Throwable {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger root = context.getLogger("root");
        Logger sakuliLogger = context.getLogger("org.sakuli");
        Appender<ILoggingEvent> stdout = root.getAppender("stdout");
        Appender<ILoggingEvent> sakuliAppender = root.getAppender("sakuli");
        //no log config in classpath
        Assert.assertNull(stdout);
        Assert.assertNull(sakuliAppender);
        Assert.assertNotNull(sakuliLogger);
        Assert.assertNotNull(root);

        when(sakuliProperties.getConfigFolder()).thenReturn(Paths.get(BaseTest.SAKULI_MAIN_FOLDER_PATH + SakuliProperties.CONFIG_FOLDER_APPEDER));

        testling.initLoggerContext();
        verify(testling).getConfigFileFromClasspath();
        verify(testling).getConfigFile();

        //verify root logger
        context = (LoggerContext) LoggerFactory.getILoggerFactory();
        root = context.getLogger("root");
        sakuliLogger = context.getLogger("org.sakuli");
        stdout = root.getAppender("stdout");
        sakuliAppender = root.getAppender("sakuli");
        Assert.assertNotNull(stdout);
        Assert.assertNotNull(sakuliLogger);
        Assert.assertNotNull(sakuliAppender);
        Assert.assertNotNull(sakuliAppender);
    }

    @Test(expectedExceptions = SakuliException.class)
    public void testInitLoggerContextException() throws Throwable {
        doReturn(null).when(testling).getConfigFileFromClasspath();
        doReturn(null).when(testling).getConfigFile();

        testling.initLoggerContext();
    }

    @Test
    public void testGetConfigFileFromClasspath() throws Exception {
        String configFileFromClasspath = testling.getConfigFileFromClasspath();
        Assert.assertNull(configFileFromClasspath);
    }

    @Test
    public void testGetConfigFileFromIncludeFolder() throws Throwable {
        when(sakuliProperties.getConfigFolder()).thenReturn(Paths.get(BaseTest.SAKULI_MAIN_FOLDER_PATH + SakuliProperties.CONFIG_FOLDER_APPEDER));
        String configFileFromConfigFolder = testling.getConfigFile();
        Assert.assertNotNull(configFileFromConfigFolder);
        Assert.assertTrue(Files.exists(Paths.get(configFileFromConfigFolder)));
    }
}