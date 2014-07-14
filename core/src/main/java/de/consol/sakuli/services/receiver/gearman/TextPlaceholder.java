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

package de.consol.sakuli.services.receiver.gearman;

/**
 * Represents all possible text placeholder in gearman result strings.
 *
 * @author tschneck
 *         Date: 14.07.14
 */
public enum TextPlaceholder {
    STATE("{{state}}"),
    STATE_SHORT("{{state_short}}"),
    STATE_DESC("{{state_description}}"),
    STATE_SUMMARY("{{state_summary}}"),
    ID("{{id}}"),
    DURATION("{{duration}}"),
    LAST_RUN("{{last_run}}"),
    ERROR_MESSAGE("{{error_message}}"),
    WARN_THRESHOLD("{{warning_threshold}}"),
    CRITICAL_THRESHOLD("{{critical_threshold}}"),
    STEP_INFORMATION("{{step_information}}"),
    TD_CSS_CLASS("{{TD_CSS_CLASS}}");

    private final String pattern;

    TextPlaceholder(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return name();
    }
}
