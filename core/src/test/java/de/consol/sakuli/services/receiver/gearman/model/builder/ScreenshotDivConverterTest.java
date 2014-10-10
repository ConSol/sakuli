package de.consol.sakuli.services.receiver.gearman.model.builder;

import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.exceptions.SakuliExceptionWithScreenshot;
import de.consol.sakuli.exceptions.SakuliReceiverException;
import de.consol.sakuli.services.receiver.gearman.GearmanProperties;
import de.consol.sakuli.services.receiver.gearman.model.ScreenshotDiv;
import org.mockito.*;
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

    private final String base64String = "iVBORw0KGgoAAAANSUhEUgAAAE4AAAAQCAIAAAA3TN7NAAAAA3NCSVQICAjb4U/gAAAAGXRFWHRT\r\n" +
            "b2Z0d2FyZQBnbm9tZS1zY3JlZW5zaG907wO/PgAABPdJREFUSInllX9MU1cUx89997YIVGgV2bIh\r\n" +
            "AwXJWBSxAwRECuPHQAGVkMkQM4W5AYpMxR/RwRSjcYrBwVRANjEiDAY4lOAQg4zfY7gtGDPZ1HT8\r\n" +
            "UIHSlgJiLO3+eOxSaAvG4bJk39w05577Oefec997p+jW3U74f4ioVGoAaG2qetT1YFracoH9Ykf3\r\n" +
            "l3+qlyIyqlIBoEddD/x93rW1tZsClUj68gvz7B3cANRai0iXUxvQxKYNmSL8BWKBKEdVrMPW1s43\r\n" +
            "ImOKiBv58QCgHB197j3+WyIq9VipXC4XAFydFqkmEgyACqC5pZ2dUl5bneIHOafTbv/aijFxcvXY\r\n" +
            "HLtdMMfsJZ1bU0Gewis1rdNijFoNajUAgFKpBACGYIIxxpgQzBBMCAME9/YP0gCW1x6PH3bvS/jI\r\n" +
            "2W1lbnFldn6Zg9A5Oz1VHzyzY4pTaQ4GIUAIAEClUgEAYjAiDEMwwgzGGGHyZ1f/4/5hWirLa4/8\r\n" +
            "3Kygde8FhIQaGRvNNjHxCQjanXyUXVIMyD/dGRPq55q0K3ZQIWedwSLh1ZKCyDU+G9f6/lhfU/bt\r\n" +
            "pQ0h3pFrfBpqqijQ+MON8NWi3XEfyKQS6qQ7snawSAgAwSIhOx0eUhzaGx/q55qcGPdkeJCSdTcr\r\n" +
            "mUlPGROMMcEY9fYPi7v629ofDo6MEg6hd6FPt5obvf1X61zKzfzCQej8TXnNEse3L2SPt4O+nsdZ\r\n" +
            "ly5Hxe04nrL/XvtvZy+Wbvp4+7kvT1Kg7ZfW3OIK15Xe58+e0rcv++peqWlljdzMdBd3UWFFbWBI\r\n" +
            "2MWcMxSTS6UMgxgGjRfMYKZPNtT2R0+v7MnQUxUmHEww4eBxADE6x4BcajbPXOdSS2PtqpAwA4NZ\r\n" +
            "gSFhzXU1dMeo2E94PBMPL7+nI08S9n5maioQ+QZKensosDE6ztDQ2H/V2p+a6qmTpqW2prOp7uYK\r\n" +
            "kQ+XwxW6uDXWVlNgqdCZoImXdPteDwBgwmEbNAJQT+zsk3gq3myTAalUMFdHH5LLpMbGPATA4/Hk\r\n" +
            "MhnNwBpcDgcAOISwtlqtpgCPN5v9HVQoJkVNsqkhl8nCg7zGngqDqf8Nq4WEQWNTtgP3irv01DIm\r\n" +
            "yk+Sg6NTbXXlmrAI7SUTU/6QYsCUL1AoFKZ8Ps2gmUqnPTI8bGRsrFAMCObMpc5RpZLD4Tx79kyT\r\n" +
            "pIYpn3++sNzAYJb2sQn8DXV0dFzP2zpFkd3d3QAAekp9f9OWPfEfmvAF7p7vPB0Zaait/rmlad/B\r\n" +
            "YwCw3N2zrKRgfWT0d8X5Lu6e4xk0U+my8y9kb4iKuXa1dPmKsah55q9WXbvi4eWblZFKSasFNp0d\r\n" +
            "YgtLKwBw9/T5/urlgODQ3+/eqSgr3rk/heYkCCEAmG/9ZlFJwRR1srKxFyI9pVovXHTo8/ScM6fS\r\n" +
            "jh3kcg3cPLxiEvaw8OaYhCNJiev83d5a4rg/5QTNoJlKp21jZx8e7G29cFHy0TTWuWXbrvQTh/O+\r\n" +
            "ztyWeKCy/DLrPHA4NSlxq5n5K8czvoqOTUg9kpR9+qSZmXlkVKzmXqjlTodc2ldfXbpje6JSqRSL\r\n" +
            "xRYWFprN9v79++xfro2NTea500udRK/Nt532Uv65fN0WX29om8GEBCGory5dHxbBvp+GhoYSiUST\r\n" +
            "EAgErCGXy9eHRRQU5b1u+W+UCnq/lRcUYRi0zMWroCjvOQOWuXgxzIweQb9mdqO/AAbcJzu9/py2\r\n" +
            "AAAAAElFTkSuQmCC";

    @Mock
    private SakuliExceptionHandler sakuliExceptionHandler;
    @Mock
    private GearmanProperties gearmanProperties;
    @Spy
    @InjectMocks
    private ScreenshotDivConverter testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(gearmanProperties.getOutputScreenshotDivWidth()).thenReturn("600px");
    }

    @Test
    public void testBuild() throws Exception {
        assertNull(new ScreenshotDivConverter().convert(null));
    }

    @Test
    public void testWithException() throws Exception {
        Path screenshotPath = Paths.get(OutputBuilder.class.getResource("computer.png").toURI());
        assertTrue(Files.exists(screenshotPath));
        ScreenshotDiv result = testling.convert(new SakuliExceptionWithScreenshot("test", screenshotPath));
        assertNotNull(result);
        assertEquals(result.getId(), ScreenshotDiv.DEFAULT_SAKULI_SCREENSHOT_DIV_ID);
        assertEquals(result.getWidth(), "600px");
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
        verify(sakuliExceptionHandler).handleException(any(SakuliReceiverException.class));
        Throwable excp = excpCaptor.getValue();
        assertTrue(excp instanceof SakuliReceiverException);
        assertEquals(excp.getMessage(), "error during the BASE64 encoding of the screenshot 'computerNOTVALID.png'");
        assertTrue(excp.getSuppressed()[0] instanceof NoSuchFileException);
    }
}