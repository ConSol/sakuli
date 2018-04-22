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

package org.sakuli.services.forwarder.configuration;

import org.jtwig.functions.FunctionRequest;
import org.sakuli.datamodel.state.SakuliState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractFunctionTest {
    private AbstractFunction testling = new AbstractFunction() {
        @Override
        public String name() {
            return "unit_test";
        }

        @Override
        protected int getExpectedNumberOfArguments() {
            return 2;
        }

        @Override
        protected List<Class> getExpectedArgumentTypes() {
            return Arrays.asList(Boolean.class, String.class);
        }

        @Override
        protected Object execute(List<Object> arguments) {
            return arguments;
        }
    };

    @DataProvider(name = "edgeValues")
    public static Object[][] edgeValues() {
        return new Object[][]{
                {true, "TEST"},
                {FALSE, "TEST"},
                {null, "TEST"},
                {TRUE, ""},
                {TRUE, null},
        };
    }

    @Test(dataProvider = "edgeValues")
    public void testVerifyFunctionArguments(Boolean bool, String s) throws Exception {
        FunctionRequest request = mock(FunctionRequest.class);
        when(request.getNumberOfArguments()).thenReturn(testling.getExpectedNumberOfArguments());
        when(request.getArguments()).thenReturn(Arrays.asList(bool, s));
        testling.verifyFunctionArguments(request, testling.getExpectedNumberOfArguments(), testling.getExpectedArgumentTypes());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Wrong number of arguments for function 'unit_test' provided. Expected: '2', actual: '1'.")
    public void testVerifyFunctionArgumentsExceptionArgument() throws Exception {
        FunctionRequest request = mock(FunctionRequest.class);
        when(request.getNumberOfArguments()).thenReturn(1);
        when(request.getArguments()).thenReturn(Collections.singletonList(true));
        testling.verifyFunctionArguments(request, testling.getExpectedNumberOfArguments(), testling.getExpectedArgumentTypes());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Wrong 1. argument type for function 'unit_test' provided. Expected: 'class java.lang.Boolean', actual: 'class java.lang.String'.")
    public void testVerifyFunctionArgumentsExceptionClass() throws Exception {
        FunctionRequest request = mock(FunctionRequest.class);
        when(request.getNumberOfArguments()).thenReturn(testling.getExpectedNumberOfArguments());
        when(request.getArguments()).thenReturn(Arrays.asList("test", "ddd"));
        testling.verifyFunctionArguments(request, testling.getExpectedNumberOfArguments(), testling.getExpectedArgumentTypes());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Wrong 2. argument type for function 'unit_test' provided. Expected: 'class java.lang.String', actual: 'class java.lang.Long'.")
    public void testVerifyFunctionArgumentsExceptionClass2() throws Exception {
        FunctionRequest request = mock(FunctionRequest.class);
        when(request.getNumberOfArguments()).thenReturn(testling.getExpectedNumberOfArguments());
        when(request.getArguments()).thenReturn(Arrays.asList(true, 1L));
        testling.verifyFunctionArguments(request, testling.getExpectedNumberOfArguments(), testling.getExpectedArgumentTypes());
    }

    @Test
    public void testSuperclass() throws Exception {
        AbstractFunction testling = new AbstractFunction() {
            @Override
            protected int getExpectedNumberOfArguments() {
                return 1;
            }

            @Override
            protected List<Class> getExpectedArgumentTypes() {
                return Collections.singletonList(SakuliState.class);
            }

            @Override
            protected Object execute(List<Object> arguments) {
                return arguments;
            }

            @Override
            public String name() {
                return "test_superclass";
            }
        };
        FunctionRequest request = mock(FunctionRequest.class);
        when(request.getNumberOfArguments()).thenReturn(testling.getExpectedNumberOfArguments());
        when(request.getArguments()).thenReturn(Collections.singletonList(TestSuiteState.OK));
        testling.verifyFunctionArguments(request, testling.getExpectedNumberOfArguments(), testling.getExpectedArgumentTypes());
    }
}