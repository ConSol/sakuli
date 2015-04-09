package org.sakuli.services;

import org.sakuli.BaseTest;

/**
 * @author tschneck
 *         Date: 09.04.15
 */
public abstract class AbstractServiceBaseTest extends BaseTest {

    @Override
    protected String getTestContextPath() {
        return "serviceTest-beanRefFactory.xml";
    }

    @Override
    protected String getSahiFolderPath() {
        return null;
    }
}
