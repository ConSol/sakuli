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
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.sakuli.exceptions.SakuliProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestCaseStepHelper {

    public static final String NAME_REPLACE_EXPRESSION = "${name}";

    /**
     * until the braclet of endOfStep: {@code .*\.endOfStep\(}  <br/>
     * capture all characters as long as no " or ' exists: {@code (?<name>[^",^']*)}
     */
    static final String REGEX_PARSE_END_OF_STEP = ".*\\.endOfStep\\(\\s*[\",'](?<name>[^\",^']*).*";

    /**
     * start with 'var ' or without: {@code [var]*\s* } <br/>
     * get the name until next = with or without space:{@code (?<name>\w*)\s?= }
     */
    static final String REGEX_PARSE_TESTCASE_VAR = "[var]*\\s*(?<name>\\w*)\\s?=\\s?new TestCase\\(.*";

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseStepHelper.class);

    public static List<TestCaseStep> parseSteps(Path tcFile) throws SakuliProxyException {
        try {
            List<TestCaseStep> result = new ArrayList<>();
            List<String> lines = FileUtils.readLines(tcFile.toFile(), Charset.forName("UTF-8"));
            Pattern patternEndOfStep = Pattern.compile(TestCaseStepHelper.REGEX_PARSE_END_OF_STEP);
            Pattern patternTestCaseVar = Pattern.compile(TestCaseStepHelper.REGEX_PARSE_TESTCASE_VAR);

            //TODO TS do some refactoring
            String testCasVarName = null;
            boolean skipLines = false;
            for (String line : lines) {
                if (line.contains("/*")) {
                    skipLines = true;
                }
                if (line.contains("*/")) {
                    skipLines = false;
                }

                if (!skipLines) {
                    Matcher matcherTestCaseVar = patternTestCaseVar.matcher(line);
                    if (matcherTestCaseVar.matches()) {
                        testCasVarName = matcherTestCaseVar.replaceAll(NAME_REPLACE_EXPRESSION);
                    }

                    Matcher matcher = patternEndOfStep.matcher(line);
                    if (testCasVarName != null  //testcase variable defined
                            && line.trim().startsWith(testCasVarName) //starts with testcase var name (also filter out // comments)
                            && matcher.matches() //matches to the 'endOfStep' regex
                            ) {
                        // get named captured value of the regex expression
                        String stepName = matcher.replaceAll(NAME_REPLACE_EXPRESSION);
                        TestCaseStep step = new TestCaseStepBuilder(stepName).build();
                        result.add(step);
                        LOGGER.debug("add parsed step: " + step);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            throw new SakuliProxyException(e, "Can't parse testcase file '%s' for steps!");
        }
    }

    public static String checkWarningTime(int warningTime, String stepName) {
        return TestDataEntityHelper.checkWarningAndCriticalTime(warningTime, 0,
                String.format("TestCaseStep [name = %s]", stepName));
    }
}
