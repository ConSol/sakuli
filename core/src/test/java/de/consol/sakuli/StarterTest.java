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

package de.consol.sakuli;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.starter.SahiConnector;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * run tests with mvn
 *
 * @author tschneck
 *         Date: 10.06.13
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class StarterTest extends BaseTest {

    /**
     * set false exclude a test of a complete test case
     */
    private boolean testACompleteTestCase = false;

    @Test
    public void testSahiConnector() throws Throwable {
        if (testACompleteTestCase) {
            SahiConnector sahiConnector = getStarterContextBeanFactory().getBean(SahiConnector.class);
            sahiConnector.init();
            sahiConnector.startSahiTestSuite();
            Assert.assertEquals(TestSuiteState.OK, getStarterContextBeanFactory().getBean(TestSuite.class).getState());

            // TODO fix sample test case
            // TODO implement stand alone jetty for executing the test website
        }
    }


}