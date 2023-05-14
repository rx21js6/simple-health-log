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

import org.apache.commons.lang3.StringUtils;

import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.db.model.UserInfo;
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
     * 文字列を暗号化してBase64文字列で返す。
     * @param userInfo
     * @param plainText
     * @param keyBytes
     * @param ivBytes
     * @param salt
     * @return
     */
    public String encrypt(UserInfo userInfo, String plainText, byte[] keyBytes, byte[] ivBytes, String salt) {
        String resultText = null;
        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL0.getInt()) {
            resultText = this.encrypt(plainText, keyBytes, ivBytes);
        } else {
            int id = userInfo.getId();
            int sec = userInfo.getCreatedDate().toLocalDateTime().getSecond();
            resultText = this.encrypt(id, sec, plainText, keyBytes, ivBytes, salt);
        }
        return resultText;
    }

    /**
     * 文字列を暗号化してBase64文字列で返す。（新形式）
     * @param id
     * @param sec
     * @param plainText
     * @param keyBytes
     * @param ivBytes
     * @param salt
     * @return
     */
    private String encrypt(int id, int sec, String plainText, byte[] keyBytes, byte[] ivBytes, String salt) {
        sec += 2;
        int loopCount = sec % 5 + 1;
        String tmpText = salt + plainText;
        for (int i = 0; i < loopCount; i++) {
            tmpText = String.valueOf(id) + tmpText;
            tmpText = this.encrypt(tmpText, keyBytes, ivBytes);
        }

        return tmpText;
    }

    /**
     * 文字列を暗号化してBase64文字列で返す。
     * @param text 生パスワード文字列
     * @param keyBytes
     * @param ivBytes
     * @return
     */
    private String encrypt(String text, byte[] keyBytes, byte[] ivBytes) {
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
     * 文字列を復号化
     * @param userInfo
     * @param base64EncryptedText
     * @param keyBytes
     * @param ivBytes
     * @param salt TODO
     * @return
     */
    public String decrypt(UserInfo userInfo, String base64EncryptedText, byte[] keyBytes, byte[] ivBytes, String salt) {
        String resultText = null;
        if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL0.getInt()) {
            // 旧型式
            resultText = this.decrypt(base64EncryptedText, keyBytes, ivBytes);
        } else {
            // 新形式
            int id = userInfo.getId();
            int sec = userInfo.getCreatedDate().toLocalDateTime().getSecond();
            resultText = this.decrypt(id, sec, base64EncryptedText, keyBytes, ivBytes, salt);
        }
        return resultText;
    }

    /**
     * 文字列を復号化（新形式）
     * @param id
     * @param sec
     * @param base64EncryptedText
     * @param keyBytes
     * @param ivBytes
     * @param salt
     * @return
     */
    private String decrypt(int id, int sec, String base64EncryptedText, byte[] keyBytes, byte[] ivBytes, String salt) {
        sec += 2;
        int loopCount = sec % 5 + 1;
        String tmpText = base64EncryptedText;
        for (int i = 0; i < loopCount; i++) {
            tmpText = this.decrypt(tmpText, keyBytes, ivBytes);
            tmpText = tmpText.replaceFirst(String.valueOf(id), StringUtils.EMPTY);
        }
        tmpText = tmpText.replaceFirst(salt, StringUtils.EMPTY);
        return tmpText;
    }

    /**
     * 文字列を復号化
     * @param base64EncryptedText
     * @param keyBytes
     * @param ivBytes
     * @return
     */
    private String decrypt(String base64EncryptedText, byte[] keyBytes, byte[] ivBytes) {
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
    public String decryptOld(String base64EncryptedText, byte[] keyBytes, byte[] ivBytes) {
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
