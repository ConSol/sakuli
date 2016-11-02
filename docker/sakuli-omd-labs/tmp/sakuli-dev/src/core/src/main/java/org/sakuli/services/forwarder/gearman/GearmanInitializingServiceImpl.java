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

import org.sakuli.services.InitializingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 09.07.14
 */
@ProfileGearman
@Component
public class GearmanInitializingServiceImpl implements InitializingService {
    private static final Logger logger = LoggerFactory.getLogger(GearmanInitializingServiceImpl.class);

    @Override
    public int getServicePriority() {
        return 0;
    }

    @Override
    public void initTestSuite() {
        logger.info("GEARMAN INIT");
    }
}
