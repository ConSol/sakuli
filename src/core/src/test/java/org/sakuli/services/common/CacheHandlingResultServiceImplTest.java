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

package org.sakuli.services.common;

import org.apache.commons.io.FileUtils;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.helper.TestCaseStepHelper;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.spy;
import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 8/25/15
 */
public class CacheHandlingResultServiceImplTest {
    private CacheHandlingResultServiceImpl testling = spy(new CacheHandlingResultServiceImpl());
    private TestSuite testSuite;

    @BeforeMethod
    public void init() {
        testSuite = new TestSuiteExampleBuilder()
                .withId("LOG_TEST_SUITE").withState(TestSuiteState.ERRORS).withException(new SakuliException("TEST")).buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
    }

    @Test
    public void testWriteCachedStepDefinitions() throws Exception {
        final String cacheFilePath = "valid/validTestCase/" + TestCaseStepHelper.SAKULI_STEPS_CACHE_FILE;
        if (this.getClass().getResource(cacheFilePath) != null) {
            Files.deleteIfExists(getResource(cacheFilePath, true));
        }

        Path tcFile = getResource("valid/validTestCase/_tc.js", true);
        testSuite.setState(TestSuiteState.ERRORS);
        TestCase tc = new TestCase("test", "test");
        tc.setTcFile(tcFile);
        tc.setSteps(Arrays.asList(
                new TestCaseStepBuilder("step_ok").withState(TestCaseStepState.OK).build(),
                new TestCaseStepBuilder("step_not_started").build(),
                new TestCaseStepBuilder("step_warning").withState(TestCaseStepState.WARNING).build()
        ));
        testSuite.setTestCases(Collections.singletonMap("1", tc));

        //on error no cache file should be written
        testling.saveAllResults();
        assertNull(getResource(cacheFilePath, false));

        //on != error cache file should be written
        testSuite.setState(TestSuiteState.CRITICAL_IN_SUITE);
        testling.saveAllResults();
        Path cacheFile = getResource(cacheFilePath, true);
        assertTrue(Files.exists(cacheFile));
        assertEquals(FileUtils.readFileToString(cacheFile.toFile(), Charset.forName("UTF-8")),
                "step_ok\nstep_warning\n");
    }

    protected Path getResource(String fileName, boolean exists) throws URISyntaxException {
        URL filePath = this.getClass().getResource(fileName);
        if (exists) {
            assertNotNull(filePath);
            return Paths.get(filePath.toURI());
        }
        assertNull(filePath);
        return null;
    }
}