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

package de.consol.sakuli;

import de.consol.sakuli.loader.BaseActionLoader;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import net.sf.sahi.report.Report;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Scanner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 25.07.13
 */
public abstract class BaseTest {

    public static final String INCLUDE_FOLDER_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "_include";
    public static final String SAHI_FOLDER_PATH = ".." + File.separator + "sahi";
    public static final String TEST_FOLDER_PATH = "." + File.separator + "src" + File.separator + "test" + File.separator +
            "resources" + File.separator + "_testsuite4JUnit";
    public static final String TEST_CONTEXT_PATH = "JUnit-beanRefFactory.xml";

    protected BaseActionLoader loaderMock;

    public static void deleteFile(Path logFile) {
        FileSystemProvider provider = logFile.getFileSystem().provider();
        try {
            provider.deleteIfExists(logFile);
        } catch (IOException e) {
            //do nothing
        }
    }

    @BeforeClass
    public void setContextProperties() {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = INCLUDE_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = TEST_CONTEXT_PATH;
        loaderMock = BeanLoader.loadBean(BaseActionLoader.class);
        when(loaderMock.getSahiReport()).thenReturn(mock(Report.class));
    }

    protected String getLastLineWithContent(Path file, String s) throws IOException {

        Scanner in;
        String lastLine = "";

        in = new Scanner(Files.newInputStream(file));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.contains(s)) {
                lastLine = line;
            }
        }
        return lastLine;

    }

    protected String getLastLineOfLogFile(Path file) throws IOException {
        return getLastLineWithContent(file, "");
    }

}
