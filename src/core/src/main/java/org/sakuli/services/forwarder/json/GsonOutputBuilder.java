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

package org.sakuli.services.forwarder.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.AbstractOutputBuilder;
import org.sakuli.services.forwarder.json.serializer.DateTimeSerializer;
import org.sakuli.services.forwarder.json.serializer.PathSerializer;
import org.sakuli.services.forwarder.json.serializer.ThrowableSerializer;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by georgi on 27/09/17.
 */
@ProfileJson
@Component
public class GsonOutputBuilder extends AbstractOutputBuilder {

    /**
     * Converts the current test suite object to a json string.
     *
     * @return
     */
    public String createOutput() throws SakuliForwarderException {
        try {
            Gson gsonBuilder = new GsonBuilder()
                    .setExclusionStrategies(new GsonExclusionStrategy())
                    .registerTypeAdapter(DateTime.class, new DateTimeSerializer())
                    .registerTypeAdapter(Path.class, new PathSerializer())
                    .registerTypeHierarchyAdapter(Throwable.class, new ThrowableSerializer())
                    .serializeNulls()
                    .create();
            return gsonBuilder.toJson(testSuite);
        } catch (Throwable thr) {
            throw new SakuliForwarderException(thr, "Exception during serializing testSuite into JSON!");
        }
    }

    @Override
    protected int getSummaryMaxLength() {
        // operation is not used for the gson output builder
        return 0;
    }

    @Override
    protected String getOutputScreenshotDivWidth() {
        // operation is not used for the gson output builder
        return null;
    }

}
