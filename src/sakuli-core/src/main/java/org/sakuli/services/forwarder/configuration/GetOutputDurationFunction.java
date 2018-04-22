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

import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Custom JtwigFunction for retrieving the duration for a provided test entity.
 * If the test entity is in error state, an 'U' will be returned since the duration is unknown.
 * Otherwise the formatted duration will be returned.
 *
 * @author Georgi Todorov
 */
public class GetOutputDurationFunction extends AbstractFunction {

    private static final String UNKNOWN_DURATION = "U";

    @Override
    public String name() {
        return "getOutputDuration";
    }

    @Override
    protected int getExpectedNumberOfArguments() {
        return 1;
    }

    @Override
    protected List<Class> getExpectedArgumentTypes() {
        return Arrays.asList(AbstractTestDataEntity.class);
    }

    @Override
    protected Object execute(List<Object> arguments) {
        AbstractTestDataEntity testDataEntity = (AbstractTestDataEntity) arguments.get(0);
        if (testDataEntity == null || testDataEntity.getState().isError() || testDataEntity.getDuration() < 0) {
            return UNKNOWN_DURATION;
        }
        return String.format(Locale.ENGLISH, "%.2fs", testDataEntity.getDuration());
    }

}
