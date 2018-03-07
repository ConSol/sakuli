package org.sakuli.services.forwarder.gearman;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.ScreenshotDiv;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;

/**
 * @author Georgi Todorov
 */
public class GearmanTemplateOutputBuilderTest extends BaseTest {

    private static final String TESTSUITE_ID = "example_xfce";
    private static final String DEFAULT_SERVICE_TYPE = "passive";
    private static final String DEFAULT_NAGIOS_HOST = "my.nagios.host";
    private static final String DEFAULT_NAGIOS_CHECK_COMMMAND = "check_sakuli";

    @InjectMocks
    @Spy
    private GearmanTemplateOutputBuilder testling;
    @Mock
    private ScreenshotDivConverter screenshotDivConverter;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private GearmanProperties gearmanProperties;
    private TestSuite testSuite = new TestSuite();
    @Mock
    private SakuliProperties sakuliProperties;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(getTemplatePath()).when(sakuliProperties).getForwarderTemplateFolder();
        doReturn(TESTSUITE_ID).when(gearmanProperties).getNagiosServiceDescription();
        doReturn(DEFAULT_SERVICE_TYPE).when(gearmanProperties).getServiceType();
        doReturn(DEFAULT_NAGIOS_HOST).when(gearmanProperties).getNagiosHost();
        doReturn(DEFAULT_NAGIOS_CHECK_COMMMAND).when(gearmanProperties).getNagiosCheckCommand();

        testSuite.setId(TESTSUITE_ID);
        testSuite.setTestCases(null);
        doReturn(testSuite).when(testling).getCurrentTestSuite();
        doReturn(null).when(testling).getCurrentTestCase();
    }

    private String getTemplatePath() {
        // If not available execute test via `mvn test-compile` to extract the file dependencies
        return getResource("common/config/templates");
    }

    private String getOutputPath() {
        return getResource("output", this.getClass());
    }

    private String loadExpectedOutput(String testCaseName) throws IOException {
        return FileUtils.readFileToString(Paths.get(getOutputPath() + File.separator + "TestSuite_" + testCaseName + ".txt").toFile());
    }

    @Test
    public void testOK() throws Exception {
        testSuite.setState(TestSuiteState.OK);
        testSuite.setWarningTime(300);
        testSuite.setCriticalTime(400);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 30, 44, 990).toDate());
        testSuite.addTestCase(
                    new TestCaseExampleBuilder()
                            .withState(TestCaseState.OK)
                            .withWarningTime(20)
                            .withCriticalTime(30)
                            .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                            .withStopDate(new DateTime(1970, 1, 1, 10, 30, 14, 20).toDate())
                            .withId("case1")
                            .withTestCaseSteps(
                                    Arrays.asList(
                                            new TestCaseStepExampleBuilder()
                                                    .withState(TestCaseStepState.OK)
                                                    .withName("Test_Sahi_landing_page")
                                                    .withWarningTime(5)
                                                    .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                    .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 160).toDate())
                                                    .buildExample(),
                                            new TestCaseStepExampleBuilder()
                                                    .withState(TestCaseStepState.OK)
                                                    .withName("Calculation")
                                                    .withWarningTime(10)
                                                    .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 10).toDate())
                                                    .withStopDate(new DateTime(1970, 1, 1, 10, 30, 7, 290).toDate())
                                                    .buildExample(),
                                            new TestCaseStepExampleBuilder()
                                                    .withState(TestCaseStepState.OK)
                                                    .withName("Editor")
                                                    .withWarningTime(10)
                                                    .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 20).toDate())
                                                    .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 500).toDate())
                                                    .buildExample()
                                    )
                            )
                            .buildExample()
        );
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                            .withState(TestCaseState.OK)
                            .withWarningTime(20)
                            .withCriticalTime(30)
                            .withStartDate(new DateTime(1970, 1, 1, 10, 30, 10).toDate())
                            .withStopDate(new DateTime(1970, 1, 1, 10, 30, 23, 580).toDate())
                            .withId("case2")
                            .withTestCaseSteps(
                                    Arrays.asList(
                                            new TestCaseStepExampleBuilder()
                                                    .withState(TestCaseStepState.OK)
                                                    .withName("Test_Sahi_landing_page_(case2)")
                                                    .withWarningTime(5)
                                                    .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0).toDate())
                                                    .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 30).toDate())
                                                    .buildExample(),
                                            new TestCaseStepExampleBuilder()
                                                    .withState(TestCaseStepState.OK)
                                                    .withName("Calculation_(case2)")
                                                    .withWarningTime(10)
                                                    .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 10).toDate())
                                                    .withStopDate(new DateTime(1970, 1, 1, 10, 30, 7, 80).toDate())
                                                    .buildExample(),
                                            new TestCaseStepExampleBuilder()
                                                    .withName("Editor_(case2)")
                                                    .withWarningTime(10)
                                                    .withStartDate(new DateTime(1970, 1, 1, 10, 30, 0, 20).toDate())
                                                    .withStopDate(new DateTime(1970, 1, 1, 10, 30, 1, 390).toDate())
                                                    .buildExample()
                                    )
                            )
                            .buildExample()
        );
        String output = testling.createOutput(testSuite);
        Assert.assertEquals(output, loadExpectedOutput(TestCaseState.OK.name()));
    }

    @Test
    public void testWarnInStep() throws Exception {
        testSuite.setState(TestSuiteState.WARNING_IN_STEP);
        testSuite.setWarningTime(300);
        testSuite.setCriticalTime(400);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 31, 44, 750).toDate());
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 13, 830).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 550).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        testSuite.addTestCase(new TestCaseExampleBuilder()
                        .withState(TestCaseState.WARNING_IN_STEP)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 31, 20).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 31, 33, 430).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 0, 930).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.WARNING)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(1)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 7, 20).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 31, 0, 30).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 31, 1, 420).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        String output = testling.createOutput(testSuite);
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.WARNING_IN_STEP.name()));
    }

    @Test
    public void testWarnInCase() throws Exception {
        testSuite.setState(TestSuiteState.WARNING_IN_CASE);
        testSuite.setWarningTime(300);
        testSuite.setCriticalTime(400);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 34, 42, 840).toDate());
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 34, 14, 30).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 7, 340).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 30).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 460).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.WARNING)
                        .withWarningTime(2)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 34, 20).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 34, 33, 540).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 0, 940).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 7, 140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 34, 0, 30).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 34, 1, 390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        String output = testling.createOutput(testSuite);
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.WARNING_IN_CASE.name()));
    }

    @Test
    public void testCritInCase() throws Exception {
        testSuite.setState(TestSuiteState.CRITICAL_IN_CASE);
        testSuite.setWarningTime(300);
        testSuite.setCriticalTime(400);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 35, 46, 960).toDate());
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 35, 14, 130).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 280).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 7, 310).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 470).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.CRITICAL)
                        .withWarningTime(2)
                        .withCriticalTime(3)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 35, 20).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 35, 33, 700).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 7, 120).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 35, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 35, 1, 440).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        String output = testling.createOutput(testSuite);
        String expected = loadExpectedOutput(TestSuiteState.CRITICAL_IN_CASE.name());
        Assert.assertEquals(output, expected);
        Assert.assertEquals(output.getBytes(), expected.getBytes());
    }

    @Test
    public void testWarnInSuite() throws Exception {
        testSuite.setState(TestSuiteState.WARNING_IN_SUITE);
        testSuite.setWarningTime(3);
        testSuite.setCriticalTime(400);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 32, 46, 940).toDate());
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 32, 14, 130).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 210).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 7, 410).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 430).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 32, 10).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 32, 23, 580).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 60).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 7, 80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 32, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 32, 1, 360).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        String output = testling.createOutput(testSuite);
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.WARNING_IN_SUITE.name()));
    }

    @Test
    public void testCritInSuite() throws Exception {
        testSuite.setState(TestSuiteState.CRITICAL_IN_SUITE);
        testSuite.setWarningTime(30);
        testSuite.setCriticalTime(40);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 33, 44, 810).toDate());
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 33, 13, 910).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 1, 160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 7, 250).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 1, 450).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 33, 10).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 33, 23, 550).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 1, 50).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 17, 30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 33, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 33, 21, 390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        String output = testling.createOutput(testSuite);
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.CRITICAL_IN_SUITE.name()));
    }

    @Test
    public void testException() throws Exception {
        testSuite.setState(TestSuiteState.ERRORS);
        testSuite.setWarningTime(300);
        testSuite.setCriticalTime(400);
        testSuite.setStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate());
        testSuite.setStopDate(new DateTime(1970, 1, 1, 10, 36, 44, 800).toDate());
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 36, 14, 200).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 7, 540).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 470).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        testSuite.addTestCase(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.ERRORS)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970, 1, 1, 10, 36, 10).toDate())
                        .withStopDate(new DateTime(1970, 1, 1, 10, 36, 23, 550).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.ERRORS)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 0).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 1, 50).toDate())
                                                .withException(new SakuliException("_highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a>"))
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 10).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 17, 30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970, 1, 1, 10, 36, 20).toDate())
                                                .withStopDate(new DateTime(1970, 1, 1, 10, 36, 21, 390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        );
        ScreenshotDiv screenshotDiv = new ScreenshotDiv();
        screenshotDiv.setId("sakuli_screenshot243575009");
        screenshotDiv.setFormat("jpg");
        screenshotDiv.setBase64screenshot("/9j/4AAQSkZJRgABAgAAAQABAAD9k=");
        doReturn(screenshotDiv).when(screenshotDivConverter).convert(notNull(Throwable.class));
        String output = testling.createOutput(testSuite);
        Assert.assertEquals(output, loadExpectedOutput(TestSuiteState.ERRORS.name()));
    }
}
