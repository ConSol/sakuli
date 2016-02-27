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

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * POJO for the response
 *
 * @author tschneck
 *         Date: 2/23/16
 */
public class Icinga2Result {

    private List<Map<String, String>> results;

    public List<Map<String, String>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, String>> results) {
        this.results = results;
    }

    public Optional<String> getStatusOfFirstElement() {
        if (results != null) {
            return results.stream().filter(Objects::nonNull).findFirst().map(e -> e.get("status"));
        }
        return Optional.empty();
    }

    public Optional<Integer> getCodeOfFirstElement() {
        if (results != null) {
            return results.stream()
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(e -> e.get("code"))
                    .map(s -> Integer.valueOf(StringUtils.substringBefore(s, ".")));
        }
        return Optional.empty();
    }

    public boolean isSuccess() {
        return getCodeOfFirstElement().filter(c -> c == 200).isPresent()
                && getStatusOfFirstElement().filter(s -> s.toLowerCase().startsWith("successful")).isPresent();
    }

    public String getFirstElementAsString() {
        return String.format("[%s] - %s",
                getCodeOfFirstElement().isPresent() ? getCodeOfFirstElement().get() : "NULL",
                getStatusOfFirstElement().isPresent() ? getStatusOfFirstElement().get() : "EMPTY MESSAGE");
    }
}
