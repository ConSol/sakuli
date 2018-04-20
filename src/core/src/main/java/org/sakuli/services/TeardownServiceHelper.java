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

package org.sakuli.services;

import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.loader.BeanLoader;

import java.util.Optional;

/**
 * @author Tobias Schneck
 */
public class TeardownServiceHelper {

    /**
     * Default for {@link #invokeTeardownServices(AbstractTestDataEntity, boolean)} {@code abstractTestDataEntity, false}
     */
    public static void invokeTeardownServices(AbstractTestDataEntity abstractTestDataEntity) {
        invokeTeardownServices(abstractTestDataEntity, false);
    }

    /**
     * Invokes all {@link TeardownService} callbacks, for example to save results.
     *
     * @param asyncCall indicates if a call is triggerd in an async process to the main process to use the correect exception handling, see {@link TeardownService#handleTeardownException(Exception, boolean, AbstractTestDataEntity)}.
     */
    public static void invokeTeardownServices(AbstractTestDataEntity abstractTestDataEntity, boolean asyncCall) {
        if (abstractTestDataEntity != null) {
            abstractTestDataEntity.refreshState();
            BeanLoader.loadMultipleBeans(TeardownService.class).values().stream()
                    .sorted(new PrioritizedServiceComparator<>())
                    .forEachOrdered(s -> s.tearDown(Optional.of(abstractTestDataEntity), asyncCall));
        }
    }

}
