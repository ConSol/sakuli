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

package org.sakuli.services.forwarder;

import org.mockito.*;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliExceptionWithScreenshot;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.gearman.model.ScreenshotDiv;
import org.sakuli.services.forwarder.gearman.model.builder.NagiosOutputBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ScreenshotDivConverterTest {

    private static final String SCREENSHOT_DIV_WIDTH_DEFAULT = "640px";
    private final String base64String = "iVBORw0KGgoAAAANSUhEUgAAAE4AAAAQCAIAAAA3TN7NAAAAA3NCSVQICAjb4U/gAAAAGXRFWHRT" +
            "b2Z0d2FyZQBnbm9tZS1zY3JlZW5zaG907wO/PgAABPdJREFUSInllX9MU1cUx89997YIVGgV2bIh" +
            "AwXJWBSxAwRECuPHQAGVkMkQM4W5AYpMxR/RwRSjcYrBwVRANjEiDAY4lOAQg4zfY7gtGDPZ1HT8" +
            "UIHSlgJiLO3+eOxSaAvG4bJk39w05577Oefec997p+jW3U74f4ioVGoAaG2qetT1YFracoH9Ykf3" +
            "l3+qlyIyqlIBoEddD/x93rW1tZsClUj68gvz7B3cANRai0iXUxvQxKYNmSL8BWKBKEdVrMPW1s43" +
            "ImOKiBv58QCgHB197j3+WyIq9VipXC4XAFydFqkmEgyACqC5pZ2dUl5bneIHOafTbv/aijFxcvXY" +
            "HLtdMMfsJZ1bU0Gewis1rdNijFoNajUAgFKpBACGYIIxxpgQzBBMCAME9/YP0gCW1x6PH3bvS/jI" +
            "2W1lbnFldn6Zg9A5Oz1VHzyzY4pTaQ4GIUAIAEClUgEAYjAiDEMwwgzGGGHyZ1f/4/5hWirLa4/8" +
            "3Kygde8FhIQaGRvNNjHxCQjanXyUXVIMyD/dGRPq55q0K3ZQIWedwSLh1ZKCyDU+G9f6/lhfU/bt" +
            "pQ0h3pFrfBpqqijQ+MON8NWi3XEfyKQS6qQ7snawSAgAwSIhOx0eUhzaGx/q55qcGPdkeJCSdTcr" +
            "mUlPGROMMcEY9fYPi7v629ofDo6MEg6hd6FPt5obvf1X61zKzfzCQej8TXnNEse3L2SPt4O+nsdZ" +
            "ly5Hxe04nrL/XvtvZy+Wbvp4+7kvT1Kg7ZfW3OIK15Xe58+e0rcv++peqWlljdzMdBd3UWFFbWBI" +
            "2MWcMxSTS6UMgxgGjRfMYKZPNtT2R0+v7MnQUxUmHEww4eBxADE6x4BcajbPXOdSS2PtqpAwA4NZ" +
            "gSFhzXU1dMeo2E94PBMPL7+nI08S9n5maioQ+QZKensosDE6ztDQ2H/V2p+a6qmTpqW2prOp7uYK" +
            "kQ+XwxW6uDXWVlNgqdCZoImXdPteDwBgwmEbNAJQT+zsk3gq3myTAalUMFdHH5LLpMbGPATA4/Hk" +
            "MhnNwBpcDgcAOISwtlqtpgCPN5v9HVQoJkVNsqkhl8nCg7zGngqDqf8Nq4WEQWNTtgP3irv01DIm" +
            "yk+Sg6NTbXXlmrAI7SUTU/6QYsCUL1AoFKZ8Ps2gmUqnPTI8bGRsrFAMCObMpc5RpZLD4Tx79kyT" +
            "pIYpn3++sNzAYJb2sQn8DXV0dFzP2zpFkd3d3QAAekp9f9OWPfEfmvAF7p7vPB0Zaait/rmlad/B" +
            "YwCw3N2zrKRgfWT0d8X5Lu6e4xk0U+my8y9kb4iKuXa1dPmKsah55q9WXbvi4eWblZFKSasFNp0d" +
            "YgtLKwBw9/T5/urlgODQ3+/eqSgr3rk/heYkCCEAmG/9ZlFJwRR1srKxFyI9pVovXHTo8/ScM6fS" +
            "jh3kcg3cPLxiEvaw8OaYhCNJiev83d5a4rg/5QTNoJlKp21jZx8e7G29cFHy0TTWuWXbrvQTh/O+" +
            "ztyWeKCy/DLrPHA4NSlxq5n5K8czvoqOTUg9kpR9+qSZmXlkVKzmXqjlTodc2ldfXbpje6JSqRSL" +
            "xRYWFprN9v79++xfro2NTea500udRK/Nt532Uv65fN0WX29om8GEBCGory5dHxbBvp+GhoYSiUST" +
            "EAgErCGXy9eHRRQU5b1u+W+UCnq/lRcUYRi0zMWroCjvOQOWuXgxzIweQb9mdqO/AAbcJzu9/py2" +
            "AAAAAElFTkSuQmCC";

    @Mock
    private SakuliExceptionHandler sakuliExceptionHandler;
    @Spy
    @InjectMocks
    private ScreenshotDivConverter testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuild() throws Exception {
        final ScreenshotDivConverter screenshotDivConverter = new ScreenshotDivConverter();
        assertNull(screenshotDivConverter.convert(null));
    }

    @Test
    public void testWithException() throws Exception {
        Path screenshotPath = Paths.get(NagiosOutputBuilder.class.getResource("computer.png").toURI());
        assertTrue(Files.exists(screenshotPath));

        ScreenshotDiv result = testling.convert(new SakuliExceptionWithScreenshot("test", screenshotPath));
        assertNotNull(result);
        assertEquals(result.getId(), ScreenshotDiv.DEFAULT_SAKULI_SCREENSHOT_DIV_ID + result.hashCode());
        assertEquals(result.getBase64screenshot(), base64String);
        assertEquals(result.getFormat(), "png");
        verify(sakuliExceptionHandler, never()).handleException(any(Exception.class));
    }

    @Test
    public void testExtractScreenshotName() throws Exception {
        assertEquals(
                testling.extractScreenshotFormat(new SakuliExceptionWithScreenshot("test", Paths.get("computer.new.png"))),
                "png");
        assertEquals(
                testling.extractScreenshotFormat(new SakuliExceptionWithScreenshot("test", Paths.get("computer.new.jpg"))),
                "jpg");
        assertEquals(
                testling.extractScreenshotFormat(new SakuliExceptionWithScreenshot("test", Paths.get("bsald_w.jpg"))),
                "jpg");

        assertEquals(
                testling.extractScreenshotFormat(new SakuliExceptionWithScreenshot("test", null)),
                null);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testThrowException() throws Exception {
        Path screenshotPath = Paths.get("computerNOTVALID.png");

        ArgumentCaptor<Throwable> excpCaptor = ArgumentCaptor.forClass(Throwable.class);
        doNothing().when(sakuliExceptionHandler).handleException(excpCaptor.capture());

        ScreenshotDiv result = testling.convert(new SakuliExceptionWithScreenshot("test", screenshotPath));
        assertNull(result);
        verify(sakuliExceptionHandler).handleException(any(SakuliForwarderException.class));
        Throwable excp = excpCaptor.getValue();
        assertTrue(excp instanceof SakuliForwarderException);
        assertEquals(excp.getMessage(), "error during the BASE64 encoding of the screenshot 'computerNOTVALID.png'");
        assertTrue(excp.getSuppressed()[0] instanceof NoSuchFileException);
    }


    @Test
    public void testRemoveBase64Data() throws Exception {
        ScreenshotDiv testling = new ScreenshotDiv();
        testling.setId("test-id");
        testling.setFormat("jpg");
        testling.setBase64screenshot("00001111");

        String result = ScreenshotDivConverter.removeBase64ImageDataString(testling.getPayloadString());
        assertEquals(result, "<div id=\"test-id\">" +
                    "<div id=\"openModal_test-id\" class=\"modalDialog\">" +
                        "<a href=\"#close\" title=\"Close\" class=\"close\">Close X</a>" +
                        "<a href=\"#openModal_test-id\"><img class=\"screenshot\" src=\"\" ></a>" +
                    "</div>" +
                "</div>");

        String srcString2 = "blas\nblakdfakdfjie";
        assertEquals(srcString2, ScreenshotDivConverter.removeBase64ImageDataString(srcString2));
    }
}