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
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.utils.TestSuitePropertiesTestUtils;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.InvalidParameterException;
import java.util.HashMap;

import static org.testng.Assert.*;

/**
 * @author tschneck Date: 17.07.13
 */
public class TestSuiteHelperTest extends TestSuitePropertiesTestUtils {

    @Test
    public void testInit() throws Exception {
        TestSuiteProperties testProps = TestSuitePropertiesTestUtils.getTestProps(this.getClass(), "valid", "suite_id_001");

        HashMap<String, TestCase> result = TestSuiteHelper.loadTestCases(testProps);
        assertEquals(1, result.size());
        //tests if onyl the valid testcase are in the suite, with there right names
        TestCase tc = result.values().iterator().next();
        assertEquals("http://localhost:8080", tc.getStartUrl());
        assertEquals(tc.getName(), "validTestCase");
        assertEquals(tc.getId(), "validTestCase");
        assertEquals(tc.getId(), result.keySet().iterator().next());
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "test case path \".*unValidTestCase.*\" doesn't exists - check your \"testsuite.suite\" file")
    public void testInitExceptionForTestCase() throws Exception {
        TestSuiteProperties testProps = TestSuitePropertiesTestUtils.getTestProps(this.getClass(), "unvalid", "unvalid_id");
        TestSuiteHelper.loadTestCases(testProps);
    }

    @Test
    public void testRelpaceEmptyLines() throws Exception {
        assertNull(TestSuiteHelper.replaceEmptyLines(null, "//"));

        String source = "line1\n\nbla\n";
        assertEquals(TestSuiteHelper.replaceEmptyLines(source, "//"),
                "line1\n//\nbla\n");

        source = "line1\n\n\nbla\n";
        assertEquals(TestSuiteHelper.replaceEmptyLines(source, "//"),
                "line1\n//\n//\nbla\n");
    }

    @Test
    public void testRelpaceEmptyLinesCR() throws Exception {
        String source = "line1\r\n\r\nbla\r\n";
        assertEquals(TestSuiteHelper.replaceEmptyLines(source, "//"),
                "line1\r\n//\r\nbla\r\n");

        source = "line1\r\n\r\n\r\nbla\r\n";
        assertEquals(TestSuiteHelper.replaceEmptyLines(source, "//"),
                "line1\r\n//\r\n//\r\nbla\r\n");
    }

    @Test
    public void testModifyFiles() throws Exception {
        Path path = Paths.get("temp-testsuite.suite");
        try {
            String source = "line1\r\n\r\nbla\r\n";
            FileUtils.writeStringToFile(path.toFile(), source);
            FileTime beforeTimeStamp = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            Thread.sleep(1100);

            String result = TestSuiteHelper.prepareTestSuiteFile(path);
            assertEquals(result, "line1\r\n//\r\nbla\r\n");
            FileTime afterTimeStamp = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            assertNotEquals(beforeTimeStamp, afterTimeStamp);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testNotModifyFiles() throws Exception {
        Path path = Paths.get("temp-testsuite.suite");
        try {
            String source = "line1\r\nbla\r\n";
            FileUtils.writeStringToFile(path.toFile(), source);
            FileTime beforeTimeStamp = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            Thread.sleep(1100);

            String result = TestSuiteHelper.prepareTestSuiteFile(path);
            assertEquals(source, result);
            FileTime afterTimeStamp = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            assertEquals(beforeTimeStamp, afterTimeStamp);
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void testRelpaceEmptyLinesWorstCase() throws Exception {
        String source = "line1\n\n\nbla\n";
        TestSuiteHelper.replaceEmptyLines(source, "//\n\n");
    }

    @Test(expectedExceptions = InvalidParameterException.class)
    public void testRelpaceEmptyLinesWorstCase2() throws Exception {
        String source = "line1\n\n\nbla\n";
        TestSuiteHelper.replaceEmptyLines(source, "");
    }
}
