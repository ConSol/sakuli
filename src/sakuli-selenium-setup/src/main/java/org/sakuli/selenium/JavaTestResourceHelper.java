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

package org.sakuli.selenium;

import org.apache.commons.lang3.StringUtils;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.utils.ResourceHelper;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public class JavaTestResourceHelper {
    public JavaTestResourceHelper() {
    }

    public static Path resolveImagePath(Class testClass, String imagePaths) {
        if (StringUtils.isNotBlank(imagePaths)) {
            try {
                return ResourceHelper.getClasspathResource(testClass, imagePaths,
                        String.format("Image path '%s' for class '%s' not found!", imagePaths, testClass.getName()))
                        .toAbsolutePath();
            } catch (NoSuchFileException e) {
                throw new SakuliRuntimeException(e);
            }
        }
        return null;
    }

    public static Optional<Path> getTestCaseFolder(Class realTestClass) {
        try {
            final Path classpathResource = ResourceHelper.getClasspathResource(realTestClass, ".", "no-message");
            return Optional.of(classpathResource);
        } catch (NoSuchFileException e) {
            return Optional.empty();
        }
    }

    public static Path getTestSuiteRootFolder() {
        Path resourcePath = resolveResource(JavaTestResourceHelper.class, "/");
        if (Files.exists(resourcePath)) {
            return resourcePath.normalize().toAbsolutePath();
        }
        throw new SakuliRuntimeException("Cannot load test suites root folder! Should be at normal test 'src/test/resources'");
    }

    public static Path resolveResource(Class<?> aClass, String resourceName) {
        try {
            return ResourceHelper.getClasspathResource(aClass, resourceName, "cannot resolve resource '" + resourceName + "' from classpath!");
        } catch (NoSuchFileException e) {
            throw new SakuliRuntimeException("cannot resolve resource '" + resourceName + "' from classpath!", e);
        }
    }
}