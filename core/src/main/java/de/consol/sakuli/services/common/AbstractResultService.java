package de.consol.sakuli.services.common;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.services.ResultService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tschneck
 */
public abstract class AbstractResultService implements ResultService {

    @Autowired
    private TestSuite testSuite;

    @Override
    public void refreshStates() {
        testSuite.refreshState();
    }
}
