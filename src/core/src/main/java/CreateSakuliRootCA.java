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

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * @author tschneck
 *         Date: 2/29/16
 */
public class CreateSakuliRootCA {

    public static final String CN_ROOT_SAHI_COM = "root.sahi.com";
    public static final String CN_SIGNER_SAHI_COM = "signer.sahi.com";
    private static final long YEARS = 10;

    public static void main(String[] args) {
        try {
            //Generate ROOT certificate
            String sslAlgorithmus = "RSA";
            CertAndKeyGen keyGen = new CertAndKeyGen(sslAlgorithmus, "SHA256WITHRSA", null);
            keyGen.generate(2048);
            PrivateKey rootPrivateKey = keyGen.getPrivateKey();
            X509Certificate rootCertificate = keyGen.getSelfCertificate(new X500Name("CN=" + CN_ROOT_SAHI_COM + ", OU=Sahi, O=Sahi, L=Bangalore, S=Karnataka, C=IN"), (long) YEARS * 365 * 24 * 60 * 60);
            rootCertificate = createSignedCertificate(rootCertificate, rootCertificate, rootPrivateKey);

            //Generate signer certificate
            CertAndKeyGen keyGen1 = new CertAndKeyGen(sslAlgorithmus, "SHA256WITHRSA", null);
            keyGen1.generate(2048);
            PrivateKey signerPrivateKey = keyGen1.getPrivateKey();
            Path signerPath = Paths.get("signer_key").toAbsolutePath();
            OutputStream outputStream = Files.newOutputStream(signerPath);
            IOUtils.write(signerPrivateKey.getEncoded(), outputStream);
            outputStream.close();
            X509Certificate signerCertificate = keyGen.getSelfCertificate(new X500Name("CN=" + CN_SIGNER_SAHI_COM + ", OU=Sahi, O=Sahi, L=Bangalore, S=Karnataka, C=IN"), (long) YEARS * 365 * 24 * 60 * 60);

            signerCertificate = createSignedCertificate(signerCertificate, rootCertificate, rootPrivateKey);

            X509Certificate[] chain = new X509Certificate[2];
            chain[0] = signerCertificate;
            chain[1] = rootCertificate;
            String keyStorPath = Paths.get("sahi_keystore.jks").toAbsolutePath().toString();
            System.out.println("STORE: " + keyStorPath);
            char[] password = "sahipassword".toCharArray();
            storeKeyAndCertificateChain("root.sahi.com", password, keyStorPath, rootPrivateKey, chain);


            byte[] fil = rootCertificate.getEncoded();
            Path cerPath = Paths.get("sahi_DER.cer").toAbsolutePath();
            OutputStream output = Files.newOutputStream(cerPath);
            IOUtils.write(fil, output);
            output.close();
            System.out.println("CER: " + cerPath);

            CreateSakuliSubCert.doSubcers(signerPrivateKey, signerCertificate);
        } catch (Exception e) {
            LoggerFactory.getLogger(CreateSakuliRootCA.class).error("error", e);
        }
    }


    public static void storeKeyAndCertificateChain(String alias, char[] password, String keystore, Key key, X509Certificate[] chain) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(null, null);

        keyStore.setKeyEntry(alias, key, password, chain);
        keyStore.store(new FileOutputStream(keystore), password);
    }

    public static X509Certificate createSignedCertificate(X509Certificate cetrificate, X509Certificate issuerCertificate, PrivateKey issuerPrivateKey) {
        try {
            Principal issuer = issuerCertificate.getSubjectDN();
            String issuerSigAlg = issuerCertificate.getSigAlgName();

            byte[] inCertBytes = cetrificate.getTBSCertificate();
            X509CertInfo info = new X509CertInfo(inCertBytes);
            info.set(X509CertInfo.ISSUER, (X500Name) issuer);

            //No need to add the BasicContraint for leaf cert
            if (Arrays.asList(CN_ROOT_SAHI_COM, CN_SIGNER_SAHI_COM).stream().filter(s -> cetrificate.getSubjectDN().getName().contains(s)).findFirst().isPresent()) {
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

