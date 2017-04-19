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

import org.gearman.client.GearmanJobResult;
import org.sakuli.exceptions.SakuliForwarderException;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class NagiosExceptionBuilderTest {

    @Test
    public void testBuildUnexpectedErrorException() throws Exception {
        SakuliForwarderException sakuliForwarderException = NagiosExceptionBuilder.buildUnexpectedErrorException(new Exception("TEST"), "localhost", 4370);
        assertEquals(sakuliForwarderException.getMessage(),
                "unexpected error by sending the results to the gearman forwarder 'localhost:'4370'");
    }

    @Test
    public void testBuildTransferException() throws Exception {
        GearmanJobResult result = mock(GearmanJobResult.class);
        when(result.getExceptions()).thenReturn("exceptions".getBytes());
        when(result.getWarnings()).thenReturn("warnings".getBytes());
        SakuliForwarderException sakuliForwarderException = NagiosExceptionBuilder.buildTransferException("localhost", 4370, result);
        assertEquals(sakuliForwarderException.getMessage(),
                "something went wrong during the transfer of the results to the gearman forwarder 'localhost:'4370':\n" +
                        "EXCEPTIONS:\n" +
                        "exceptions\n" +
                        "WARNINGS:\n" +
                        "warnings\n");
    }
}