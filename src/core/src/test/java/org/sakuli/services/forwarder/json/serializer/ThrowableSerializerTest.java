/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.services.forwarder.json.serializer;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Created by georgi on 29/09/17.
 */
public class ThrowableSerializerTest {

    private JsonSerializationContext jsonSerializationContext;
    private ThrowableSerializer testling = new ThrowableSerializer();

    @BeforeMethod
    public void setUp() throws Exception {
        jsonSerializationContext = mock(JsonSerializationContext.class);
        doReturn(new JsonPrimitive("pathToScreenshot")).when(jsonSerializationContext).serialize(any(), eq(Path.class));
    }

    @DataProvider
    public Object[][] serializeDP() {
        return new Object[][]{
                {null, "\"\"", false},
                {new Exception("This is an exception!", new RuntimeException("This is the cause!")), "\\{\"stackTrace\":.*This is the cause!.*\"detailMessage\":\"This is an exception!\"\\}", false},
                {new SakuliExceptionWithScreenshot(new NullPointerException("This is a null pointer!"), Paths.get("pathToScreenshot")), "\\{\"stackTrace\":.*This is a null pointer!.*NullPointerException.*\"detailMessage\":\"This is a null pointer!\",\"screenshot\":\".*pathToScreenshot\"\\}", true},
        };
    }

    @Test(dataProvider = "serializeDP")
    public void serialize(Throwable throwable, String regexForExpectedOutput, boolean exceptionWithScreenshot) {
        assertTrue(testling.serialize(throwable, Throwable.class, jsonSerializationContext).toString().matches(regexForExpectedOutput));
        verify(jsonSerializationContext, times(exceptionWithScreenshot ? 1 : 0)).serialize(any(), eq(Path.class));
    }

}
