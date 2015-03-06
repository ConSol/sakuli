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

package org.sakuli.datamodel.helper;

import java.io.File;

/**
 * @author tschneck
 *         Date: 22.05.14
 */
public class TestCaseHelper {
    /**
     * transforms a URI folder path to a test case id.
     * It will be generated from the folder name of the test case. Blanks will be replaced with "_".
     *
     * @param uriPathToTestCaseFile path to the "_tc.js" file
     * @return a fitting test case id or NULL if something went wrong
     */
    public static String convertTestCaseFileToID(String uriPathToTestCaseFile) {
        final String foldername = convertFolderPathToName(uriPathToTestCaseFile);
        return foldername == null ? null : foldername.replace(" ", "_");
    }

    /**
     * transforms a URI folder path into a test case Name
     *
     * @param uriPathToTestCaseFile path to the "_tc.js" file
     * @return null if the Path is not valid.
     */
    public static String convertFolderPathToName(String uriPathToTestCaseFile) {
        uriPathToTestCaseFile = uriPathToTestCaseFile.replace(File.separator, "/");
        if (uriPathToTestCaseFile.lastIndexOf("/") != uriPathToTestCaseFile.indexOf("/")) {
            uriPathToTestCaseFile = uriPathToTestCaseFile.substring(0, uriPathToTestCaseFile.lastIndexOf('/'));
            return uriPathToTestCaseFile.substring(uriPathToTestCaseFile.lastIndexOf("/") + 1);
        } else if (uriPathToTestCaseFile.lastIndexOf("/") == uriPathToTestCaseFile.indexOf("/")) {
            return uriPathToTestCaseFile.substring(0, uriPathToTestCaseFile.lastIndexOf("/"));
        } else {
            return null;
        }
    }
}
