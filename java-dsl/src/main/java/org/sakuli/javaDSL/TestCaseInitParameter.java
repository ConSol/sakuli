/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.javaDSL;

import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliException;

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

    public static Path checkFolder(String path) throws SakuliException {
        Path folder = Paths.get(path);
        if (Files.exists(folder)) {
            return folder;
        }
        throw new SakuliException(String.format("The required folder '%s' does not exist!", folder.toAbsolutePath().toString()));
    }

    public TestCaseInitParameter withWarningTime(int warningTime) {
        this.warningTime = warningTime;
        return this;
    }

    public TestCaseInitParameter withCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
        return this;
    }

    public TestCaseInitParameter addImagePath(String... imagePaths) throws SakuliException {
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
