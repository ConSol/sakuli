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

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.services.ResultService;
import de.consol.sakuli.services.receiver.gearman.model.NagiosCheckResult;
import de.consol.sakuli.services.receiver.gearman.model.builder.NagiosCheckResultBuilder;
import de.consol.sakuli.services.receiver.gearman.model.builder.NagiosExceptionBuilder;
import org.apache.commons.codec.binary.Base64;
import org.gearman.client.*;
import org.gearman.common.GearmanJobServerConnection;
import org.gearman.common.GearmanNIOJobServerConnection;
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
public class GearmanResultServiceImpl implements ResultService {
    private static final Logger logger = LoggerFactory.getLogger(GearmanResultServiceImpl.class);
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private SakuliExceptionHandler exceptionHandler;
    @Autowired
    private GearmanProperties properties;

    @Override
    public void saveAllResults() {

        String hostname = properties.getServerHost();
        int port = properties.getServerPort();
        GearmanClient gearmanClient = new GearmanClientImpl();
        GearmanJobServerConnection connection = new GearmanNIOJobServerConnection(hostname, port);

        testSuite.refreshState();
        NagiosCheckResult checkResult = new NagiosCheckResultBuilder().withTestSuite(testSuite, properties).build();
        String message = checkResult.getPayloadString();
//
//        String message = "type=passive\n" +
//                "host_name=win7sakuli\n" +
//                "start_time=1405004781.939134\n" +
//                "finish_time=1405004781.939134\n" +
//                "latency=0.0\n" +
//                "return_code=0\n" +
//                "service_description=sakuli_demo\n" +
//                "output=OK - [OK] case \"demo_win7\" (52.95s) ok, [OK] Sakuli suite \"sakuli_demo\" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)\\\\n<table style=\"border-collapse: collapse;\"><tr valign=\"top\"><td class=\"serviceOK\">[OK] case \"demo_win7\" (52.95s) ok</td></tr><tr valign=\"top\"><td class=\"serviceOK\">[OK] Sakuli suite \"sakuli_demo\" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)</td></tr></table>|s_1_1_notepad=12.22s;20;;; s_1_2_project=17.89s;20;;; s_1_3_print_test_client=7.89s;10;;; s_1_4_open_calc=3.01s;5;;; s_1_5_calculate_525_+100=9.95s;20;;; c_1_demo_win7=52.95s;60;70;; c_1state=0;;;; suite_state=0;;;; suite_runtime_sakuli_demo=64.10s;120;140;;";
        try {

            logger.info("MESSAGE for GEARMAN:\n{}", message);
            byte[] bytesBase64 = Base64.encodeBase64(message.getBytes());
            gearmanClient.addJobServer(connection);
            GearmanJob job = GearmanJobImpl.createBackgroundJob(checkResult.getQueueName(), bytesBase64, checkResult.getUuid());
            Future<GearmanJobResult> future = gearmanClient.submit(job);
            GearmanJobResult result = future.get();
            if (result.jobSucceeded()) {
                gearmanClient.shutdown();
            } else {
                exceptionHandler.handleException(
                        NagiosExceptionBuilder.buildTransferException(hostname, port, message, result)
                );
            }

        } catch (Throwable e) {
            exceptionHandler.handleException(
                    NagiosExceptionBuilder.buildUnexpectedErrorException(e, hostname, port, message),
                    true);
        }

    }

}
