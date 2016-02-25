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

package org.sakuli.services.forwarder.gearman.model.builder;

import org.sakuli.datamodel.Builder;
import org.sakuli.services.forwarder.AbstractPerformanceDataBuilder;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.ProfileGearman;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 8/26/15
 */
@ProfileGearman
@Component
public class NagiosPerformanceDataBuilder extends AbstractPerformanceDataBuilder implements Builder<String> {

    @Autowired
    private GearmanProperties gearmanProperties;

    /**
     * Generates the performance data for the nagios server as an {@link String}.
     *
     * @return formatted payload string
     */
    @Override
    public String build() {
        return getTestSuitePerformanceData(testSuite) + " [" + gearmanProperties.getNagiosCheckCommand() + "]";
    }
}
