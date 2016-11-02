package org.sakuli.builder;

import org.sakuli.datamodel.TestSuite;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author tschneck
 *         Date: 09.04.15
 */
public class TestSuiteExampleFactory implements FactoryBean<TestSuite> {
    @Override
    public TestSuite getObject() throws Exception {
        return new TestSuiteExampleBuilder().buildExample();
    }

    @Override
    public Class<?> getObjectType() {
        return TestSuite.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
