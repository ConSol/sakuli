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

package org.sakuli.services.forwarder.icinga2.model;

import java.util.List;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
public class Icinga2Request {

    private String check_source;
    private int exit_status;
    private List<String> performance_data;
    private String plugin_output;

    public String getCheck_source() {
        return check_source;
    }

    public void setCheck_source(String check_source) {
        this.check_source = check_source;
    }

    public int getExit_status() {
        return exit_status;
    }

    public void setExit_status(int exit_status) {
        this.exit_status = exit_status;
    }

    public List<String> getPerformance_data() {
        return performance_data;
    }

    public void setPerformance_data(List<String> performance_data) {
        this.performance_data = performance_data;
    }

    public String getPlugin_output() {
        return plugin_output;
    }

    public void setPlugin_output(String plugin_output) {
        this.plugin_output = plugin_output;
    }

    @Override
    public String toString() {
        return "Icinga2Request{" +
                "check_source='" + check_source + '\'' +
                ", exit_status=" + exit_status +
                ", performance_data=" + performance_data +
                ", plugin_output='" + plugin_output + '\'' +
                '}';
    }
}
