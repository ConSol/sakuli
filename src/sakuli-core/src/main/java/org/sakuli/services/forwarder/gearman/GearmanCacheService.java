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
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.gearman.model.NagiosCachedCheckResult;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static final String CACHE_FILE = ".cache/gearman.cache";
    private static final String CACHE_SEPARATOR = "=======";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String LINE_SEPARATOR = "\n";
    @Autowired
    private SakuliExceptionHandler exceptionHandler;

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
                StringBuilder resultBuilder = new StringBuilder();
                String queueName = "";
                String uuid = "";
                for (String line : lines) {
                    if (line.trim().equals(CACHE_SEPARATOR)) {
                        results.add(new NagiosCachedCheckResult(queueName, uuid, resultBuilder.toString()));
                    } else if (line.startsWith(CACHE_SEPARATOR)) {
                        resultBuilder = new StringBuilder();
                        queueName = line.substring(CACHE_SEPARATOR.length() + 1, line.indexOf(":"));
                        uuid = line.substring(line.indexOf(":") + 1).trim();
                    } else if (StringUtils.isNotEmpty(line)) {
                        resultBuilder.append(line).append(LINE_SEPARATOR);
                    }
                }
            } catch (IOException e) {
                exceptionHandler.handleException(new SakuliForwarderException(e, String.format("Failed to read Gearman cache file '%s'", cacheFile)), true);
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
        if (!output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(output)) {
            for (NagiosCheckResult result : results) {
                fos.write((CACHE_SEPARATOR + " " + result.getQueueName() + ":" + result.getUuid() + LINE_SEPARATOR).getBytes(CHARSET_NAME));
                fos.write((result.getPayloadString().trim() + LINE_SEPARATOR).getBytes(CHARSET_NAME));
                fos.write((CACHE_SEPARATOR + LINE_SEPARATOR).getBytes(CHARSET_NAME));
            }

            if (results.isEmpty()) {
                fos.write(LINE_SEPARATOR.getBytes(CHARSET_NAME));
            }

            fos.flush();
        } catch (IOException e) {
            exceptionHandler.handleException(new SakuliForwarderException(e, "Failed to write Gearman cache file"), true);
        }
    }
}
