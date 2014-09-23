package de.consol.sakuli.starter.helper;

import de.consol.sakuli.datamodel.properties.SahiProxyProperties;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class SahiProxyTest {
    @Mock
    private SahiProxyProperties props;
    @InjectMocks
    private SahiProxy testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInjectCustomJavaScriptFiles() throws Exception {
        Path configOrig = Paths.get(this.getClass().getResource("mock-files/inject_top.txt").toURI());
        Path config = Paths.get(configOrig.getParent().toString() + File.separator + "inject_top_temp.txt");
        Path target = Paths.get(configOrig.getParent().toString() + File.separator + "inject.js");
        Path source = Paths.get(SahiProxy.class.getResource("inject.js").toURI());
        FileUtils.copyFile(configOrig.toFile(), config.toFile());

        when(props.getSahiJSInjectConfigFile()).thenReturn(config);
        when(props.getSahiJSInjectTargetFile()).thenReturn(target);
        when(props.getSahiJSInjectSourceFile()).thenReturn(source);
        assertFalse(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));

        testling.injectCustomJavaScriptFiles();
        assertTrue(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));
        assertTrue(FileUtils.readFileToString(target.toFile()).contains(
                "Sahi.prototype.originalEx = Sahi.prototype.ex;\n" +
                        "\n" +
                        "Sahi.prototype.ex = function (isStep) {"
        ));

    }

    @Test
    public void testInjectCustomJavaScriptFilesOverridingNewer() throws Exception {
        Path configOrig = Paths.get(this.getClass().getResource("mock-files/inject_top.txt").toURI());
        Path config = Paths.get(configOrig.getParent().toString() + File.separator + "inject_top_temp.txt");
        Path targetOrig = Paths.get(this.getClass().getResource("mock-old-js/inject.js").toURI());
        Path target = Paths.get(targetOrig.getParent().toString() + File.separator + "inject_temp.js");
        FileUtils.copyFile(configOrig.toFile(), config.toFile());
        FileUtils.copyFile(targetOrig.toFile(), target.toFile());

        Files.setLastModifiedTime(target, FileTime.fromMillis(DateTime.now().minusYears(10).getMillis()));
        Path source = Paths.get(SahiProxy.class.getResource("inject.js").toURI());

        when(props.getSahiJSInjectConfigFile()).thenReturn(config);
        when(props.getSahiJSInjectTargetFile()).thenReturn(target);
        when(props.getSahiJSInjectSourceFile()).thenReturn(source);
        assertFalse(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));
        assertEquals(FileUtils.readFileToString(target.toFile()), "//old inject.js");

        testling.injectCustomJavaScriptFiles();
        assertTrue(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));
        assertTrue(FileUtils.readFileToString(target.toFile()).contains(
                "Sahi.prototype.originalEx = Sahi.prototype.ex;\n" +
                        "\n" +
                        "Sahi.prototype.ex = function (isStep) {"
        ));

    }
}