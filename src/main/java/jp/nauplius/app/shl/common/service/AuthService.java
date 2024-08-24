package jp.nauplius.app.shl.common.service;

import java.io.Serializable;
import java.util.Objects;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jp.nauplius.app.shl.common.constants.AuthInfoConstants;
import jp.nauplius.app.shl.common.constants.UserRole;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

/**
 * 認証サービス
 */
@Named
@SessionScoped
public class AuthService implements Serializable {
    @Inject
    private LoginInfo loginInfo;

    /**
     * 遷移可否判定
     *
     * @param requestPath
     * @param servletPath
     * @return
     */
    public boolean isUserAccessible(String servletPath) {
        if (Objects.isNull(this.loginInfo.getUserInfo())) {
            return false;
        }

        if (this.loginInfo.getUserInfo().getRoleId().equals(UserRole.ADMIN.getInt())) {
            return true;
        }

        for (String visiblePage : AuthInfoConstants.USER_ACCESSIBLE_PAGES) {
            if (servletPath.startsWith(visiblePage)) {
                return true;
            }
        }
        return false;
    }
}
