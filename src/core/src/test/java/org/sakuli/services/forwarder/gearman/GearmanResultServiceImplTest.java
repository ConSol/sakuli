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

import org.apache.commons.lang.time.DateUtils;
import org.gearman.client.GearmanClient;
import org.gearman.client.GearmanClientImpl;
import org.gearman.client.GearmanJob;
import org.gearman.client.GearmanJobResult;
import org.gearman.common.GearmanJobServerConnection;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliActionException;
import org.sakuli.services.forwarder.gearman.model.NagiosCheckResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;

public class GearmanResultServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private GearmanResultServiceImpl testling;
    @Mock
    private GearmanProperties properties;

//TODO change this to set up the context and test if all works
//    @BeforeMethod
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        properties = GearmanPropertiesTestHelper.initMock(properties);
//    }

    @Test(enabled = false)
    public void testSaveAllResults() throws Exception {
        when(properties.getServiceType()).thenReturn("passive");
        final String queueName = "check_results";
        when(properties.getServerQueue()).thenReturn(queueName);
        final String host = "99.99.99.20";
        when(properties.getServerHost()).thenReturn(host);
        final int port = 4730;
        when(properties.getServerPort()).thenReturn(port);
        when(properties.getNagiosHost()).thenReturn("win7sakuli");

        GearmanClient gearmanClient = mock(GearmanClientImpl.class);
        doReturn(gearmanClient).when(testling).getGearmanClient();
        GearmanJobServerConnection connection = mock(GearmanJobServerConnection.class);
        doReturn(connection).when(testling).getGearmanConnection(host, port);
        GearmanJob job = mock(GearmanJob.class);
        doReturn(job).when(testling).creatJob(any(NagiosCheckResult.class));
        Future future = mock(Future.class);
        when(gearmanClient.submit(job)).thenReturn(future);
        GearmanJobResult jobResult = mock(GearmanJobResult.class);
        when(future.get()).thenReturn(jobResult);
        when(jobResult.jobSucceeded()).thenReturn(true);

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
        Assert.assertEquals(checkresult.getValue().getPayloadString(), "" +
                "type=passive\n" +
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
                "[check_sakuli_db_suite]");
    }
}