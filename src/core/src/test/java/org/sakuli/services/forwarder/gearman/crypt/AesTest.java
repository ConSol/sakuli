/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.forwarder.gearman.crypt;

import org.gearman.common.GearmanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author Christoph Deppisch
 */
public class AesTest {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(AesTest.class);

    private final String text = "Secret text to encrypt";

    @BeforeClass
    public void setKeyLength() {
        //set key length to 16 byte in unit tests, so default Java JRE security policy applies
        Aes.keyLength = 16;
    }

    @AfterClass(alwaysRun = true)
    public void restoreKeyLength() {
        //restore default key length
        Aes.keyLength = Aes.DEFAULT_KEY_LENGTH;
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        final String password = "encryptor_secret_key";

        byte[] encrypted = Aes.encrypt(text.trim(), password);
        String decrypted = Aes.decrypt(encrypted, password);

        log.info("Text to Encrypt: " + text);
        log.info("Decrypted : " + decrypted);

        Assert.assertEquals(decrypted, text);
    }

    @Test
    public void testEncryptDecryptShortPassword() throws Exception {
        final String password = "x";

        byte[] encrypted = Aes.encrypt(text.trim(), password);
        String decrypted = Aes.decrypt(encrypted, password);

        log.info("Text to Encrypt: " + text);
        log.info("Decrypted : " + decrypted);

        Assert.assertEquals(decrypted, text);
    }

    @Test
    public void testEncryptDecryptVeryLongPassword() throws Exception {
        final String password = "xyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyxyx";

        byte[] encrypted = Aes.encrypt(text.trim(), password);
        String decrypted = Aes.decrypt(encrypted, password);

        log.info("Text to Encrypt: " + text);
        log.info("Decrypted : " + decrypted);

        Assert.assertEquals(decrypted, text);
    }

    @Test(expectedExceptions = GearmanException.class, expectedExceptionsMessageRegExp = "Error while decrypting.*")
    public void testWrongPassword() throws Exception {
        final String password = "encryptor_secret_key";

        byte[] encrypted = Aes.encrypt(text.trim(), password);
        Aes.decrypt(encrypted, "wrongPassword");
    }

}