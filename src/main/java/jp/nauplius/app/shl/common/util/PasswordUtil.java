package jp.nauplius.app.shl.common.util;

import org.apache.commons.lang3.RandomUtils;

public class PasswordUtil {
    public enum PasswordStrength {
        SIMPLE, STRONG, HIGH
    }

    /**
     *
     * @param strength
     * @return
     */
    public static final String createRandomText(PasswordStrength strength) {
        StringBuilder passwordBuilder = new StringBuilder();
        int length = 0;
        StringBuilder builder = new StringBuilder();
        switch (strength) {
        case HIGH:
            // !-~
            for (char c = '!'; c <= '~'; c++) {
                builder.append(c);
            }
            length = RandomUtils.nextInt(12, 17);
            break;
        case STRONG:
            // A-Z
            for (char c = 'A'; c <= 'Z'; c++) {
                builder.append(c);
            }
            // a-z
            for (char c = 'a'; c <= 'z'; c++) {
                builder.append(c);
            }
            // 0-9
            for (char c = '0'; c <= '9'; c++) {
                builder.append(c);
            }
            length = RandomUtils.nextInt(10, 13);
            break;
        default:
            // a-z
            for (char c = 'a'; c <= 'z'; c++) {
                builder.append(c);
            }
            // 0-9
            for (char c = '0'; c <= '9'; c++) {
                builder.append(c);
            }
            length = RandomUtils.nextInt(8, 11);
            break;
        }

        String params = builder.toString();

        for (int i = 0; i < length; i++) {
            passwordBuilder.append(params.charAt((RandomUtils.nextInt(0, params.length()))));
        }

        return passwordBuilder.toString();
    }
}
