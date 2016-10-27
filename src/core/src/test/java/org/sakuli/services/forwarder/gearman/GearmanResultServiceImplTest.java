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
import org.sakuli.builder.*;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.*;
import org.sakuli.services.forwarder.gearman.crypt.Aes;
import org.sakuli.services.forwarder.gearman.model.NagiosCachedCheckResult;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosCheckResultBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;

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

    private String testResult = "type=passive\n" +
            "host_name=win7sakuli\n" +
            "start_time=1425729540.000\n" +
            "finish_time=1425729600.000\n" +
            "return_code=2\n" +
            "service_description=sakuli_demo22\n" +
            "output=CRITICAL - CRITICAL - [CRIT] Sakuli suite \"sakuli_demo22\" ran in 60.00 seconds - EXCEPTION: \" - CASE 'error_case': exception test message\". (Last suite run: 07.03 13:00:00)" +
            "\\\\n<table style=\"border-collapse: collapse;\">" +
            "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] Sakuli suite \"sakuli_demo22\" ran in 60.00 seconds - EXCEPTION: \" - CASE 'error_case': exception test message\". (Last suite run: 07.03 13:00:00)</td></tr>" +
            "<tr valign=\"top\"><td class=\"serviceOK\">[OK] case \"ok_case\" ran in 12.00s - ok</td></tr>" +
            "<tr valign=\"top\"><td class=\"serviceWARNING\">[WARN] case \"warn_case\" over runtime (30.00s /warning at 20s) </td></tr>" +
            "<tr valign=\"top\"><td class=\"serviceWARNING\">[WARN] case \"warn_in_step\" over runtime (30.00s /warning in step at 40s) step \"warn_step_1\" (9.00s /warn at 5s), step \"warn_step_2\" (9.00s /warn at 5s)</td></tr>" +
            "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] case \"crit_case\" over runtime (14.00s /critical at 13s) </td></tr>" +
            "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] case \"error_case\" EXCEPTION: exception test message</td></tr></table>" +
            "|" +
            "suite__state=2;;;; " +
            "suite_sakuli_demo22=60.00s;;;; " +
            "c_1__state_ok_case=0;;;; " +
            "c_1_ok_case=12.00s;13;20;; " +
            "s_1_1_step_for_unit_test=1.00s;2;;; " +
            "c_2__state_warn_case=1;;;; " +
            "c_2_warn_case=30.00s;20;40;; " +
            "s_2_1_step_for_unit_test=1.00s;2;;; " +
            "c_3__state_warn_in_step=1;;;; " +
            "c_3_warn_in_step=30.00s;40;50;; " +
            "s_3_1_warn_step_1=9.00s;5;;; " +
            "s_3_2_warn_step_2=9.00s;5;;; " +
            "c_4__state_crit_case=2;;;; " +
            "c_4_crit_case=14.00s;10;13;; " +
            "s_4_1_step_for_unit_test=1.00s;2;;; " +
            "c_5__state_error_case=2;;;; " +
            "c_5_error_case=-1.00s;4;5;; " +
            "s_5_1_step_for_unit_test=1.00s;2;;; " +
            "[check_sakuli]";

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");

        when(checkResultBuilder.build()).thenReturn(new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        GearmanClient gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();
        GearmanJobServerConnection connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        Future future = mock(Future.class);
        when(gearmanClient.addJobServer(connection)).thenReturn(true);
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
                .withTestCases(Collections.singletonList(
                        new TestCaseExampleBuilder()
                                .withId("ok_case")
                                .withStartDate(DateUtils.addSeconds(stopDate, -12))
                                .withStopDate(stopDate)
                                .withWarningTime(13)
                                .withCriticalTime(20)
                                .buildExample()))
                .buildExample();

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("warn_case")
                .withStartDate(DateUtils.addSeconds(stopDate, -30))
                .withStopDate(stopDate)
                .withWarningTime(20)
                .withCriticalTime(40)
                .buildExample());

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("warn_in_step")
                .withStartDate(DateUtils.addSeconds(stopDate, -30))
                .withStopDate(stopDate)
                .withWarningTime(40)
                .withCriticalTime(50)
                .withTestCaseSteps(Arrays.asList(new TestCaseStepExampleBuilder()
                                .withName("warn_step_1")
                                .withStartDate(DateUtils.addSeconds(stopDate, -29))
                                .withStopDate(DateUtils.addSeconds(stopDate, -20))
                                .withWarningTime(5)
                                .buildExample(),
                        new TestCaseStepExampleBuilder()
                                .withName("warn_step_2")
                                .withStartDate(DateUtils.addSeconds(stopDate, -19))
                                .withStopDate(DateUtils.addSeconds(stopDate, -10))
                                .withWarningTime(5)
                                .buildExample()))
                .buildExample());

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("crit_case")
                .withStartDate(DateUtils.addSeconds(stopDate, -14))
                .withStopDate(stopDate)
                .withWarningTime(10)
                .withCriticalTime(13)
                .buildExample());

        stopDate = DateUtils.addSeconds(stopDate, 300);
        testSuite.addTestCase(new TestCaseExampleBuilder()
                .withId("error_case")
                .withStartDate(DateUtils.addSeconds(stopDate, -14))
                .withException(new SakuliActionException("exception test message"))
                .buildExample());

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
        Assert.assertEquals(checkresult.getValue().getQueueName(), queueName);
        Assert.assertEquals(checkresult.getValue().getUuid(), testSuite.getGuid());
        Assert.assertEquals(checkresult.getValue().getPayloadString(), testResult);
    }

    @Test
    public void testCreateJobEncryptionDisabled() throws Exception {
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");
        when(properties.isEncryption()).thenReturn(false);

        GearmanJob gearmanJob = testling.creatJob(new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));
        Assert.assertEquals(new String(Base64.decodeBase64(gearmanJob.getData())), testResult);
    }

    @Test
    public void testCreateJobEncryptionEnabled() throws Exception {
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");
        when(properties.isEncryption()).thenReturn(true);
        final String secretKey = "abcdefghijklmnopqrstuvwxyz";
        when(properties.getSecretKey()).thenReturn(secretKey);

        GearmanJob gearmanJob = testling.creatJob(new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));
        Assert.assertEquals(Aes.decrypt(gearmanJob.getData(), secretKey), testResult);
    }

    @Test
    public void testSaveAllResultsConnectionFailed() throws Exception {
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");

        when(checkResultBuilder.build()).thenReturn(new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        GearmanClient gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();
        GearmanJobServerConnection connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        when(gearmanClient.addJobServer(connection)).thenReturn(false);

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
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");
        when(properties.isCacheEnabled()).thenReturn(true);

        when(checkResultBuilder.build()).thenReturn(new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult));

        GearmanClient gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();
        GearmanJobServerConnection connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        when(gearmanClient.addJobServer(connection)).thenReturn(false);

        when(gearmanCacheService.getCachedResults()).thenReturn(Collections.emptyList());

        doAnswer(invocationOnMock -> {
            Assert.assertEquals(((List) invocationOnMock.getArguments()[0]).size(), 1L);
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
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");
        when(properties.isCacheEnabled()).thenReturn(true);

        GearmanClient gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();
        GearmanJobServerConnection connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        when(gearmanClient.addJobServer(connection)).thenReturn(true);
        when(gearmanClient.submit(job)).thenThrow(new SakuliRuntimeException("Something went wrong!"));

        NagiosCheckResult mockedResult1 = Mockito.mock(NagiosCheckResult.class);
        NagiosCheckResult mockedResult2 = Mockito.mock(NagiosCheckResult.class);
        when(gearmanCacheService.getCachedResults()).thenReturn(Arrays.asList(mockedResult1, mockedResult2));

        NagiosCheckResult newResult = new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult);
        when(checkResultBuilder.build()).thenReturn(newResult);

        doAnswer(invocationOnMock -> {
            List<NagiosCheckResult> results = ((List) invocationOnMock.getArguments()[0]);
            Assert.assertEquals(results.size(), 3L);

            Assert.assertEquals(results.get(0), newResult);
            Assert.assertEquals(results.get(1), mockedResult1);
            Assert.assertEquals(results.get(2), mockedResult2);
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
        Assert.assertEquals(sendOrder.toString(), "" + mockedResult2.hashCode() + mockedResult1.hashCode() + newResult.hashCode());
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
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");
        when(properties.isCacheEnabled()).thenReturn(true);

        GearmanClient gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();
        GearmanJobServerConnection connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        when(gearmanClient.addJobServer(connection)).thenReturn(false);

        NagiosCheckResult mockedResult1 = Mockito.mock(NagiosCheckResult.class);
        when(gearmanCacheService.getCachedResults()).thenReturn(Collections.singletonList(mockedResult1));

        NagiosCheckResult newResult = new NagiosCachedCheckResult(queueName, "sakuli_demo22__2015_03_07_12_59_00_00", testResult);
        when(checkResultBuilder.build()).thenReturn(newResult);

        doAnswer(invocationOnMock -> {
            List<NagiosCheckResult> results = ((List) invocationOnMock.getArguments()[0]);
            Assert.assertEquals(results.size(), 2L);

            Assert.assertEquals(results.get(0), newResult);
            Assert.assertEquals(results.get(1), mockedResult1);
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
}