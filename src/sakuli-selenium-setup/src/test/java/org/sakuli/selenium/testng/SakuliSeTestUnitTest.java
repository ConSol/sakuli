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

package org.sakuli.selenium.testng;


import org.sakuli.loader.BeanLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.nio.file.Paths;


/**
 * @author Tobias Schneck
 */
//@Listeners(SakuliSeTest.class)
public class SakuliSeTestUnitTest {

    @BeforeSuite
    public void setUp() throws Exception {
//        SakuliSePropertyPlaceholderConfigurer.SE_CONTEXT_PATH = AbstractBaseTest.TEST_CONTEXT_PATH;
    }

    //TODO TS make screen unindepedent
    @Test(enabled = false)
//    @SakuliTestCase
    public void testFolder() throws Exception {
        Assert.assertEquals(BeanLoader.loadTestCaseAction().getTestCaseFolderPath(),
                Paths.get(getClass().getResource(".").toURI()).normalize().toAbsolutePath().toString());
        Assert.assertEquals(BeanLoader.loadTestCaseAction().getTestSuiteFolderPath(),
                Paths.get(getClass().getResource("/").toURI()).normalize().toAbsolutePath().toString());
    }
}
