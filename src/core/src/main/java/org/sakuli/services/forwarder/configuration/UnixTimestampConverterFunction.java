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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Custom JtwigFunction for converting a date object into an unix timestamp.
 *
 * @author Georgi Todorov
 */
public class UnixTimestampConverterFunction extends AbstractFunction {

    @Override
    public String name() {
        return "convertToUnixTimestamp";
    }

    @Override
    public int getExpectedNumberOfArguments() {
        return 1;
    }

    @Override
    public List<Class> getExpectedArgumentTypes() {
        return Arrays.asList(Date.class);
    }

    @Override
    protected Object execute(List<Object> arguments) {
        Date date = (Date) arguments.get(0);

        if (date == null) {
            return "-1";
        } else {
            return String.format("%.3f", new BigDecimal(date.getTime()).divide(new BigDecimal(1000)));
        }
    }

}
