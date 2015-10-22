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
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.MockUtil;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BaseActionLoaderImpl;
import org.sakuli.loader.BeanLoader;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck Date: 25.07.13
 */
public abstract class BaseTest extends AbstractLogAwareTest {

    public static final String SAKULI_HOME_FOLDER_PATH = StringUtils.replace(
            "../common/src/main/resources/org/sakuli/common", "/", File.separator);
    public static final String SAHI_FOLDER_PATH = ".." + File.separator + "sahi";
    public static final String TEST_FOLDER_PATH = getResource("/_testsuite4JUnit");
    public static final String TEST_CONTEXT_PATH = "JUnit-beanRefFactory.xml";
    protected BaseActionLoader loaderMock;


    public static void assertRegExMatch(String string, String regex) {
        assertTrue(string.matches(regex),
                String.format("string '%s' won't match to regex '%s'", string, regex));
    }

    public static void assertContains(String string, String contains) {
        assertTrue(string.contains(contains),
                String.format("string '%s' won't contain '%s'", string, contains));
    }

    protected String getTestContextPath() {
        return TEST_CONTEXT_PATH;
    }

    protected String getSahiFolderPath() {
        return SAHI_FOLDER_PATH;
    }

    protected String getSakuliHomeFolderPath() {
        return SAKULI_HOME_FOLDER_PATH;
    }

    protected String getTestFolderPath() {
        return TEST_FOLDER_PATH;
    }

    @BeforeClass(alwaysRun = true)
    public void setContextProperties() {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = getTestFolderPath();
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = getSakuliHomeFolderPath();
        SakuliPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = getSahiFolderPath();
        BeanLoader.CONTEXT_PATH = getTestContextPath();
        BeanLoader.refreshContext();
        loaderMock = BeanLoader.loadBean(BaseActionLoaderImpl.class);
        if (new MockUtil().isMock(loaderMock)) {
            reset(loaderMock);
            when(loaderMock.getSahiReport()).thenReturn(mock(Report.class));
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = BaseTest.SAKULI_HOME_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }
}
