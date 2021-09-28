package jp.nauplius.app.shl.common.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuthInfo {
    public static final String DAILY_RECORD = "/contents/record/dailyRecord.xhtml";
    private static final String INITAL_SETTING = "/contents/initial/initialSetting.xhtml";
    public static final String INITAL_SETTING_COMPLETE = "/contents/initial/initialSettingComplete.xhtml";

    // 一般ユーザが遷移可能なページ
    private static final String[] TMP_NORMAL_VISIBLE_PAGES = { "/index.xhtml", // index
            "/loginTimeout.xhtml", // タイムアウト
            "/error/authError.xhtml", // 権限エラー
            "/contents/record/recordInput.xhtml", // 日次入力
            "/contents/record/monthlyRecord.xhtml", // 月次表示
            "/contents/maint/settings/customSetting.xhtml", // カスタム設定

    };

    @Deprecated
    public static final List<String> TARGET_PAGES;

    public static final List<String> NORMAL_USER_VISIBLE_PAGES;

    static {
        List<String> tmpList = new ArrayList<>();
        tmpList.add(DAILY_RECORD);
        tmpList.add(INITAL_SETTING);
        tmpList.add(INITAL_SETTING_COMPLETE);
        TARGET_PAGES = Collections.unmodifiableList(tmpList);

        NORMAL_USER_VISIBLE_PAGES = Collections.unmodifiableList(Arrays.asList(TMP_NORMAL_VISIBLE_PAGES));

    }
}
