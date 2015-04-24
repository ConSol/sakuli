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

package org.sakuli;


import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author tschneck
 *         Date: 15.05.14
 *         <p>
 *         A {@link FactoryBean} for creating spyed beans based on Mockito so that they
 *         can be {@code @Autowired} into Spring test configurations.
 * @author Mattias Severson, Jayway
 * @see FactoryBean
 * @see Mockito
 */
public class MockitoSpyFactoryBean<T> implements FactoryBean<T> {

    private T instance;
    private Class<T> classToBeMocked;

    /**
     * Creates a Mockito spy instance of the provided class.
     *
     * @param classToBeMocked The class to be mocked.
     */
    public MockitoSpyFactoryBean(Class<T> classToBeMocked) {
        this.classToBeMocked = classToBeMocked;
    }

    public MockitoSpyFactoryBean(Class<T> classToBeMocked, T instance) {
        this.instance = instance;
        this.classToBeMocked = classToBeMocked;
    }

    @Override
    public T getObject() throws Exception {
        return Mockito.spy(instance != null ? instance : classToBeMocked.newInstance());
    }

    @Override
    public Class<?> getObjectType() {
        return classToBeMocked;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
