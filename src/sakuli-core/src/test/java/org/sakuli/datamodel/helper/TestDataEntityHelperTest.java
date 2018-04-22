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

package org.sakuli.datamodel.helper;

import org.sakuli.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestDataEntityHelperTest {

    @Test
    public void testCheckWarningAndCriticalTime() throws Exception {
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(1, 1, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(1, 0, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(0, 1, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(0, 0, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(3, 5, "TestCase"));

        BaseTest.assertRegExMatch(TestDataEntityHelper.checkWarningAndCriticalTime(-1, 0, "TestCase"),
                "TestCase - the warning threshold.*");
        BaseTest.assertRegExMatch(TestDataEntityHelper.checkWarningAndCriticalTime(0, -1, "TestCase"),
                "TestCase - the critical threshold.*");
        BaseTest.assertRegExMatch(TestDataEntityHelper.checkWarningAndCriticalTime(5, 3, "TestCase"),
                "warning threshold must be less than critical threshold!");
    }
}