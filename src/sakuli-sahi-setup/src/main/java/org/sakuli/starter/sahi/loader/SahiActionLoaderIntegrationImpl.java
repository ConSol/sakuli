/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.starter.sahi.loader;

import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.sakuli.aop.BaseSakuliAspect;
import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.loader.ActionLoaderCallback;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BaseActionLoaderImpl;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.sakuli.starter.sahi.exceptions.SahiActionException;
import org.sakuli.starter.sahi.utils.SahiStarterPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 4/19/17
 */
@Component
public class SahiActionLoaderIntegrationImpl implements ActionLoaderCallback, SahiActionLoader {
    public static final Logger LOGGER = LoggerFactory.getLogger(SahiActionLoaderIntegrationImpl.class);

    @Autowired
    private SahiProxyProperties sahiProxyProperties;
    @Autowired
    private SakuliExceptionHandler exceptionHandler;
    @Qualifier(BaseActionLoaderImpl.QUALIFIER)
    @Autowired
    private BaseActionLoader loader;
    /**
     * ** Fields which will be filled at runtime ***
     */
    private RhinoScriptRunner rhinoScriptRunner;

    @Override
    public void initTestCase(TestCase testCase) {
//        if (sakuliProperties.isLoadJavaScriptEngine()) {
        //add the "sakuli-delay-active" var to the script runner context
        if (rhinoScriptRunner == null || rhinoScriptRunner.getSession() == null) {
            //could be possible if the aspectj compiler won't worked correctly, see RhinoAspect#getRhinoScriptRunner
            exceptionHandler.handleException(String.format("cannot init rhino script runner with sakuli custom delay variable '%s'",
                    SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR), false);
        } else {
            String isRequestDelayActive = String.valueOf(sahiProxyProperties.isRequestDelayActive());
            rhinoScriptRunner.getSession().setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR, isRequestDelayActive);
            LOGGER.debug("set isRequestDelayActive={} for testcase '{}'", isRequestDelayActive, testCase.getId());
        }
    }

    @Override
    public void handleException(SakuliException transformedException) {
        if (!loader.getSakuliProperties().isSuppressResumedExceptions()) {
            addExceptionToSahiReport(transformedException);
        } else {
            // a {@link SakuliForwarderException}, should only added to the report and not stop sahi, because
            // this error types only on already started the tear down of test suite.
            if (transformedException instanceof SakuliForwarderException) {
                addExceptionToSahiReport(transformedException);
            }
            //stop the execution and add to report if the exception is not caused by Sahi
            else if (!(transformedException instanceof SahiActionException)) {
                stopExecutionAndAddExceptionToSahiReport(transformedException);
            }
        }

    }

    @Override
    public void releaseContext() {
        BeanLoader.loadBean(SahiStarterPropertyPlaceholderConfigurer.class).restoreProperties();
    }

    /**
     * save the exception to the current sahi report (HTML Report in the log folder).
     *
     * @param e any {@link SakuliException}
     */
    private void addExceptionToSahiReport(SakuliException e) {
        if (getSahiReport() != null) {
            getSahiReport().addResult(
                    e.getMessage(),
                    ResultType.ERROR,
                    e.getStackTrace().toString(),
                    e.getMessage() + BaseSakuliAspect.ALREADY_PROCESSED);
        }
    }

    /**
     * stops the execution of the current test case and add the exception to the sahi report (HTML Report in the log
     * folder).
     *
     * @param e any {@link SakuliException}
     */
    private void stopExecutionAndAddExceptionToSahiReport(SakuliException e) {
        addExceptionToSahiReport(e);
        if (getRhinoScriptRunner() != null) {
            getRhinoScriptRunner().setStopOnError(true);
            getRhinoScriptRunner().setHasError();
            throw new RuntimeException(BaseSakuliAspect.ALREADY_PROCESSED + e.getMessage());
        }
    }

    @Override
    public SahiProxyProperties getSahiProxyProperties() {
        return sahiProxyProperties;
    }

    public void setSahiProxyProperties(SahiProxyProperties sahiProxyProperties) {
        this.sahiProxyProperties = sahiProxyProperties;
    }

    @Override
    public RhinoScriptRunner getRhinoScriptRunner() {
        return rhinoScriptRunner;
    }

    @Override
    public void setRhinoScriptRunner(RhinoScriptRunner rhinoScriptRunner) {
        this.rhinoScriptRunner = rhinoScriptRunner;

    }

    @Override
    public Report getSahiReport() {
        return rhinoScriptRunner == null ? null : rhinoScriptRunner.getReport();
    }

    @Override
    public BaseActionLoader getBaseActionLoader() {
        return this.loader;
    }
}
