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

package org.sakuli.services.forwarder.configuration;

import org.jtwig.spaceless.remover.SpaceRemover;

/**
 * The LeadingWhitespaceRemover is used for the twig template configuration and removes
 * all leading whitespaces for every line and all line endings within the template.
 * It can be activated by adding the text snippet or the whole template within both tags
 * {% spaceless %} and {% endspaceless %}.
 *
 * @author Georgi Todorov
 */
public class LeadingWhitespaceRemover implements SpaceRemover {

    /**
     * Removes leading spaces and line endings.
     * (?m)    -> enables multi line mode
     * ^       -> matches begin of line only if multi line mode is enabled
     * [\s\t]+ -> leading white spaces and tabs
     * \n      -> line endings
     * After removing the leading spaces and the line endings, the special strings $whitespace$ and $newline$
     * are replaced by a real white space resp. a new line.
     *
     * @param input
     * @return the input string without line endings and leading whitespaces per line
     */
    @Override                                 //TODO REVIEW: write unit test
    public String removeSpaces(String input) {
        return input
                .replaceAll("(?m)^[\\s\\t]+|\\n", "")
                .replaceAll("\\$whitespace\\$", " ")
                .replaceAll("\\$newline\\$", "\n");
    }

}
