package de.consol.sakuli.aop;

import de.consol.sakuli.BaseTest;
import de.consol.sakuli.PropertyHolder;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

/**
 * @author tschneck
 *         Date: 23.09.14
 */
public abstract class AopBaseTest {
    private Path logFile;

    @BeforeMethod
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = BaseTest.INCLUDE_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "aopTest-beanRefFactory.xml";
        logFile = Paths.get(BeanLoader.loadBean(PropertyHolder.class).getLogFileAll());
    }

    protected void assertLastLine(String filter, LogLevel logLevel, String expectedMessage) throws IOException {
        String preFix = null;
        switch (logLevel) {
            case ERROR:
                preFix = "ERROR";
                break;
            case INFO:
                preFix = "INFO ";
                break;
            case DEBUG:
                preFix = "DEBUG";
                break;
            case WARNING:
                preFix = "WARN ";
                break;
        }
        String lastLineOfLogFile = BaseTest.getLastLineWithContent(logFile, filter);
        assertEquals(lastLineOfLogFile.substring(0, 5), preFix);
        assertEquals(lastLineOfLogFile.substring(lastLineOfLogFile.indexOf("]") + 4), expectedMessage);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }
}
