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
        assertNotNull(result.getBase64screenshot());
        verify(sakuliExceptionHandler, never()).handleException(any(Exception.class));
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