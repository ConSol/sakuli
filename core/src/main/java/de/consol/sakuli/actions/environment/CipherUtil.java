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

import de.consol.sakuli.datamodel.properties.ActionProperties;
import de.consol.sakuli.exceptions.SakuliCipherException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.net.NetworkInterface;
import java.util.Enumeration;

import static java.net.NetworkInterface.getNetworkInterfaces;

/**
 * a util class to encrypt and decrypt secrets based on the MAC address of a network interface.
 *
 * @author tschneck
 *         Date: 06.08.13
 */
@Component
public class CipherUtil {
    private static byte[] keyPart1 =
            {
                    0x63, 0x6f, 0x6e, 0x31, 0x33, 0x53, 0x61, 0x6b, 0x53, 0x6f
            };//"con13SakSo"
    private static String algorithm = "AES/ECB/PKCS5Padding";
    private String interfaceName;
    private boolean testMode;
    private byte[] macOfEncryptionInterface;
    private String interfaceLog = "";

    public CipherUtil() {
    }

    @Autowired
    public CipherUtil(ActionProperties cipherProps) {
        interfaceName = cipherProps.getEncryptionInterface();
        testMode = cipherProps.isEncryptionInterfaceTestMode();
    }

    /**
     * Determines a valid default network interfaces, useful in case of testing.
     *
     * @return ethernet interface name
     * @throws Exception
     */
    public static String determineAValidDefaultNetworkInterface() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface anInterface = interfaces.nextElement();
            if (anInterface.getHardwareAddress() != null && anInterface.getHardwareAddress().length == 6) {
                return anInterface.getName();
            }
        }
        throw new Exception("No network interface with a MAC address is present, please check your os settings!");
    }

    /**
     * fetch the local network interfaceLog and reads out the MAC of the chosen encryption interface.
     * Must be called before the methods {@link #encrypt(String)} or {@link #decrypt(String)}.
     *
     * @throws SakuliCipherException for wrong interface names and MACs.
     */
    @PostConstruct
    public void getNetworkInterfaceNames() throws SakuliCipherException {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            interfaceName = checkEthInterfaceName();
            networkInterfaces = getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface anInterface = networkInterfaces.nextElement();
                if (anInterface.getHardwareAddress() != null) {
                    interfaceLog = interfaceLog + "\nNET-Interface " + anInterface.getIndex() + " - Name: " + anInterface.getName()
                            + "\t MAC: " + formatMAC(anInterface.getHardwareAddress())
                            + "\t VirtualAdapter: " + anInterface.isVirtual()
                            + "\t Loopback: " + anInterface.isLoopback()
                            + "\t Desc.: " + anInterface.getDisplayName();
                }
                if (anInterface.getName().equals(interfaceName)) {
                    macOfEncryptionInterface = anInterface.getHardwareAddress();
                }

            }
            if (macOfEncryptionInterface == null) {
                throw new SakuliCipherException("Cannot resolve mac adresse ... please check your config of the property: " + ActionProperties.ENCRYPTION_INTERFACE + "=" + interfaceName, interfaceLog);
            }
        } catch (Exception e) {
            throw new SakuliCipherException(e, interfaceLog);
        }
    }

    /**
     * checks if {@link #testMode} is enabled and returns:
     * <ul>
     * <li>true: the first valid interface at this computer</li>
     * <li>false: the interface name defined at the property {@link ActionProperties#ENCRYPTION_INTERFACE}</li>
     * </ul>
     *
     * @return
     */
    private String checkEthInterfaceName() throws Exception {
        if (testMode) {
            return determineAValidDefaultNetworkInterface();
        }
        return interfaceName;
    }

    private String formatMAC(byte[] mac) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            b.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return b.toString();
    }

    /**
     * Encrypts the secret into a encrypted {@link String}, based on the MAC address of the first network interface of a machine.
     * Therewith it should be secured, that an encrypted secret is only valid on one physical machine.
     *
     * @param strToEncrypt the secret
     * @return a encrypted String, which is coupled to one physical machine
     * @throws SakuliCipherException if the encryption fails.
     */
    public String encrypt(String strToEncrypt) throws SakuliCipherException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            throw new SakuliCipherException(e, interfaceLog);
        }
    }

    /**
     * Decrypts a String to the secret. The decryption must be take place on the same physical machine like the encryption, see {@link #encrypt(String)}.
     *
     * @param strToDecrypt String to encrypt
     * @return the decrypted secret
     * @throws SakuliCipherException if the decryption fails.
     */
    public String decrypt(String strToDecrypt) throws SakuliCipherException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
        } catch (IllegalBlockSizeException e) {
            throw new SakuliCipherException("Maybe this secret hasn't been encrypted correctly! Maybe encrypt it again!", interfaceLog, e);
        } catch (Exception e) {
            throw new SakuliCipherException(e, interfaceLog);
        }
    }

    /**
     * generates the key for encryption from salt (keyPart1) and the MAC address of the choosen interface.
     *
     * @return valid {@link SecretKeySpec}
     */
    private SecretKeySpec getKey() {
        // the length of the MAC address must be 6, to get secrect key length of 16 bytes
        assert (macOfEncryptionInterface.length == 6);
        byte[] keyPar2 = macOfEncryptionInterface;
        byte[] key = new byte[keyPart1.length + keyPar2.length];
        System.arraycopy(keyPart1, 0, key, 0, keyPart1.length);
        System.arraycopy(keyPar2, 0, key, keyPart1.length, keyPar2.length);
        return new SecretKeySpec(key, "AES");
    }

}
