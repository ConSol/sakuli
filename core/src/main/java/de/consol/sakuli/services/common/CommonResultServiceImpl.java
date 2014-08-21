package de.consol.sakuli.services.common;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The common result service will be called every time after the test suite execution.
 * Currently the result will be only logged.
 *
 * @author tschneck
 */
@Component
public class CommonResultServiceImpl extends AbstractResultService {
    @Autowired
    private TestSuite testSuite;
    private static Logger logger = LoggerFactory.getLogger(CommonResultServiceImpl.class);

    @Override
    public void saveAllResults() {
        logger.info(testSuite.getResultString()
                + "\n===========  SAKULI Testsuite \"" + testSuite.getId() + "\" execution FINISHED - "
                + testSuite.getState() + " ======================\n");
        if (testSuite.getState().equals(TestSuiteState.ERRORS)) {
            String errorMsg = "ERROR-Summary:\n" + testSuite.getExceptionMessages();
            logger.error(errorMsg + "\n");
        }
    }
}
