/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2015 the original author or authors.
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

package org.sakuli.selenium.testng.example;

import org.sakuli.actions.screenbased.Region;
import org.sakuli.selenium.testng.SakuliSeTest;
import org.sakuli.selenium.testng.SakuliTestCase;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TODO: make cleaner
 * 
 * @author Tobias Schneck
 */
//@Listeners(SakuliSeTest.class)
//@Test(suiteName = "mysakulisuiteXXXX", enabled = false)
public class Example2Test {

    @Test(enabled = false)
    @SakuliTestCase(testCaseName = "test-example", additionalImagePaths = "test_pics", warningTime = 2)
    public void test1() throws Exception {
        new Region().find("first");
        new Region().find("625");
        //TODO testcasestep
        Assert.assertTrue(false);
    }
}
