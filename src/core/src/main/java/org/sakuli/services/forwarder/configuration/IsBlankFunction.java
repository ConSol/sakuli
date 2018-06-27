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

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Custom JtwigFunction for checking whether the provided string is blank.
 *
 * @author Georgi Todorov
 */
public class IsBlankFunction extends AbstractFunction {

    @Override
    public String name() {
        return "isBlank";
    }

    @Override
    protected int getExpectedNumberOfArguments() {
        return 1;
    }

    @Override
    protected List<Class> getExpectedArgumentTypes() {
        return Arrays.asList(String.class);
    }

    @Override
    protected Object execute(List<Object> arguments) {
        return StringUtils.isBlank((String) arguments.get(0));
    }

}
