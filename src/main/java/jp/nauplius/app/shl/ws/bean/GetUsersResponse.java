package jp.nauplius.app.shl.ws.bean;

import java.util.List;

import jp.nauplius.app.shl.common.model.LoginUser;
import lombok.Getter;
import lombok.Setter;

public class GetUsersResponse {
    @Getter
    @Setter
    private List<LoginUser> loginUsers;

    @Getter
    @Setter
    private int count;

    @Getter
    @Setter
    private String message;
}
