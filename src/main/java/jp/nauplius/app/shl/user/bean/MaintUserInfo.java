package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Named
@SessionScoped
@Data
public class MaintUserInfo implements Serializable {
    private int id;

    @NotEmpty(message = "入力してください。")
    @Size(min = 5, max = 10)
    @Pattern(regexp = "[a-z]+[a-z0-9_]+", message = "英数小文字で入力してください。")
    private String loginId;

    @NotEmpty(message = "入力してください。")
    @Size(max = 10)
    private String name;

    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Email: 形式が不正です。")
    private String mailAddress;

    private String password;

    private String passwordRetype;

    private boolean passwordChanged;
    private int roleId;
    private boolean newData;
}
