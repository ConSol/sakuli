/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.exceptions;

import org.sakuli.BaseTest;
import org.sakuli.loader.ActionLoaderCallback;
import org.sakuli.loader.BeanLoader;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.Mockito.verify;

/**
 * @author tschneck
 *         Date: 4/19/17
 */
public class SakuliExceptionHandlerCallbackTest {
    @BeforeMethod
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = BaseTest.SAKULI_HOME_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "org/sakuli/exceptions/exceptionCallbackTest-beanRefFactory.xml";
        BeanLoader.refreshContext();
    }


    @Test
    public void testHandleExceptionCallback() throws Exception {
        final SakuliExceptionHandler sakuliExceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
        Assert.assertNotNull(sakuliExceptionHandler);
        SakuliException exception = new SakuliInitException("test it");
        sakuliExceptionHandler.triggerCallbacks(exception);

        final Collection<ActionLoaderCallback> callbacks = BeanLoader.loadMultipleBeans(ActionLoaderCallback.class).values();
        Assert.assertEquals(callbacks.size(), 1);
        verify(callbacks.stream().findFirst().get()).handleException(exception);
    }
}