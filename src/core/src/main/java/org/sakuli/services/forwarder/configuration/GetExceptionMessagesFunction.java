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
import org.jtwig.functions.SimpleJtwigFunction;
import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Map;

/**
 * Custom JtwigFunction for retrieving the excepton text for a provided test data entity.
 *
 * @author Georgi Todorov
 */
public class GetExceptionMessagesFunction extends SimpleJtwigFunction {

    private Map<String, String> formatExpressions;

    public GetExceptionMessagesFunction(Map<String, String> formatExpressions) {
        this.formatExpressions = formatExpressions;
    }

    @Override
    public String name() {
        return "getExceptionMessages";
    }

    @Override
    public Object execute(FunctionRequest request) {
        //TODO REVIEW: maybe is it better to throw a exception? The default assert message is maybe misleading
        assert request.getNumberOfArguments() == 2;
        AbstractTestDataEntity testDataEntity = (AbstractTestDataEntity) request.getArguments().get(0);
        boolean flatFormatted = (boolean) request.getArguments().get(1);
        return testDataEntity.getExceptionMessages(flatFormatted, formatExpressions);
    }

}
