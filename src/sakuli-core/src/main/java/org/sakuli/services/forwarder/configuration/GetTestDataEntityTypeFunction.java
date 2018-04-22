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

import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Custom JtwigFunction for retrieving the type of a provided AbstractTestDataEntity.
 *
 * @author Georgi Todorov
 */
public class GetTestDataEntityTypeFunction extends AbstractFunction {

    @Override
    public String name() {
        return "getTestDataEntityType";
    }

    @Override
    protected int getExpectedNumberOfArguments() {
        return 1;
    }

    @Override
    protected List<Class> getExpectedArgumentTypes() {
        return Collections.singletonList(AbstractTestDataEntity.class);
    }

    @Override
    protected Object execute(List<Object> arguments) {
       return Optional.ofNullable((AbstractTestDataEntity) arguments.get(0))
                .map(o -> o.getClass().getSimpleName())
                .orElse(null);
    }

}
