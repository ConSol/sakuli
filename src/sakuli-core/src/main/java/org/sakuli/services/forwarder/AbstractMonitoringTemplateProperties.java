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

package org.sakuli.services.forwarder;

import org.sakuli.datamodel.state.TestCaseState;

/**
 * @author tschneck
 *         Date: 2/26/16
 */
public abstract class AbstractMonitoringTemplateProperties {


    public String lookUpTemplate(TestCaseState state) {
        if (state == null || state.isError()) {
            return getTemplateCaseError();
        } else if (state.isOk()) {
            return getTemplateCaseOk();
        } else if (state.isWarning()) {
            return state.equals(TestCaseState.WARNING_IN_STEP) ? getTemplateCaseWarningInStep() : getTemplateCaseWarning();
        } else if (state.isCritical()) {
            return getTemplateCaseCritical();
        }
        return null;
    }

    public abstract String getTemplateCaseOk();

    public abstract String getTemplateCaseWarning();

    public abstract String getTemplateCaseWarningInStep();

    public abstract String getTemplateCaseCritical();

    public abstract String getTemplateCaseError();

    public abstract int getTemplateSuiteSummaryMaxLength();

    public abstract String getTemplateSuiteSummary();
}
