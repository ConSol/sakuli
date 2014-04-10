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

package de.consol.sakuli.loader;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * autor tschneck
 */
public class BaseActionEnvironmentLoaderTest {
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliExceptionHandler exceptionHandler;

    @InjectMocks
    private BaseActionLoaderImpl testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("Test", testCaseId));
        testling.init(testCaseId, ".");
        verify(exceptionHandler, never()).handleException(any(Exception.class));
    }

    @Test
    public void testInitTCNull() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(null);
        testling.init(testCaseId, ".");
        verify(exceptionHandler, times(1)).handleException(any(SakuliException.class));
    }

    @Test
    public void testInitImagePathNull() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("test", testCaseId));
        testling.init(testCaseId);
        verify(exceptionHandler, times(1)).handleException(any(IOException.class));
    }

    @Test
    public void testInitImagePathEmpty() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("test", testCaseId));
        testling.init(testCaseId, new String[0]);
        verify(exceptionHandler, times(1)).handleException(any(IOException.class));
    }
}
