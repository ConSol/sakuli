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

package org.sakuli.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;
import org.sakuli.actions.environment.Environment;
import org.sakuli.exceptions.SakuliException;

import java.io.ByteArrayOutputStream;

/**
 * Separate helper class to make testing of {@link Environment#runCommand(String, boolean)} easier.
 *
 * @author tschneck
 *         Date: 9/17/15
 */
public class CommandLineUtil {

    static public CommandLineResult runCommand(String command, boolean throwException) throws SakuliException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        CommandLineResult result = new CommandLineResult();
        try {
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(outputStream, error));
            int exitCode = executor.execute(CommandLine.parse(command));
            result.setExitCode(exitCode);
            result.setOutput(error.toString() + outputStream.toString());
        } catch (Exception e) {
            if (throwException) {
                throw new SakuliException(e, String.format("Error during execution of command '%s': %s", command, error.toString()));
            }
            result.setExitCode(resolveExitCode(e.getMessage()));
            result.setOutput(e.getMessage());
        }
        return result;
    }

    private static int resolveExitCode(String error) {
        try {
            final String pattern = "error=";
            if (StringUtils.contains(error, pattern)) {
                int i = error.indexOf(pattern);
                return Integer.valueOf(error.substring(i + pattern.length(), i + pattern.length() + 1));
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    public static class CommandLineResult {
        private String output;
        private int exitCode;

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public int getExitCode() {
            return exitCode;
        }

        public void setExitCode(int exitCode) {
            this.exitCode = exitCode;
        }

        @Override
        public String toString() {
            return exitCode + ", " + output;

        }
    }
}
