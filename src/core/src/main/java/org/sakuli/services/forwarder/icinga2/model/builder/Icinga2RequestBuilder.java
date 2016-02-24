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
import org.sakuli.services.forwarder.icinga2.model.Icinga2Request;

import java.util.LinkedList;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
public class Icinga2RequestBuilder implements Builder<Icinga2Request> {
    private Icinga2Request request;

    public Icinga2RequestBuilder() {
        request = new Icinga2Request();
        request.setPerformance_data(new LinkedList<>());
        request.setExit_status(-1);
    }

    @Override
    public Icinga2Request build() {
        return request;
    }

    public Icinga2RequestBuilder withCheckSource(String checkSource) {
        request.setCheck_source(checkSource);
        return this;
    }

    public Icinga2RequestBuilder withExitStatus(int exitStatus) {
        request.setExit_status(0);
        return this;
    }

    public Icinga2RequestBuilder addPerformanceData(String performanceData) {
        request.getPerformance_data().add(performanceData);
        return this;
    }

    public Icinga2RequestBuilder withPluginOutput(String pluginOutput) {
        request.setPlugin_output(pluginOutput);
        return this;
    }
}
