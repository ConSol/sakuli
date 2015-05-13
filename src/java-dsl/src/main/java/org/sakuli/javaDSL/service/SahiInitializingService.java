package org.sakuli.javaDSL.service;

import net.sf.sahi.client.Browser;
import org.sakuli.datamodel.properties.TestSuiteProperties;

/**
 * Interface for all Sahi initializing tasks
 *
 * @author tschneck
 *         Date: 07.05.15
 */
public interface SahiInitializingService {

    /**
     * Initialize a Browser which is controlled by Sahi.
     * The Browser is configurable in the {@link TestSuiteProperties}.
     *
     * @return a initialized instance of {@link Browser}.
     */
    Browser getBrowser();

}
