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

package org.sakuli.utils;

import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck
 *         Date: 06.03.15
 */
public class ResourceHelper {

    /**
     * Resolves a resource from the Classpath.
     *
     * @param classDef          a instance of {@link Class}
     * @param classPathResource resource string like e.g. "/filename.file".
     * @param exceptionMessage  custom exception message, if file couldn't resolved.
     * @return a {@link Path} object of the classpath resource.
     * @throws NoSuchFileException
     */
    public static Path getClasspathResource(Class<?> classDef, String classPathResource, String exceptionMessage) throws NoSuchFileException {
        try {
            return Paths.get(classDef.getResource(classPathResource).toURI());
        } catch (FileSystemNotFoundException | URISyntaxException | NullPointerException e) {
            NoSuchFileException exc = new NoSuchFileException(classPathResource, null, exceptionMessage);
            exc.addSuppressed(e);
            throw exc;
        }
    }
}
