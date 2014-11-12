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

package de.consol.sakuli.services.receiver.database;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliReceiverException;
import de.consol.sakuli.services.InitializingService;
import de.consol.sakuli.services.common.CommonInitializingServiceImpl;
import de.consol.sakuli.services.receiver.database.dao.DaoTestSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Additional initializing actions for the database based result saving.
 * The {@link CommonInitializingServiceImpl#initTestSuite()} will be also called.
 *
 * @author tschneck
 *         Date: 09.07.14
 */
@ProfileJdbcDb
@Component
public class DatabaseInitializingServiceImpl implements InitializingService {
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private DaoTestSuite daoTestSuite;
    @Autowired
    private SakuliExceptionHandler exceptionHandler;

    @Override
    public void initTestSuite() {
        try {
            testSuite.setDbPrimaryKey(daoTestSuite.insertInitialTestSuiteData());
        } catch (Throwable e) {
            exceptionHandler.handleException(new SakuliReceiverException(e, "error at the initializing of the database"), true);
        }
    }

}
