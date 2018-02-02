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

import org.joda.time.DateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 29/09/17.
 */
public class DateTimeSerializerTest {

    DateTimeSerializer testling = new DateTimeSerializer();

    @DataProvider
    public Object[][] serializeDP() {
        return new Object[][]{
                {null, "\"\""},
                {new DateTime(2017, 9, 29, 11, 15, 25, 123), "\"2017-09-29T11:15:25.123+02:00\""},
                {new DateTime(2015, 7, 26, 12, 35, 55, 999), "\"2015-07-26T12:35:55.999+02:00\""},
                {new DateTime(2015, 7, 26, 0, 0), "\"2015-07-26T00:00:00.000+02:00\""},
        };
    }

    @Test(dataProvider = "serializeDP")
    public void serialize(DateTime dateTime, String expectedDateTimeAsJson) {
        assertEquals(testling.serialize(dateTime, DateTime.class, null).toString(), expectedDateTimeAsJson);
    }

}
