/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.javaDSL.selenium.testng;

/**
 * @author tschneck
 * Date: 4/18/17
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Reading files from jar file URL, taking care of nested jar
 * <ul>
 * <li>jar:file:/C:/Temp/single.jar!/foo</li>
 * <li>jar:file:/C:/Temp/outer.jar!/lib/inner.jar!/foo</li>
 * </ul>
 * see  /tmp/sakuli-tmp1638281192281940163/./org/sakuli/ocr/tessdata/lib/eng.cube.lm
 *
 * @author Trung
 */
public class JarReader {

    private static final Logger logger = LoggerFactory.getLogger(JarReader.class);

    public static void read(URL jarUrl, InputStreamCallback callback) throws IOException {
        if (!"jar".equals(jarUrl.getProtocol())) {
            throw new IllegalArgumentException("Jar protocol is expected but get " + jarUrl.getProtocol());
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null");
        }
        String jarPath = jarUrl.getPath().substring(5);
        String[] paths = jarPath.split("!");
        FileInputStream jarFileInputStream = new FileInputStream(paths[0]);
        readStream(jarFileInputStream, paths[0], 1, paths, callback);
    }

    private static void readStream(InputStream jarFileInputStream, String name, int i, String[] paths, InputStreamCallback callback) throws IOException {
        if (i == paths.length) {
            callback.onFile(name, jarFileInputStream);
            return;
        }
        ZipInputStream jarInputStream = new ZipInputStream(jarFileInputStream);
        try {
            ZipEntry jarEntry = null;
            while ((jarEntry = jarInputStream.getNextEntry()) != null) {
                String jarEntryName = "/" + jarEntry.getName();
                if (!jarEntry.isDirectory() && jarEntryName.startsWith(paths[i])) {
                    byte[] byteArray = copyStream(jarInputStream, jarEntry);
                    logger.debug("Entry {} with size {} and data size {}", jarEntryName, jarEntry.getSize(), byteArray.length);
                    readStream(new ByteArrayInputStream(byteArray), jarEntryName, i + 1, paths, callback);
                }
            }
        } finally {
            jarInputStream.close();
        }
    }

    private static byte[] copyStream(InputStream in, ZipEntry entry)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long size = entry.getSize();
        if (size > -1) {
            byte[] buffer = new byte[1024 * 4];
            int n = 0;
            long count = 0;
            while (-1 != (n = in.read(buffer)) && count < size) {
                baos.write(buffer, 0, n);
                count += n;
            }
        } else {
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                baos.write(b);
            }
        }
        baos.close();
        return baos.toByteArray();
    }

    public static interface InputStreamCallback {
        void onFile(String name, InputStream is) throws IOException;
    }
}