package de.consol.sakuli.aop;

import de.consol.sakuli.actions.environment.Environment;
import de.consol.sakuli.actions.screenbased.Region;
import de.consol.sakuli.actions.screenbased.TypingUtil;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.loader.ScreenActionLoader;
import org.sikuli.script.IScreen;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;

import java.awt.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ModifySahiTimerAspectTest extends AopBaseTest {

    /**
     * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
     * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
     * with the option 'Execute After Make'.
     */
    @Test
    public void testCallEnvironmentMethod() throws Exception {
        ScreenActionLoader loaderMock = mock(ScreenActionLoader.class);
        TypingUtil typingUtil = mock(TypingUtil.class);
        when(typingUtil.type(anyString(), anyString())).thenReturn(null);
        Environment env = new Environment(false, loaderMock);
        ReflectionTestUtils.setField(env, "typingUtil", typingUtil);
        env.type("BLA");
        assertLastLine("SAHI-TIMER", LogLevel.DEBUG, "MODIFY SAHI-TIMER for Environment.type()");
    }

    @Test
    public void testCallRegionMethod() throws Exception {
        ScreenActionLoader loaderMock = mock(ScreenActionLoader.class);
        TypingUtil typingUtil = mock(TypingUtil.class);
        when(typingUtil.type(anyString(), anyString())).thenReturn(null);

        org.sikuli.script.Region sikuliRegionMock = mock(org.sikuli.script.Region.class);
        IScreen iScreen = mock(IScreen.class);
        when(iScreen.getRect()).thenReturn(new Rectangle());
        when(sikuliRegionMock.getScreen()).thenReturn(iScreen);

        Region region = new Region(sikuliRegionMock, false, loaderMock);
        ReflectionTestUtils.setField(region, "typingUtil", typingUtil);

        region.type("BLA");
        assertLastLine("SAHI-TIMER", LogLevel.DEBUG, "MODIFY SAHI-TIMER for Region.type()");
    }
}