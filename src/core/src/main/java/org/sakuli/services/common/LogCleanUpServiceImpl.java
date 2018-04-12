/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.common;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.services.TeardownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author tschneck
 * Date: 2/12/16
 */
@Component
public class LogCleanUpServiceImpl implements TeardownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCleanUpServiceImpl.class);

    @Autowired
    private SakuliProperties sakuliProperties;

    @Override
    public int getServicePriority() {
        return 20;
    }

    @Override
    public void teardownTestSuite(@NonNull TestSuite testSuite) {
        if (Files.exists(sakuliProperties.getLogFolder())) {
            cleanUpDirectory(sakuliProperties.getLogFolder());
        }
    }

    @Override
    public void teardownTestCase(@NonNull TestCase testCase) {
        //Not needed
    }

    @Override
    public void teardownTestCaseStep(@NonNull TestCaseStep testCaseStep) {
        //Not needed
    }

    /**
     * Cleans the {@link Path} from files, which are older then {@link SakuliProperties#logMaxAge}.
     * On error no exception will be thrown, du to the facts, that`s only a optional cleanup.
     *
     * @param path root folder of files to clean
     */
    void cleanUpDirectory(Path path) {
        try {
            Instant maxDate = Instant.now().minus(sakuliProperties.getLogMaxAge(), ChronoUnit.DAYS);
            Files.newDirectoryStream(path).forEach(e -> {
                if (Files.isDirectory(e)) {
                    cleanUpDirectory(e);
                } else if (Files.isRegularFile(e)) {
                    try {
                        if (Files.getLastModifiedTime(e).toInstant().isBefore(maxDate)) {
                            LOGGER.info("cleanup too old log file '{}'", e);
                            Files.deleteIfExists(e);
                        }
                    } catch (IOException e1) {
                        LOGGER.error("can't delete file", e1);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("couldn't access log file directory '" + path + "'", e);
        }
    }
}
