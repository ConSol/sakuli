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

package org.sakuli.services.receiver.gearman.model;

import java.util.Comparator;

/**
 * <p>
 * Represents the single fields in the Nagios check_results payload, see the following example:
 * </p>
 * {@code type=passive}
 * <br>
 * {@code host_name=win7sakuli}
 * <br>
 * {@code start_time=1405004781.939134}
 * <br>
 * {@code finish_time=1405004781.939134}
 * <br>
 * {@code return_code=2}
 * <br>
 * {@code service_description=sakuli_demo}
 * <br>
 * {@code output=OK - [OK] case "demo_win7" (52.95s) ok, [OK] Sakuli suite "sakuli_demo" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)\\n<table style="border-collapse: collapse;"><tr valign="top"><td class="serviceOK">[OK] case "demo_win7" (52.95s) ok</td></tr><tr valign="top"><td class="serviceOK">[OK] Sakuli suite "sakuli_demo" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)</td></tr></table>|s_1_1_notepad=12.22s;20;;; s_1_2_project=17.89s;20;;; s_1_3_print_test_client=7.89s;10;;; s_1_4_open_calc=3.01s;5;;; s_1_5_calculate_525_+100=9.95s;20;;; c_1_demo_win7=52.95s;60;70;; c_1state=0;;;; suite_state=0;;;; suite_runtime_sakuli_demo=64.10s;120;140;;}
 *
 * @author tschneck
 *         Date: 10.07.14
 */
public enum PayLoadFields implements Comparator<PayLoadFields> {
    TYPE("type", 0),
    HOST("host_name", 1),
    START_TIME("start_time", 2),
    FINISH_TIME("finish_time", 3),
    RETURN_CODE("return_code", 4),
    SERVICE_DESC("service_description", 5),
    OUTPUT("output", 6);

    private final String fieldName;
    private final int order;


    PayLoadFields(String fieldName, int order) {
        this.fieldName = fieldName;
        this.order = order;
    }

    @Override
    public int compare(PayLoadFields o1, PayLoadFields o2) {
        return Integer.compare(o1.order, o2.order);
    }

    public String getFieldName() {
        return fieldName;
    }
}
