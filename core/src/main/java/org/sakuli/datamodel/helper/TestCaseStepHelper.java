package org.sakuli.datamodel.helper;

import org.sakuli.datamodel.TestCaseStep;

import java.nio.file.Path;
import java.util.List;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestCaseStepHelper {
    public static List<TestCaseStep> parseSteps(Path tcFile) {
        //TODO TS write parser
        return null;
    }

    public static String checkWarningTime(int warningTime, String stepName) {
        return TestDataEntityHelper.checkWarningAndCriticalTime(warningTime, 0,
                String.format("TestCaseStep [name = %s]", stepName));
    }
}
