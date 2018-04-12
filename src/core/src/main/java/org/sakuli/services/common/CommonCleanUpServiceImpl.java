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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.ResultService;
import org.sakuli.utils.CleanUpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The common result service will be called every time after the test suite execution.
 * Currently the result will be only logged.
 *
 * @author tschneck
 */
@Component
public class CommonCleanUpServiceImpl implements ResultService {
    private static Logger LOGGER = LoggerFactory.getLogger(CommonCleanUpServiceImpl.class);

    @Override
    public int getServicePriority() {
        return 5;
    }

    @Override
    public void teardownTestSuite(@NonNull TestSuite testSuite) {
        try {
            CleanUpHelper.cleanClipboard();
            CleanUpHelper.releaseAllModifiers();
        } catch (Throwable e) {
            LOGGER.warn("Some unexpected errors during the clean up procedure:", e);
        }
    }
}
