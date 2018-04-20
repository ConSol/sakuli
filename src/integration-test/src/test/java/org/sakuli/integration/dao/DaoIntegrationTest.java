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

package org.sakuli.integration.dao;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.LogResult;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.integration.IntegrationTest;
import org.sakuli.integration.builder.TestSuiteBuilder;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.forwarder.database.dao.impl.Dao;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.sakuli.integration.IntegrationTest.GROUP;

/**
 * @author tschneck Date: 22.07.13
 */
@Test(groups = GROUP)
public abstract class DaoIntegrationTest<D extends Dao> implements IntegrationTest {
    public static final String TEST_CONTEXT_PATH = "db-beanRefFactory.xml";
    @InjectMocks
    protected D testling;

    protected DataSource dataSource;

    @Mock
    protected TestSuite testSuiteMock;
    @Mock
    protected SakuliExceptionHandler sakuliExceptionHandlerMock;
    @Mock
    private SakuliProperties sakuliProperties;

    protected abstract D createTestling() throws SakuliCheckedException;

    @BeforeClass
    public void initTestFolder() {
        System.out.println(">>>>>>>>>>>>>>>>>> INITALIZE DAO INTEGRATION TEST!");
        BeanLoader.CONTEXT_PATH = TEST_CONTEXT_PATH;
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = getTestSuiteFolder();
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = SAKULI_MAIN_FOLDER_PATH;
        dataSource = BeanLoader.loadBean(DataSource.class);
    }

    private String getTestSuiteFolder() {
        URL test_suites = this.getClass().getResource(".");
        try {
            return Paths.get(test_suites.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Can't load test_suites folder:", e);
        }
    }

    @BeforeMethod
    public void init() throws SakuliCheckedException {
        testling = createTestling();
        MockitoAnnotations.initMocks(this);
        testling.setDataSource(dataSource);
        initExceptionHandlerMock(sakuliExceptionHandlerMock);
    }

    protected void initExceptionHandlerMock(SakuliExceptionHandler sakuliExceptionHandlerMock) {
        doAnswer(invocation -> {
            throw (Throwable) invocation.getArguments()[0];
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
