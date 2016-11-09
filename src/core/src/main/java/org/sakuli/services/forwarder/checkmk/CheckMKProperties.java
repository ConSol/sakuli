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

package org.sakuli.services.forwarder.checkmk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Georgi Todorov
 */
@ProfileCheckMK
@Component
public class CheckMKProperties {
    private static final String SPOOL_DIR = "sakuli.forwarder.check_mk.spooldir";
    private static final String SPOOL_FILE_PREFIX = "sakuli.forwarder.check_mk.spoolfile_prefix";
    private static final String FRESHNESS = "sakuli.forwarder.check_mk.freshness";
    private static final String SERVICE_DESCRIPTION = "sakuli.forwarder.check_mk.service_description";

    @Value("${" + SPOOL_DIR + "}")
    private String spoolDir;
    @Value("${" + SPOOL_FILE_PREFIX + "}")
    private String spoolFilePrefix;
    @Value("${" + FRESHNESS + "}")
    private String freshness;

    @Value("${" + SERVICE_DESCRIPTION + "}")
    private String serviceDescription;

    public String getSpoolDir() {
        return spoolDir;
    }

    public String getSpoolFilePrefix() {
        return spoolFilePrefix;
    }

    public String getFreshness() {
        return freshness;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

}
