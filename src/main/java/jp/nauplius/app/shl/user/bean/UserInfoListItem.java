package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserInfoListItem implements Serializable {
    private int id;
    private String loginId;
    private String name;
    private String mailAddress;
    private int roleId;
}
