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

package org.sakuli.services.common;

import org.sakuli.actions.environment.Application;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The common result service will be called every time after the test suite execution.
 * Currently the result will be only logged.
 *
 * @author tschneck
 */
@Component
public class CommonResultServiceImpl extends AbstractResultService {
    private static Logger logger = LoggerFactory.getLogger(CommonResultServiceImpl.class);
    @Autowired
    private TestSuite testSuite;

    @Override
    public int getServicePriority() {
        return 0;
    }

    @Override
    public void saveAllResults() {
        cleanClipboard();
        logger.info(testSuite.getResultString()
                + "\n===========  SAKULI Testsuite \"" + testSuite.getId() + "\" execution FINISHED - "
                + testSuite.getState() + " ======================\n");
        if (testSuite.getState().equals(TestSuiteState.ERRORS)) {
            String errorMsg = "ERROR-Summary:\n" + testSuite.getExceptionMessages();
            logger.error(errorMsg + "\n");
        }
    }

    protected void cleanClipboard() {
        Application.setClipboard(" ");
    }
}
