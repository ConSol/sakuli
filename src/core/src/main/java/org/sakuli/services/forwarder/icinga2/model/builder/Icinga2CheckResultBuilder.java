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

import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.forwarder.icinga2.ProfileIcinga2;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
@ProfileIcinga2
@Component
public class Icinga2CheckResultBuilder implements Builder<Icinga2Request> {

    @Autowired
    private Icinga2PerformanceDataBuilder performanceDataBuilder;
    @Autowired
    private Icinga2OutputBuilder outputBuilder;
    @Autowired
    private TestSuite testSuite;


    @Override
    public Icinga2Request build() {
        Icinga2RequestBuilder requestBuilder = new Icinga2RequestBuilder()
                .withCheckSource("check_sakuli")
                .withPluginOutput(outputBuilder.build())
                .withExitStatus(testSuite.getState().getNagiosErrorCode());
        return addPerformanceData(requestBuilder).build();
    }

    private Icinga2RequestBuilder addPerformanceData(Icinga2RequestBuilder builder) {
        performanceDataBuilder.build().forEach(builder::addPerformanceData);
        return builder;
    }
}
