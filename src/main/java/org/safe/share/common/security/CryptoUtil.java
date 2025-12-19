package org.safe.share.common.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class CryptoUtil {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128;
    private static final byte[] IV = "SafeShareInitVec".getBytes(); // 16 bytes

    public static byte[] encrypt(byte[] data, byte[] keyBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, IV));
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encrypted, byte[] keyBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, IV));
        return cipher.doFinal(encrypted);
    }

    public static byte[] deriveKey(String secret) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        return Arrays.copyOf(sha.digest(secret.getBytes()), 32);
    }
}
