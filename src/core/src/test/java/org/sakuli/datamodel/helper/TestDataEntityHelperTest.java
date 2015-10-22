package org.sakuli.datamodel.helper;

import org.sakuli.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestDataEntityHelperTest {

    @Test
    public void testCheckWarningAndCriticalTime() throws Exception {
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(1, 1, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(1, 0, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(0, 1, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(0, 0, "TestCase"));
        Assert.assertNull(TestDataEntityHelper.checkWarningAndCriticalTime(3, 5, "TestCase"));

        BaseTest.assertRegExMatch(TestDataEntityHelper.checkWarningAndCriticalTime(-1, 0, "TestCase"),
                "TestCase - the warning threshold.*");
        BaseTest.assertRegExMatch(TestDataEntityHelper.checkWarningAndCriticalTime(0, -1, "TestCase"),
                "TestCase - the critical threshold.*");
        BaseTest.assertRegExMatch(TestDataEntityHelper.checkWarningAndCriticalTime(5, 3, "TestCase"),
                "warning threshold must be less than critical threshold!");
    }
}