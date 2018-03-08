/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.common;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 * Date: 2/12/16
 */
public class LogCleanUpResultServiceImplTest {

    @Mock
    private SakuliProperties sakuliProperties;

    @InjectMocks
    @Spy
    private LogCleanUpResultServiceImpl testling;
    private Path tempLog;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(sakuliProperties.getLogMaxAge()).thenReturn(14);
        tempLog = Paths.get(BaseTest.getResource(".", this.getClass()) + "/temp_log");
        deletePath(tempLog);
    }

    private void deletePath(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.newDirectoryStream(path).forEach(e -> {
                try {
                    if (Files.isDirectory(e)) {
                        deletePath(e);
                    } else {
                        Files.deleteIfExists(e);
                    }
                } catch (IOException e1) {
                    throw new RuntimeException(e.toString());
                }
            });
        }
    }

    @Test
    public void testNoLogFolder() throws Exception {
        when(sakuliProperties.getLogFolder()).thenReturn(Paths.get("NOT_EXIST"));
        testling.triggerAction(new TestSuite());
        verify(testling, never()).cleanUpDirectory(any());
    }

    @Test
    public void testTriggerAction() throws Exception {
        Files.createDirectories(tempLog);
        Path folder1 = Files.createDirectories(tempLog.resolve("folder1"));
        Path today = Files.createFile(folder1.resolve("today.log"));
        FileTime todayMinus15 = FileTime.from(Instant.now().minus(15, ChronoUnit.DAYS));
        Path toOld1 = createFileWithDate(folder1.resolve("today-15.log"), todayMinus15);
        Path toOld2 = createFileWithDate(tempLog.resolve("root-today-15.log"), todayMinus15);
        FileTime todayMinus13 = FileTime.from(Instant.now().minus(13, ChronoUnit.DAYS));
        Path notToOld = createFileWithDate(folder1.resolve("today-13.log"), todayMinus13);
        Path notToOld2 = createFileWithDate(tempLog.resolve("today-13.log"), todayMinus13);

        when(sakuliProperties.getLogFolder()).thenReturn(tempLog);
        testling.triggerAction(new TestSuite());
        verify(testling, times(2)).cleanUpDirectory(any());
        assertTrue(Files.exists(folder1));
        assertTrue(Files.exists(today));
        assertTrue(Files.exists(notToOld));
        assertTrue(Files.exists(notToOld2));
        assertFalse(Files.exists(toOld1));
        assertFalse(Files.exists(toOld2));

    }

    private Path createFileWithDate(Path path, FileTime todayMinus15) throws IOException {
        Path file = Files.createFile(path);
        BasicFileAttributeView fileAttributeView = Files.getFileAttributeView(file, BasicFileAttributeView.class);
        fileAttributeView.setTimes(todayMinus15, todayMinus15, todayMinus15);
        return file;
    }
}