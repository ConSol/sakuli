/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.actions.environment;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Copied form {@link org.sikuli.script.App.Clipboard}.
 *
 * @author tschneck
 *         Date: 16.11.13
 */
public class Clipboard {

    public static final TextType HTML = new TextType("text/html");
    public static final TextType PLAIN = new TextType("text/plain");
    public static final Charset UTF8 = new Charset("UTF-8");
    public static final Charset UTF16 = new Charset("UTF-16");
    public static final Charset UNICODE = new Charset("unicode");
    public static final Charset US_ASCII = new Charset("US-ASCII");
    public static final TransferType READER = new TransferType(Reader.class);
    public static final TransferType INPUT_STREAM = new TransferType(InputStream.class);
    public static final TransferType CHAR_BUFFER = new TransferType(CharBuffer.class);
    public static final TransferType BYTE_BUFFER = new TransferType(ByteBuffer.class);

    private Clipboard() {
    }

    /**
     * Dumps a given text (either String or StringBuffer) into the Clipboard, with a default MIME type
     */
    public static void putText(CharSequence data) {
        StringSelection copy = new StringSelection(data.toString());
        getSystemClipboard().setContents(copy, copy);
    }

    /**
     * Dumps a given text (either String or StringBuffer) into the Clipboard with a specified MIME type
     */
    public static void putText(TextType type, Charset charset, TransferType transferType, CharSequence data) {
        String mimeType = type + "; charset=" + charset + "; class=" + transferType;
        TextTransferable transferable = new TextTransferable(mimeType, data.toString());
        getSystemClipboard().setContents(transferable, transferable);
    }

    public static java.awt.datatransfer.Clipboard getSystemClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    private static class TextTransferable implements Transferable, ClipboardOwner {
        private String data;
        private DataFlavor flavor;

        public TextTransferable(String mimeType, String data) {
            flavor = new DataFlavor(mimeType, "Text");
            this.data = data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{flavor, DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean b = this.flavor.getPrimaryType().equals(flavor.getPrimaryType());
            return b || flavor.equals(DataFlavor.stringFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.isRepresentationClassInputStream()) {
                return new StringReader(data);
            } else if (flavor.isRepresentationClassReader()) {
                return new StringReader(data);
            } else if (flavor.isRepresentationClassCharBuffer()) {
                return CharBuffer.wrap(data);
            } else if (flavor.isRepresentationClassByteBuffer()) {
                return ByteBuffer.wrap(data.getBytes());
            } else if (flavor.equals(DataFlavor.stringFlavor)) {
                return data;
            }
            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, Transferable contents) {
        }
    }

    /**
     * Enumeration for the text type property in MIME types
     */
    public static class TextType {
        private String type;

        private TextType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    /**
     * Enumeration for the charset property in MIME types (UTF-8, UTF-16, etc.)
     */
    public static class Charset {
        private String name;

        private Charset(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Enumeration for the transfert type property in MIME types (InputStream, CharBuffer, etc.)
     */
    public static class TransferType {
        private Class dataClass;

        private TransferType(Class streamClass) {
            this.dataClass = streamClass;
        }

        public Class getDataClass() {
            return dataClass;
        }

        @Override
        public String toString() {
            return dataClass.getName();
        }
    }

}

