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
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestCaseStepHelper {

    public static final String SAKULI_STEPS_CACHE_FILE = ".sakuli-steps-cache";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseStepHelper.class);

    public static List<TestCaseStep> readCachedStepDefinitions(Path tcFile) throws IOException {
        List<TestCaseStep> result = new ArrayList<>();
        if (tcFile != null) {
            Path stepsCacheFile = tcFile.getParent().resolve(SAKULI_STEPS_CACHE_FILE);
            if (Files.exists(stepsCacheFile)) {
                try {
                    List<String> lines = FileUtils.readLines(stepsCacheFile.toFile(), Charset.forName("UTF-8"));
                    DateTime creationDate = new DateTime();
                    for (String line : lines) {
                        if (StringUtils.isNotEmpty(line)) {
                            TestCaseStep step = new TestCaseStepBuilder(line).withCreationDate(creationDate).build();
                            result.add(step);
                            LOGGER.debug("add cached step definition: " + step);
                            //increase creation date to ensure correct sorting
                            creationDate = creationDate.plusMillis(1);
                        }
                    }
                } catch (IOException e) {
                    throw new IOException(String.format("Can't read in cached step definitions in file '%s'", stepsCacheFile), e);
                }
            }
        }
        return result;
    }

    public static void writeCachedStepDefinitions(TestSuite testSuite) throws IOException {
        if (testSuite.getTestCases() != null) {
            for (TestCase tc : testSuite.getTestCases().values()) {
                if (tc.getTcFile() != null) {
                    Path stepsCacheFile = tc.getTcFile().getParent().resolve(SAKULI_STEPS_CACHE_FILE);
                    try {
                        String stepNames = getStepName(tc.getSteps());
                        LOGGER.debug("write following step IDs to file '{}':\n{}", stepsCacheFile, stepNames);
                        FileUtils.writeStringToFile(stepsCacheFile.toFile(), stepNames, Charset.forName("UTF-8"), false);
                    } catch (IOException e) {
                        throw new IOException(String.format("error during writing into '%s' file ", stepsCacheFile), e);
                    }
                }
            }
        }
    }

    private static String getStepName(List<TestCaseStep> steps) {
        StringBuilder sb = new StringBuilder();
        for (TestCaseStep step : steps) {
            sb.append(step.getId()).append('\n');
        }
        return sb.toString();
    }

    public static String checkWarningTime(int warningTime, String stepName) {
        return TestDataEntityHelper.checkWarningAndCriticalTime(warningTime, 0,
                String.format("TestCaseStep [name = %s]", stepName));
    }
}
