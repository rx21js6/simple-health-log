package jp.nauplius.app.shl.ws.bean;

import java.util.List;

import jp.nauplius.app.shl.common.db.model.UserInfo;
import lombok.Getter;
import lombok.Setter;

public class GetUsersResponse {
    @Getter
    @Setter
    private List<UserInfo> userInfos;

    @Getter
    @Setter
    private int count;

    @Getter
    @Setter
    private String message;
}
