/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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
import org.sakuli.services.forwarder.OutputState;

/**
 * Custom JtwigFunction for retrieving the OutputState for a provided SakuliState.
 *
 * @author Georgi Todorov
 */
public class GetOutputStateFunction extends AbstractFunction {

    @Override
    public String name() {
        return "getOutputState";
    }

    @Override
    public Object execute(FunctionRequest request) {
        verifyFunctionArguments(request, 1, SakuliState.class);
        SakuliState sakuliState = (SakuliState) request.getArguments().get(0);
        return OutputState.lookupSakuliState(sakuliState);
    }

}
