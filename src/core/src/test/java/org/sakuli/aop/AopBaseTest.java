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

import org.sakuli.BaseTest;
import org.sakuli.PropertyHolder;
import org.sakuli.loader.BeanLoader;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck Date: 23.09.14
 */
public abstract class AopBaseTest extends BaseTest {

    protected Path logFile;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        logFile = Paths.get(BeanLoader.loadBean(PropertyHolder.class).getLogFile());
    }

    @Override
    protected String getTestFolderPath() {
        return getResource("/_testsuite4aop");
    }

    @Override
    protected String getTestContextPath() {
        return "aopTest-beanRefFactory.xml";
    }
}

