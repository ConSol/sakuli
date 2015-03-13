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

package org.sakuli;

import net.sf.sahi.report.Report;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.BeforeClass;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck Date: 25.07.13
 */
public abstract class BaseTest extends AbstractLogAwareTest {

    public static final String SAKULI_HOME_FOLDER_PATH = "." + File.separator + "src" + File.separator + "main";
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

    @BeforeClass(alwaysRun = true)
    public void setContextProperties() {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = SAKULI_HOME_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = TEST_CONTEXT_PATH;
        BeanLoader.refreshContext();
        loaderMock = BeanLoader.loadBean(BaseActionLoader.class);
        when(loaderMock.getSahiReport()).thenReturn(mock(Report.class));
    }
}
