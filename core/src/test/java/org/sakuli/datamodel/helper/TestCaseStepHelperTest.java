package org.sakuli.datamodel.helper;

import org.sakuli.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestCaseStepHelperTest {

    @Test
    public void testCheckWarningTime() throws Exception {
        assertNull(TestCaseStepHelper.checkWarningTime(0, "test"));
        assertNull(TestCaseStepHelper.checkWarningTime(1, "test"));
        String regex = "TestCaseStep \\[name = test\\] - the warning threshold.*";
        BaseTest.assertRegExMatch(TestCaseStepHelper.checkWarningTime(-1, "test"), regex);
    }
}