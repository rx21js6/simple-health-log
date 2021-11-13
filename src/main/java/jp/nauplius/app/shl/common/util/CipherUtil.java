package jp.nauplius.app.shl.common.util;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Named;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;

/**
 * 暗号ユーティリティ
 * @author developer
 *
 */
@Named
public class CipherUtil implements Serializable {
    /**
     * 乱数アルゴリズム
     */
    public static final String RANDOM_ALGORITHM;
    static {
        String OS_NAME = System.getProperty("os.name").toLowerCase();
        // System.out.println(OS_NAME);
        if (OS_NAME.startsWith("windows")) {
            RANDOM_ALGORITHM = "Windows-PRNG";
        } else {
            RANDOM_ALGORITHM = "NativePRNG";
        }
    }
    /**
     * Key生成アルゴリズム（AES: 128bytes）
     */
    public static final String KEY_ALGORITHM = "AES";

    /**
     * 鍵サイズ: 128bytes
     */
    public static final int KEY_SIZE = 128;
    /**
     * Cipher変換方式（AES/GCM/NoPadding: 128bytes）
     */
    public static final String CIPHER_TRANSFORMATION = "AES";

    /**
     * Cipher変換方式（AES/CBC/PKCS5Padding: 128bytes）
     * @deprecated because of weakness.
     */
    @Deprecated
    public static final String CIPHER_TRANSFORMATION_OLD = "AES/CBC/PKCS5Padding";

    /**
     * Initial Vector生成
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] createInitialVector() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    /**
     * Key 生成
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] createKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        keygen.init(KEY_SIZE, random);
        SecretKey key = keygen.generateKey();
        return key.getEncoded();
    }

    /**
     * パスワード暗号化してBase64文字列で返す。
     * @param text 生パスワード文字列
     * @param keyBytes
     * @param ivBytes
     * @return
     */
    public String encrypt(String text, byte[] keyBytes, byte[] ivBytes) {
        try {
            byte[] textBytes = text.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            // IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // 暗号化
            byte[] encryptedBytes = cipher.doFinal(textBytes);
            // Base64に変換して返却
            return byteToBase64String(encryptedBytes);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 暗号化パスワード復号
     * @param base64EncryptedText
     * @param keyBytes
     * @param ivBytes
     * @return
     */
    public String decrypt(String base64EncryptedText, byte[] keyBytes, byte[] ivBytes) {
        try {
            byte[] encryptedBytes = base64StringToBytes(base64EncryptedText);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // 復号化
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 文字列に変換して返却
            return new String(decryptedBytes);

        } catch (GeneralSecurityException e) {
            throw new SimpleHealthLogException(e);
        } finally {

        }

    }

    /**
     * 旧型式でデコード
     * @param base64EncryptedText
     * @param keyBytes
     * @param ivBytes
     * @return
     */
    public  String decryptOld(String base64EncryptedText, byte[] keyBytes, byte[] ivBytes) {
        try {
            byte[] encryptedBytes = base64StringToBytes(base64EncryptedText);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_OLD);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // 復号化
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 文字列に変換して返却
            return new String(decryptedBytes);

        } catch (GeneralSecurityException e) {
            throw new SimpleHealthLogException(e);
        } finally {

        }
    }

    /**
     * バイト列をBase64文字列に変換
     * @param bytes
     * @return
     */
    public String byteToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64文字列をバイト列に変換
     * @param base64String
     * @return
     */
    public byte[] base64StringToBytes(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

}
