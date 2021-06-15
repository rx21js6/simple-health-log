package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Data;

@ViewScoped
@Named
@Data
public class MaintUserInfo implements Serializable {
    private int id;
    private String loginId;
    private String name;
    private String mailAddress;
    private String password;
    private String passwordRetype;
    private boolean passwordChanged;
    private int roleId;
    private boolean newData;
}
