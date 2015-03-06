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

package org.sakuli.aop;

import org.sakuli.AbstractLogAwareTest;
import org.sakuli.BaseTest;
import org.sakuli.PropertyHolder;
import org.sakuli.loader.BeanLoader;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck Date: 23.09.14
 */
public abstract class AopBaseTest extends AbstractLogAwareTest {

    protected Path logFile;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_MAIN_FOLDER_VALUE = BaseTest.SAKULI_MAIN_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "aopTest-beanRefFactory.xml";
        logFile = Paths.get(BeanLoader.loadBean(PropertyHolder.class).getLogFile());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }

}
