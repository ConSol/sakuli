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
import org.apache.commons.lang.time.DateUtils;
import org.gearman.client.*;
import org.gearman.common.GearmanJobServerConnection;
import org.mockito.*;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.services.forwarder.gearman.crypt.Aes;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosCheckResultBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.channels.UnresolvedAddressException;
import java.util.*;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class GearmanResultServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private GearmanResultServiceImpl testling;
    @Mock
    private GearmanCacheService gearmanCacheService;
    @Mock
    private GearmanProperties properties;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private NagiosCheckResultBuilder checkResultBuilder;
    private GearmanClient gearmanClient;
    private GearmanJobServerConnection connection;

    private final String host = "99.99.99.20";
    private final int port = 4730;
    private final String queueName = "check_results";
    private String testResult = "blub";

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(properties.getServerHost()).thenReturn(host);
        when(properties.getServerPort()).thenReturn(port);
        when(properties.isCacheEnabled()).thenReturn(false);

        gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();

        connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        when(gearmanClient.addJobServer(connection)).thenReturn(true);
    }

    @BeforeClass
    public void setKeyLength() {
        //set key length to 16 byte in unit tests, so default Java JRE security policy applies
        Aes.keyLength = 16;
    }

    @AfterClass(alwaysRun = true)
    public void restoreKeyLength() {
        //restore default key length
        Aes.keyLength = Aes.DEFAULT_KEY_LENGTH;
    }

    @Test
    public void testSaveAllResults() throws Exception {
        when(checkResultBuilder.build()).thenReturn(new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        Future future = mock(Future.class);
        when(gearmanClient.submit(job)).thenReturn(future);
        GearmanJobResult jobResult = mock(GearmanJobResult.class);
        when(future.get()).thenReturn(jobResult);
        when(jobResult.jobSucceeded()).thenReturn(true);
        GearmanJobStatus jobStatus = mock(GearmanJobStatus.class);
        when(jobStatus.isRunning()).thenReturn(false);
        when(gearmanClient.getJobStatus(job)).thenReturn(jobStatus);

        Date stopDate = new GregorianCalendar(2014, 14, 7, 13, 0).getTime();

        TestSuite testSuite = new TestSuiteExampleBuilder()
                .withHost("win7sakuli")
                .withId("sakuli_demo22")
                .withStopDate(stopDate)
                .withStartDate(DateUtils.addSeconds(stopDate, -60))
                .buildExample();

        ReflectionTestUtils.setField(testling, "testSuite", testSuite);

        testling.saveAllResults();

        //checks
        verify(gearmanCacheService, never()).cacheResults(anyList());
        verify(gearmanCacheService, never()).getCachedResults();
        verify(exceptionHandler, never()).handleException(any(Throwable.class));
        verify(exceptionHandler, never()).handleException(any(Throwable.class), anyBoolean());
        verify(testling).getGearmanClient();
        verify(testling).getGearmanConnection(host, port);
        verify(gearmanClient).addJobServer(connection);
        verify(gearmanClient).submit(job);
        verify(future).get();
        verify(gearmanClient).shutdown();
        ArgumentCaptor<NagiosCheckResult> checkresult = ArgumentCaptor.forClass(NagiosCheckResult.class);
        verify(testling).creatJob(checkresult.capture());
        assertEquals(checkresult.getValue().getQueueName(), queueName);
        assertEquals(checkresult.getValue().getUuid(), testSuite.getGuid());
        assertEquals(checkresult.getValue().getPayload(), testResult);
    }

    @Test
    public void testCreateJobEncryptionDisabled() throws Exception {
        GearmanJob gearmanJob = testling.creatJob(new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));
        assertEquals(new String(Base64.decodeBase64(gearmanJob.getData())), testResult);
    }

    @Test
    public void testCreateJobEncryptionEnabled() throws Exception {
        when(properties.isEncryption()).thenReturn(true);
        final String secretKey = "abcdefghijklmnopqrstuvwxyz";
        when(properties.getSecretKey()).thenReturn(secretKey);

        GearmanJob gearmanJob = testling.creatJob(new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));
        assertEquals(Aes.decrypt(gearmanJob.getData(), secretKey), testResult);
    }

    @Test
    public void testSaveAllResultsConnectionFailed() throws Exception {
        when(gearmanClient.addJobServer(connection)).thenReturn(false);

        when(checkResultBuilder.build()).thenReturn(new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        testling.saveAllResults();

        //checks
        verify(gearmanCacheService, never()).cacheResults(anyList());
        verify(gearmanCacheService, never()).getCachedResults();
        verify(exceptionHandler).handleException(any(Throwable.class), eq(true));
        verify(testling).getGearmanClient();
        verify(testling).getGearmanConnection(host, port);
        verify(gearmanClient).addJobServer(connection);
        verify(gearmanClient).shutdown();
    }

    @Test
    public void testSaveAllResultsConnectionFailedCacheResults() throws Exception {
        when(properties.isCacheEnabled()).thenReturn(true);

        when(checkResultBuilder.build()).thenReturn(new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        when(gearmanClient.addJobServer(connection)).thenReturn(false);

        when(gearmanCacheService.getCachedResults()).thenReturn(Collections.emptyList());

        doAnswer(invocationOnMock -> {
            assertEquals(((List) invocationOnMock.getArguments()[0]).size(), 1L);
            return null;
        }).when(gearmanCacheService).cacheResults(anyList());

        testling.saveAllResults();

        //checks
        verify(gearmanCacheService).cacheResults(anyList());
        verify(gearmanCacheService).getCachedResults();
        verify(exceptionHandler).handleException(any(Throwable.class), eq(true));
        verify(testling).getGearmanClient();
        verify(testling).getGearmanConnection(host, port);
        verify(gearmanClient).addJobServer(connection);
        verify(gearmanClient).shutdown();
    }

    @Test
    public void testSaveAllResultsJobSubmitFailedAddCacheResults() throws Exception {
        when(properties.isCacheEnabled()).thenReturn(true);

        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        when(gearmanClient.submit(job)).thenThrow(new SakuliRuntimeException("Something went wrong!"));

        NagiosCheckResult mockedResult1 = Mockito.mock(NagiosCheckResult.class);
        NagiosCheckResult mockedResult2 = Mockito.mock(NagiosCheckResult.class);
        when(gearmanCacheService.getCachedResults()).thenReturn(Arrays.asList(mockedResult1, mockedResult2));

        NagiosCheckResult newResult = new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult);
        when(checkResultBuilder.build()).thenReturn(newResult);

        doAnswer(invocationOnMock -> {
            List<NagiosCheckResult> results = ((List) invocationOnMock.getArguments()[0]);
            assertEquals(results.size(), 3L);

            assertEquals(results.get(0), newResult);
            assertEquals(results.get(1), mockedResult1);
            assertEquals(results.get(2), mockedResult2);
            return null;
        }).when(gearmanCacheService).cacheResults(anyList());
        StringBuilder sendOrder = new StringBuilder();
        doAnswer(invocationOnMock -> {
            Object result = invocationOnMock.getArguments()[1];
            Assert.assertTrue(result instanceof NagiosCheckResult);
            sendOrder.append(result.hashCode());
            return invocationOnMock.callRealMethod();

        }).when(testling).sendResult(any(), any());

        testling.saveAllResults();

        //checks
        assertEquals(sendOrder.toString(), "" + mockedResult2.hashCode() + mockedResult1.hashCode() + newResult.hashCode());
        verify(gearmanCacheService).cacheResults(anyList());
        verify(gearmanCacheService).getCachedResults();
        verify(exceptionHandler, times(3)).handleException(any(Throwable.class), eq(true));
        verify(testling).getGearmanClient();
        verify(testling).getGearmanConnection(host, port);
        verify(gearmanClient).addJobServer(connection);
        verify(gearmanClient).shutdown();
    }

    @Test
    public void testSaveAllResultsConnectionFailedAddCacheResults() throws Exception {
        when(properties.isCacheEnabled()).thenReturn(true);

        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        when(gearmanClient.addJobServer(connection)).thenReturn(false);

        NagiosCheckResult mockedResult1 = Mockito.mock(NagiosCheckResult.class);
        when(gearmanCacheService.getCachedResults()).thenReturn(Collections.singletonList(mockedResult1));

        NagiosCheckResult newResult = new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult);
        when(checkResultBuilder.build()).thenReturn(newResult);

        doAnswer(invocationOnMock -> {
            List<NagiosCheckResult> results = ((List) invocationOnMock.getArguments()[0]);
            assertEquals(results.size(), 2L);

            assertEquals(results.get(0), newResult);
            assertEquals(results.get(1), mockedResult1);
            return null;
        }).when(gearmanCacheService).cacheResults(anyList());

        testling.saveAllResults();

        //checks
        verify(gearmanCacheService).cacheResults(anyList());
        verify(gearmanCacheService).getCachedResults();
        verify(exceptionHandler).handleException(any(Throwable.class), eq(true));
        verify(testling).getGearmanClient();
        verify(testling).getGearmanConnection(host, port);
        verify(gearmanClient).addJobServer(connection);
        verify(gearmanClient).shutdown();
    }

    @Test
    public void testSaveAllResultsWrongConnectionCacheResults() throws Exception {
        final String host = "not-resolveable-host.de";
        when(properties.getServerHost()).thenReturn(host);
        when(properties.isCacheEnabled()).thenReturn(true);

        when(checkResultBuilder.build()).thenReturn(new NagiosCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        doReturn(connection).when(testling).getGearmanConnection(host, port);
        when(gearmanClient.addJobServer(connection)).thenThrow(new UnresolvedAddressException());

        when(gearmanCacheService.getCachedResults()).thenReturn(Collections.emptyList());

        doAnswer(invocationOnMock -> {
            assertEquals(((List) invocationOnMock.getArguments()[0]).size(), 1L);
            return null;
        }).when(gearmanCacheService).cacheResults(anyList());

        doAnswer(invocationOnMock -> {
            Exception exception = (Exception) invocationOnMock.getArguments()[0];
            assertRegExMatch(exception.getMessage(),
                    "Could not transfer Sakuli results to the Gearman server.*");
            assertEquals(exception.getSuppressed()[0].getClass(), UnresolvedAddressException.class);
            return null;
        }).when(exceptionHandler).handleException(any(Throwable.class), anyBoolean());

        testling.saveAllResults();

        //checks
        verify(gearmanCacheService).cacheResults(anyList());
        verify(gearmanCacheService).getCachedResults();
        verify(exceptionHandler).handleException(any(SakuliForwarderException.class), anyBoolean());
        verify(testling).getGearmanClient();
        verify(testling).getGearmanConnection(host, port);
        verify(gearmanClient).addJobServer(connection);
        verify(gearmanClient).shutdown();
    }
}
