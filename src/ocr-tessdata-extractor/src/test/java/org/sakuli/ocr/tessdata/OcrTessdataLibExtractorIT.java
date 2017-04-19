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

package org.sakuli.ocr.tessdata;

import org.testng.annotations.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration Test for {@link org.sakuli.javaDSL.selenium.testng.JarReader} and {@link OcrTessdataLibExtractor}.
 *
 * @author tschneck
 *         Date: 4/18/17
 */
public class OcrTessdataLibExtractorIT {
    @Test
    public void testOcrExtraction() throws Exception {
        Path tempDirectory = Files.createTempDirectory(Paths.get("target"), "sakuli-tmp");

        final String jarName = "ocr-tessdata-extractor.jar";
        final Path urlJarFile = Paths.get("target/" + jarName);
        assertTrue(Files.exists(urlJarFile));
        final String jarURL = "jar:" + urlJarFile.toUri().toURL().toString() + "!/org/sakuli/ocr/tessdata/lib";
        final URL jarAsResource = new URL(jarURL);
        assertEquals(jarAsResource.getProtocol(), "jar");
        Path output = OcrTessdataLibExtractor.extract(tempDirectory, jarAsResource);
        assertTrue(Files.exists(output));
        assertTrue(Files.exists(output.resolve("tessconfigs")));
        assertTrue(Files.isDirectory(output));
        assertTrue(Files.isDirectory(output.resolve("tessconfigs")));
    }
}
