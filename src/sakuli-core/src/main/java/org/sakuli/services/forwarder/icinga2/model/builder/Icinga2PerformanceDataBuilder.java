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

package org.sakuli.services.forwarder.icinga2.model.builder;

import org.apache.commons.lang3.StringUtils;
import org.sakuli.datamodel.Builder;
import org.sakuli.services.forwarder.AbstractPerformanceDataBuilder;
import org.sakuli.services.forwarder.icinga2.ProfileIcinga2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
@ProfileIcinga2
@Component
public class Icinga2PerformanceDataBuilder extends AbstractPerformanceDataBuilder implements Builder<List<String>> {

    @Override
    public List<String> build() {
        String performanceData = getTestSuitePerformanceData(testSuite);
        if (performanceData != null) {
            return Arrays.asList(StringUtils.split(performanceData));
        }
        return Collections.emptyList();
    }
}
