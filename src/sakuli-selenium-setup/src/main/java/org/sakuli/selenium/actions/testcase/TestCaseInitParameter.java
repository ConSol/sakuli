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

package org.sakuli.selenium.actions.testcase;

import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliRuntimeException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Tobias Schneck
 */
public class TestCaseInitParameter {

    private String testCaseId;
    private Path testCaseFolder;
    private int warningTime;
    private int criticalTime;
    private List<Path> imagePaths;
    private Date startDate;

    /**
     * Configures the init parameters with testCaseId and the testCaseFolderName. Warning and critical time will be 0
     * per default.
     *
     * @param testCaseId identifier of a {@link TestCase}
     */
    public TestCaseInitParameter(String testCaseId) {
        this.testCaseId = testCaseId;
        warningTime = 0;
        criticalTime = 0;
        startDate = new Date();
        imagePaths = new ArrayList<>();
    }

    public static Path checkFolder(Path path) {
        if (Files.exists(path)) {
            return path;
        }
        throw new SakuliRuntimeException(String.format("The required folder '%s' does not exist!", path.toAbsolutePath().toString()));
    }

    public TestCaseInitParameter withWarningTime(int warningTime) {
        this.warningTime = warningTime;
        return this;
    }

    public TestCaseInitParameter withCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
        return this;
    }

    public TestCaseInitParameter withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public TestCaseInitParameter withId(String id) {
        this.testCaseId = id;
        return this;
    }

    public TestCaseInitParameter withTestCaseFolder(Path testCaseFolder) {
        this.testCaseFolder = testCaseFolder;
        addImagePath(testCaseFolder);
        return this;
    }

    public TestCaseInitParameter addImagePath(Path... imagePaths) {
        if (imagePaths != null) {
            Arrays.stream(imagePaths)
                    .filter(Objects::nonNull)
                    .forEach(p -> this.imagePaths.add(checkFolder(p)));
        }
        return this;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public Path getTestCaseFolder() {
        return testCaseFolder;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public List<Path> getImagePaths() {
        return imagePaths;
    }

    @Override
    public String toString() {
        return "TestCaseInitParameter{" +
                "testCaseId='" + testCaseId + '\'' +
                ", warningTime=" + warningTime +
                ", criticalTime=" + criticalTime +
                ", startDate=" + startDate +
                ", testCaseFolder" + testCaseFolder +
                ", imagePaths=" + imagePaths +
                '}';
    }
}
