package org.sakuli.datamodel.actions;

import org.sakuli.BaseTest;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * @author tschneck
 *         Date: 09.04.2015
 */
public class ImageLibTest {

    @Test
    public void testAddImagesFromFolder() throws Exception {
        Path path = Paths.get(BaseTest.getResource(".", this.getClass()));
        ImageLib imageLib = new ImageLib();
        imageLib.addImagesFromFolder(path);

        List<String> strings = Arrays.asList("calc.jpged.jpg", "calc.jpged", "calc.pngfile.PNG", "calc.pngfile");
        for (String s : strings) {
            assertNotNull(imageLib.getImage(s));
            assertNotNull(imageLib.getPattern(s));
        }
    }
}