package de.consol.sakuli.services.receiver.gearman.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ScreenshotDivTest {

    @Test
    public void testGetPayloadString() throws Exception {
        ScreenshotDiv testling = new ScreenshotDiv();
        testling.setWidth("600px");
        testling.setId("test-id");
        testling.setFormat("jpg");
        testling.setBase64screenshot("00001111");

        assertEquals(testling.getPayloadString(), "<div style=\"width:600px\" id=\"test-id\">" +
                "<img style=\"width:98%;border:2px solid gray;display: block;margin-left:auto;margin-right:auto;margin-bottom:4px\" " +
                "src=\"data:image/jpg;base64,00001111\" >" +
                "</div>");
    }
}