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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 23/02/18.
 */
public class AbbreviateFunctionTest {

    @DataProvider
    public Object[][] abbreviateDP() {
        return new Object[][] {
                { null, 1, true, null},
                { null, 1, false, null},
                { "  ", 4, false, "  "},
                { "  ", 4, true, ""},
                { "abcdefg", 6, false, "abc..."},
                { "abcdefg", 7, false, "abcdefg"},
                { "abcdefg", 8, false, "abcdefg"},
                { "abcdefg", 4, false, "a..."},
                { "   abc ", 4, true, "abc "},
                { "       abc ", 4, true, "abc "},
                { "\t\t\t\n\nabc ", 4, true, "abc "},
                { "   abc  ", 4, true, "a..."},
                {"   abc  ", 7, false, "   a..."},
                {"   abc  ", 7, null, "   a..."},
        };
    }

    @Test(dataProvider = "abbreviateDP")
    public void abbreviate(String input, int maxLength, Boolean removeWhitespaces, String expectedOutput) {
        AbbreviateFunction abbreviateFunction = new AbbreviateFunction();
        assertEquals(abbreviateFunction.execute(Arrays.asList(input, new BigDecimal(maxLength), removeWhitespaces)), expectedOutput);
    }

}