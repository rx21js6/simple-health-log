package jp.nauplius.app.shl.common.constants;

import java.time.format.DateTimeFormatter;

public class ShlConstants {
    public static final String LOGIN_SESSION_KEY = "LOGIN_SESSION_KEY";

    public static final String PERSISTENCE_UNIT_NAME = "simple-health-log";

    public static final String PERSISTENCE_UNIT_NAME_TEST = PERSISTENCE_UNIT_NAME + "_test";

    public static final String COOKIE_KEY_TOKEN = "token";

    public static final DateTimeFormatter RECORDING_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
}