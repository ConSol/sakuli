/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.forwarder.icinga2.model;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 2/23/16
 */
public class Icinga2ResultTest {

    @Test
    public void testGetStatusOfFirstElement() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        String msg = "my status";
        map.put("status", msg);
        result.setResults(Collections.singletonList(map));
        assertEquals(result.getStatusOfFirstElement().get(), msg);

        result.setResults(null);
        assertFalse(result.getStatusOfFirstElement().isPresent());

        map.put("status", null);
        result.setResults(Collections.singletonList(map));
        assertFalse(result.getStatusOfFirstElement().isPresent());

        result.setResults(Collections.singletonList(new HashMap<>()));
        assertFalse(result.getStatusOfFirstElement().isPresent());

        assertEquals(result.getFirstElementAsString(), "[NULL] - EMPTY MESSAGE");
    }

    @Test
    public void testGetCodeOfFirstElement() throws Exception {
        Icinga2Result result = new Icinga2Result();
        Map<String, String> map = new HashMap<>();
        map.put("code", "200.00");
        result.setResults(Collections.singletonList(map));
        assertEquals(result.getCodeOfFirstElement().get().intValue(), 200);

        result.setResults(null);
        assertFalse(result.getCodeOfFirstElement().isPresent());

        map.put("code", null);
        result.setResults(Collections.singletonList(map));
        assertFalse(result.getCodeOfFirstElement().isPresent());

        assertEquals(result.getFirstElementAsString(), "[NULL] - EMPTY MESSAGE");

        result.setResults(Collections.singletonList(new HashMap<>()));
        assertFalse(result.getCodeOfFirstElement().isPresent());

    }

    @Test
    public void testIsSuccess() throws Exception {
        Icinga2Result result = new Icinga2Result();
        result.setResults(Collections.singletonList(null));
        assertFalse(result.isSuccess());
        result.setResults(Collections.singletonList(new HashMap<>()));
        assertFalse(result.isSuccess());

        Map<String, String> map = new HashMap<>();
        map.put("code", null);
        map.put("status", null);
        result.setResults(Collections.singletonList(map));
        assertFalse(result.isSuccess());

        map.put("code", "200.00");
        assertFalse(result.isSuccess());

        map.put("status", "unsuccessful");
        assertFalse(result.isSuccess());

        map.put("status", "Successfully processed");
        assertTrue(result.isSuccess());

        assertEquals(result.getFirstElementAsString(), "[200] - Successfully processed");
    }

}