/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.starter.sahi.helper;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.time.LocalDate;

/**
 * @author tschneck
 *         Date: 2/13/16
 */
public class CmdPrintHelper {

    public static void printHelp(Options options) {
        printSakuliHeader();
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(80);
        helpFormatter.printHelp("sakuli [options]", options);
    }

    private static void printSakuliHeader() {
        System.out.println("\n" +
                "Sakuli JAR starter:\n" +
                LocalDate.now().getYear() + " - The Sakuli team.\n" +
                "http://www.sakuli.org\n" +
                "https://github.com/ConSol/sakuli\n"
        );
    }
}
