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

package org.sakuli.datamodel.helper;

import org.apache.commons.io.FileUtils;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.datamodel.state.TestCaseStepState;
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
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestCaseStepHelperTest {

    public static final String CACHEFILE_NAME = "stephelper/" + TestCaseStepHelper.SAKULI_STEPS_CACHE_FILE;

    @BeforeMethod
    public void setUp() throws Exception {
        if (this.getClass().getResource(CACHEFILE_NAME) != null) {
            Files.deleteIfExists(getResource(CACHEFILE_NAME));
        }
    }

    @Test
    public void testCheckWarningTime() throws Exception {
        assertNull(TestCaseStepHelper.checkWarningTime(0, "test"));
        assertNull(TestCaseStepHelper.checkWarningTime(1, "test"));
        String regex = "TestCaseStep \\[name = test\\] - the warning threshold.*";
        BaseTest.assertRegExMatch(TestCaseStepHelper.checkWarningTime(-1, "test"), regex);
    }

    @Test
    public void testParseSteps() throws Throwable {
        Path tcFile = getResource("stephelper/tc.js");
        FileUtils.writeStringToFile(
                tcFile.getParent().resolve(TestCaseStepHelper.SAKULI_STEPS_CACHE_FILE).toFile(),
                "z_step_1\nother_step_2\nstep_3?special\n",
                Charset.forName("UTF-8"));
        List<TestCaseStep> steps = TestCaseStepHelper.readCachedStepDefinitions(tcFile);
        //do this to ensure correct sorting
        List<TestCaseStep> result = Arrays.asList(steps.get(2), steps.get(0), steps.get(1));
        Collections.sort(result);
        assertNotNull(getResource(CACHEFILE_NAME));
        assertEquals(result.size(), 3);
        Iterator<TestCaseStep> it = result.iterator();
        assertInitStep(it.next(), "z_step_1");
        assertInitStep(it.next(), "other_step_2");
        assertInitStep(it.next(), "step_3?special");
    }

    protected void assertInitStep(TestCaseStep step, String stepId) {
        assertEquals(step.getId(), stepId);
        assertEquals(step.getState(), TestCaseStepState.INIT);
    }

    @Test
    public void testNoCacheFileFound() throws Throwable {
        assertNull(this.getClass().getResource(CACHEFILE_NAME));
        List<TestCaseStep> steps = TestCaseStepHelper.readCachedStepDefinitions(getResource("stephelper/tc.js"));
        assertEquals(steps.size(), 0);
    }

    @Test
    public void testWritCachedStepsError() throws Throwable {
        Path tcFile = getResource("stephelper/tc.js");
        TestSuite testSuite = mock(TestSuite.class);
        TestCase tc = mock(TestCase.class);
        when(tc.getTcFile()).thenReturn(tcFile);
        when(tc.getSteps()).thenReturn(Arrays.asList(
                new TestCaseStepBuilder("step_warning").withState(TestCaseStepState.WARNING).build(),
                new TestCaseStepBuilder("step_ok").withState(TestCaseStepState.OK).build(),
                new TestCaseStepBuilder("step_not_started_1").build(),
                new TestCaseStepBuilder("step_not_started_2").build()
        ));
        when(testSuite.getTestCases()).thenReturn(Collections.singletonMap("1", tc));

        TestCaseStepHelper.writeCachedStepDefinitions(testSuite);
        Path cacheFile = getResource(CACHEFILE_NAME);
        assertTrue(Files.exists(cacheFile));
        assertEquals(FileUtils.readFileToString(cacheFile.toFile(), Charset.forName("UTF-8")),
                "step_warning\nstep_ok\nstep_not_started_1\nstep_not_started_2\n");
    }

    protected Path getResource(String fileName) throws URISyntaxException {
        URL filePath = this.getClass().getResource(fileName);
        assertNotNull(filePath);
        return Paths.get(filePath.toURI());
    }
}