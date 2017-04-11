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

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

public class KeyLengthDetector {
    public static void main(String[] args) throws Exception {
        int allowedKeyLength = 0;

        try {
            allowedKeyLength = Cipher.getMaxAllowedKeyLength("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (allowedKeyLength <= 256) {
            throw new Exception("Detect the allowed size of AES keys on the JVM is <= 256. So in that case the Gearman encryption feature can not be supported! To fix it JCE unlimted stregth files are needed.");
        }
        System.out.println("The allowed key length for AES is: " + allowedKeyLength);
    }
}