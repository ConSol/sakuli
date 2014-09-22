/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.datamodel.properties;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author tschneck
 *         Date: 15.05.14
 */
public abstract class AbstractProperties {

    /**
     * checks all assigned folders, if they are existing
     *
     * @param folders {@link Path} array or vararg
     * @throws FileNotFoundException
     */
    protected void checkFolders(Path... folders) throws FileNotFoundException {
        if (folders != null) {
            for (Path folder : folders) {
                if (!Files.exists(folder)) {
                    throw new FileNotFoundException("necessary Folder '" + folder.toString() + "' does not exists!");
                }
            }
        }
    }

    /**
     * checks all assigned files, if they are existing
     *
     * @param files {@link Path} array or vararg
     * @throws FileNotFoundException
     */
    protected void checkFiles(Path... files) throws FileNotFoundException {
        if (files != null) {
            for (Path folder : files) {
                if (!Files.exists(folder)) {
                    throw new FileNotFoundException("necessary File '" + folder.toString() + "' does not exists!");
                }
            }
        }
    }
}
