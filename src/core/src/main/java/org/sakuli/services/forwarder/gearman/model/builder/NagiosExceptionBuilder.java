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

import org.apache.commons.lang.ArrayUtils;
import org.gearman.client.GearmanJobResult;
import org.sakuli.exceptions.SakuliForwarderRuntimeException;

import java.nio.charset.Charset;

/**
 * @author tschneck
 * Date: 11.07.14
 */
public class NagiosExceptionBuilder {


    public static SakuliForwarderRuntimeException buildUnexpectedErrorException(Exception e, String host, int port) {
        return new SakuliForwarderRuntimeException(
                String.format("unexpected error by sending the results to the gearman forwarder '%s:'%s'",
                        host,
                        port
                ), e);
    }

    public static SakuliForwarderRuntimeException buildTransferException(String host, int port, GearmanJobResult result) {
        return new SakuliForwarderRuntimeException(
                String.format("something went wrong during the transfer of the results to the gearman forwarder '%s:'%s':\n%s",
                        host,
                        port,
                        buildExceptionForGearmanResult(result)
                ));
    }

    private static String buildExceptionForGearmanResult(GearmanJobResult result) {
        StringBuilder sb = new StringBuilder();
        if (ArrayUtils.isNotEmpty(result.getExceptions())) {
            sb.append("EXCEPTIONS:\n").append(getStr(result.getExceptions())).append("\n");
        }
        if (ArrayUtils.isNotEmpty(result.getWarnings())) {
            sb.append("WARNINGS:\n").append(getStr(result.getWarnings())).append("\n");
        }
        return sb.toString();
    }

    private static String getStr(byte[] exceptions) {
        return new String(exceptions, Charset.forName("UTF-8"));
    }

}
