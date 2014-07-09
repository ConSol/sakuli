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

package de.consol.sakuli.services.gearman;

import de.consol.sakuli.services.InitializingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

/**
 * @author tschneck
 *         Date: 09.07.14
 */
@Profile("gearman")
@Component
public class GearmanInitializingServiceImpl implements InitializingService {
    private static final Logger logger = LoggerFactory.getLogger(GearmanInitializingServiceImpl.class);

    @Override
    public void initTestSuite() throws FileNotFoundException {
        logger.info("GEARMAN INIT");
    }
}
