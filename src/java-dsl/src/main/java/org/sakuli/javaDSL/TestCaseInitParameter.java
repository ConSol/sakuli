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

package org.sakuli.javaDSL;

import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliCheckedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Schneck
 */
public class TestCaseInitParameter {

    private String testCaseId;
    private String testCaseFolderName;
    private int warningTime;
    private int criticalTime;
    private List<Path> imagePaths;

    /**
     * Configures the init parameters so that the testCaseId is equal to the testCaseFolderName. Warning and critical
     * time will be 0 per default.
     *
     * @param testCaseId identifier of a {@link TestCase}
     */
    public TestCaseInitParameter(String testCaseId) {
        this(testCaseId, testCaseId);
    }

    /**
     * Configures the init parameters with testCaseId and the testCaseFolderName. Warning and critical time will be 0
     * per default.
     *
     * @param testCaseId         identifier of a {@link TestCase}
     * @param testCaseFolderName name of the test case folder, which contains the set of images for the test case execution
     */
    public TestCaseInitParameter(String testCaseId, String testCaseFolderName) {
        this.testCaseId = testCaseId;
        this.testCaseFolderName = testCaseFolderName;
        warningTime = 0;
        criticalTime = 0;
        imagePaths = new ArrayList<>();
    }

    public static Path checkFolder(String path) throws SakuliCheckedException {
        Path folder = Paths.get(path);
        if (Files.exists(folder)) {
            return folder;
        }
        throw new SakuliCheckedException(String.format("The required folder '%s' does not exist!", folder.toAbsolutePath().toString()));
    }

    public TestCaseInitParameter withWarningTime(int warningTime) {
        this.warningTime = warningTime;
        return this;
    }

    public TestCaseInitParameter withCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
        return this;
    }

    public TestCaseInitParameter addImagePath(String... imagePaths) throws SakuliCheckedException {
        if (imagePaths != null) {
            for (String imagePath : imagePaths) {
                this.imagePaths.add(checkFolder(imagePath));
            }
        }
        return this;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getTestCaseFolderName() {
        return testCaseFolderName;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    public List<Path> getImagePaths() {
        return imagePaths;
    }

    @Override
    public String toString() {
        return "TestCaseInitParameter{" +
                "testCaseId='" + testCaseId + '\'' +
                ", testCaseFolderName='" + testCaseFolderName + '\'' +
                ", warningTime=" + warningTime +
                ", criticalTime=" + criticalTime +
                ", imagePaths=" + imagePaths +
                '}';
    }
}
