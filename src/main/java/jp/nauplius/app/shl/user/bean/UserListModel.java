package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import lombok.Data;

@Named
@SessionScoped
@Data
public class UserListModel implements Serializable {
    private List<UserInfoListItem> userInfoListItems;

    private int selectedId;
}
