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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.LoggerFactory;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author tschneck
 *         Date: 2/29/16
 */
public class CreateSakuliSubCert {

    private static final long YEARS = 10;

    public static void doSubcers(PrivateKey signerKey, X509Certificate signerCer) {
        try {
            //Generate ROOT certificate
            String sslAlgorithmus = "RSA";

            //Generate intermediate certificate
            CertAndKeyGen keyGen1 = new CertAndKeyGen(sslAlgorithmus, "SHA256WITHRSA", null);
            keyGen1.generate(2048);
            PrivateKey leafPrivateKey = keyGen1.getPrivateKey();
            String leafAlias = "labs.consol.de";
            X509Certificate leafCert = keyGen1.getSelfCertificate(new X500Name("CN=labs.consol.de,OU=Sahi, O=Sahi, L=Bangalore, S=Karnataka, C=IN"), (long) YEARS * 365 * 24 * 60 * 60);


            String keyStorPath = Paths.get("sahi_keystore.jks").toAbsolutePath().toString();
            System.out.println("STORE: " + keyStorPath);
            char[] password = "sahipassword".toCharArray();
            ImmutablePair<X509Certificate, PrivateKey> rootCaPair = resolveAlias(CreateSakuliRootCA.CN_ROOT_SAHI_COM, password, keyStorPath);
            ImmutablePair<X509Certificate, PrivateKey> signerCaPair = resolveAlias(CreateSakuliRootCA.CN_SIGNER_SAHI_COM, password, keyStorPath);


            leafCert = CreateSakuliRootCA.createSignedCertificate(leafCert, signerCer, signerKey);
//            createSignedCertificate(topCertificate, middleCertificate, middlePrivateKey);

            X509Certificate[] chain = new X509Certificate[3];
            chain[0] = leafCert;
            chain[1] = signerCer;
            chain[2] = rootCaPair.getLeft();


            KeyStore keyStore = getKeyStore(password, keyStorPath);
            keyStore.setKeyEntry(leafAlias, leafPrivateKey, password, chain);
            keyStore.store(new FileOutputStream(keyStorPath), password);

        } catch (Exception e) {
            LoggerFactory.getLogger(CreateSakuliSubCert.class).error("error", e);
        }
    }

    private static ImmutablePair<X509Certificate, PrivateKey> resolveAlias(String alias, char[] password, String keystore) throws Exception {
        KeyStore keyStore = getKeyStore(password, keystore);

        Key key = keyStore.getKey(alias, password);

        if (key instanceof PrivateKey) {
            System.out.println("Get private key : ");
            System.out.println(key.toString());

            Certificate[] certs = keyStore.getCertificateChain(alias);
            System.out.println("Certificate chain length : " + certs.length);
            for (Certificate cert : certs) {
                System.out.println(cert.toString());
            }
            Certificate certificate = keyStore.getCertificate(alias);
            if (certificate instanceof X509Certificate) {
                return new ImmutablePair<>((X509Certificate) certificate, (PrivateKey) key);
            }
        } else {
            System.out.println("Key is not private key ");
        }
        return null;
    }

    private static KeyStore getKeyStore(char[] password, String keystore) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        //Reload the keystore
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(new FileInputStream(keystore), password);
        return keyStore;
    }

}

