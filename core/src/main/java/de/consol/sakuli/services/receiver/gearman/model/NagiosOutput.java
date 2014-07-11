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

package de.consol.sakuli.services.receiver.gearman.model;

/**
 * @author tschneck
 *         Date: 11.07.14
 */
public class NagiosOutput {
    private String statusSummary;


    public String getOutputString() {

        //TODO go on here
        //gesammt - cases
        String statusSum = "OK - [OK] case \"demo_win7\" (52.95s) ok, " +
                "[OK] Sakuli suite \"sakuli_demo\" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)\\\\n";
        String statusSumNEW = "OK - Sakuli suite \"sakuli_demo\" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)" +
                "[OK] case \"demo_win7\" (52.95s) ok " +
                "\\\\n";
        String htmlTable = "<table style=\"border-collapse: collapse;\"><tr valign=\"top\"><td class=\"serviceOK\">[OK] case \"demo_win7\" (52.95s) ok</td></tr><tr valign=\"top\"><td class=\"serviceOK\">[OK] Sakuli suite \"sakuli_demo\" (ID: 19) ran in 64.10 seconds. (Last suite run: 14.05. 11:06:57)</td></tr></table>";
        return statusSum + htmlTable + "|s_1_1_notepad=12.22s;20;;; s_1_2_project=17.89s;20;;; s_1_3_print_test_client=7.89s;10;;; s_1_4_open_calc=3.01s;5;;; s_1_5_calculate_525_+100=9.95s;20;;; c_1_demo_win7=52.95s;60;70;; c_1state=0;;;; suite_state=0;;;; suite_runtime_sakuli_demo=64.10s;120;140;;";
    }
}
