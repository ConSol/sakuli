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

import org.apache.commons.io.FileUtils;
import org.sakuli.javaDSL.selenium.testng.JarReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck
 *         Date: 4/18/17
 */
public class OcrTessdataLibExtractor {
    public static final String TESTDATA = "eng.traineddata";
    private static final Logger LOGGER = LoggerFactory.getLogger(OcrTessdataLibExtractor.class);

    public static Path extract(Path outputDir) throws IOException {
        return extract(outputDir, null);
    }

    public static Path extract(Path outputDir, URL tessdataClasspathResourceFolder) throws IOException {
        if (tessdataClasspathResourceFolder == null) {
            tessdataClasspathResourceFolder = OcrTessdataLibExtractor.class.getResource("lib");
        }
        try {
            if (tessdataClasspathResourceFolder.getProtocol().equals("jar")) {
                final Path targetDir = outputDir.resolve("org").resolve("sakuli").resolve("ocr").resolve("tessdata").resolve("lib");
                if (!isTessdataLibFolderContentPresent(targetDir)) {
                    LOGGER.debug("Extract classpath resource '{}' to:{}", tessdataClasspathResourceFolder.toString(), outputDir.toString());
                    JarReader.read(tessdataClasspathResourceFolder, (name, is) -> {
                        Path targetFile = outputDir.resolve("." + name).normalize();
                        LOGGER.debug("write file: " + targetFile);
                        FileUtils.copyInputStreamToFile(is, targetFile.toFile());
                    });
                    checkTessdataLibFolderContent(targetDir);
                }
                return targetDir.normalize().toAbsolutePath();
            } else if (tessdataClasspathResourceFolder.getProtocol().equals("file")) {
                final Path libPath = Paths.get(tessdataClasspathResourceFolder.toURI());
                LOGGER.debug("Use direct file path for OCR tessdata lib: {}", libPath);
                checkTessdataLibFolderContent(libPath);
                return libPath.normalize().toAbsolutePath();
            }
            throw new FileSystemException("URL type '" + tessdataClasspathResourceFolder.getProtocol()
                    + "' not supported: " + tessdataClasspathResourceFolder.toString());
        } catch (URISyntaxException e) {
            throw new FileNotFoundException("File URL is not valid: " + tessdataClasspathResourceFolder.toString()
                    + " - " + e.getMessage());
        }
    }

    public static void checkTessdataLibFolderContent(Path output) throws FileNotFoundException {
        //test the lib content is available
        Path testdata = output.resolve(TESTDATA);
        if (Files.notExists(testdata)) {
            throw new FileNotFoundException("OCR required tessdata lib (e.g. '" + testdata + "') is missing in folder: " + output);
        }
        LOGGER.debug("Use OCR tessdata lib path: {}", output.toString());
    }

    public static boolean isTessdataLibFolderContentPresent(Path output) {
        return Files.exists(output.resolve(TESTDATA));
    }
}
