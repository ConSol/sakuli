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

import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.services.forwarder.ScreenshotDivConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Custom JtwigFunction for extracting the screenshot from a test data entity, which has an exception.
 * If the test entity doesn't have any exception, then <code>null</code> is returend by the function.
 *
 * @author Georgi Todorov
 */
public class ExtractScreenshotFunction extends AbstractFunction {

    private ScreenshotDivConverter screenshotDivConverter;

    public ExtractScreenshotFunction(ScreenshotDivConverter screenshotDivConverter) {
        this.screenshotDivConverter = screenshotDivConverter;
    }

    @Override
    public String name() {
        return "extractScreenshot";
    }

    @Override
    protected int getExpectedNumberOfArguments() {
        return 1;
    }

    @Override
    protected List<Class> getExpectedArgumentTypes() {
        return Arrays.asList(AbstractTestDataEntity.class);
    }

    @Override
    protected Object execute(List<Object> arguments) {
        AbstractTestDataEntity testDataEntity = (AbstractTestDataEntity) arguments.get(0);
        return screenshotDivConverter.convert(testDataEntity.getException());
    }

}
