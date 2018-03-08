/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.services.forwarder.configuration;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNull;

@SuppressWarnings("ObviousNullCheck")
public class ExtractScreenshotFunctionTest {

    @Mock
    private ScreenshotDivConverter converter;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() throws Exception {
        assertNull(new ExtractScreenshotFunction(converter).execute(Collections.singletonList(null)));
        verify(converter, never()).convert(any());
    }

    @Test
    public void testExecuteNoExeception() throws Exception {
        AbstractTestDataEntity entity = mock(AbstractTestDataEntity.class);
        when(entity.getException()).thenReturn(null);
        assertNull(new ExtractScreenshotFunction(converter).execute(Collections.singletonList(entity)));
        verify(converter).convert(any());
    }
}