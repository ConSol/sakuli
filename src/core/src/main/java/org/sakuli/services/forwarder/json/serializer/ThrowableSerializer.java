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

import com.google.gson.*;
import org.apache.commons.lang.StringUtils;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * Created by georgi on 27/09/17.
 */
public class ThrowableSerializer implements JsonSerializer<Throwable> {

    @Override
    public JsonElement serialize(final Throwable src, final Type typeOfSrc, final JsonSerializationContext context) {

        if (src == null) {
            return new JsonPrimitive(StringUtils.EMPTY);
        }
        JsonObject obj = new JsonObject();
        StringWriter sw = new StringWriter();
        src.printStackTrace(new PrintWriter(sw));
        obj.addProperty("stackTrace", sw.toString());
        obj.addProperty("detailMessage", src.getMessage());
        if (SakuliExceptionWithScreenshot.class.isAssignableFrom(src.getClass())) {
            obj.addProperty("screenshot", ((SakuliExceptionWithScreenshot)src).getScreenshot().toString());
        }

        return obj;
    }

}
