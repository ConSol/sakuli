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

package de.consol.sakuli.loader;

import de.consol.sakuli.actions.TestCaseActions;
import de.consol.sakuli.actions.environment.Application;
import de.consol.sakuli.actions.environment.Environment;
import de.consol.sakuli.actions.screenbased.Region;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

/**
 * @author tschneck
 *         Date: 24.06.13
 */
public class BeanLoader {
    protected static final Logger logger = LoggerFactory.getLogger(BeanLoader.class);
    public static String CONTEXT_PATH = "beanRefFactory.xml";

    public static BaseActionLoader loadBaseActionLoader() {
        return loadBean(BaseActionLoaderImpl.class);
    }

    public static ScreenActionLoader loadScreenActionLoader() {
        return loadBean(ScreenActionLoader.class);
    }

    public static TestCaseActions loadTestCaseActions() {
        logger.debug("create new TestCaseAction object over BeanFactory.");
        return getBeanFacotry().getBean(TestCaseActions.class);
    }

    public static Application loadApplication(String applicationNameOrPath, String resumeOnException) {
        logger.debug("Get an new application object over BeanFactory for \""
                + applicationNameOrPath + "\"");
        return new Application(applicationNameOrPath, Boolean.valueOf(resumeOnException), loadScreenActionLoader());
    }

    public static Environment loadEnvironment(String resumeOnException) {
        logger.debug("Get an new environment object over BeanFactory.");
        return new Environment(Boolean.valueOf(resumeOnException), loadScreenActionLoader());
    }

    public static Region loadRegion(String resumeOnException) {
        logger.debug("Get an new Region object over BeanFactory.");
        return new Region(Boolean.valueOf(resumeOnException), loadScreenActionLoader());
    }

    public static Region loadRegionRectangle(int x, int y, int w, int h, String resumeOnException) {
        logger.debug("Get an new Region object over BeanFactory.");
        return new Region(x, y, w, h, Boolean.valueOf(resumeOnException), loadScreenActionLoader());
    }

    public static Region loadRegionImage(String imageName, String resumeOnException) {
        logger.debug("Get an new Region object over BeanFactory.");
        return new Region(imageName, Boolean.valueOf(resumeOnException), loadScreenActionLoader());
    }


    public static <T> T loadBean(Class<T> classDef) {
        try {
            logger.debug("load bean '{}' from application context", classDef.getSimpleName());
            return getBeanFacotry().getBean(classDef);
        } catch (Throwable e) {
            logger.error("error in BeanLoader", e);
            throw e;
        }
    }

    public static <T> T loadBean(String qualifier, Class<T> classDef) {
        logger.debug("load Bean '{}' with qualifier '{}' from application context", classDef.getSimpleName(), qualifier);
        return getBeanFacotry().getBean(qualifier, classDef);
    }

    private static BeanFactory getBeanFacotry() {
        BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance(CONTEXT_PATH);
        BeanFactoryReference bf = bfl.useBeanFactory("de.consol.sakuli.app.root");
        return getBeanFacotryReference().getFactory();
    }

    public static void releaseContext() {
        loadBean(SakuliPropertyPlaceholderConfigurer.class).restoreProperties();
        getBeanFacotryReference().release();
    }

    private static BeanFactoryReference getBeanFacotryReference() {
        BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance(CONTEXT_PATH);
        return bfl.useBeanFactory("de.consol.sakuli.app.root");
    }
}
