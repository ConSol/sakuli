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

package org.sakuli.datamodel;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author tschneck Date: 19.07.13
 */
public class AbstractTestDataEntityTest {
    private AbstractTestDataEntity testling;

    @BeforeMethod
    public void init() throws Exception {
        testling = new AbstractTestDataEntity() {
            @Override
            public void refreshState() {
            }

            @Override
            public String getResultString() {
                return null;
            }
        };
    }

    @Test
    public void testCreateDateTimeString() throws Exception {
        Date aspectedDate = new Date();
        long aspectedLong = aspectedDate.getTime();
        Assert.assertEquals("-1", testling.createUnixTimestamp(null));
        String result = testling.createUnixTimestamp(aspectedDate);
        Assert.assertEquals(aspectedLong, Long.parseLong(result.replace(".", "")));
        Assert.assertTrue(result.charAt(result.length() - 4) == '.');
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testAddException() throws Exception {
        testling.addException(new Exception("test"));
        Assert.assertNotNull(testling.getException());
        Assert.assertTrue(testling.getExceptionMessages().contains("test"));

        testling.addException(new Exception("SuppressedTest"));
        Assert.assertNotNull(testling.getException().getSuppressed());
        Assert.assertTrue(testling.getExceptionMessages().contains("SuppressedTest"));
    }
}
