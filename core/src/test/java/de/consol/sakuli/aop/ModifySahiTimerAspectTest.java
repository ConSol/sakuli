package de.consol.sakuli.aop;

import de.consol.sakuli.actions.environment.Environment;
import de.consol.sakuli.actions.screenbased.Region;
import de.consol.sakuli.actions.screenbased.RegionImpl;
import de.consol.sakuli.actions.screenbased.TypingUtil;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.datamodel.properties.SahiProxyProperties;
import de.consol.sakuli.loader.BaseActionLoader;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.loader.ScreenActionLoader;
import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class ModifySahiTimerAspectTest extends AopBaseTest {

    @Spy
    private ModifySahiTimerAspect testling;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

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

        Region region = new Region(mock(RegionImpl.class), false, loaderMock);
        ReflectionTestUtils.setField(region, "typingUtil", typingUtil);
        region.type("BLA");
        assertLastLine("SAHI-TIMER", LogLevel.DEBUG, "MODIFY SAHI-TIMER for Region.type()");
    }

    @Test
    public void testModifySahiTimer() throws Exception {
        RhinoScriptRunner runner = mock(RhinoScriptRunner.class);
        Session session = mock(Session.class);
        when(runner.getSession()).thenReturn(session);
        BaseActionLoader baseActionLoader = BeanLoader.loadBaseActionLoader();
        baseActionLoader.setRhinoScriptRunner(runner);
        when(baseActionLoader.getSahiProxyProperties().isRequestDelayActive()).thenReturn(true);
        doReturn(1000).when(testling).determineDelay(any(JoinPoint.class), any(BaseActionLoader.class));

        doReturn(LoggerFactory.getLogger(this.getClass())).when(testling).getLogger(any(JoinPoint.class));
        doReturn("sig.method()").when(testling).getClassAndMethodAsString(any(JoinPoint.class));

        //test modifcation of timer to 1000ms
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, "1000");
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));
        assertLastLine("sahi-proxy-timer", LogLevel.INFO, "sahi-proxy-timer modified to 1000 ms");

        //test reset timer
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session).setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, null);
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));
        assertLastLine("sahi-proxy-timer", LogLevel.INFO, "reset sahi-proxy-timer");
    }

    @Test
    public void testDisabledModifySahiTimer() throws Exception {
        RhinoScriptRunner runner = mock(RhinoScriptRunner.class);
        Session session = mock(Session.class);
        when(runner.getSession()).thenReturn(session);
        BaseActionLoader baseActionLoader = BeanLoader.loadBaseActionLoader();
        baseActionLoader.setRhinoScriptRunner(runner);
        when(baseActionLoader.getSahiProxyProperties().isRequestDelayActive()).thenReturn(false);

        doReturn(LoggerFactory.getLogger(this.getClass())).when(testling).getLogger(any(JoinPoint.class));
        doReturn("sig.method()").when(testling).getClassAndMethodAsString(any(JoinPoint.class));

        //test modifcation of timer is disabled
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session, never()).setVariable(anyString(), anyString());
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));

        //test no session available
        when(baseActionLoader.getSahiProxyProperties().isRequestDelayActive()).thenReturn(true);
        when(runner.getSession()).thenReturn(null);
        testling.modifySahiTimer(mock(JoinPoint.class), true);
        testling.modifySahiTimer(mock(JoinPoint.class), false);
        verify(session, never()).setVariable(anyString(), anyString());
        verify(baseActionLoader.getExceptionHandler(), never()).handleException(any(Throwable.class));
    }


    @Test
    public void testDetermineDelay() throws Exception {
        BaseActionLoader loader = BeanLoader.loadBaseActionLoader();
        when(loader.getSahiProxyProperties().getRequestDelayMs()).thenReturn(500);
        when(loader.getActionProperties().getTypeDelay()).thenReturn(0.05);

        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("pasteSomething");
        assertEquals(testling.determineDelay(joinPoint, loader).intValue(), 500);

        when(signature.getName()).thenReturn("typeMasked");
        when(joinPoint.getArgs()).thenReturn(new String[]{"1", "MOD"});
        assertEquals(testling.determineDelay(joinPoint, loader).intValue(), 550);

        when(joinPoint.getArgs()).thenReturn(new String[]{"12characters", "MOD"});
        assertEquals(testling.determineDelay(joinPoint, loader).intValue(), 12 * 550);
    }
}