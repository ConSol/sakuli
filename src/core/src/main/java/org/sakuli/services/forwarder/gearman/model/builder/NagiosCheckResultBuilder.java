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
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.ProfileGearman;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.sakuli.services.forwarder.gearman.model.PayLoadFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.sakuli.services.forwarder.gearman.model.PayLoadFields.*;

/**
 * @author tschneck
 *         Date: 10.07.14
 */
@ProfileGearman
@Component
public class NagiosCheckResultBuilder implements Builder<NagiosCheckResult> {

    private String queueName;
    private String type;
    private String uuid;
    private String startTime;
    private String finishTime;
    private String returnCode;
    private String serviceDesc;
    private NagiosOutput output;
    private String hostSuite;
    private String hostProperties;
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private GearmanProperties gearmanProperties;
    @Autowired
    private NagiosOutputBuilder nagiosOutputBuilder;


    @Override
    public NagiosCheckResult build() {
        extractData(testSuite, gearmanProperties);
        NagiosCheckResult result = new NagiosCheckResult(queueName, uuid);
        SortedMap<PayLoadFields, String> payload = new TreeMap<>();
        payload.put(TYPE, type);
        //host name from properties file can overwrites the determined of the suite
        if (hostProperties != null) {
            payload.put(HOST, hostProperties);
        } else {
            payload.put(HOST, hostSuite);
        }
        payload.put(START_TIME, startTime);
        payload.put(FINISH_TIME, finishTime);
        payload.put(RETURN_CODE, returnCode);
        payload.put(SERVICE_DESC, serviceDesc);
        payload.put(OUTPUT, output.getOutputString());
        result.setPayload(payload);
        return result;
    }

    protected void extractData(TestSuite testSuite, GearmanProperties gearmanProperties) {
        //fields form properties file
        queueName = gearmanProperties.getServerQueue();
        type = gearmanProperties.getServiceType();
        hostProperties = gearmanProperties.getNagiosHost();

        hostSuite = testSuite.getHost();
        uuid = testSuite.getGuid();
        startTime = testSuite.getStartDateAsUnixTimestamp();
        finishTime = testSuite.getStopDateAsUnixTimestamp();
        returnCode = String.valueOf(testSuite.getState().getNagiosErrorCode());
        serviceDesc = gearmanProperties.getNagiosServiceDescription();
        output = nagiosOutputBuilder.build();
    }


}
