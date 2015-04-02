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

package org.sakuli.services.receiver.gearman.model;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author tschneck
 *         Date: 10.07.14
 */
public class NagiosCheckResult implements NagiosPayloadString {

    private final String uuid;
    private final String queueName;
    private SortedMap<PayLoadFields, String> payload;

    public NagiosCheckResult(String queueName, String uuid) {
        this.queueName = queueName;
        this.uuid = uuid;
        payload = new TreeMap<>();
    }

    public String getUuid() {
        return uuid;
    }

    public String getQueueName() {
        return queueName;
    }

    public SortedMap<PayLoadFields, String> getPayload() {
        return payload;
    }

    public void setPayload(SortedMap<PayLoadFields, String> payload) {
        this.payload = payload;
    }

    @Override
    public String getPayloadString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<PayLoadFields, String>> entryIterator = payload.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<PayLoadFields, String> entry = entryIterator.next();
            sb.append(entry.getKey().getFieldName())
                    .append("=")
                    .append(entry.getValue());
            if (entryIterator.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
