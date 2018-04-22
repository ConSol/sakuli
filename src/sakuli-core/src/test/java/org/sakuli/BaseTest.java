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

import org.mockito.internal.util.MockUtil;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BaseActionLoaderImpl;
import org.sakuli.loader.BeanLoader;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.reset;

/**
 * @author tschneck Date: 25.07.13
 */
public abstract class BaseTest extends AbstractBaseTest {

    protected BaseActionLoader loaderMock;

    @BeforeClass(alwaysRun = true)
    public void setContextProperties() {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = getTestFolderPath();
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = getSakuliHomeFolderPath();
        BeanLoader.CONTEXT_PATH = getTestContextPath();
        BeanLoader.refreshContext();
        initBaseActionLoader();
    }

    protected void initBaseActionLoader() {
        loaderMock = BeanLoader.loadBean(BaseActionLoaderImpl.class);
        if (MockUtil.isMock(loaderMock)) {
            reset(loaderMock);
            when(loaderMock.getSahiReport()).thenReturn(mock(Report.class));
            when(loaderMock.getSakuliProperties()).thenReturn(mock(SakuliProperties.class));
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = BaseTest.SAKULI_HOME_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }

    protected synchronized void assertLastLine(Path logFile, String filter, LogLevel logLevel, String expectedMessage) throws IOException {
        super.assertLastLine(logFile, filter, logLevel.name(), expectedMessage);
    }
}
