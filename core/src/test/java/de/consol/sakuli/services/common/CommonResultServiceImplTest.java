package de.consol.sakuli.services.common;

import de.consol.sakuli.LoggerTest;
import de.consol.sakuli.builder.TestSuiteExampleBuilder;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.state.TestSuiteState;
import de.consol.sakuli.exceptions.SakuliException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommonResultServiceImplTest extends LoggerTest {

    private CommonResultServiceImpl testling = new CommonResultServiceImpl();
    private TestSuite testSuite;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @BeforeMethod
    public void init() {
        super.init();
        testSuite = new TestSuiteExampleBuilder()
                .withId("LOG_TEST_SUITE").withState(TestSuiteState.ERRORS).withException(new SakuliException("TEST")).buildExample();
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
    }

    @Test
    public void testSaveAllResults() throws Exception {
        Path logfile = Paths.get(properties.getLogFile());
        testling.saveAllResults();
        String lastLineOfLogFile = getLastLineOfLogFile(logfile, 42);
        List<String> regExes = Arrays.asList(
                "INFO.*",
                "=========== test suite \"LOG_TEST_SUITE\" ended with ERRORS =================",
                "test suite id: LOG_TEST_SUITE",
                "guid: LOG_TEST_SUITE.*",
                "name: Unit Test Sample Test Suite",
                "RESULT STATE: ERRORS",
                "result code: 6",
                "ERRORS:TEST",
                "db primary key: -1",
                "duration: 120.0 sec.",
                "warning time: 0 sec.",
                "critical time: 0 sec.",
                "start time: 17-08-2014 02:00:00",
                "end time: 17-08-2014 02:02:00",
                "db primary key of job table: -1",
                "browser: firefox",
                "\t======== test case \"UNIT_TEST.*\" ended with OK =================",
                "\ttest case id: UNIT_TEST_CASE.*",
                "\tname: Unit Test Case",
                "\tRESULT STATE: OK",
                "\tresult code: 0",
                "\tdb primary key: -1",
                "\tduration: 3.0 sec.",
                "\twarning time: 4 sec.",
                "\tcritical time: 5 sec.",
                "\tstart time: .*",
                "\tend time: .*",
                "\tstart URL: http://www.start-url.com",
                "\tlast URL: http://www.last-url.com",
                "\t\t======== test case step \"step for unit test\" ended with OK =================",
                "\t\tname: step for unit test",
                "\t\tRESULT STATE: OK",
                "\t\tresult code: 0",
                "\t\tdb primary key: -1*",
                "\t\tduration: 1.0 sec.",
                "\t\twarning time: 2 sec.",
                "\t\tstart time: .*",
                "\t\tend time: .*",
                "===========  SAKULI Testsuite \"LOG_TEST_SUITE\" execution FINISHED - ERRORS ======================",
                "",
                "ERROR .* ERROR-Summary:",
                "TEST.*");
        List<String> strings = Arrays.asList(lastLineOfLogFile.split("\n"));
        Iterator<String> regExIterator = regExes.iterator();
        for (String string : strings) {
            String regex = regExIterator.next();
            logger.debug("\nREGEX: {}\n |\n - STR: {}", regex, string);
            Assert.assertTrue(string.matches(regex));
        }
    }
}