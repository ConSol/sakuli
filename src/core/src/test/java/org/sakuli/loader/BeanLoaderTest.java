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

package org.sakuli.loader;

import org.sakuli.BaseTest;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BeanLoaderTest {
    @BeforeMethod
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_MAIN_FOLDER_VALUE = BaseTest.SAKULI_MAIN_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "loaderTest-beanRefFactory.xml";
        BeanLoader.refreshContext();
    }

    @Test
    public void testLoadBaseActionLoader() throws Exception {
        Assert.assertNotNull(BeanLoader.loadBaseActionLoader());
    }

    @Test
    public void testLoadBean() throws Exception {
        Assert.assertNotNull(BeanLoader.loadBean(SakuliProperties.class));
    }

    @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
    public void testLoadBeanException() throws Exception {
        Assert.assertNotNull(BeanLoader.loadBean("unvalidQualifier", SakuliProperties.class));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }
}