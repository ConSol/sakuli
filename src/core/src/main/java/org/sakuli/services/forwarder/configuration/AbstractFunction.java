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

package org.sakuli.services.forwarder.configuration;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

/**
 * This class defines an abstract jtwig function, which has to be used as base for all other custom funcitons.
 *
 * @author Georgi Todorov
 */
public abstract class AbstractFunction extends SimpleJtwigFunction {

    /**
     * Verifies the number and the types of the function arguments.
     * @param request
     * @param expectedNumberOfArguments
     * @param expectedArgumentTypes
     */
    protected void verifyFunctionParameters(FunctionRequest request, int expectedNumberOfArguments, Class... expectedArgumentTypes) {
        if (request.getNumberOfArguments() != expectedNumberOfArguments) {
            throw new IllegalArgumentException(
                    String.format("Wrong number of arguments for function '%s' provided. Expected: '%s', actual: '%s'.",
                            name(), expectedNumberOfArguments, request.getNumberOfArguments())
            );
        }
        for (int i = 0; i < request.getNumberOfArguments(); i++) {
            Object argument = request.getArguments().get(i);
            Class expectedArgumentType = expectedArgumentTypes[i];
            if (!expectedArgumentType.isAssignableFrom(argument.getClass())) {
                throw new IllegalArgumentException(
                        String.format("Wrong argument type for function '%s' provided. Expected: '%s', actual: '%s'.",
                                name(), expectedArgumentType, argument.getClass())
                );
            }

        }
    }

}
