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

package org.sakuli.services.forwarder.gearman;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.services.forwarder.gearman.model.NagiosCachedCheckResult;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@ProfileGearman
@Component
public class GearmanCacheService {

    private static Logger logger = LoggerFactory.getLogger(GearmanCacheService.class);

    private static final String CACHE_FILE = ".gearman-cache";
    private static final String CACHE_SEPARATOR = "=======";
    private static final String CHARSET_NAME = "UTF-8";

    @Autowired
    private GearmanProperties properties;

    @Autowired
    private TestSuiteProperties testSuiteProperties;

    /**
     * Reads cached results from Gearman cache file.
     * @return
     */
    public List<NagiosCheckResult> getCachedResults() {
        List<NagiosCheckResult> results = new ArrayList<>();

        Path cacheFile = testSuiteProperties.getTestSuiteFolder().resolve(CACHE_FILE);
        if (Files.exists(cacheFile)) {
            try {
                List<String> lines = FileUtils.readLines(cacheFile.toFile(), Charset.forName(CHARSET_NAME));
                StringBuilder resultBuilder = null;
                String queueName = "";
                String uuid = "";
                for (String line : lines) {
                    if (line.trim().equals(CACHE_SEPARATOR)) {
                        results.add(new NagiosCachedCheckResult(queueName, uuid, resultBuilder.toString()));
                    } else if (line.startsWith(CACHE_SEPARATOR)) {
                        resultBuilder = new StringBuilder();
                        queueName = line.substring(CACHE_SEPARATOR.length() + 1, line.indexOf(":"));
                        uuid = line.substring(line.indexOf(":")).trim();
                    } else if (StringUtils.isNotEmpty(line)) {
                        resultBuilder.append(line + System.lineSeparator());
                    }
                }
            } catch (IOException e) {
                logger.error(String.format("Failed to read Gearman cache file '%s'", cacheFile), e);
            }
        }

        return results;
    }

    /**
     * Writes results to Gearman cache file.
     * @param results
     */
    public void cacheResults(List<NagiosCheckResult> results) {
        Path cacheFile = testSuiteProperties.getTestSuiteFolder().resolve(CACHE_FILE);
        File output = new File(cacheFile.toUri());
        try (FileOutputStream fos = new FileOutputStream(output)) {
            for (NagiosCheckResult result : results) {
                fos.write((CACHE_SEPARATOR + " " + result.getQueueName() + ":" + result.getUuid() + System.lineSeparator()).getBytes(CHARSET_NAME));
                fos.write((result.getPayloadString() + System.lineSeparator()).getBytes(CHARSET_NAME));
                fos.write((CACHE_SEPARATOR + System.lineSeparator()).getBytes(CHARSET_NAME));
            }

            if (results.isEmpty()) {
                fos.write(System.lineSeparator().getBytes(CHARSET_NAME));
            }

            fos.flush();
        } catch (IOException e) {
            logger.error("Failed to write Gearman cache file", e);
        }
    }
}
