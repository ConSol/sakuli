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

package org.sakuli.loader;

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author tschneck
 *         Date: 4/19/17
 */
public class ActionLoaderCallbackTest extends BeanLoaderTest {
    @Test
    public void testInitTestCase() throws Exception {
        final String testCaseID = "test-action-hook";
        final TestCase testCase = new TestCase(testCaseID, testCaseID);
        Assert.assertNotNull(BeanLoader.loadBaseActionLoader());
        final Collection<ActionLoaderCallback> callbacks = BeanLoader.loadMultipleBeans(ActionLoaderCallback.class).values();
        Assert.assertEquals(callbacks.size(), 1);
        when(BeanLoader.loadBean(TestSuite.class).getTestCase(any())).thenReturn(testCase);

        BeanLoader.loadBaseActionLoader().init(testCaseID, Paths.get("."));
        verify(BeanLoader.loadBean(SakuliExceptionHandler.class), never()).handleException(any());
        verify(callbacks.stream().findFirst().get()).initTestCase(testCase);
    }

    @Test
    public void testReleaseCallback() throws Exception {
        final Collection<ActionLoaderCallback> callbacks = BeanLoader.loadMultipleBeans(ActionLoaderCallback.class).values();
        Assert.assertEquals(callbacks.size(), 1);
        BeanLoader.releaseContext();
        verify(callbacks.stream().findFirst().get()).releaseContext();

    }
}