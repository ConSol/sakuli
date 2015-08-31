package org.sakuli.services;

import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.common.CacheHandlingResultServiceImpl;
import org.sakuli.services.forwarder.database.DatabaseResultServiceImpl;
import org.sakuli.services.forwarder.gearman.GearmanResultServiceImpl;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 09.04.15
 */
public class ResultServiceHelperTest extends AbstractServiceBaseTest {

    @Test
    public void testInvokeAllResultServices() throws Exception {
        assertEquals(BeanLoader.loadMultipleBeans(ResultService.class).size(), 4);
        DatabaseResultServiceImpl databaseResultService = mockDatabaseResultService();
        GearmanResultServiceImpl gearmanResultService = mockGearmanResultService();
        CacheHandlingResultServiceImpl cacheHandlingResultService = mockCacheHandlingResultService();
        TestSuite testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.setState(TestSuiteState.RUNNING);

        ResultServiceHelper.invokeResultServices();
        assertEquals(testSuite.getState(), TestSuiteState.OK);
        assertTrue(testSuite.getStopDate().after(testSuite.getStartDate()));
        verify(databaseResultService).saveAllResults();
        verify(databaseResultService).refreshStates();
        verify(gearmanResultService).saveAllResults();
        verify(gearmanResultService).refreshStates();
        verify(cacheHandlingResultService).saveAllResults();
        verify(cacheHandlingResultService).refreshStates();
    }

    private GearmanResultServiceImpl mockGearmanResultService() {
        GearmanResultServiceImpl gearmanResultService = BeanLoader.loadBean(GearmanResultServiceImpl.class);
        doNothing().when(gearmanResultService).refreshStates();
        doNothing().when(gearmanResultService).saveAllResults();
        return gearmanResultService;
    }

    private DatabaseResultServiceImpl mockDatabaseResultService() {
        DatabaseResultServiceImpl databaseResultService = BeanLoader.loadBean(DatabaseResultServiceImpl.class);
        doNothing().when(databaseResultService).refreshStates();
        doNothing().when(databaseResultService).saveAllResults();
        return databaseResultService;
    }

    private CacheHandlingResultServiceImpl mockCacheHandlingResultService() {
        CacheHandlingResultServiceImpl cacheHandlingResultService = BeanLoader.loadBean(CacheHandlingResultServiceImpl.class);
        doNothing().when(cacheHandlingResultService).refreshStates();
        doNothing().when(cacheHandlingResultService).saveAllResults();
        return cacheHandlingResultService;
    }
}