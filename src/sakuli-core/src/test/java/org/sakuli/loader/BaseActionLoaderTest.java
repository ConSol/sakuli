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

package org.sakuli.loader;

import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.utils.CleanUpHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * autor tschneck
 */
public class BaseActionLoaderTest {
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private CleanUpHelper cleanUpHelper;

    @Spy
    @InjectMocks
    private BaseActionLoaderImpl testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(testling).callInitTestCaseCallback();
    }

    @Test
    public void testInitTCNull() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(null);
        testling.init(testCaseId, ".");
        verify(exceptionHandler, times(1)).handleException(any(SakuliException.class));
        verify(testling, never()).callInitTestCaseCallback();
        verify(cleanUpHelper, never()).releaseAllModifiers();
    }

    @Test
    public void testInitImagePathNull() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("test", testCaseId));
        testling.init(testCaseId, new String[]{});
        verify(exceptionHandler, times(0)).handleException(any(IOException.class));
        verify(testling).callInitTestCaseCallback();
        verify(cleanUpHelper).releaseAllModifiers();

    }

    @Test
    public void testInitImagePathEmpty() throws Exception {
        String testCaseId = "xyz";
        when(testSuite.getTestCase(testCaseId)).thenReturn(new TestCase("test", testCaseId));
        testling.init(testCaseId, new String[0]);
        verify(exceptionHandler, times(0)).handleException(any(IOException.class));
        verify(testling).callInitTestCaseCallback();
        verify(cleanUpHelper).releaseAllModifiers();
    }

    @Test
    public void testGetCurrenTestCase() throws Exception {
        assertNull(testling.getCurrentTestCaseStep());
        TestCase tc = new TestCase("test", "nocase");
        ReflectionTestUtils.setField(testling, "currentTestCase", tc);
        assertNotNull(testling.getCurrentTestCase());
        assertNull(testling.getCurrentTestCaseStep());
        DateTime creationDate = new DateTime();
        tc.addStep(new TestCaseStepBuilder("step_ok")
                .withState(TestCaseStepState.OK)
                .withCreationDate(creationDate)
                .build());
        tc.addStep(new TestCaseStepBuilder("step_warning")
                .withState(TestCaseStepState.WARNING)
                .withCreationDate(creationDate.plusMillis(1))
                .build());
        assertNull(testling.getCurrentTestCaseStep());


        tc.addStep(new TestCaseStepBuilder("step_init_1")
                .withState(TestCaseStepState.INIT)
                .withCreationDate(creationDate.plusMillis(10))
                .build());
        assertNotNull(testling.getCurrentTestCaseStep());
        assertEquals(testling.getCurrentTestCaseStep().getName(), "step_init_1");

        tc.addStep(new TestCaseStepBuilder("step_init_2")
                .withState(TestCaseStepState.INIT)
                .withCreationDate(creationDate.plusMillis(11))
                .build());
        assertNotNull(testling.getCurrentTestCaseStep());
        assertEquals(testling.getCurrentTestCaseStep().getName(), "step_init_1");

        //add step before other init state's
        tc.addStep(new TestCaseStepBuilder("step_error")
                .withState(TestCaseStepState.ERRORS)
                .withCreationDate(creationDate.plusMillis(2))
                .build());
        assertNotNull(testling.getCurrentTestCaseStep());
        assertEquals(testling.getCurrentTestCaseStep().getName(), "step_error");
    }
}
