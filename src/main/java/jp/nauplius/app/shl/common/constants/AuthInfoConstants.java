package jp.nauplius.app.shl.common.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 権限情報
 *
 */
public class AuthInfoConstants {
    /**
     * 一般ユーザが遷移可能なページ
     */
    public static final List<String> USER_ACCESSIBLE_PAGES;

    static {
        final String[] tmpAccessiblePages = {
                // index
                "/index.xhtml",
                // タイムアウト
                "/loginTimeout.xhtml",
                // 権限エラー
                "/error/authError.xhtml",
                // 日次入力
                "/contents/record/recordInput.xhtml",
                // 月次表示
                "/contents/record/monthlyRecord.xhtml",
                // カスタム設定
                "/contents/maint/setting/customSetting.xhtml",};
        USER_ACCESSIBLE_PAGES = Collections.unmodifiableList(Arrays.asList(tmpAccessiblePages));
    }
}
