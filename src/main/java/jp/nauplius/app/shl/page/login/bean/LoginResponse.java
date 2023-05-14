package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;

import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.db.model.UserToken;
import lombok.Data;

@Data
public class LoginResponse implements Serializable {
    private UserInfo userInfo;
    private UserToken userToken;
}
