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

import org.sakuli.actions.logging.LogToResult;
import org.sakuli.datamodel.actions.ImageLib;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;

import java.nio.file.Path;

/**
 * @author tschneck
 *         Date: 4/25/17
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
    @LogToResult(message = "init a new test case")
    void init(String testCaseID, int warningTime, int criticalTime, String... imagePaths);

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
    @LogToResult(message = "init a new test case")
    void initWithPaths(String testCaseID, int warningTime, int criticalTime, Path... imagePaths);

    /**
     * Adds the additional paths to the current {@link ImageLib} object.
     * If a relative path is assigned, the current testcase folder will be used as current directory.
     *
     * @param imagePaths one or more paths as {@link String} elements
     * @throws SakuliException if an IO error occurs
     */
    void addImagePathsAsString(String... imagePaths) throws SakuliException;

    /**
     * Adds the additional paths to the current {@link ImageLib} object.
     *
     * @param imagePaths one or more {@link Path} elements
     * @throws SakuliException if an IO error occurs
     */
    @LogToResult
    void addImagePaths(Path... imagePaths) throws SakuliException;


    /**
     * calls the method {@link SakuliExceptionHandler#handleException(Throwable)}
     *
     * @param e the original exception
     */
    void handleException(Throwable e);

    /**
     * @param exceptionMessage String message
     */
    void handleException(String exceptionMessage);

    @Override
    String toString();

    /**
     * @return the folder path of the current testcase as {@link String}.
     */
    @LogToResult
    String getTestCaseFolderPath();

    /**
     * @return the folder path of the current testsuite as {@link String}.
     */
    @LogToResult
    String getTestSuiteFolderPath();
}
