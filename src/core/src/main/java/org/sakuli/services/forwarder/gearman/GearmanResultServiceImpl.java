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

package org.sakuli.services.forwarder.gearman;

import org.apache.commons.codec.binary.Base64;
import org.gearman.client.*;
import org.gearman.common.GearmanJobServerConnection;
import org.gearman.common.GearmanNIOJobServerConnection;
import org.sakuli.services.common.AbstractResultService;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosCheckResultBuilder;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosExceptionBuilder;
import org.sakuli.services.forwarder.gearman.model.builder.ScreenshotDivConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @author tschneck
 *         Date: 23.05.14
 */
@ProfileGearman
@Component
public class GearmanResultServiceImpl extends AbstractResultService {
    private static final Logger logger = LoggerFactory.getLogger(GearmanResultServiceImpl.class);
    @Autowired
    private GearmanProperties properties;
    @Autowired
    private NagiosCheckResultBuilder nagiosCheckResultBuilder;

    @Override
    public int getServicePriority() {
        return 10;
    }

    @Override
    public void saveAllResults() {
        logger.info("======= SEND RESULTS TO GEARMAN SERVER ======");
        String hostname = properties.getServerHost();
        int port = properties.getServerPort();
        GearmanClient gearmanClient = getGearmanClient();
        GearmanJobServerConnection connection = getGearmanConnection(hostname, port);

        testSuite.refreshState();
        NagiosCheckResult checkResult = nagiosCheckResultBuilder.build();

        String message = checkResult.getPayloadString();
        logger.info("MESSAGE for GEARMAN:\n{}", ScreenshotDivConverter.removeBase64ImageDataString(message));
        try {
            GearmanJob job = creatJob(checkResult);
            gearmanClient.addJobServer(connection);

            //send results to geraman
            Future<GearmanJobResult> future = gearmanClient.submit(job);
            GearmanJobResult result = future.get();
            if (result.jobSucceeded()) {
                gearmanClient.shutdown();
                logger.info("======= FINISHED: SEND RESULTS TO GEARMAN SERVER ======");
            } else {
                exceptionHandler.handleException(
                        NagiosExceptionBuilder.buildTransferException(hostname, port, result)
                );
            }

        } catch (Throwable e) {
            logger.error(e.getMessage());
            exceptionHandler.handleException(
                    NagiosExceptionBuilder.buildUnexpectedErrorException(e, hostname, port),
                    true);
        }
    }

    protected GearmanJob creatJob(NagiosCheckResult checkResult) {
        byte[] bytesBase64 = Base64.encodeBase64(checkResult.getPayloadString().getBytes());
        return GearmanJobImpl.createBackgroundJob(checkResult.getQueueName(), bytesBase64, checkResult.getUuid());
    }

    protected GearmanJobServerConnection getGearmanConnection(String hostname, int port) {
        return new GearmanNIOJobServerConnection(hostname, port);
    }


    protected GearmanClient getGearmanClient() {
        return new GearmanClientImpl();
    }
}
