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

package org.sakuli;

import net.sf.sahi.report.Report;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.mockito.internal.util.MockUtil;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.loader.SahiActionLoader;
import org.sakuli.starter.sahi.utils.SahiStarterPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

/**
 * @author tschneck Date: 25.07.13
 */
public abstract class BaseTest extends AbstractBaseTest {
    public static final String SAHI_FOLDER_PATH = getResource("starter/sahi/sahi_proxy");

    protected SahiActionLoader loaderMock;

    @BeforeClass(alwaysRun = true)
    public void setContextProperties() {
        SahiStarterPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = getTestFolderPath();
        SahiStarterPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = getSakuliHomeFolderPath();
        SahiStarterPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = getSahiFolderPath();
        BeanLoader.CONTEXT_PATH = getTestContextPath();
        BeanLoader.refreshContext();
        initBaseActionLoader();
    }

    protected String getSahiFolderPath() {
        return SAHI_FOLDER_PATH;
    }

    protected void initBaseActionLoader() {
        loaderMock = BeanLoader.loadBean(SahiActionLoader.class);
        if (new MockUtil().isMock(loaderMock)) {
            reset(loaderMock);
            when(loaderMock.getSahiReport()).thenReturn(mock(Report.class));
        } else {
            RhinoScriptRunner runnermock = mock(RhinoScriptRunner.class);
            when(runnermock.getReport()).thenReturn(mock(Report.class));
            loaderMock.setRhinoScriptRunner(runnermock);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        SahiStarterPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SahiStarterPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = BaseTest.SAKULI_HOME_FOLDER_PATH;
        SahiStarterPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }

    protected synchronized void assertLastLine(Path logFile, String filter, LogLevel logLevel, String expectedMessage) throws IOException {
        super.assertLastLine(logFile, filter, logLevel.name(), expectedMessage);
    }
}
