package de.consol.sakuli.services.common;

import de.consol.sakuli.datamodel.TestSuite;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;

public class AbstractResultServiceTest {

    @Mock
    private TestSuite testSuite;
    @InjectMocks
    private AbstractResultService testling = new AbstractResultService() {
        @Override
        public void saveAllResults() {
        }
    };

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRefreshStates() throws Exception {
        testling.refreshStates();
        verify(testSuite).refreshState();
    }
}