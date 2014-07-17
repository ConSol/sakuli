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

package de.consol.sakuli.datamodel.helper;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import net.sf.sahi.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author tschneck
 *         Date: 22.05.14
 */
public class TestSuiteHelper {
    private static Logger logger = LoggerFactory.getLogger(TestSuiteHelper.class);

    /**
     * read out the 'testsuite.suite' file and create the corresponding test cases objects.
     *
     * @param properties loaded {@link TestSuiteProperties} of a {@link TestSuite}.
     * @return a map of {@link TestCase}s with her {@link TestCase#id} as key.
     * @throws FileNotFoundException if files are not reachable
     */
    public static HashMap<String, TestCase> loadTestCases(TestSuiteProperties properties) throws FileNotFoundException {
        Path testSuiteFile = properties.getTestSuiteSuiteFile();
        Path testSuiteFolder = properties.getTestSuiteFolder();

        HashMap<String, TestCase> tcMap = new HashMap<>();

        if (!Files.exists(testSuiteFile)) {
            throw new FileNotFoundException("Can not find specified " + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + " file at \"" + testSuiteFolder.toString() + "\"");
        }
        String testSuiteString = Utils.readFileAsString(testSuiteFile.toFile());
        logger.info(
                "\n--- TestSuite initialization: read test suite information of file \"" + testSuiteFile.toAbsolutePath().toString() + "\" ----\n"
                        + testSuiteString
                        + "\n --- End of File \"" + testSuiteFile.toAbsolutePath().toString() + "\" ---"
        );

        //handle each line of the .suite file
        String regExLineSep = System.getProperty("line.separator") + "|\n";
        for (String line : testSuiteString.split(regExLineSep)) {
            if (!line.startsWith("//") && !(line.isEmpty())) {
                //get the start URL from suite
                String startURL = line.substring(line.lastIndexOf(' ') + 1);

                //extract tc file name name and generate new test case
                String tcFileName = line.substring(0, line.lastIndexOf(' '));  // get tc file name
                Path tcFile = Paths.get(testSuiteFolder.toAbsolutePath().toString() + File.separator + tcFileName.replace("/", File.separator));
                if (Files.exists(tcFile)) {
                    TestCase tc = new TestCase(
                            TestCaseHelper.convertFolderPathToName(tcFileName),
                            TestCaseHelper.convertTestCaseFileToID(tcFileName));

                    tc.setStartUrl(startURL);
                    tc.setTcFile(tcFile);
                    //set the Map with the test case id as key
                    tcMap.put(tc.getId(), tc);
                } else {
                    throw new FileNotFoundException("test case path \"" + tcFile.toAbsolutePath().toString() + "\" doesn't exists - check your \"" + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + "\" file");
                }
            }
        }
        return tcMap;
    }
}
