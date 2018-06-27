/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.forwarder.AbstractTemplateOutputBuilder;
import org.sakuli.services.forwarder.configuration.TemplateModelEntityName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Georgi Todorov
 */
@ProfileGearman
@Component
public class GearmanTemplateOutputBuilder extends AbstractTemplateOutputBuilder {

    @Autowired
    private GearmanProperties gearmanProperties;

    @Override
    public String getConverterName() {
        return "Gearman";
    }

    @Override
    public Map<TemplateModelEntityName, Object> getSpecificModelEntities() {
        Map modelEntitiesMap = new HashMap<TemplateModelEntityName, Object>();
        modelEntitiesMap.put(TemplateModelEntityName.GEARMAN_PROPERTIES, gearmanProperties);
        TestSuite testSuite = getCurrentTestSuite();
        if (testSuite != null) {
            modelEntitiesMap.put(TemplateModelEntityName.TEST_SUITE_ID, testSuite.getId());
        }
        TestCase currentTestCase = getCurrentTestCase();
        if (currentTestCase != null) {
            modelEntitiesMap.put(TemplateModelEntityName.TEST_CASE_ID, currentTestCase.getId());
        }
        return modelEntitiesMap;
    }

    public TestSuite getCurrentTestSuite() {
        return BeanLoader.loadBaseActionLoader().getTestSuite();
    }

    public TestCase getCurrentTestCase() {
        return BeanLoader.loadBaseActionLoader().getCurrentTestCase();
    }

}
