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

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Custom JtwigFunction for abbreviating a specified string to a certain length.
 * The function is using the StringUtils.abbreviate method from the Apache commons library,
 * which is working in the following way:
 *
 * <p>
 * <ul>
 *   <li>If <code>str</code> is less than <code>maxWidth</code> characters long, return it.</li>
 *   <li>Else abbreviate it to <code>(substring(str, 0, max-3) + "...")</code>.</li>
 *   <li>If <code>maxWidth</code> is less than <code>4</code>, throw an
 *       <code>IllegalArgumentException</code>.</li>
 *   <li>In no case the function will return a String of length greater than <code>maxWidth</code>.</li>
 * </ul>
 * </p>
 *
 * <pre>
 * StringUtils.abbreviate(null, *)      = null
 * StringUtils.abbreviate("", 4)        = ""
 * StringUtils.abbreviate("abcdefg", 6) = "abc..."
 * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
 * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
 * StringUtils.abbreviate("abcdefg", 4) = "a..."
 * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
 * </pre>
 *
 * @author Georgi Todorov
 */
public class AbbreviateFunction extends AbstractFunction {

    @Override
    public String name() {
        return "abbreviate";
    }

    @Override
    protected int getExpectedNumberOfArguments() {
        return 3;
    }

    @Override
    protected List<Class> getExpectedArgumentTypes() {
        return Arrays.asList(String.class, BigDecimal.class, Boolean.class);
    }

    @Override
    protected Object execute(List<Object> arguments) {
        String toAbbreviate = (String) arguments.get(0);
        BigDecimal summaryMaxLength = (BigDecimal) arguments.get(1);
        boolean removeLeadingWhitespaces = Optional.ofNullable((Boolean) arguments.get(2)).orElse(false);
        toAbbreviate = removeLeadingWhitespaces && toAbbreviate != null ? toAbbreviate.replaceAll("(?m)^[\\s\\t]+|\\n", "") : toAbbreviate;
        return StringUtils.abbreviate(toAbbreviate, summaryMaxLength.intValue());
    }

}
