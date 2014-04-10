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

import de.consol.sakuli.dao.DaoTestSuite;
import de.consol.sakuli.datamodel.state.TestCaseState;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.exceptions.SakuliException;
import net.sf.sahi.util.FileNotFoundRuntimeException;
import net.sf.sahi.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tschneck
 *         Date: 10.06.13
 */
@Component
public class TestSuite extends AbstractSakuliTest<SakuliException, TestSuiteState> {

    public static final String SUITE_FILE_NAME = "testsuite.suite";

    //Property names
    public static final String TYPE_DELAY_PROPERTY = "sakuli.screenbased.typeDelay";
    public static final String CLICK_DELAY_PROPERTY = "sakuli.screenbased.clickDelay";
    public static final String LOG_FOLDER_PROPERTY = "sakuli.log.folder";
    public static final String SCREENSHOT_FOLDER_PROPERTY = "sakuli.screenshot.dir";
    public static final String SCREENSHOT_FORMAT_PROPERTY = "sakuli.screenshot.format";
    public static final String INCLUDE_FOLDER_PROPERTY = "test.suite.includefolder";
    public static final String SUITE_FOLDER_PROPERTY = "test.suite.folder";
    public static final String SUITE_ID_PROPERTY = "testsuite.id";
    public static final String TAKE_SCREENSHOT_PROPERTY = "testsuite.takeScreenShots";
    public static final String AUTO_HIGHLIGHT_PROPERTY = "testsuite.autoHighlight.enabled";
    public static final String AUTO_HIGHLIGHT_SEC_PROPERTY = "testsuite.autoHighlight.seconds";
    public static final String BROWSER_PROPERTY = "testsuite.browser";
    public static final String SUITE_NAME_PROPERTY = "testsuite.name";
    public static final String WARNING_TIME_PROPERTY = "testsuite.warningTime";
    public static final String CRITICAL_TIME_PROPERTY = "testsuite.criticalTime";
    public static final String IS_BY_RESUME_ON_EXCEPTION_LOGGING = "testsuite.resumeOnException.logException";


    @Autowired
    private DaoTestSuite dao;
    /**
     * value from the set system property in {@link de.consol.sakuli.starter.SakuliStarter#main(String[])}
     */
    @Value("${" + SUITE_FOLDER_PROPERTY + "}")
    private String folder;
    /**
     * Value of the javascript include folder, per default "_inlcude" in the projekt dir
     */
    @Value("${" + TestSuite.INCLUDE_FOLDER_PROPERTY + "}")
    private String includeFolderPath;
    /**
     * values from the "testsuiteXX.properties" file
     */
    @Value("${" + SUITE_ID_PROPERTY + "}")
    private String id;
    @Value("${" + IS_BY_RESUME_ON_EXCEPTION_LOGGING + "}")
    private boolean isByResumOnExceptionLogging;
    @Value("${" + TAKE_SCREENSHOT_PROPERTY + "}")
    private boolean takeScreenshots;
    @Value("${" + SCREENSHOT_FOLDER_PROPERTY + "}")
    private String screenShotFolderPath;
    //Browser-Info
    @Value("${" + BROWSER_PROPERTY + "}")
    private String browserName;
    @Value("${" + SUITE_NAME_PROPERTY + "}")
    private String injected_name;
    @Value("${" + WARNING_TIME_PROPERTY + "}")
    private int injected_warningTime;
    @Value("${" + CRITICAL_TIME_PROPERTY + "}")
    private int injected_criticalTime;
    //additional browser infos from sahi proxy
    private String browserInfo;
    private String host;
    private Path testSuiteFolder;
    private Path testSuiteFile;
    private Path screenShotFolder;
    private int dbJobPrimaryKey;
    private Map<String, TestCase> testCases;


    /**
     * initialize the test suite object
     * 1. set and check .suite file
     * 2. set the id from the db
     * 3. load the testcases
     */
    @PostConstruct
    public void init() throws IOException, SakuliException {
        this.name = injected_name;
        this.warningTime = injected_warningTime;
        this.criticalTime = injected_criticalTime;

        logger.info("Initialize test suite");
        state = TestSuiteState.RUNNING;
        setFileBase();
        testCases = loadTestCases();
        startDate = new Date();
        dbPrimaryKey = dao.getTestSuitePrimaryKey();
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            host = "UNKNOWN HOST";
        }
        logger.info("test suite \"" + id + "\" has been initialzied");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshState() {
        if (testCases == null) {
            addException(new SakuliException("NO TEST CASES FOUND !!!"));
            state = TestSuiteState.ERRORS;
        } else if (exception != null) {
            state = TestSuiteState.ERRORS;
        } else {
            for (TestCase tc : testCases.values()) {

                tc.refreshState();
                if (tc.getState() == null) {
                    tc.addException(new SakuliException("ERROR: NO RESULT STATE SET"));
                    state = TestSuiteState.ERRORS;
                } else if (tc.getState().equals(TestCaseState.ERRORS)) {
                    state = TestSuiteState.ERRORS;
                } else if (tc.getState().equals(TestCaseState.CRITICAL)
                        && state.getErrorCode() < TestSuiteState.CRITICAL_IN_CASE.getErrorCode()) {
                    state = TestSuiteState.CRITICAL_IN_CASE;
                } else if (tc.getState().equals(TestCaseState.WARNING)
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_CASE.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_CASE;
                } else if (tc.getState().equals(TestCaseState.WARNING_IN_STEP)
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_STEP.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_STEP;
                } else if (tc.getState().equals(TestCaseState.OK)
                        && state.getErrorCode() < TestSuiteState.OK.getErrorCode()) {
                    state = TestSuiteState.OK;
                }
            }
            if (state.getErrorCode() < TestSuiteState.ERRORS.getErrorCode()) {
                if (criticalTime > 0
                        && getDuration() > criticalTime
                        && state.getErrorCode() < TestSuiteState.CRITICAL_IN_SUITE.getErrorCode()) {
                    state = TestSuiteState.CRITICAL_IN_SUITE;
                } else if (warningTime > 0
                        && getDuration() > warningTime
                        && state.getErrorCode() < TestSuiteState.WARNING_IN_SUITE.getErrorCode()) {
                    state = TestSuiteState.WARNING_IN_SUITE;
                }
            }
        }
    }

    @Override
    public String getResultString() {
        String stout = "\n=========== test suite \"" + getId() + "\" ended with " + getState() + " ================="
                + "\ntest suite id: " + this.getId()
                + "\nguid: " + this.getGuid()
                + super.getResultString()
                + "\ndb primary key of job table: " + this.getDbJobPrimaryKey()
                + "\nbrowser: " + this.getBrowserInfo();
        if (testCases != null && !testCases.isEmpty()) {
            for (TestCase tc : testCases.values()) {
                stout += tc.getResultString();
            }
        }
        return stout;
    }

    @Override
    public String getExceptionMessages() {
        String errorMessages = super.getExceptionMessages();
        if (errorMessages == null) {
            errorMessages = "";
        }
        for (TestCase testCase : getTestCases().values()) {
            if (testCase.getExceptionMessages() != null) {
                errorMessages += "\n" + testCase.getExceptionMessages();
            }
        }
        return errorMessages;
    }

    /**
     * read out the .suite file and create the corresponding test cases
     */
    private HashMap<String, TestCase> loadTestCases() throws SakuliException, IOException {
        HashMap<String, TestCase> tcMap = new HashMap<>();

        String testSuiteString = Utils.readFileAsString(testSuiteFile.toFile());
        logger.info(
                "\n--- TestSuite initialization: read test suite information of file \"" + testSuiteFile.toFile().getAbsolutePath() + "\" ----\n"
                        + testSuiteString
                        + "\n --- End of File \"" + testSuiteFile.toFile().getAbsolutePath() + "\" ---"
        );

        //handle each line of the .suite file
        for (String line : testSuiteString.split(System.getProperty("line.separator"))) {
            if (!line.startsWith("//") && !(line.isEmpty())) {
                //get the start URL from suite
                String startURL = line.substring(line.lastIndexOf(' ') + 1);

                //extract tc file name name and generate new test case
                String tcFileName = line.substring(0, line.lastIndexOf(' '));  // get tc file name
                Path tcFile = Paths.get(testSuiteFolder.toFile().getCanonicalPath() + File.separator + tcFileName.replace("/", File.separator));
                if (Files.exists(tcFile)) {
                    TestCase tc = new TestCase(
                            TestCase.convertFolderPathToName(tcFileName),
                            TestCase.convertTestCaseFileToID(tcFileName, id));

                    tc.setStartUrl(startURL);
                    tc.setTcFile(tcFile);
                    //set the Map with the test case id as key
                    tcMap.put(tc.getId(), tc);
                } else {
                    throw new SakuliException("test case path \"" + tcFile.toFile().getAbsolutePath() + "\" doesn't exists - check your \"testsuite.suite\" file");
                }
            }
        }
        return tcMap;
    }

    /**
     * check if the specified test suite in the "testsuite.properies" file can be found
     *
     * @throws FileNotFoundRuntimeException
     */
    private void setFileBase() throws IOException {
        testSuiteFolder = Paths.get(folder).normalize();
        if (!Files.exists(testSuiteFolder)) {
            throw new FileNotFoundException("Can not find specified test suite base folder \"" + testSuiteFolder.toFile().getCanonicalFile() + "\"");
        }
        testSuiteFile = Paths.get(testSuiteFolder.toFile().getCanonicalPath() + File.separator + SUITE_FILE_NAME);
        if (!Files.exists(testSuiteFile)) {
            throw new FileNotFoundException("Can not find specified " + SUITE_FILE_NAME + " file at \"" + testSuiteFolder.toFile().getCanonicalFile() + "\"");
        }
        screenShotFolder = Paths.get(screenShotFolderPath).normalize();

    }

    public String getId() {
        return id;
    }

    public String getAbsolutePathOfTestSuiteFile() {
        return testSuiteFile.toFile().getAbsolutePath();
    }

    /**
     * returns the path tot the include folder *
     */
    public String getIncludeFolderPath() {
        return includeFolderPath;
    }

    public String getBrowserName() {
        return browserName;
    }

    public String getHost() {
        return host;
    }

    public Map<String, TestCase> getTestCases() {
        return testCases;
    }

    public int getDbJobPrimaryKey() {
        return dbJobPrimaryKey;
    }

    public void setDbJobPrimaryKey(int dbJobPrimaryKey) {
        this.dbJobPrimaryKey = dbJobPrimaryKey;
    }

    public boolean isTakeScreenshots() {
        return takeScreenshots;
    }

    public boolean isByResumOnExceptionLogging() {
        return isByResumOnExceptionLogging;
    }

    public Path getScreenShotFolderPath() {
        return screenShotFolder;
    }

    public String getBrowserInfo() {
        if (browserInfo == null || browserInfo.isEmpty()) {
            return browserName;
        }
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    /**
     * @return a unique identifier for each execution of the test suite
     */
    public String getGuid() {
        return dbPrimaryKey + "_" + id + "__" + GUID_DATE_FORMATE.format(startDate);
    }

    public TestCase getTestCase(String testCaseId) {
        if (this.getTestCases().containsKey(testCaseId)) {
            return this.getTestCases().get(testCaseId);
        }
        return null;
    }

    /**
     * checks if the test case id is valid
     *
     * @return true if valid
     */
    public boolean checkTestCaseID(String testCaseID) {
        return testCaseID != null && this.getTestCases() != null && this.getTestCases().containsKey(testCaseID);
    }

    public TestCase getTestCaseByDBKey(int primaryKeyOfTestCase) {
        for (TestCase testCase : testCases.values()) {
            if (testCase.getDbPrimaryKey() == primaryKeyOfTestCase) {
                return testCase;
            }
        }
        return null;
    }
}
