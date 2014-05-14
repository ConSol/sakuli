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
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
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

    //Property names
    public static final String TYPE_DELAY_PROPERTY = "sakuli.screenbased.typeDelay";
    public static final String CLICK_DELAY_PROPERTY = "sakuli.screenbased.clickDelay";
    public static final String SCREENSHOT_FOLDER_PROPERTY = "sakuli.screenshot.dir";
    public static final String SCREENSHOT_FORMAT_PROPERTY = "sakuli.screenshot.format";


    public static final String LOAD_TEST_CASES_AUTOMATIC_PROPERTY = "saklui.load.testcases.automatic";


    @Autowired
    private DaoTestSuite dao;

    private String id;
    private boolean byResumOnExceptionLogging;
    private boolean takeScreenshots;
    @Value("${" + SCREENSHOT_FOLDER_PROPERTY + "}")
    private String screenShotFolderPath;
    //browser name where to start the test execution
    private String browserName;

    @Value("${" + LOAD_TEST_CASES_AUTOMATIC_PROPERTY + ":true}") //default = TRUE
    private boolean loadTestCasesAutomatic = true;
    //additional browser infos from sahi proxy
    private String browserInfo;
    private String host;
    private Path testSuiteFolder;
    private Path testSuiteFile;
    private Path screenShotFolder;
    private int dbJobPrimaryKey;
    private Map<String, TestCase> testCases;

    public TestSuite() {
    }

    @Autowired
    public TestSuite(TestSuiteProperties properties) {
        id = properties.getTestSuiteId();
        name = properties.getTestSuiteName();
        warningTime = properties.getWarningTime();
        criticalTime = properties.getCriticalTime();
        testSuiteFolder = properties.getTestSuiteFolder();
        testSuiteFile = properties.getTestSuiteSuiteFile();
        browserName = properties.getBrowserName();
        takeScreenshots = properties.isTakeScreenshots();
        byResumOnExceptionLogging = properties.isByResumOnExceptionLogging();
    }

    /**
     * initialize the test suite object
     * 1. set and check .suite file
     * 2. set the id from the db
     * 3. load the testcases
     */
    @PostConstruct
    public void init() throws IOException, SakuliException {
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
        if (loadTestCasesAutomatic) {
            if (!Files.exists(testSuiteFile)) {
                throw new FileNotFoundException("Can not find specified " + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + " file at \"" + testSuiteFolder.toString() + "\"");
            }
            String testSuiteString = Utils.readFileAsString(testSuiteFile.toFile());
            logger.info(
                    "\n--- TestSuite initialization: read test suite information of file \"" + testSuiteFile.toAbsolutePath().toString() + "\" ----\n"
                            + testSuiteString
                            + "\n --- End of File \"" + testSuiteFile.toAbsolutePath().toString() + "\" ---"
            );

            //handle each line of the .suite file
            String regExLineSep = System.getProperty("line.separator") + "|\n";
            for (String line : testSuiteString.split(regExLineSep)) {
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
                        throw new SakuliException("test case path \"" + tcFile.toAbsolutePath().toString() + "\" doesn't exists - check your \"" + TestSuiteProperties.TEST_SUITE_SUITE_FILE_NAME + "\" file");
                    }
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
        screenShotFolder = Paths.get(screenShotFolderPath).normalize();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbsolutePathOfTestSuiteFile() {
        return testSuiteFile.toAbsolutePath().toString();
    }

    public Path getTestSuiteFolder() {
        return testSuiteFolder;
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
        return byResumOnExceptionLogging;
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
        return id + "__" + GUID_DATE_FORMATE.format(startDate);
    }

    public void addTestCase(String testCaseId, TestCase testCase) {
        if (this.testCases == null) {
            this.testCases = new HashMap<>();
        }
        this.testCases.put(testCaseId, testCase);
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
