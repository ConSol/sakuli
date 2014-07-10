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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck Date: 09.05.14
 */
@Component
public class ReceiverProperties extends AbstractProperties {

    public static final String DATABASE_ENABLED = "sakuli.receiver.database.enabled";
    protected static final boolean DATABASE_ENABLED_DEFAULT = false;
    public static final String GEARMAN_ENABLED = "sakuli.receiver.gearman.enabled";
    protected static final boolean GEARMAN_ENABLED_DEFAULT = false;

    @Value("${" + DATABASE_ENABLED + ":" + DATABASE_ENABLED_DEFAULT + "}")
    private boolean databaseReceiverEnabled;
    @Value("${" + GEARMAN_ENABLED + ":" + GEARMAN_ENABLED_DEFAULT + "}")
    private boolean gearmanReceiverEnabled;

    public boolean isDatabaseReceiverEnabled() {
        return databaseReceiverEnabled;
    }

    public void setDatabaseReceiverEnabled(boolean databaseReceiverEnabled) {
        this.databaseReceiverEnabled = databaseReceiverEnabled;
    }

    public boolean isGearmanReceiverEnabled() {
        return gearmanReceiverEnabled;
    }

    public void setGearmanReceiverEnabled(boolean gearmanReceiverEnabled) {
        this.gearmanReceiverEnabled = gearmanReceiverEnabled;
    }
}
