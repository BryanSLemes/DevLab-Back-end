package com.bryanmzili.DevLab.security;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncryptionDecryptionUtil implements PasswordEncoder {

    private final AES256TextEncryptor textEncryptor;

    private String privateKeyPEM;

    public EncryptionDecryptionUtil(String encryptionKey) {
        textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(encryptionKey);
    }

    public EncryptionDecryptionUtil() {
        textEncryptor = new AES256TextEncryptor();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return textEncryptor.encrypt(rawPassword.toString());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }

    @Override
    public boolean matches(CharSequence senhaDescriptografada, String senhaCriptografada) {
        try {
            return senhaDescriptografada.equals(decrypt(senhaCriptografada));
        } catch (Exception e) {
            return false;
        }
    }

    public String decrypt(String encryptedData) {
        return textEncryptor.decrypt(encryptedData);
    }

    public String decryptMessage(List<String> encryptedMessage) throws Exception {
        try {
            PrivateKey privateKey = getPrivateKeyFromPEM(privateKeyPEM);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            String completeDecryptedMessage = "";

            for (String mensagem : encryptedMessage) {
                completeDecryptedMessage += new String(cipher.doFinal(Base64.getDecoder().decode(mensagem)));
            }

            return completeDecryptedMessage;
        } catch (Exception e) {
            return "Dados criptografados inválidos";
        }
    }

    private PrivateKey getPrivateKeyFromPEM(String privateKeyPEM) throws Exception {
        PemReader pemReader = new PemReader(new StringReader(privateKeyPEM));
        PemObject pemObject = pemReader.readPemObject();
        byte[] pemContent = pemObject.getContent();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public void setPrivateKeyPEM(String privateKey) {
        privateKeyPEM = privateKey;
    }
}
