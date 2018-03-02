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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.common.AbstractResultService;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Request;
import org.sakuli.services.forwarder.icinga2.model.Icinga2Result;
import org.sakuli.services.forwarder.icinga2.model.builder.Icinga2CheckResultBuilder;
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
    @Autowired
    private Icinga2CheckResultBuilder icinga2CheckResultBuilder;

    public static String convertToJSON(Entity<?> entity) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entity.getEntity());
        } catch (JsonProcessingException e) {
            return entity.getEntity().toString();
        }
    }

    @Override
    public int getServicePriority() {
        return 10;
    }

    @Override
    public void saveAllResults(AbstractTestDataEntity abstractTestDataEntity) {
        if (abstractTestDataEntity != null &&
                TestSuite.class.isAssignableFrom(abstractTestDataEntity.getClass())) {
            LOGGER.info("======= SEND RESULTS TO ICINGA SERVER ======");
            LOGGER.info("POST Sakuli results to '{}'", icinga2RestCient.getTargetCheckResult().getUri().toString());

            Entity<Icinga2Request> payload = Entity.json(icinga2CheckResultBuilder.build());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ICINGA Payload: {}", convertToJSON(payload));
            }
            Response response = icinga2RestCient.getTargetCheckResult()
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(payload);

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

}
