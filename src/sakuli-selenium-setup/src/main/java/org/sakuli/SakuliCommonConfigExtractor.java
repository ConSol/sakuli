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

package org.sakuli;

import org.apache.commons.io.FileUtils;
import org.sakuli.ocr.tessdata.JarReader;
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
public class SakuliCommonConfigExtractor {
    public static final String TESTDATA = "sakuli-default.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(SakuliCommonConfigExtractor.class);

    public static Path extract(Path outputDir) throws IOException {
        return extract(outputDir, null);
    }

    public static Path extract(Path outputDir, URL confResourceUrl) throws IOException {
        if (confResourceUrl == null) {
            //load config fils from classpath
            confResourceUrl = SakuliCommonConfigExtractor.class.getResource("common/config");
        }
        try {
            if (confResourceUrl.getProtocol().equals("jar")) {
                final Path targetDir = outputDir.resolve("org").resolve("sakuli").resolve("common").resolve("config");
                LOGGER.debug("Extract classpath resource '{}' to:{}", confResourceUrl.toString(), outputDir.toString());
                JarReader.read(confResourceUrl, (name, is) -> {
                    Path targetFile = outputDir.resolve("." + name).normalize();
                    LOGGER.debug("write file: " + targetFile);
                    FileUtils.copyInputStreamToFile(is, targetFile.toFile());
                });
                checkTestFilePresent(targetDir);
                return targetDir.normalize().toAbsolutePath();
            } else if (confResourceUrl.getProtocol().equals("file")) {
                final Path libPath = Paths.get(confResourceUrl.toURI());
                LOGGER.debug("Use direct file path for OCR tessdata lib: {}", libPath);
                checkTestFilePresent(libPath);
                return libPath.normalize().toAbsolutePath();
            }
            throw new FileSystemException("URL type '" + confResourceUrl.getProtocol()
                    + "' not supported: " + confResourceUrl.toString());
        } catch (URISyntaxException e) {
            throw new FileNotFoundException("File URL is not valid: " + confResourceUrl.toString() + " - " + e.getMessage());
        }
    }

    public static void checkTestFilePresent(Path output) throws FileNotFoundException {
        //test the config content is available
        Path testdata = output.resolve(TESTDATA);
        if (Files.notExists(testdata)) {
            throw new FileNotFoundException("Sakuli requires '" + testdata + "'. It is missing in folder: " + output);
        }
        LOGGER.debug("Sakuli default config extracted: {}", output.toString());
    }

}
