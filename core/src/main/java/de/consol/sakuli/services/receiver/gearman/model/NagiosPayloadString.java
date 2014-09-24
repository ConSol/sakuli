package de.consol.sakuli.services.receiver.gearman.model;

/**
 * @author tschneck
 *         Date: 05.09.14
 */
public interface NagiosPayloadString {

    /**
     * @return a nagios conform payload string to display different stuff in Thruk GUI
     */
    String getPayloadString();
}
