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

package org.sakuli.datamodel;


import org.joda.time.DateTime;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.builder.TestCaseStepBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

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

    @Test
    public void testCompareTo() throws Exception {
        TestCaseStep second = new TestCaseStepBuilder("second")
                .withCreationDate(new DateTime())
                .build();
        TestCaseStep third = new TestCaseStepBuilder("third")
                .withCreationDate(new DateTime().plusMillis(1))    //ensure that the creation time is not equal
                .build();
        List<TestCaseStep> values = Arrays.asList(second, third, new TestCaseStepExampleBuilder().withName("first").buildExample());

        //first should be the step with startdate, after that the init steps will follow after the creation date
        TreeSet<TestCaseStep> sorted = new TreeSet<>(values);
        print(sorted);
        Assert.assertEquals(sorted.size(), 3);
        Assert.assertEquals(sorted.first().getName(), "first");
        Assert.assertEquals(sorted.last().getName(), "third");
    }

    protected void print(TreeSet<TestCaseStep> sorted) {
        for (TestCaseStep s : sorted) {
            System.out.println(s.getId());
        }
    }

    @Test
    public void testCompareToMixedSteps() throws Exception {
        TestCaseStep third = new TestCaseStepBuilder("third")
                .withCreationDate(new DateTime())
                .build();
        TestCaseStep fourth = new TestCaseStepBuilder("fourth")
                .withCreationDate(new DateTime().plusMillis(1))    //ensure that the creation time is not equal
                .build();
        List<TestCaseStep> values = Arrays.asList(new TestCaseStepExampleBuilder()
                        .withStartDate(new DateTime().plusMinutes(5).toDate())
                        .withName("second")
                        .buildExample(),
                third, fourth,
                new TestCaseStepExampleBuilder().withName("first").buildExample()
        );

        //first should be the step with startdate, after that the init steps will follow after the creation date
        TreeSet<TestCaseStep> sorted = new TreeSet<>(values);
        print(sorted);
        Assert.assertEquals(sorted.size(), 4);
        Iterator<TestCaseStep> it = sorted.iterator();
        Assert.assertEquals(it.next().getName(), "first");
        Assert.assertEquals(it.next().getName(), "second");
        Assert.assertEquals(it.next().getName(), "third");
        Assert.assertEquals(it.next().getName(), "fourth");
    }
}
