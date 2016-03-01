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

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

//Tested in jdk1.8.0_40
public class CertificateChainGeneration {
    public static void main(String[] args) {
        try {
            //Generate ROOT certificate
            String sslAlgorithmus = "RSA";
            CertAndKeyGen keyGen = new CertAndKeyGen(sslAlgorithmus, "SHA1WithRSA", null);
            keyGen.generate(1024);
            PrivateKey rootPrivateKey = keyGen.getPrivateKey();

            X509Certificate rootCertificate = keyGen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 60 * 60);

            //Generate intermediate certificate
            CertAndKeyGen keyGen1 = new CertAndKeyGen(sslAlgorithmus, "SHA1WithRSA", null);
            keyGen1.generate(1024);
            PrivateKey middlePrivateKey = keyGen1.getPrivateKey();

            X509Certificate middleCertificate = keyGen1.getSelfCertificate(new X500Name("CN=MIDDLE"), (long) 365 * 24 * 60 * 60);

            //Generate leaf certificate
            CertAndKeyGen keyGen2 = new CertAndKeyGen(sslAlgorithmus, "SHA1WithRSA", null);
            keyGen2.generate(1024);
            PrivateKey topPrivateKey = keyGen2.getPrivateKey();

            X509Certificate topCertificate = keyGen2.getSelfCertificate(new X500Name("CN=TOP"), (long) 365 * 24 * 60 * 60);

            rootCertificate = createSignedCertificate(rootCertificate, rootCertificate, rootPrivateKey);
            middleCertificate = createSignedCertificate(middleCertificate, rootCertificate, rootPrivateKey);
            topCertificate = createSignedCertificate(topCertificate, middleCertificate, middlePrivateKey);

            X509Certificate[] chain = new X509Certificate[3];
            chain[0] = topCertificate;
            chain[1] = middleCertificate;
            chain[2] = rootCertificate;

            String keyStorPath = Paths.get("sakuli_keystore.jks").toAbsolutePath().toString();
            System.out.println("STORE: " + keyStorPath);
            char[] password = "sahipassword".toCharArray();
            storeKeyAndCertificateChain("test.sakuli.org", password, keyStorPath, topPrivateKey, chain);
//            KeyStore keyStore = KeyStore.getInstance("jks");
//            keyStore.
            KeyStore keyStore = loadAndDisplayChain("test.sakuli.org", password, keyStorPath);
            System.out.println(Arrays.toString(chain));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void storeKeyAndCertificateChain(String alias, char[] password, String keystore, Key key, X509Certificate[] chain) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(null, null);

        keyStore.setKeyEntry(alias, key, password, chain);
        keyStore.store(new FileOutputStream(keystore), password);
    }

    private static KeyStore loadAndDisplayChain(String alias, char[] password, String keystore) throws Exception {
        //Reload the keystore
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(new FileInputStream(keystore), password);

        Key key = keyStore.getKey(alias, password);

        if (key instanceof PrivateKey) {
            System.out.println("Get private key : ");
            System.out.println(key.toString());

            Certificate[] certs = keyStore.getCertificateChain(alias);
            System.out.println("Certificate chain length : " + certs.length);
            for (Certificate cert : certs) {
                System.out.println(cert.toString());
            }
        } else {
            System.out.println("Key is not private key");
        }
        return keyStore;
    }

    private static void clearKeyStore(String alias, char[] password, String keystore) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(new FileInputStream(keystore), password);
        keyStore.deleteEntry(alias);
        keyStore.store(new FileOutputStream(keystore), password);
    }

    private static X509Certificate createSignedCertificate(X509Certificate cetrificate, X509Certificate issuerCertificate, PrivateKey issuerPrivateKey) {
        try {
            Principal issuer = issuerCertificate.getSubjectDN();
            String issuerSigAlg = issuerCertificate.getSigAlgName();

            byte[] inCertBytes = cetrificate.getTBSCertificate();
            X509CertInfo info = new X509CertInfo(inCertBytes);
            info.set(X509CertInfo.ISSUER, (X500Name) issuer);

            //No need to add the BasicContraint for leaf cert
            if (!cetrificate.getSubjectDN().getName().equals("CN=TOP")) {
                CertificateExtensions exts = new CertificateExtensions();
                BasicConstraintsExtension bce = new BasicConstraintsExtension(true, -1);
                exts.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(false, bce.getExtensionValue()));
                info.set(X509CertInfo.EXTENSIONS, exts);
            }

            X509CertImpl outCert = new X509CertImpl(info);
            outCert.sign(issuerPrivateKey, issuerSigAlg);

            return outCert;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}