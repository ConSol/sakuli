
## Encryption of a secret
**************************

If some test cases have the need to encrypt a secret, you have to do the following steps:

1. Encrypt your secret with the following command in the command line of your system:

    * unix

        ```
        java -classpath sakuli.jar:lib/* org.sakuli.starter.SakuliStarter -encrypt yourSecrect -interface eth0
        ```

    * windows

         ```
         java -classpath sakuli.jar;lib\* org.sakuli.starter.SakuliStarter -encrypt yourSecrect -interface eth0
         ```

2. Copy the encrypted secret in your clipoard.

3. Paste the encrypted secret in your test case into the function:

    ```
        sakuli.decryptSecret("yourEncryptedSecrect");
    ```