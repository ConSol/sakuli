/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.services.receiver.gearman;

import de.consol.sakuli.datamodel.properties.ReceiverProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 10.07.14
 */
@ProfileGearman
@Component
public class GearmanProperties extends ReceiverProperties {
    public static final String SERVICE_TYPE_DEFAULT = "passive";
    private String serviceType = SERVICE_TYPE_DEFAULT;
    public static final String SERVER_QUEUE_DEFAULT = "check_results";
    public static final String SERVER_QUEUE = "sakuli.receiver.gearman.server.queue";
    public static final String SERVER_HOST = "sakuli.receiver.gearman.server.host";
    public static final String SERVER_PORT = "sakuli.receiver.gearman.server.port";
    public static final String NAGIOS_HOSTNAME = "sakuli.receiver.gearman.nagios.hostname";
    public static final String NAGIOS_OUTPUT_SUITE = "sakuli.receiver.gearman.nagios.output.suite";
    public static final String NAGIOS_OUTPUT_CASE = "sakuli.receiver.gearman.nagios.output.case";
    public static final String NAGIOS_OUTPUT_STEP = "sakuli.receiver.gearman.nagios.output.step";
    public static final String NAGIOS_OUTPUT_SUITE_DEFAULT = "{{state}} Sakuli suite \"{{id}}\" ran in {{duration}} seconds, {{state_description}}. (Last suite run: {{last_run}})";
    public static final String NAGIOS_OUTPUT_CASE_DEFAULT = "{{state}} case \"{{id}}\" ran in {{duration}}s ({{state_description}})";
    public static final String NAGIOS_OUTPUT_STEP_DEFAULT = "{{state}} step \"{{id}}\" ran in {{duration}}s ({{state_description}})";

    @Value("${" + SERVER_QUEUE + ":" + SERVER_QUEUE_DEFAULT + "}")
    private String serverQueue;
    @Value("${" + SERVER_HOST + "}")
    private String serverHost;
    @Value("${" + SERVER_PORT + "}")
    private int serverPort;
    @Value("${" + NAGIOS_HOSTNAME + ":null}")
    private String nagiosHost;
    @Value("${" + NAGIOS_OUTPUT_SUITE + ":" + NAGIOS_OUTPUT_SUITE_DEFAULT + "}")
    private String outputSuite;
    @Value("${" + NAGIOS_OUTPUT_CASE + ":" + NAGIOS_OUTPUT_CASE_DEFAULT + "}")
    private String outputCase;
    @Value("${" + NAGIOS_OUTPUT_STEP + ":" + NAGIOS_OUTPUT_STEP_DEFAULT + "}")
    private String outputStep;

    //TODO write test with context
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServerQueue() {
        return serverQueue;
    }

    public void setServerQueue(String serverQueue) {
        this.serverQueue = serverQueue;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getNagiosHost() {
        return nagiosHost;
    }

    public void setNagiosHost(String nagiosHost) {
        this.nagiosHost = nagiosHost;
    }

    public String getOutputSuite() {
        return outputSuite;
    }

    public void setOutputSuite(String outputSuite) {
        this.outputSuite = outputSuite;
    }

    public String getOutputCase() {
        return outputCase;
    }

    public void setOutputCase(String outputCase) {
        this.outputCase = outputCase;
    }

    public String getOutputStep() {
        return outputStep;
    }

    public void setOutputStep(String outputStep) {
        this.outputStep = outputStep;
    }

    public static enum TextPlaceholder {
        STATE("{{state}}"),
        STATE_DESC("{{state_description}}"),
        ID("{{id}}"),
        DURATION("{{duration}}"),
        LAST_RUN("{{last_run}}");

        private final String pattern;

        TextPlaceholder(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        @Override
        public String toString() {
            return name();
        }
    }
}
