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

package de.consol.sakuli.integration.dao;

import de.consol.sakuli.actions.screenbased.RegionImpl;
import de.consol.sakuli.dao.impl.Dao;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.actions.LogResult;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.integration.IntegrationTest;
import de.consol.sakuli.integration.builder.TestSuiteBuilder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author tschneck Date: 22.07.13
 */
@Test(groups = IntegrationTest.GROUP)
@ContextConfiguration(locations = {"classpath*:db-applicationContext.xml"})
public abstract class DaoIntegrationTest<D extends Dao> extends AbstractTestNGSpringContextTests {

    @InjectMocks
    protected D testling;

    @Autowired
    protected DataSource dataSource;

    @Mock
    protected TestSuite testSuiteMock;
    @Mock
    protected SakuliExceptionHandler sakuliExceptionHandlerMock;

    protected abstract D createTestling() throws SakuliException;

    @BeforeMethod
    public void init() throws SakuliException {
        testling = createTestling();
        MockitoAnnotations.initMocks(this);
        testling.setDataSource(dataSource);
        initExceptionHandlerMock(sakuliExceptionHandlerMock);
    }

    protected void initExceptionHandlerMock(SakuliExceptionHandler sakuliExceptionHandlerMock) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw (Throwable) invocation.getArguments()[0];
            }
        }).when(sakuliExceptionHandlerMock).handleException(any(Throwable.class));
        doCallRealMethod().when(sakuliExceptionHandlerMock).handleException(any(Throwable.class), anyBoolean());
        doCallRealMethod().when(sakuliExceptionHandlerMock).handleException(any(LogResult.class));
        doCallRealMethod().when(sakuliExceptionHandlerMock).handleException(anyString(), anyBoolean());
        doCallRealMethod().when(sakuliExceptionHandlerMock).handleException(anyString(), any(RegionImpl.class), anyBoolean());
        doCallRealMethod().when(sakuliExceptionHandlerMock).handleException(any(Throwable.class), any(RegionImpl.class), anyBoolean());
    }

    protected void initDeafultTestSuiteMock() {
        TestSuite sample = TestSuiteBuilder.createEmptyTestSuite();
        when(testSuiteMock.getName()).thenReturn(sample.getName());
        when(testSuiteMock.getId()).thenReturn(sample.getId());
        when(testSuiteMock.getGuid()).thenReturn(sample.getGuid());
        when(testSuiteMock.getStartDate()).thenReturn(sample.getStartDate());
        when(testSuiteMock.getWarningTime()).thenReturn(sample.getWarningTime());
        when(testSuiteMock.getCriticalTime()).thenReturn(sample.getCriticalTime());
        when(testSuiteMock.getState()).thenReturn(sample.getState());
        when(testSuiteMock.getBrowserInfo()).thenReturn(sample.getBrowserInfo());
        when(testSuiteMock.getTestCases()).thenReturn(sample.getTestCases());
    }

}
