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

package org.sakuli.loader;

import org.sakuli.actions.TestCaseAction;
import org.sakuli.actions.environment.Application;
import org.sakuli.actions.environment.Environment;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public static TestCaseAction loadTestCaseAction() {
        logger.debug("create new TestCaseAction object over BeanFactory.");
        return getBeanFactory().getBean(TestCaseAction.class);
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

    /**
     * @param classDef class definition of the expected bean
     * @param <T>      generic type of the returned bean.
     * @return an singleton bean of type {@code <T>} or null
     */
    public static <T> T loadBean(Class<T> classDef) {
        try {
            logger.debug("load bean '{}' from application context", classDef.getSimpleName());
            return getBeanFactory().getBean(classDef);
        } catch (Throwable e) {
            logger.error("error in BeanLoader", e);
            throw e;
        }
    }

    /**
     * load a singleton bean like {@link #loadBean(Class)}, but with an additional qualifier.
     */
    public static <T> T loadBean(String qualifier, Class<T> classDef) {
        logger.debug("load Bean '{}' with qualifier '{}' from application context", classDef.getSimpleName(), qualifier);
        return getBeanFactory().getBean(qualifier, classDef);
    }

    private static BeanFactory getBeanFactory() {
        BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance(CONTEXT_PATH);
        bfl.useBeanFactory("org.sakuli.app.root");
        return getBeanFactoryReference().getFactory();
    }

    /**
     * Release the context and shuts the hole context down
     */
    public static void releaseContext() {
        loadBean(SakuliPropertyPlaceholderConfigurer.class).restoreProperties();
        BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) beanFactory).close();
        }
    }

    /**
     * Reload the hole context
     */
    public static void refreshContext() {
        BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) beanFactory).refresh();
        }
    }

    private static BeanFactoryReference getBeanFactoryReference() {
        BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance(CONTEXT_PATH);
        return bfl.useBeanFactory("org.sakuli.app.root");
    }

    /**
     * @param classDef class definition of the expected beans
     * @param <T>      generic type of the returned {@link List}.
     * @return all available beans of type {@code <T>}. If no beans are available, the method returns an empty List.
     */
    public static <T> java.util.Map<String, T> loadMultipleBeans(Class<T> classDef) {
        BeanFactory beanFactory = getBeanFactory();
        Map<String, T> beans = null;
        if (beanFactory instanceof ListableBeanFactory) {
            beans = ((ListableBeanFactory) beanFactory).getBeansOfType(classDef);
        }
        return beans != null ? beans : Collections.<String, T>emptyMap();
    }

}
