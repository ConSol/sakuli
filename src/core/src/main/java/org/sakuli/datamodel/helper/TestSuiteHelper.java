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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tschneck Date: 22.05.14
 */
public class TestSuiteHelper {

    public static class TestCaseCollection {
        private Map<String, TestCase> enabledTests;
        private List<String> skippedTests;

        public TestCaseCollection() {
            enabledTests = new HashMap<>();
            skippedTests = new ArrayList<>();
        }

        public void addEnabledTest(String id, TestCase testCase) {
            enabledTests.put(id, testCase);
        }

        public Map<String, TestCase> getEnabledTests() {
            return enabledTests;
        }

        public void setEnabledTests(final Map<String, TestCase> enabledTests) {
            this.enabledTests = enabledTests;
        }

        public void addSkippedTest(String id) {
            skippedTests.add(id);
        }

        public List<String> getSkippedTests() {
            return skippedTests;
        }

        public void setSkippedTests(final List<String> skippedTests) {
            this.skippedTests = skippedTests;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(TestSuiteHelper.class);

    /**
     * read out the 'testsuite.suite' file and create the corresponding test cases objects.
     *
     * @param properties loaded {@link TestSuiteProperties} of a {@link TestSuite}.
     * @return a map of {@link TestCase}s with her {@link TestCase#id} as key.
     * @throws FileNotFoundException if files are not reachable
     */
    public static TestCaseCollection loadTestCases(TestSuiteProperties properties) throws IOException {
        Path testSuiteFile = properties.getTestSuiteSuiteFile();
        Path testSuiteFolder = properties.getTestSuiteFolder();

        TestCaseCollection testCaseCollection = new TestCaseCollection();

        if (!Files.exists(testSuiteFile)) {
            throw new FileNotFoundException("Can not find specified " + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + " file at \"" + testSuiteFolder.toString() + "\"");
        }

        String testSuiteString = prepareTestSuiteFile(testSuiteFile);
        logger.info(
                "\n--- TestSuite initialization: read test suite information of file \"" + testSuiteFile.toAbsolutePath().toString() + "\" ----\n"
                        + testSuiteString
                        + "\n --- End of File \"" + testSuiteFile.toAbsolutePath().toString() + "\" ---"
        );

        List<String> testCaseFilters = Optional
                .ofNullable(properties.getTestCaseFilters())
                .orElse(new ArrayList<>());

        if (testCaseFilters.isEmpty()) {
            logger.info("No test case filter applied, skipping.");
        } else {
            logger.info("Applying filter: {}", String.join(",", testCaseFilters));
        }

        //handle each line of the .suite file
        String regExLineSep = System.getProperty("line.separator") + "|\n";
        for (String line : testSuiteString.split(regExLineSep)) {
            if (!line.startsWith("//") && !(line.isEmpty())) {
                //get the start URL from suite
                String startURL = line.substring(line.lastIndexOf(' ') + 1);

                //extract tc file name name and generate new test case
                String tcFileName = line.substring(0, line.lastIndexOf(' '));  // get tc file name
                Path tcFile = Paths.get(testSuiteFolder.toAbsolutePath().toString() + File.separator + tcFileName.replace("/", File.separator));

                // Only add test cases which were specified via CLI
                if (!testCaseFilters.isEmpty() && !testCaseFilters.contains(TestCaseHelper.convertFolderPathToName(tcFileName))) {
                    logger.info("Skipping filtered testcase {}", tcFileName);
                    testCaseCollection.addSkippedTest(TestCaseHelper.convertTestCaseFileToID(tcFileName));
                    continue;
                }
                if (Files.exists(tcFile)) {
                    TestCase tc = new TestCase(
                            TestCaseHelper.convertFolderPathToName(tcFileName),
                            TestCaseHelper.convertTestCaseFileToID(tcFileName));
                    tc.setStartUrl(startURL);
                    tc.setTcFile(tcFile);
                    tc.setSteps(TestCaseStepHelper.readCachedStepDefinitions(tcFile));
                    //set the Map with the test case id as key
                    testCaseCollection.addEnabledTest(tc.getId(), tc);
                } else {
                    throw new FileNotFoundException("test case path \"" + tcFile.toAbsolutePath().toString() + "\" doesn't exists - check your \"" + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + "\" file");
                }
            }
        }

        return testCaseCollection;
    }

    /**
     * Modifies the testsuite.suite file so that no empty lines are included.
     */
    static String prepareTestSuiteFile(Path testSuiteFile) throws IOException {
        String suiteFile = FileUtils.readFileToString(testSuiteFile.toFile());
        String updatedFile = replaceEmptyLines(suiteFile, "//");
        if (suiteFile.equals(updatedFile)) {
            return suiteFile;
        } else {
            FileUtils.writeStringToFile(testSuiteFile.toFile(), updatedFile);
            return updatedFile;
        }
    }

    /**
     * Creates a temporary testsuite file which only contains filtered test cases
     */
    public static Path generateFilteredTestSuiteFile(TestSuite suite) throws IOException {
        File tmp = File.createTempFile(suite.getId(), ".suite");
        tmp.deleteOnExit();
        ArrayList<String> testCaseLines = new ArrayList<>();
        suite.getTestCases().forEach((k, v) -> testCaseLines.add(v.getTcFile().toString() + " " + v.getStartUrl()));

        Files.write(tmp.getAbsoluteFile().toPath(), testCaseLines, Charset.forName("UTF-8"));
        return tmp.getAbsoluteFile().toPath();
    }

    /**
     * Replace an string like:
     * <ul>
     * <li>replaceEmptyLines("bla\n\nbla", "123")   =   "bla\n123\nbla"</li>
     * <li>replaceEmptyLines("bla\r\n\r\nbla", "123")   =   "bla\r\n123\r\nbla"</li>
     * </ul>
     *
     * @param source        source String
     * @param replaceString replace String without double new line
     * @return modify String without empty lines
     */
    protected static String replaceEmptyLines(String source, String replaceString) {
        if (source != null) {
            for (String newlineChars : Arrays.asList("\n", "\r\n")) {
                String doubleNewLine = newlineChars + newlineChars;
                if (replaceString.contains(doubleNewLine) || StringUtils.isEmpty(replaceString)) {
                    throw new InvalidParameterException("The assigned replace pattern contains the not allowed chars '" + doubleNewLine + "'!");
                }
                while (source.contains(doubleNewLine)) {
                    source = source.replace(doubleNewLine, newlineChars + replaceString + newlineChars);
                }
            }
        }
        return source;
    }
}
