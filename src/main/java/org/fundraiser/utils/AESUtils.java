package org.fundraiser.utils;

import org.fundraiser.config.security.AESConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AESUtils {

    private final AESConfig aesConfig;

    @Autowired
    public AESUtils(AESConfig aesConfig) {
        this.aesConfig = aesConfig;
    }

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private Cipher createCipher(int mode, byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Key key = generateKey();
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, key, gcmParameterSpec);
        return cipher;
    }

    private Key generateKey() {
        String algorithm = "AES";
        return new SecretKeySpec(aesConfig.getSecretKey(), algorithm);
    }

    public String encrypt(String data) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, iv);
        byte[] encVal = cipher.doFinal(data.getBytes());
        byte[] encryptedWithIv = new byte[encVal.length + iv.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encVal, 0, encryptedWithIv, iv.length, encVal.length);
        byte[] encryptedData = Base64.getEncoder().encode(encryptedWithIv);
        return new String(encryptedData, StandardCharsets.UTF_8);
    }

    public String decrypt(String encryptedData) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] iv = Arrays.copyOfRange(decodedValue, 0, GCM_IV_LENGTH);
        byte[] cipherBytes = Arrays.copyOfRange(decodedValue, GCM_IV_LENGTH, decodedValue.length);
        Cipher cipher = createCipher(Cipher.DECRYPT_MODE, iv);
        byte[] decValue = cipher.doFinal(cipherBytes);
        return new String(decValue);
    }

}
