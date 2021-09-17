package jp.nauplius.app.shl.common.service;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import jp.nauplius.app.shl.common.constants.AuthInfo;
import jp.nauplius.app.shl.common.constants.UserRole;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

/**
 * 認証サービス
 */
@Named
public class AuthService implements Serializable {
    @Inject
    private LoginInfo loginInfo;

    /**
     * 遷移可否判定
     *
     * @param requestPath 遷移先URL
     * @param contextPath コンテキストURL
     * @return 遷移可能な場合はtrue／不可の場合はfalse
     * @deprecated 廃止
     */
    @Deprecated
    public boolean isVisible(String requestPath, String contextPath) {
        for (String authPage : AuthInfo.TARGET_PAGES) {
            if ((contextPath + authPage).contains(requestPath)) {
                if (!this.loginInfo.getUserInfo().getRoleId().equals(UserRole.ADMIN.getInt())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 遷移可否判定
     *
     * @param requestPath
     * @param servletPath
     * @return
     */
    public boolean isNormalUserVisible(String servletPath) {
        if (Objects.isNull(this.loginInfo.getUserInfo())) {
            return false;
        }

        if (this.loginInfo.getUserInfo().getRoleId().equals(UserRole.ADMIN.getInt())) {
            return true;
        }

        for (String visiblePage : AuthInfo.NORMAL_USER_VISIBLE_PAGES) {
            if (servletPath.startsWith(visiblePage)) {
                return true;
            }
        }
        return false;
    }
}
