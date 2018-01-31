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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 29/09/17.
 */
public class PathSerializerTest {

    PathSerializer testling = new PathSerializer();

    @DataProvider
    public Object[][] serializeDP() {
        return new Object[][]{
                {null, ""},
                {Paths.get("testPath"), Paths.get("testPath").toAbsolutePath().normalize().toString()},
                {Paths.get("testPath/sub"), Paths.get("testPath").resolve("sub").toAbsolutePath().normalize().toString()},
                {Paths.get("testPath" + File.separator + "sub"), Paths.get("testPath").resolve("sub").toAbsolutePath().normalize().toString()},
        };
    }

    @Test(dataProvider = "serializeDP")
    public void serialize(Path path, String expectedPathAsJson) {
        assertEquals(testling.serialize(path, Path.class, null).getAsString(), expectedPathAsJson);
    }

}
