/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.datamodel.actions;

import org.sakuli.BaseTest;
import org.sakuli.exceptions.SakuliCheckedException;
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

    @Test(expectedExceptions = SakuliCheckedException.class, expectedExceptionsMessageRegExp = "Image-File '.*no-existing-path' does not exists!")
    public void testException() throws Exception {
        new ImageLibObject(Paths.get("no-existing-path"));
    }
}