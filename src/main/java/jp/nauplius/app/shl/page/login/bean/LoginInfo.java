package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import jp.nauplius.app.shl.common.model.UserInfo;
import lombok.Data;

/**
 * セッションに格納するログイン情報
 *
 */
@Named
@SessionScoped
@Data
public class LoginInfo implements Serializable {
    private UserInfo userInfo;
}
