/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.consol.sakuli.javaDSL;

import de.consol.sakuli.exceptions.SakuliException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tobias Schneck
 */
public class TestCaseInitParameter {

    private String testCaseId;
    private Path testCaseFolderPath;
    private int warningTime;
    private int criticalTime;
    private Map<String, Path> imagePaths;

    public TestCaseInitParameter(String testCaseId, String testCaseFolder) throws SakuliException {
        this.testCaseId = testCaseId;
        warningTime = 0;
        criticalTime = 0;
        imagePaths = new HashMap<>();
        testCaseFolderPath = checkTestCaseFolder(testCaseFolder);
        imagePaths.put(testCaseFolderPath.toString(), testCaseFolderPath);
    }

    protected static Path checkTestCaseFolder(String path) throws SakuliException {
        Path folder = Paths.get(path);
        if (Files.exists(folder)) {
            return folder;
        }
        throw new SakuliException(String.format("Test case folder '%s' does not exist!", path));
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
                Path imageFolder = checkTestCaseFolder(imagePath);
                this.imagePaths.put(imageFolder.toString(), imageFolder);
            }
        }
        return this;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getTestCaseFolder() {
        return testCaseFolderPath.toString();
    }

    public Path getTestCaseFolderPath() {
        return testCaseFolderPath;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    public Map<String, Path> getImagePaths() {
        return imagePaths;
    }
}
