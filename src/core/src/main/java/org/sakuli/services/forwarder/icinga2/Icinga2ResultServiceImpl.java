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

import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.common.AbstractResultService;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author tschneck
 *         Date: 2/22/16
 */
@ProfileIcinga2
@Component
public class Icinga2ResultServiceImpl extends AbstractResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Icinga2ResultServiceImpl.class);
    @Autowired
    private Icinga2RestCient icinga2RestCient;

    @Override
    public int getServicePriority() {
        return 10;
    }

    @Override
    public void saveAllResults() {
        //TODO create JSON from TestSuite object
        LOGGER.info("======= SEND RESULTS TO ICINGA SERVER ======");
        String request = "{ \"check_source\": \"check_sakuli\", \"exit_status\": 0, \"performance_data\": [ \"s_001_001_Test_Sahi_landing_page=1.96s;15;;;\", \"s_001_002_Calculation=8.96s;30;;;\", \"s_001_003_Editor=8.38s;30;;;\", \"c_001_case1=25.22s;40;50;;\", \"c_001__state=0;;;;\", \"c_001__warning=40s;;;;\", \"c_001__critical=50s;;;;\", \"suite__state=0;;;;\", \"suite__warning=50s;;;;\", \"suite__critical=60s;;;;\" ], \"plugin_output\": \"Sakuli suite 'example_ubuntu_0' (ID: 25100) ran in 99.31 seconds.\" }";


        LOGGER.info("POST Sakuli results to '{}'", icinga2RestCient.getTargetCheckResult().getUri().toString());
        LOGGER.debug("ICINGA Payload: {}", request);
        Response response = icinga2RestCient.getTargetCheckResult()
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.text(request));

        Icinga2Result result = response.readEntity(Icinga2Result.class);
        if (result.isSuccess()) {
            LOGGER.info("ICINGA Response: {}", result.getFirstElementAsString());
            LOGGER.info("======= FINISHED: SEND RESULTS TO ICINGA SERVER ======");
        } else {
            exceptionHandler.handleException(new SakuliForwarderException(String.format(
                    "Unexpected result of REST-POST to Incinga monitoring server (%s): %s",
                    icinga2RestCient.getTargetCheckResult().getUri(),
                    result.getFirstElementAsString())));
        }

    }
}
