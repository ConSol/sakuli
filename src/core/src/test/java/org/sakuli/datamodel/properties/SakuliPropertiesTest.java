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

package org.sakuli.datamodel.properties;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class SakuliPropertiesTest {

    @Test
    public void testGetLogExceptionFormat() throws Exception {
        SakuliProperties testling = new SakuliProperties();
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ });

        testling.setLogExceptionFormat("");
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ });

        testling.setLogExceptionFormat("[]");
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ });

        testling.setLogExceptionFormat("Some format expression");
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ "Some format expression" });

        testling.setLogExceptionFormat("[foo");
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ "[foo" });

        testling.setLogExceptionFormat("foo]");
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ "foo]" });

        testling.setLogExceptionFormat("[\"Some format expression\", \"Another format expression\"]");
        Assert.assertEquals(testling.getLogExceptionFormat(), new String[]{ "Some format expression", "Another format expression" });
    }
}