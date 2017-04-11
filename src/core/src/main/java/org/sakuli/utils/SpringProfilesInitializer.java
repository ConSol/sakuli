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

package org.sakuli.utils;

import org.sakuli.datamodel.properties.ForwarderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tschneck
 *         Date: 09.07.14
 */
@Component
public class SpringProfilesInitializer implements InitializingBean, ApplicationContextAware {
    public static final String JDBC_DB = "JDBC_DB";
    public static final String GEARMAN = "GEARMAN";
    public static final String INCINGA2 = "ICINGA2";
    public static final String CHECK_MK = "CHECK_MK";
    private static final Logger logger = LoggerFactory.getLogger(SpringProfilesInitializer.class);
    private ConfigurableApplicationContext ctx;
    @Autowired
    private ForwarderProperties forwarderProperties;

    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        ctx = (ConfigurableApplicationContext) ac;
    }

    public void afterPropertiesSet() throws Exception {
        String[] configuredProfiles = getConfiguredProfiles();
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        if (configuredProfiles.length > 0 && activeProfiles.length == 0) {
            logger.info("activate the spring context profiles '{}'", Arrays.toString(configuredProfiles));
            ctx.getEnvironment().setActiveProfiles(configuredProfiles);
            ctx.refresh();
        } else if (configuredProfiles.length == 0) {
            logger.info("no spring context profile activated", configuredProfiles);
        }
    }

    protected String[] getConfiguredProfiles() {
        List<String> profileNames = new ArrayList<>();
        if (forwarderProperties.isDatabaseEnabled()) {
            profileNames.add(JDBC_DB);
        }
        if (forwarderProperties.isGearmanEnabled()) {
            profileNames.add(GEARMAN);
        }
        if (forwarderProperties.isIcinga2Enabled()) {
            profileNames.add(INCINGA2);
        }
        if (forwarderProperties.isCheckMKEnabled()) {
            profileNames.add(CHECK_MK);
        }
        return profileNames.toArray(new String[profileNames.size()]);
    }
}
