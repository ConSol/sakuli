/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.starter.helper;

import de.consol.sakuli.datamodel.TestSuite;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by thomasr on 25.04.14.
 */
public class SahiProxyTest {

    @Mock
    private TestSuite testSuiteMock;
    @InjectMocks
    private SahiProxy testling;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testStartProxy() throws Exception {
        //TODO impl
//        when(Configuration.setUnmodifiedTrafficLogging(true)) {
        //TODO proxy infos auslesen
//        }
    }

    @Test
    public void testStartProxy1() throws Exception {
        //TODO impl
    }

    @Test
    public void testShutdown() throws Exception {
        //TODO impl
//        when(testling.stop())
    }
}
