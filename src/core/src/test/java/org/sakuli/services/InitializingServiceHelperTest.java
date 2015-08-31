package org.sakuli.services;

import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.forwarder.database.DatabaseInitializingServiceImpl;
import org.sakuli.services.forwarder.gearman.GearmanInitializingServiceImpl;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 09.04.15
 */
public class InitializingServiceHelperTest extends AbstractServiceBaseTest {

    @Test
    public void testInvokeAllResultServices() throws Exception {
        assertEquals(BeanLoader.loadMultipleBeans(InitializingService.class).size(), 3);
        DatabaseInitializingServiceImpl databaseService = BeanLoader.loadBean(DatabaseInitializingServiceImpl.class);
        doNothing().when(databaseService).initTestSuite();
        GearmanInitializingServiceImpl gearmanService = BeanLoader.loadBean(GearmanInitializingServiceImpl.class);
        doNothing().when(gearmanService).initTestSuite();

        TestSuite testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.setState(null);
        testSuite.setStartDate(null);
        InitializingServiceHelper.invokeInitializingServcies();
        assertEquals(testSuite.getState(), TestSuiteState.RUNNING);
        assertTrue(testSuite.getStartDate().before(new Date()));
        verify(databaseService).initTestSuite();
        verify(gearmanService).initTestSuite();
    }
}