package jp.nauplius.app.shl.common.util;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import jp.nauplius.app.shl.common.util.PasswordUtil.PasswordStrength;

public class PasswordUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    /**
     * SIMPLE
     */
    @Test
    public final void testCreateRandomTextSimple() {
        IntStream.range(0, 500).forEach(i -> {
            String randomPassword = PasswordUtil.createRandomText(PasswordStrength.SIMPLE);
            // System.out.println(randomPassword);
            assertTrue(8 <= randomPassword.length() && randomPassword.length() <= 10);
            assertTrue(Pattern.compile("^[0-9a-z]*$").matcher(randomPassword).matches());
        });

    }

    /**
     * STRONG
     */
    @Test
    public final void testCreateRandomTextStrong() {
        IntStream.range(0, 500).forEach(i -> {
            String randomPassword = PasswordUtil.createRandomText(PasswordStrength.STRONG);
            // System.out.println(randomPassword);
            assertTrue(10 <= randomPassword.length() && randomPassword.length() <= 12);
            assertTrue(Pattern.compile("^[0-9a-zA-Z]*$").matcher(randomPassword).matches());
        });
    }

    /**
     * HIGH
     */
    @Test
    public final void testCreateRandomTextHigh() {
        IntStream.range(0, 500).forEach(i -> {
            String randomPassword = PasswordUtil.createRandomText(PasswordStrength.HIGH);
            // System.out.println(randomPassword);
            assertTrue(12 <= randomPassword.length() && randomPassword.length() <= 16);
            assertTrue(Pattern.compile("^[!-~]*$").matcher(randomPassword).matches());
        });

    }
}
