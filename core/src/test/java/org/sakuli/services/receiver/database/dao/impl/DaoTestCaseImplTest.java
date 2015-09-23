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

package org.sakuli.services.receiver.database.dao.impl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

/**
 * @author tschneck
 *         Date: 9/23/15
 */
public class DaoTestCaseImplTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private LobHandler lobHandler;
    @Mock
    private LobCreator lobCreator;

    @InjectMocks
    private DaoTestCaseImpl testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(lobHandler.getLobCreator()).thenReturn(lobCreator);
        ReflectionTestUtils.setField(testling, "lobHandler", lobHandler);
    }

    @Test
    public void testGetScreenshotAsSqlLobValue() throws Exception {
        Path screenshotPath = Paths.get(this.getClass().getResource("computer.png").toURI());
        TestCase testCase = new TestCaseExampleBuilder()
                .withException(new SakuliExceptionWithScreenshot("test-exception", screenshotPath))
                .buildExample();

        SqlLobValue result = testling.getScreenshotAsSqlLobValue(testCase);
        assertNotNull(result);
    }

    @Test
    public void testGetScreenshotAsSqlLobValueTestCase() throws Exception {
        Path screenshotPath = Paths.get(this.getClass().getResource("computer.png").toURI());
        TestCase testCase = new TestCaseExampleBuilder()
                .withException(null)
                .withTestCaseSteps(Collections.singletonList(
                        new TestCaseStepExampleBuilder()
                                .withException(new SakuliExceptionWithScreenshot("test-exception", screenshotPath))
                                .buildExample()
                )).buildExample();

        SqlLobValue result = testling.getScreenshotAsSqlLobValue(testCase);
        assertNotNull(result);
    }
}