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

import org.apache.commons.lang.StringUtils;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;
import org.sakuli.datamodel.TestSuite;

/**
 * Custom JtwigFunction for retrieving the service description for a provided test suite.
 * The service description can be configured within the testsuite.properties. If the description
 * within the properties file is not set, then the id of the test suite is used as description.
 *
 * @author Georgi Todorov
 */
//TODO REVIEW: function is not needed, because the spring based property replace mechanism, should already autowire the correct service description or the testsuite id

public class GetServiceDescriptionFunction extends SimpleJtwigFunction {

    private String configuredServiceDescription;

    public GetServiceDescriptionFunction(String configuredServiceDescription) {
        this.configuredServiceDescription = configuredServiceDescription;
    }

    @Override
    public String name() {
        return "getServiceDescription";
    }

    @Override
    public Object execute(FunctionRequest request) {
        assert request.getNumberOfArguments() == 1;
        TestSuite testSuite = (TestSuite) request.getArguments().get(0);
        return StringUtils.isNotEmpty(configuredServiceDescription)
                ? configuredServiceDescription
                : testSuite.getId();
    }

}
