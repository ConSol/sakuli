/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.actions.testcase;

import org.sakuli.datamodel.actions.ImageLib;
import org.sakuli.exceptions.SakuliCheckedException;

import java.nio.file.Path;

/**
 * @author tschneck
 * Date: 4/25/17
 */
public interface TestCaseAction {
    /**
     * Set the warning and critical Time to the specific test case.
     *
     * @param testCaseID   current ID of the test case
     * @param warningTime  warning threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param criticalTime critical threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param imagePaths   multiple paths to images
     */
    void init(String testCaseID, int warningTime, int criticalTime, String... imagePaths);

    void initWithCaseID(String testCaseID, String newTestCaseID, int warningTime, int criticalTime, String... imagePaths);

    /**
     * Set the warning and critical Time to the specific test case.
     *
     * @param testCaseID   current ID of the test case
     * @param warningTime  warning threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param criticalTime critical threshold in seconds. If the threshold is set to 0,
     *                     the execution time will never exceed, so the state will be always OK!
     * @param imagePaths   multiple paths to images
     */
    void initWithPaths(String testCaseID, int warningTime, int criticalTime, Path... imagePaths);

    /**
     * Adds the additional paths to the current {@link ImageLib} object.
     * If a relative path is assigned, the current testcase folder will be used as current directory.
     *
     * @param imagePaths one or more paths as {@link String} elements
     * @throws SakuliCheckedException if an IO error occurs
     */
    void addImagePathsAsString(String... imagePaths) throws SakuliCheckedException;

    /**
     * Adds the additional paths to the current {@link ImageLib} object.
     *
     * @param imagePaths one or more {@link Path} elements
     * @throws SakuliCheckedException if an IO error occurs
     */
    void addImagePaths(Path... imagePaths) throws SakuliCheckedException;


    /**
     * calls the method {@link org.sakuli.exceptions.SakuliExceptionHandler#handleException(Exception)}
     *
     * @param e the original exception
     */
    void handleException(Exception e);

    /**
     * @param exceptionMessage String message
     */
    void handleException(String exceptionMessage);

    @Override
    String toString();

    /**
     * @return the folder path of the current testcase as {@link String}.
     */
    String getTestCaseFolderPath();

    /**
     * @return the folder path of the current testsuite as {@link String}.
     */
    String getTestSuiteFolderPath();
}
