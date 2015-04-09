package org.sakuli.datamodel.actions;

import org.sakuli.BaseTest;
import org.sakuli.exceptions.SakuliException;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 09.04.2015
 */
public class ImageLibObjectTest {

    @Test
    public void testIsValidInputImage() throws Exception {
        assertTrue(ImageLibObject.isValidInputImageFileEnding("dalfa.dfadfkjaifww.png"));
        assertTrue(ImageLibObject.isValidInputImageFileEnding("dalfa.png.eidk.PNG"));
        assertTrue(ImageLibObject.isValidInputImageFileEnding("dalfa.png.eidk.jpg"));
        assertTrue(ImageLibObject.isValidInputImageFileEnding("dalfa.jpg.eidk.JPG"));
        assertFalse(ImageLibObject.isValidInputImageFileEnding("otherbladl.ds"));
    }

    @Test
    public void testCreationJPG() throws Exception {
        Path file = Paths.get(BaseTest.getResource("calc.jpged.jpg", this.getClass()));
        ImageLibObject testling = new ImageLibObject(file);
        assertEquals(testling.getId(), "calc.jpged");
        assertEquals(testling.getImageFile(), file);
        assertNotNull(testling.getPattern());
    }

    @Test
    public void testCreationPNG() throws Exception {
        Path file = Paths.get(BaseTest.getResource("calc.pngfile.PNG", this.getClass()));
        ImageLibObject testling = new ImageLibObject(file);
        assertEquals(testling.getId(), "calc.pngfile");
        assertEquals(testling.getImageFile(), file);
        assertNotNull(testling.getPattern());
    }

    @Test(expectedExceptions = SakuliException.class, expectedExceptionsMessageRegExp = "Image-File '.*no-existing-path' does not exists!")
    public void testException() throws Exception {
        new ImageLibObject(Paths.get("no-existing-path"));
    }
}