/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.services.forwarder;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.*;
import org.sakuli.loader.ScreenActionLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.sakuli.BaseTest.assertRegExMatch;
import static org.testng.Assert.*;

@SuppressWarnings({"unchecked", "ThrowableNotThrown"})
public class AbstractTeardownServiceTest {

    @Spy
    @InjectMocks
    private AbstractTeardownService testling = new AbstractTeardownService() {
        @Override
        public int getServicePriority() {
            return 0;
        }

        @Override
        public void teardownTestCase(@NonNull TestCase testCase) throws RuntimeException {
            throw new SakuliRuntimeException("teardown test case");
        }
    };
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private ScreenActionLoader loader;
    @Mock
    private ActionProperties props;
    @Mock
    private SakuliProperties sakuliProps;
    private TestSuite testsuite;

    @BeforeMethod
    public void setUp() {
        exceptionHandler = spy(SakuliExceptionHandler.class);
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(exceptionHandler, "loader", loader);
        testsuite = new TestSuiteExampleBuilder().buildExample();
        when(props.isTakeScreenshots()).thenReturn(false);
        when(sakuliProps.isSuppressResumedExceptions()).thenReturn(false);
        when(loader.getActionProperties()).thenReturn(props);
        when(loader.getSakuliProperties()).thenReturn(sakuliProps);
        when(loader.getTestSuite()).thenReturn(testsuite);
    }

    @Test
    public void testHandleTeardownExceptionSequential() {
        testling.tearDown(Optional.of(testsuite));
        verify(exceptionHandler).handleException(any(SakuliRuntimeException.class));
        verify(testling, never()).addTestRef(any(), any());

        List<Exception> resumeExceptions = (List<Exception>) ReflectionTestUtils.getField(exceptionHandler, "resumeExceptions");
        //ensures that no stop execution action wil be triggered
        assertTrue(resumeExceptions.stream().noneMatch(e -> e.getClass().isAssignableFrom(SakuliRuntimeException.class)));

        assertEquals(testsuite.getException().getClass(), SakuliRuntimeException.class);
        assertRegExMatch(testsuite.getException().toString(), "Method 'teardownTestSuite' is not implemented for forwarder class.*");
    }

    @Test
    public void testHandleTeardownExceptionAsync() {
        final TestCase testCase = testsuite.getTestCases().values().stream().findFirst().get();
        testling.tearDown(Optional.of(testCase), true);
        verify(exceptionHandler).handleException(any(SakuliRuntimeException.class));
        verify(testling).addTestRef(any(SakuliRuntimeException.class), eq(testCase));

        List<Exception> resumeExceptions = (List<Exception>) ReflectionTestUtils.getField(exceptionHandler, "resumeExceptions");
        //ensures that no stop execution action wil be triggered
        assertTrue(resumeExceptions.stream().allMatch(e -> e.getClass().isAssignableFrom(SakuliRuntimeException.class)));

        assertNull(testsuite.getException());
        assertEquals(testCase.getException().getClass(), SakuliRuntimeException.class);
        assertEquals(testCase.getException().toString(), "teardown test case");
    }

    @Test
    public void testAddTestRefSakuliException() {
        @NonNull Exception e = new SakuliForwarderRuntimeException("");
        final Exception result = testling.addTestRef(e, testsuite);
        assertEquals(result.getClass(), SakuliForwarderRuntimeException.class);
        assertEquals(((SakuliException) result).getAsyncTestDataRef().get(), testsuite);
    }

    @Test
    public void testAddTestRefNoSakuliException() {
        @NonNull Exception e = new RuntimeException("");
        final Exception result = testling.addTestRef(e, testsuite);
        assertEquals(result.getClass(), SakuliCheckedException.class);
        assertEquals(result.getCause(), e);
        assertEquals(((SakuliException) result).getAsyncTestDataRef().get(), testsuite);
    }
}