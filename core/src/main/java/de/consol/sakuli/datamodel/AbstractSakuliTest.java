/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.consol.sakuli.datamodel;

import de.consol.sakuli.datamodel.state.SakuliState;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliExceptionWithScreenshot;
import de.consol.sakuli.services.InitializingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tschneck
 *         Date: 12.07.13
 */
public abstract class AbstractSakuliTest<E extends Throwable, S extends SakuliState> implements Comparable<AbstractSakuliTest> {

    public final static DateFormat GUID_DATE_FORMATE = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SS");
    public final static DateFormat PRINT_DATE_FORMATE = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Date startDate;
    protected Date stopDate;
    protected E exception;
    protected S state;
    protected String name;
    /**
     * is initial set to -1, if the database receiver profile is enabled the service call {@link InitializingService#initTestSuite()}
     * should set the primary key.
     */
    protected int dbPrimaryKey = -1;
    /**
     * needed to be set to -1, so the function {@link de.consol.sakuli.actions.TestCaseAction#addTestCaseStep(String, String, String, int)}
     * can check if the method {@link de.consol.sakuli.actions.TestCaseAction#initWarningAndCritical(int, int)}
     * have been called at the beginning of this test case.
     */
    protected int warningTime = -1;
    protected int criticalTime = -1;
    protected String id;

    /**
     * set the times to the format "time in millisec / 1000"
     *
     * @param date regular {@link Date} object
     * @return UNIX-Time formatted String
     */
    protected String createUnixTimestamp(Date date) {
        if (date == null) {
            return "-1";
        } else {
            String milliSec = String.valueOf(date.getTime());
            return new StringBuilder(milliSec).insert(milliSec.length() - 3, ".").toString();
        }
    }

    /**
     * calculate the duration
     *
     * @return the duration in seconds
     */
    public float getDuration() {
        try {
            float result = (float) ((stopDate.getTime() - startDate.getTime()) / 1000.0);
            return (result < 0) ? -1 : result;
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public String getStartDateAsUnixTimestamp() {
        return createUnixTimestamp(startDate);
    }

    public String getStopDateAsUnixTimestamp() {
        return createUnixTimestamp(stopDate);
    }

    public void addException(E e) {
        if (exception == null) {
            this.exception = e;
        } else {
            exception.addSuppressed(e);
        }
    }

    public Throwable getException() {
        return exception;
    }

    public String getExceptionMessages() {
        return getExceptionMessages(false);
    }


    public String getExceptionMessages(boolean flatFormatted) {
        return SakuliExceptionHandler.getAllExceptionMessages(exception, flatFormatted);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDbPrimaryKey() {
        return dbPrimaryKey;
    }

    public void setDbPrimaryKey(int dbPrimaryKey) {
        this.dbPrimaryKey = dbPrimaryKey;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    public void setCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * refresh the current state based on the set warning and critical times
     */
    public abstract void refreshState();

    public Path getScreenShotPath() {
        return SakuliExceptionHandler.getScreenshotFile(exception);
    }

    protected String getResultString() {
        String stout = "\nname: " + this.getName()
                + "\nRESULT STATE: " + this.getState();
        if (this.getState() != null) {
            stout += "\nresult code: " + this.getState().getErrorCode();
        }
        //if no exception is there, don't print it
        if (this.exception != null) {
            stout += "\nERRORS:" + this.getExceptionMessages();
            if (this.exception instanceof SakuliExceptionWithScreenshot) {
                stout += "\nERROR - SCREENSHOT: "
                        + this.getScreenShotPath().toFile().getAbsolutePath();
            }
        }
        stout += "\ndb primary key: " + this.getDbPrimaryKey()
                + "\nduration: " + this.getDuration() + " sec.";
        if (!(warningTime == -1)) {
            stout += "\nwarning time: " + this.getWarningTime() + " sec.";
        }
        if (!(criticalTime == -1)) {
            stout += "\ncritical time: " + this.getCriticalTime() + " sec.";
        }
        if (this.getStartDate() != null) {
            stout += "\nstart time: " + PRINT_DATE_FORMATE.format(this.getStartDate());
        }
        if (this.getStopDate() != null) {
            stout += "\nend time: " + PRINT_DATE_FORMATE.format(this.getStopDate());
        }
        return stout;
    }

    public S getState() {
        return state;
    }

    public void setState(S state) {
        this.state = state;
    }

    @Override
    public int compareTo(AbstractSakuliTest abstractSakuliTest) {
        if (abstractSakuliTest == null) {
            return 1;
        }
        if (startDate == null || startDate.compareTo(abstractSakuliTest.getStartDate()) == 0) {
            if (id == null) {
                return abstractSakuliTest.getId() != null ? 1 : 0;
            }
            return id.compareTo(abstractSakuliTest.id);
        }
        return startDate.compareTo(abstractSakuliTest.getStartDate());
    }

    @Override
    public String toString() {
        return "startDate=" + startDate +
                ", stopDate=" + stopDate +
                ", exception=" + exception +
                ", state=" + state +
                ", name='" + name + '\'' +
                ", dbPrimaryKey=" + dbPrimaryKey +
                ", warningTime=" + warningTime +
                ", criticalTime=" + criticalTime
                ;
    }

}
