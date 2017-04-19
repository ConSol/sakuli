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

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ResourceHelperTest {

    @Test
    public void testGetClasspathResource() throws Exception {
        Path classpathResource = ResourceHelper.getClasspathResource(this.getClass(), "classpath-resource.file", "no exception wanted!");
        assertTrue(Files.exists(classpathResource));
        assertEquals(FileUtils.readFileToString(classpathResource.toFile()), "test-file-content");
    }

    @Test(expectedExceptions = NoSuchFileException.class, expectedExceptionsMessageRegExp = "XX-resource.file: wanted Exception")
    public void testGetClasspathResourceException() throws Exception {
        ResourceHelper.getClasspathResource(this.getClass(), "XX-resource.file", "wanted Exception");
    }
}