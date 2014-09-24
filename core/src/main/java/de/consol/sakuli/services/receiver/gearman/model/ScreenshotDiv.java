package de.consol.sakuli.services.receiver.gearman.model;

/**
 * Represents the scrrenshot div TAG in nagios output.
 *
 * @author tschneck
 *         Date: 05.09.14
 */
public class ScreenshotDiv implements NagiosPayloadString {
    public static final String DEFAULT_SAKULI_SCREENSHOT_DIV_ID = "sakuli_screenshot";
    private static final String DIV_HEADER = "<div style=\"width:%s\" id=\"%s\">";
    private static final String DIV_FOOTER = "</div>";
    private static final String IMG_TAG_HEADER = "<img style=\"width:98%;border:2px solid gray;display: block;margin-left:auto;margin-right:auto;margin-bottom:4px\" src=\"data:image/jpg;base64,";
    private static final String IMG_TAG_FOOTER = "\">";

    /**
     * Width of HTML DIV tag, e.g. "640px"
     */
    private String width;
    /**
     * ID vor the DIV TAG
     */
    private String id;
    /**
     * BASE64 encoded screenshot
     */
    private String base64screenshot;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBase64screenshot() {
        return base64screenshot;
    }

    public void setBase64screenshot(String base64screenshot) {
        this.base64screenshot = base64screenshot;
    }

    @Override
    public String getPayloadString() {
        return String.format(DIV_HEADER, width, id)
                + IMG_TAG_HEADER
                + base64screenshot
                + IMG_TAG_FOOTER
                + DIV_FOOTER;
    }
}
