package org.sakuli.datamodel.helper;

import org.sakuli.datamodel.TestCase;

/**
 * @author tschneck
 *         Date: 02.07.15
 */
public class TestDataEntityHelper {
    public static final String ERROR_NOT_SET_THRESHOLD_VARIABLE = "%s - the %s threshold have to be set correctly! If the %s threshold should NOT be considered, please set it to 0!";

    /**
     * Check if the warning time is set correctly:
     * <ul>
     * <li>Greater or equal then 0</li>
     * <li>warning time > critical time</li>
     * </ul>
     *
     * @param warningTime  warning time in seconds
     * @param criticalTime critical tim in seconds
     * @param entityName   name of the entity which will be checked, e.g. {@link TestCase}
     * @return {@code null} on success, {@link String} on error with an error message
     */
    public static String checkWarningAndCriticalTime(int warningTime, int criticalTime, String entityName) {
        if (criticalTime < 0) {
            return getErrorNotSetTimeVariableMessage("critical", entityName);
        }
        if (warningTime < 0) {
            return getErrorNotSetTimeVariableMessage("warning", entityName);
        }
        if (warningTime > 0 && criticalTime > 0) {
            if (warningTime > criticalTime) {
                return "warning threshold must be less than critical threshold!";
            }
        }
        return null;
    }

    private static String getErrorNotSetTimeVariableMessage(String thersholdName, String entityName) {
        return String.format(ERROR_NOT_SET_THRESHOLD_VARIABLE, entityName, thersholdName, thersholdName);
    }
}
