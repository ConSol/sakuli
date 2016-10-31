/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.forwarder.checkmk;

import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.builder.TestCaseExampleBuilder;
import org.sakuli.builder.TestCaseStepExampleBuilder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.datamodel.state.TestCaseStepState;
import org.sakuli.datamodel.state.TestSuiteState;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.services.forwarder.ScreenshotDivConverter;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Georgi Todorov
 */
public class CheckMKTemplateOutputBuilderTest extends BaseTest {

    private static final String EXPECTED_OUTPUT_OK = "0 sakuli_0_OK suite__state=0;;;;|suite__warning=300s;;;;|suite__critical=400s;;;;|suite_example_xfce=44.99s;300;400;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=14.02s;20;30;;|s_001_001_Test_Sahi_landing_page=1.16s;5;;;|s_001_002_Calculation=7.29s;10;;;|s_001_003_Editor=1.5s;10;;;|c_002__state=0;;;;|c_002__warning=20s;;;;|c_002__critical=30s;;;;|c_002_case2=13.58s;20;30;;|s_002_001_Test_Sahi_landing_page_(case2)=1.03s;5;;;|s_002_002_Calculation_(case2)=7.08s;10;;;|s_002_003_Editor_(case2)=1.39s;10;;; Sakuli suite \"example_xfce\" ok (44.99s). (Last suite run: 01.01.70 10:30:44)";
    private static final String EXPECTED_OUTPUT_WARN_IN_STEP = "1 sakuli_1_WARNING_IN_STEP suite__state=1;;;;|suite__warning=300s;;;;|suite__critical=400s;;;;|suite_example_xfce=44.75s;300;400;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=13.83s;20;30;;|s_001_001_Test_Sahi_landing_page=1.08s;5;;;|s_001_002_Calculation=7.14s;10;;;|s_001_003_Editor=1.55s;10;;;|c_002__state=1;;;;|c_002__warning=20s;;;;|c_002__critical=30s;;;;|c_002_case2=13.43s;20;30;;|s_002_001_Test_Sahi_landing_page_(case2)=0.93s;5;;;|s_002_002_Calculation_(case2)=7.02s;1;;;|s_002_003_Editor_(case2)=1.42s;10;;; Sakuli suite \"example_xfce\" warning in step, step \"Calculation_(case2)\" over runtime (7.02s/warn at 1s). (Last suite run: 01.01.70 10:31:44)";
    private static final String EXPECTED_OUTPUT_WARN_IN_CASE = "1 sakuli_2_WARNING_IN_CASE suite__state=2;;;;|suite__warning=300s;;;;|suite__critical=400s;;;;|suite_example_xfce=42.84s;300;400;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=14.03s;20;30;;|s_001_001_Test_Sahi_landing_page=1.16s;5;;;|s_001_002_Calculation=7.34s;10;;;|s_001_003_Editor=1.46s;10;;;|c_002__state=2;;;;|c_002__warning=2s;;;;|c_002__critical=30s;;;;|c_002_case2=13.54s;2;30;;|s_002_001_Test_Sahi_landing_page_(case2)=0.94s;5;;;|s_002_002_Calculation_(case2)=7.14s;10;;;|s_002_003_Editor_(case2)=1.39s;10;;; Sakuli suite \"example_xfce\" warning in case, case \"case2\" over runtime (13.54s/warn at 2s). (Last suite run: 01.01.70 10:34:42)";
    private static final String EXPECTED_OUTPUT_WARN_IN_SUITE = "1 sakuli_3_WARNING_IN_SUITE suite__state=3;;;;|suite__warning=3s;;;;|suite__critical=400s;;;;|suite_example_xfce=46.94s;3;400;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=14.13s;20;30;;|s_001_001_Test_Sahi_landing_page=1.21s;5;;;|s_001_002_Calculation=7.41s;10;;;|s_001_003_Editor=1.43s;10;;;|c_002__state=0;;;;|c_002__warning=20s;;;;|c_002__critical=30s;;;;|c_002_case2=13.58s;20;30;;|s_002_001_Test_Sahi_landing_page_(case2)=1.06s;5;;;|s_002_002_Calculation_(case2)=7.08s;10;;;|s_002_003_Editor_(case2)=1.36s;10;;; Sakuli suite \"example_xfce\" warning (46.94s/warn at 3s). (Last suite run: 01.01.70 10:32:46)";
    private static final String EXPECTED_OUTPUT_CRIT_IN_SUITE = "2 sakuli_5_CRITICAL_IN_SUITE suite__state=5;;;;|suite__warning=30s;;;;|suite__critical=40s;;;;|suite_example_xfce=44.81s;30;40;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=13.91s;20;30;;|s_001_001_Test_Sahi_landing_page=1.16s;5;;;|s_001_002_Calculation=7.25s;10;;;|s_001_003_Editor=1.45s;10;;;|c_002__state=0;;;;|c_002__warning=20s;;;;|c_002__critical=30s;;;;|c_002_case2=13.55s;20;30;;|s_002_001_Test_Sahi_landing_page_(case2)=1.05s;5;;;|s_002_002_Calculation_(case2)=7.03s;10;;;|s_002_003_Editor_(case2)=1.39s;10;;; Sakuli suite \"example_xfce\" critical (44.81s/crit at 40s). (Last suite run: 01.01.70 10:33:44)";
    private static final String EXPECTED_OUTPUT_CRIT_IN_CASE = "2 sakuli_4_CRITICAL_IN_CASE suite__state=4;;;;|suite__warning=300s;;;;|suite__critical=400s;;;;|suite_example_xfce=46.96s;300;400;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=14.13s;20;30;;|s_001_001_Test_Sahi_landing_page=1.28s;5;;;|s_001_002_Calculation=7.31s;10;;;|s_001_003_Editor=1.47s;10;;;|c_002__state=3;;;;|c_002__warning=2s;;;;|c_002__critical=3s;;;;|c_002_case2=13.7s;2;3;;|s_002_001_Test_Sahi_landing_page_(case2)=1.08s;5;;;|s_002_002_Calculation_(case2)=7.12s;10;;;|s_002_003_Editor_(case2)=1.44s;10;;; Sakuli suite \"example_xfce\" critical in case, case \"case2\" over runtime (13.7s/crit at 3s). (Last suite run: 01.01.70 10:35:46)";
    private static final String EXPECTED_OUTPUT_EXCEPTION = "2 sakuli_6_ERRORS suite__state=6;;;;|suite__warning=300s;;;;|suite__critical=400s;;;;|suite_example_xfce=44.8s;300;400;;|c_001__state=0;;;;|c_001__warning=20s;;;;|c_001__critical=30s;;;;|c_001_case1=14.2s;20;30;;|s_001_001_Test_Sahi_landing_page=1.14s;5;;;|s_001_002_Calculation=7.54s;10;;;|s_001_003_Editor=1.47s;10;;;|c_002__state=4;;;;|c_002__warning=20s;;;;|c_002__critical=30s;;;;|c_002_case2=13.55s;20;30;;|s_002_001_Test_Sahi_landing_page_(case2)=1.05s;5;;;|s_002_002_Calculation_(case2)=7.03s;10;;;|s_002_003_Editor_(case2)=1.39s;10;;; Sakuli suite \"example_xfce\" (44.8s) EXCEPTION: CASE \"case2\": STEP \"Test_Sahi_landing_page_(case2)\": _highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sah ... <style>.modalDialog {width: 640px;}.modalDialog:target {width: auto;margin: 20px auto;overflow: scroll;position: fixed;top: 0;right: 0;bottom: 0;left: 0;z-index: 99999;opacity: 1;pointer-events: auto;}.modalDialog:target .close {display: block;}.modalDialog:target .screenshot {width: 100%;border: 2px solid #333;}.screenshot {width: 98%;border: 2px solid  gray;display: block;margin-left: auto;margin-right: auto;margin-bottom: 4px;cursor: -webkit-zoom-in;cursor: -moz-zoom-in;}.close {display: none;background: #aaa;color: #fff;line-height: 25px;position: absolute;right: 10px;text-align: center;top: 25px;width: 65px;text-decoration: none;font-weight: bold;-webkit-border-radius: 12px;-moz-border-radius: 12px;border-radius: 12px;}.close:hover {background: #333;}</style><table style=\"border-collapse: collapse;\"><tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] Sakuli suite \"example_xfce\" (44.8s) EXCEPTION: CASE \"case2\": STEP \"Test_Sahi_landing_page_(case2)\": _highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a>. (Last suite run: 01.01.70 10:36:44)</td></tr><tr valign=\"top\"><td class=\"serviceOK\">[OK] case \"case1\" ran in 14.2s - ok</td></tr>" +
            "<tr valign=\"top\"><td class=\"serviceCRITICAL\">[CRIT] case \"case2\" EXCEPTION: STEP \"Test_Sahi_landing_page_(case2)\": _highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a><div id=\"sakuli_screenshot243575009\"><div id=\"openModal_sakuli_screenshot243575009\" class=\"modalDialog\"><a href=\"#close\" title=\"Close\" class=\"close\">Close X</a><a href=\"#openModal_sakuli_screenshot243575009\"><img class=\"screenshot\" src=\"data:image/jpg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD9k=\" ></a></div></div></td></tr></table>";

    @InjectMocks
    private CheckMKTemplateOutputBuilder testling;
    @Mock
    private ScreenshotDivConverter screenshotDivConverter;
    @Mock
    private SakuliExceptionHandler exceptionHandler;
    @Mock
    private CheckMKProperties checkMKProperties;
    @Mock
    private TestSuite testSuite;
    @Mock
    private SakuliProperties sakuliProperties;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(getTemplatePath()).when(sakuliProperties).getForwarderTemplateFolder();
    }

    private String getTemplatePath() {
        String fileSeparator = System.getProperty("file.separator");
        String currentAbsolutePath = Paths.get("").toAbsolutePath().toString();
        String canonicalNameDir = this.getClass().getPackage().getName().replaceAll("\\.", "/");
        return currentAbsolutePath + fileSeparator
                + "src" + fileSeparator
                + "test" + fileSeparator
                + "resources" + fileSeparator
                + canonicalNameDir + fileSeparator
                + "templates";
    }

    @Test
    public void testOK() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.OK).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(44.99f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,30,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,30,44,99).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,30,14,20).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,30,1,160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,30,7,290).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,30,1,500).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,30,10).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,30,23,580).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,30,1,30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,30,7,80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,30,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,30,1,390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_OK);
    }

    @Test
    public void testWarnInStep() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.WARNING_IN_STEP).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(44.75f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,31,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,31,44,750).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,31,13,830).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,31,1,80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,31,7,140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,31,1,550).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.WARNING_IN_STEP)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,31,20).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,31,33,430).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,31,0,930).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.WARNING)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(1)
                                                .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,31,7,20).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,31,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,31,1,420).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_WARN_IN_STEP);
    }

    @Test
    public void testWarnInCase() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.WARNING_IN_CASE).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(42.84f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,34,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,34,42,840).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,34,14,30).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,34,1,160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,34,7,340).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,34,1,460).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.WARNING)
                        .withWarningTime(2)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,34,20).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,34,33,540).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,34,0,940).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,34,7,140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,34,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,34,1,390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_WARN_IN_CASE);
    }

    @Test
    public void testCritInCase() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.CRITICAL_IN_CASE).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(46.96f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,35,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,35,46,960).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,35,14,130).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,35,1,280).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,35,7,310).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,35,1,470).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.CRITICAL)
                        .withWarningTime(2)
                        .withCriticalTime(3)
                        .withStartDate(new DateTime(1970,1,1,10,35,20).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,35,33,700).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,35,1,80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,35,7,120).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,35,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,35,1,440).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_CRIT_IN_CASE);
    }

    @Test
    public void testWarnInSuite() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.WARNING_IN_SUITE).when(testSuite).getState();
        doReturn(3).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(46.94f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,32,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,32,46,940).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,32,14,130).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,32,1,210).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,32,7,410).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,32,1,430).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,32,10).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,32,23,580).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,32,1,60).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,32,7,80).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,32,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,32,1,360).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_WARN_IN_SUITE);
    }

    @Test
    public void testCritInSuite() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.CRITICAL_IN_SUITE).when(testSuite).getState();
        doReturn(30).when(testSuite).getWarningTime();
        doReturn(40).when(testSuite).getCriticalTime();
        doReturn(44.81f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,33,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,33,44,810).toDate()).when(testSuite).getStopDate();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,33,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,33,13,910).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,33,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,33,1,160).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,33,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,33,7,250).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,33,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,33,1,450).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,33,10).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,33,23,550).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,33,00).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,33,1,50).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,33,10).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,33,17,30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,33,20).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,33,21,390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_CRIT_IN_SUITE);
    }

    @Test
    public void testException() {
        doReturn("example_xfce").when(testSuite).getId();
        doReturn(TestSuiteState.ERRORS).when(testSuite).getState();
        doReturn(300).when(testSuite).getWarningTime();
        doReturn(400).when(testSuite).getCriticalTime();
        doReturn(44.80f).when(testSuite).getDuration();
        doReturn(new DateTime(1970,1,1,10,36,0).toDate()).when(testSuite).getStartDate();
        doReturn(new DateTime(1970,1,1,10,36,44,800).toDate()).when(testSuite).getStopDate();
        when(testSuite.getExceptionMessages(anyBoolean(), any())).thenCallRealMethod();
        when(testSuite.getException()).thenCallRealMethod();
        SortedSet<TestCase> testCaseAsSortedSet = new TreeSet<>(Arrays.asList(
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.OK)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,36,0).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,36,14,200).toDate())
                        .withId("case1")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Test_Sahi_landing_page")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,36,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,36,1,140).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,36,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,36,7,540).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,36,0).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,36,1,470).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample(),
                new TestCaseExampleBuilder()
                        .withState(TestCaseState.ERRORS)
                        .withWarningTime(20)
                        .withCriticalTime(30)
                        .withStartDate(new DateTime(1970,1,1,10,36,10).toDate())
                        .withStopDate(new DateTime(1970,1,1,10,36,23,550).toDate())
                        .withId("case2")
                        .withTestCaseSteps(
                                Arrays.asList(
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.ERRORS)
                                                .withName("Test_Sahi_landing_page_(case2)")
                                                .withWarningTime(5)
                                                .withStartDate(new DateTime(1970,1,1,10,36,00).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,36,1,50).toDate())
                                                .withException(new SakuliException("_highlight(_link(\"xSL Manager\")); TypeError: el is undefined Sahi.prototype._highlight@http://sahi.example.com/_s_/spr/concat.js:1210:9 @http://sahi.example.com/_s_/spr/concat.js line 3607 > eval:1:1 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/concat.js:3607:9 Sahi.prototype.ex@http://sahi.example.com/_s_/spr/sakuli/inject.js:46:12 @http://sahi.example.com/_s_/spr/concat.js:3373:5  <a href='/_s_/dyn/Log_getBrowserScript?href=/root/sakuli/example_test_suites/example_xfce/case2/sakuli_demo.js&n=1210'><b>Click for browser script</b></a>"))
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Calculation_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,36,10).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,36,17,30).toDate())
                                                .buildExample(),
                                        new TestCaseStepExampleBuilder()
                                                .withState(TestCaseStepState.OK)
                                                .withName("Editor_(case2)")
                                                .withWarningTime(10)
                                                .withStartDate(new DateTime(1970,1,1,10,36,20).toDate())
                                                .withStopDate(new DateTime(1970,1,1,10,36,21,390).toDate())
                                                .buildExample()
                                )
                        )
                        .buildExample()
        ));
        doReturn(testCaseAsSortedSet).when(testSuite).getTestCasesAsSortedSet();
        ScreenshotDiv screenshotDiv = new ScreenshotDiv();
        screenshotDiv.setId("sakuli_screenshot243575009");
        screenshotDiv.setFormat("jpg");
        screenshotDiv.setBase64screenshot("/9j/4AAQSkZJRgABAgAAAQABAAD9k=");
        doReturn(screenshotDiv).when(screenshotDivConverter).convert(notNull(Throwable.class));
        String output = testling.createOutput();
        Assert.assertEquals(output, EXPECTED_OUTPUT_EXCEPTION);
    }

}
