package jp.nauplius.app.shl.page.login.bean;

import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import lombok.Data;

@Data
public class LoginResponse {
    private UserInfo userInfo;
    private UserToken userToken;
}
