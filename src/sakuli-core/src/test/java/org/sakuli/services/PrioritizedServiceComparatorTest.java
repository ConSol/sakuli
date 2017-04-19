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

package org.sakuli.services;

import org.testng.annotations.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.testng.Assert.assertEquals;

public class PrioritizedServiceComparatorTest {

    @Test
    public void testCompare() throws Exception {
        PrioritizedService s1 = () -> 10;
        PrioritizedService s2 = () -> 20;
        PrioritizedService s3 = () -> 30;

        SortedSet<PrioritizedService> set = new TreeSet<>(new PrioritizedServiceComparator<>());
        set.add(s2);
        set.add(s3);
        set.add(s1);

        assertEquals(set.size(), 3);
        assertEquals(set.first(), s3);
        assertEquals(set.last(), s1);
    }

    @Test
    public void testCompareSamePriority() throws Exception {
        PrioritizedService s1 = () -> 10;
        PrioritizedService s2 = () -> 20;
        PrioritizedService s3 = () -> 10;

        SortedSet<PrioritizedService> set = new TreeSet<>(new PrioritizedServiceComparator<>());
        set.add(s2);
        set.add(s3);
        set.add(s1);

        assertEquals(set.size(), 3);
        assertEquals(set.first().getServicePriority(), 20);
        assertEquals(set.first(), s2);
        assertEquals(set.last().getServicePriority(), 10);
    }
}