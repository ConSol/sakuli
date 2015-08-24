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

import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.exceptions.SakuliProxyException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestCaseStepHelperTest {

    @DataProvider(name = "testFiles")
    public static Object[][] testFiles() {
        return new Object[][]{{"stephelper/tc_3_steps.js"}, {"stephelper/tc_3_steps_apostrophe.js"}};
    }

    @DataProvider(name = "stepString")
    public static Object[][] stepString() {
        return new Object[][]{
                {"testCase.endOfStep( \"step_1\", \"5\");", "step_1"},
                {"testCase.endOfStep(\"step_1\");", "step_1"},
                {"testCase.endOfStep(\"step_1\", \"5\");", "step_1"},
                {"testCase.endOfStep( \"step_1\" , '5');", "step_1"},
                {"testCase.endOfStep( \"step_1\" , 5);", "step_1"},
                {"testCase.endOfStep( 'step_1' , '5');", "step_1"},
                {"testCase.endOfStep( 'step adfadf /_1' , '5');", "step adfadf /_1"},
                {"testCase.endOfStep( 'step adfadf /_1' );", "step adfadf /_1"},
        };
    }

    @DataProvider(name = "testCaseVarString")
    public static Object[][] testCaseVarString() {
        return new Object[][]{
                {"testCase = new TestCase(80, 100);", "testCase"},
                {"tc = new TestCase(80, 100);", "tc"},
                {"var testCase = new TestCase(80, 100);", "testCase"},
                {"var tc = new TestCase(80, 100);", "tc"},
                {"var testCase = new TestCase();", "testCase"},
                {"var tc = new TestCase();", "tc"},
                {"var testCase=new TestCase(40, 50);", "testCase"},
                {"testCase=new TestCase(40, 50);", "testCase"},
        };
    }

    @Test
    public void testCheckWarningTime() throws Exception {
        assertNull(TestCaseStepHelper.checkWarningTime(0, "test"));
        assertNull(TestCaseStepHelper.checkWarningTime(1, "test"));
        String regex = "TestCaseStep \\[name = test\\] - the warning threshold.*";
        BaseTest.assertRegExMatch(TestCaseStepHelper.checkWarningTime(-1, "test"), regex);
    }

    @Test(dataProvider = "testFiles")
    public void testParseSteps(String fileName) throws Throwable {
        URL filePath = this.getClass().getResource(fileName);
        assertNotNull(filePath);

        List<TestCaseStep> steps = TestCaseStepHelper.parseSteps(Paths.get(filePath.toURI()));
        assertEquals(steps.size(), 3);
        Iterator<TestCaseStep> it = steps.iterator();
        assertEquals(it.next().getId(), "step_1");
        assertEquals(it.next().getId(), "step_2");
        assertEquals(it.next().getId(), "step_3?special");
    }

    @Test(dataProvider = "stepString")
    public void testRegexEndOfStep(String teststring, String result) throws Exception {
        Matcher matcher = Pattern.compile(TestCaseStepHelper.REGEX_PARSE_END_OF_STEP).matcher(teststring);
        assertTrue(matcher.matches());
        String step = matcher.replaceAll("${name}");
        assertEquals(step, result);
    }

    @Test(dataProvider = "testCaseVarString")
    public void testRegexTestCaseVar(String teststring, String result) throws Exception {
        Matcher matcher = Pattern.compile(TestCaseStepHelper.REGEX_PARSE_TESTCASE_VAR).matcher(teststring);
        assertTrue(matcher.matches());
        String step = matcher.replaceAll("${name}");
        assertEquals(step, result);
    }

    @Test(expectedExceptions = SakuliProxyException.class,
            expectedExceptionsMessageRegExp = "Can't parse testcase file 'not_valid_file.js' for steps!")
    public void testNoTcFileFound() throws Throwable {
        TestCaseStepHelper.parseSteps(Paths.get("not_valid_file.js"));
    }
}