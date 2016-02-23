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

package org.sakuli.services.forwarder.icinga2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 2/22/16
 */
@ProfileIcinga2
@Component
public class Icinga2Properties {

    private static final String HOSTNAME = "sakuli.forwarder.icinga2.hostname";
    private static final String API_HOST = "sakuli.forwarder.icinga2.api.host";
    private static final String API_PORT = "sakuli.forwarder.icinga2.api.port";
    private static final String API_USERNAME = "sakuli.forwarder.icinga2.api.username";
    private static final String API_PASSWORD = "sakuli.forwarder.icinga2.api.password";
    private static final String API_URL = "sakuli.forwarder.icinga2.api.url";
    @Value("${" + HOSTNAME + "}")
    private String hostName;
    @Value("${" + API_HOST + "}")
    private String apiHost;
    @Value("${" + API_PORT + "}")
    private int apiPort;
    @Value("${" + API_USERNAME + "}")
    private String apiUsername;
    @Value("${" + API_PASSWORD + "}")
    private String apiPassword;
    @Value("${" + API_URL + "}")
    private String apiURL;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public void setApiUsername(String apiUsername) {
        this.apiUsername = apiUsername;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }
}
