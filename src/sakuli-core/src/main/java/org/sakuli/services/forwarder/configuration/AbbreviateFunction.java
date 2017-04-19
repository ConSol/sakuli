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
import org.jtwig.functions.FunctionRequest;

import java.math.BigDecimal;

/**
 * Custom JtwigFunction for abbreviating a provided string to a certain length.
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
    public Object execute(FunctionRequest request) {
        verifyFunctionArguments(request, 3, String.class, BigDecimal.class, Boolean.class);
        String toAbbreviate = (String) request.getArguments().get(0);
        BigDecimal summaryMaxLength = (BigDecimal) request.getArguments().get(1);
        boolean removeWhitespaces = (boolean) request.getArguments().get(2);
        toAbbreviate = removeWhitespaces ? toAbbreviate.replaceAll("(?m)^[\\s\\t]+|\\n", "") : toAbbreviate;
        return StringUtils.abbreviate(toAbbreviate, summaryMaxLength.intValue());
    }

}
