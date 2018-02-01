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

package org.sakuli.datamodel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * Created by georgi on 21/09/17.
 */
public class TestAction {
    private String object;
    private String method;
    private List<String> args;
    private String message;
    private String documentationURL;

    private TestAction(String object, String method, List<String> args, String message, String documentationURL) {
        this.object = object;
        this.method = method;
        this.args = args == null ? Collections.EMPTY_LIST : args;
        this.message = message;
        this.documentationURL = documentationURL;
    }

    public static TestAction createSakuliTestAction(String object, String method, Object[] args, String message, String documentationURL) {
        //resolve object list to strings so no recursive exception will occur on parsing
        final List<String> argList = Stream.of(Optional.ofNullable(args).orElse(new Object[]{}))
                .filter(Objects::nonNull)
                .map(o -> (o instanceof Object[])
                        ? Stream.of((Object[]) o).map(Object::toString).collect(Collectors.joining(","))
                        : o.toString())
                .collect(Collectors.toList());
        return new TestAction(object, method, argList, message,
                createDocumentationURL(documentationURL, object, method));
    }

    public static TestAction createSahiTestAction(String method, String documentationURL) {
        return new TestAction(null, removeEnd(method, ";"), null, "Sahi Action", documentationURL + "_" + substringBetween(method, "_", "("));
    }

    private static String createDocumentationURL(String baseUrl, String clazz, String method) {
        return baseUrl + clazz + "." + method;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getMethod() {
        return method;
    }

    public Object getObject() {
        return object;
    }

    public String getDocumentationURL() {
        return documentationURL;
    }

}
