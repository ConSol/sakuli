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

import static org.testng.Assert.*;

/**
 * Integration Test for {@link JarReader} and {@link OcrTessdataLibExtractor}.
 *
 * @author tschneck
 *         Date: 4/18/17
 */
public class OcrTessdataLibExtractorTest {
    @Test
    public void testOcrExtractionAsFile() throws Exception {
        Path tempDirectory = Files.createTempDirectory(Paths.get("target"), "sakuli-tmp");
        final URL libAsFile = OcrTessdataLibExtractor.class.getResource("lib");
        assertEquals(libAsFile.getProtocol(), "file");
        Path output = OcrTessdataLibExtractor.extract(tempDirectory, libAsFile);
        assertTrue(Files.exists(output));
        assertTrue(Files.isDirectory(output));
        assertFalse(Files.exists(output.resolve("org")));
    }
}
