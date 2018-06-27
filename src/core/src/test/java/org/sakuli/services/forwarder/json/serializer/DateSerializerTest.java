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
import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 05/10/17.
 */
public class DateSerializerTest {

    private JsonSerializationContext jsonSerializationContext;
    private DateSerializer testling = new DateSerializer();

    @BeforeMethod
    public void setUp() throws Exception {
        jsonSerializationContext = mock(JsonSerializationContext.class);
        doReturn(new JsonPrimitive("2017-10-01T00:00:00.000+02:00")).when(jsonSerializationContext).serialize(any(), eq(DateTime.class));
    }

    @DataProvider
    public Object[][] serializeDP() {
        return new Object[][]{
                {null, "\"\""},
                {new GregorianCalendar(2017, 10, 1).getTime(), "\"2017-10-01T00:00:00.000+02:00\""},
        };
    }

    @Test(dataProvider = "serializeDP")
    public void serialize(Date date, String expectedOutput) {
        assertEquals(testling.serialize(date, Date.class, jsonSerializationContext).toString(), expectedOutput);
        verify(jsonSerializationContext, times(date != null ? 1 : 0)).serialize(any(), eq(DateTime.class));
    }

}
