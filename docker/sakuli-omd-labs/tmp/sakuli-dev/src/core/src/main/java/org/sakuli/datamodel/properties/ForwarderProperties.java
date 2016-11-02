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

package org.sakuli.datamodel.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck Date: 09.05.14
 */
@Component
public class ForwarderProperties extends AbstractProperties {

    public static final String DATABASE_ENABLED = "sakuli.forwarder.database.enabled";
    public static final String GEARMAN_ENABLED = "sakuli.forwarder.gearman.enabled";
    public static final String ICINGA_ENABLED = "sakuli.forwarder.icinga2.enabled";
    protected static final boolean DATABASE_ENABLED_DEFAULT = false;
    protected static final boolean GEARMAN_ENABLED_DEFAULT = false;
    protected static final boolean ICINGA_ENABLED_DEFAULT = false;

    @Value("${" + DATABASE_ENABLED + ":" + DATABASE_ENABLED_DEFAULT + "}")
    private boolean databaseEnabled;
    @Value("${" + GEARMAN_ENABLED + ":" + GEARMAN_ENABLED_DEFAULT + "}")
    private boolean gearmanEnabled;
    @Value("${" + ICINGA_ENABLED + ":" + ICINGA_ENABLED_DEFAULT + "}")
    private boolean icinga2Enabled;

    public boolean isDatabaseEnabled() {
        return databaseEnabled;
    }

    public void setDatabaseEnabled(boolean databaseEnabled) {
        this.databaseEnabled = databaseEnabled;
    }

    public boolean isGearmanEnabled() {
        return gearmanEnabled;
    }

    public void setGearmanEnabled(boolean gearmanEnabled) {
        this.gearmanEnabled = gearmanEnabled;
    }

    public boolean isIcinga2Enabled() {
        return icinga2Enabled;
    }

    public void setIcinga2Enabled(boolean icinga2Enabled) {
        this.icinga2Enabled = icinga2Enabled;
    }
}
