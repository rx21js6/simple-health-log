package jp.nauplius.app.shl.common.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthInfo {
    private static final String DAILY_RECORD = "/contents/record/dailyRecord.xhtml";
    private static final String INITAL_SETTING = "/contents/initial/initialSetting.xhtml";
    private static final String INITAL_SETTING_COMPLETE = "/contents/initial/initialSettingComplete.xhtml";

    public static final List<String> TARGET_PAGES;
    static {
        List<String> tmpList = new ArrayList<>();
        tmpList.add(DAILY_RECORD);
        tmpList.add(INITAL_SETTING);
        tmpList.add(INITAL_SETTING_COMPLETE);
        TARGET_PAGES = Collections.unmodifiableList(tmpList);
    }
}
