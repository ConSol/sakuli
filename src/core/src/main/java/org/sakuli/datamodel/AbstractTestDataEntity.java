/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2015 the original author or authors.
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

package org.sakuli.datamodel;

import org.joda.time.DateTime;
import org.sakuli.datamodel.state.SakuliState;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.sakuli.services.InitializingService;
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
public abstract class AbstractTestDataEntity<E extends Throwable, S extends SakuliState> implements Comparable<AbstractTestDataEntity> {

    public final static DateFormat GUID_DATE_FORMATE = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SS");
    public final static DateFormat PRINT_DATE_FORMATE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Date startDate;
    protected Date stopDate;
    protected E exception;
    protected S state;
    protected String name;
    /**
     * is initial set to -1, if the database forwarder profile is enabled the service call {@link InitializingService#initTestSuite()}
     * should set the primary key.
     */
    protected int dbPrimaryKey = -1;
    /**
     * needed to be set to -1, so the function {@link org.sakuli.actions.TestCaseAction#addTestCaseStep(String, String, String, int)}
     * can check if the method {@link org.sakuli.actions.TestCaseAction#initWarningAndCritical(int, int)}
     * have been called at the beginning of this test case.
     */
    protected int warningTime = -1;
    protected int criticalTime = -1;
    protected String id;

    /**
     * represent the creation date for this entity, to enable effective sorting after it.
     * This is only for INTERNAL use. To get the date of starting this entity, see {@link #startDate}.
     **/
    private DateTime creationDate;

    public AbstractTestDataEntity() {
        creationDate = new DateTime();
    }

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

    /**
     * If the threshold is set to 0, the execution time will never exceed, so the state will be always OK!
     *
     * @param warningTime time in seconds
     */
    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    /**
     * If the threshold is set to 0, the execution time will never exceed, so the state will be always OK!
     *
     * @param criticalTime time in seconds
     */
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

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(AbstractTestDataEntity abstractSakuliTest) {
        if (abstractSakuliTest == null) {
            return 1;
        }
        if (super.equals(abstractSakuliTest)) {
            return 0;
        }
        Date startDate2 = abstractSakuliTest.getStartDate();

        if (this.startDate == null || startDate2 == null || this.startDate.compareTo(startDate2) == 0) {
            boolean boothNull = this.startDate == null && startDate2 == null;
            if (!boothNull) {
                if (this.startDate == null) {
                    return 1;
                }
                if (startDate2 == null) {
                    return -1;
                }
            }
            return creationDate.compareTo(abstractSakuliTest.getCreationDate());
        }
        return this.startDate.compareTo(startDate2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractTestDataEntity) {
            return compareTo((AbstractTestDataEntity) obj) == 0;
        }
        return super.equals(obj);
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

    public String toStringShort() {
        return String.format("%s [id = %s, name = %s]", this.getClass().getSimpleName(), id, name);
    }
}
