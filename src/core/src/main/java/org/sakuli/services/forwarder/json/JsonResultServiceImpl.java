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

package org.sakuli.services.forwarder.json;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliForwarderCheckedException;
import org.sakuli.exceptions.SakuliForwarderRuntimeException;
import org.sakuli.services.ResultService;
import org.sakuli.services.forwarder.AbstractTeardownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Georgi Todorov
 */
@ProfileJson
@Component
public class JsonResultServiceImpl extends AbstractTeardownService implements ResultService {

    public static final SimpleDateFormat JSON_FILE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd-HH-mm-ss-SSS");
    private static final Logger logger = LoggerFactory.getLogger(JsonResultServiceImpl.class);

    @Autowired
    private GsonOutputBuilder outputBuilder;

    @Autowired
    private JsonProperties jsonProperties;

    @Override
    public int getServicePriority() {
        return 10;
    }

    @Override
    public void teardownTestSuite(@NonNull TestSuite testSuite) throws RuntimeException {
        try {
            logger.info("======= WRITE TEST OUTPUT AS JSON FILE ======");
            String output = outputBuilder.createOutput();
            logger.debug(String.format("JSON Output:\n%s", output));
            writeToFile(createJsonFilePath(testSuite.getId()), output);
            logger.info("======= FINISHED: WRITE TEST OUTPUT AS JSON FILE ======");
        } catch (Exception e) {
            throw new SakuliForwarderRuntimeException("Couldn't create the JSON result file for: " + testSuite);
        }
    }

    protected Path createJsonFilePath(String dataId) throws SakuliForwarderCheckedException {
        Path outputDir = jsonProperties.getOutputJsonDir();
        createDirectoryIfNotExists(outputDir);
        String fileName = dataId +
                "_" +
                JSON_FILE_DATE_FORMAT.format(new Date()) +
                ".json";
        return outputDir.resolve(fileName);
    }

    protected void createDirectoryIfNotExists(Path outputDir) throws SakuliForwarderCheckedException {
        if (!Files.exists(outputDir)) {
            try {
                Files.createDirectories(outputDir);
            } catch (IOException e) {
                throw new SakuliForwarderCheckedException(e,
                        String.format("Unexpected error during creating the json output directory '%s'", outputDir.toString()));
            }
        }
    }

    private void writeToFile(Path file, String output) throws SakuliForwarderCheckedException {
        try {
            logger.info(String.format("Write file to '%s'", file));
            Files.write(file, output.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new SakuliForwarderCheckedException(e,
                    String.format("Unexpected error by writing the json output to the following file '%s'", file));
        }
    }

}
