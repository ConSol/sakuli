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

package org.sakuli.services.forwarder.gearman;

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
    ID("{{id}}"),
    NAME("{{NAME}}"),
    DURATION("{{duration}}"),
    START_DATE("{{start_date}}"),
    STOP_DATE("{{stop_date}}"),
    ERROR_MESSAGE("{{error_message}}"),
    ERROR_SCREENSHOT("{{error_screenshot}}"),
    WARN_THRESHOLD("{{warning_threshold}}"),
    CRITICAL_THRESHOLD("{{critical_threshold}}"),
    //test suite specific placeholder
    SUITE_SUMMARY("{{suite_summary}}"),
    SUITE_FOLDER("{{suite_folder}}"),
    BROWSER_INFO("{{browser_info}}"),
    HOST("{{host}}"),
    //test case specific placeholder
    CASE_FILE("{{case_file}}"),
    CASE_START_URL("{{case_start_URL}}"),
    CASE_LAST_URL("{{case_last_URL}}"),
    STEP_INFORMATION("{{step_information}}"),
    //just for internal usage
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
