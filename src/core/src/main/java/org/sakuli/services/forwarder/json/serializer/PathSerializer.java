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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.nio.file.Path;

/**
 * Created by georgi on 27/09/17.
 */
public final class PathSerializer implements JsonSerializer<Path> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PathSerializer.class);
    private final Path basePath;

    public PathSerializer(Path basePath) {
        this.basePath = basePath.toAbsolutePath().normalize();
        LOGGER.debug("PathSerializer base path: {}", this.basePath);
    }

    @Override
    public JsonElement serialize(final Path src, final Type typeOfSrc, final JsonSerializationContext context) {
        if (src == null) {
            return new JsonPrimitive(StringUtils.EMPTY);
        }
        final Path normalized = src.toAbsolutePath().normalize();
        Path relativePath = basePath.relativize(normalized);
        LOGGER.debug("Relativized Path: '{}' => '{}'", normalized, relativePath);
        return new JsonPrimitive(relativePath.toString());
    }
}
