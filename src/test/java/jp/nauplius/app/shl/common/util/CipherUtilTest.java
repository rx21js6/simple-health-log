package jp.nauplius.app.shl.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cdiunit.CdiRunner;
import jakarta.inject.Inject;
import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.db.model.UserInfo;

@RunWith(CdiRunner.class)
public class CipherUtilTest {
    @Inject
    private CipherUtil cipherUtil;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateInitialVector() throws NoSuchAlgorithmException {
        byte[] initialVector = this.cipherUtil.createInitialVector();
        assertNotNull(initialVector);
    }

    @Test
    public void testCreateKey() throws NoSuchAlgorithmException {
        byte[] key = this.cipherUtil.createKey();
        assertNotNull(key);
    }

    @Test
    public void testEncryptLevel0() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(10);
        userInfo.setSecurityLevel(SecurityLevel.LEVEL0.getInt());
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2022, 11, 1), LocalTime.of(2, 33, 44));
        userInfo.setCreatedDate(Timestamp.valueOf(localDateTime));
        String plainText = "plain_text";

        byte[] keyBytes = this.cipherUtil.base64StringToBytes("Se/vfRtH4znqe5zlihNhDg==");
        byte[] ivBytes = this.cipherUtil.base64StringToBytes("+iCM/TDi4iefrmGmBrSvfQ==");
        String salt = "abc123xyz";

        String result = this.cipherUtil.encrypt(userInfo, plainText, keyBytes, ivBytes, salt);
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void testEncryptLevel1() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(10);
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt());
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2022, 11, 1), LocalTime.of(2, 33, 44));
        userInfo.setCreatedDate(Timestamp.valueOf(localDateTime));
        String plainText = "plain_text";

        byte[] keyBytes = this.cipherUtil.base64StringToBytes("Se/vfRtH4znqe5zlihNhDg==");
        byte[] ivBytes = this.cipherUtil.base64StringToBytes("+iCM/TDi4iefrmGmBrSvfQ==");
        String salt = "abc123xyz";

        String result = this.cipherUtil.encrypt(userInfo, plainText, keyBytes, ivBytes, salt);
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void testDecryptLevel0() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(10);
        userInfo.setSecurityLevel(SecurityLevel.LEVEL0.getInt());
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2022, 11, 1), LocalTime.of(2, 33, 44));
        userInfo.setCreatedDate(Timestamp.valueOf(localDateTime));
        String encodedText = "T1fjJ/fBlixfi/RzdxYpVg==";

        byte[] keyBytes = this.cipherUtil.base64StringToBytes("Se/vfRtH4znqe5zlihNhDg==");
        byte[] ivBytes = this.cipherUtil.base64StringToBytes("+iCM/TDi4iefrmGmBrSvfQ==");
        String salt = "abc123xyz";

        String result = this.cipherUtil.decrypt(userInfo, encodedText, keyBytes, ivBytes, salt);

        // System.out.println(result);
        assertEquals("plain_text", result);
    }

    @Test
    public void testDecryptLevel1() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(10);
        userInfo.setSecurityLevel(SecurityLevel.LEVEL1.getInt());
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2022, 11, 1), LocalTime.of(2, 33, 44));
        userInfo.setCreatedDate(Timestamp.valueOf(localDateTime));
        String encodedText = "ZY0x1PyekUj4ZzqUqTNM5iVj+PfBYMi/sWMjh2Kj8T/PQFCuC7Slo61fauqJbsTz";

        byte[] keyBytes = this.cipherUtil.base64StringToBytes("Se/vfRtH4znqe5zlihNhDg==");
        byte[] ivBytes = this.cipherUtil.base64StringToBytes("+iCM/TDi4iefrmGmBrSvfQ==");
        String salt = "abc123xyz";

        String result = this.cipherUtil.decrypt(userInfo, encodedText, keyBytes, ivBytes, salt);

        // System.out.println(result);
        assertEquals("plain_text", result);
    }
}
