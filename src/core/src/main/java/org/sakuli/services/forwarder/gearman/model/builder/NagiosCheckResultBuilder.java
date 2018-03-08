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

import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.GearmanTemplateOutputBuilder;
import org.sakuli.services.forwarder.gearman.ProfileGearman;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 * Date: 10.07.14
 */
@ProfileGearman
@Component
public class NagiosCheckResultBuilder {

    private static final Logger logger = LoggerFactory.getLogger(NagiosCheckResultBuilder.class);

    @Autowired
    private GearmanProperties gearmanProperties;
    @Autowired
    private GearmanTemplateOutputBuilder outputBuilder;

    public NagiosCheckResult build(AbstractTestDataEntity abstractTestDataEntity) throws SakuliForwarderException {
        logger.info("======= CREATING OUTPUT FOR GEARMAN ======");
        String payload = outputBuilder.createOutput(abstractTestDataEntity);
        logger.info("======= FINISHED: CREATING OUTPUT FOR GEARMAN ======");
        return new NagiosCheckResult(gearmanProperties.getServerQueue(),  abstractTestDataEntity.getGuid(), payload);
    }

}
