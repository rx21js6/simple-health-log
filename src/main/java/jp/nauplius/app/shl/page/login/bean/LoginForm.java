package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;

import javax.inject.Named;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Named
@Data
public class LoginForm implements Serializable {
    @NotEmpty(message = "{login.loginid.notEmpty}")
    // @Size(min = 5, max = 10)
    // @Pattern(regexp = "~[a-z]+[a-z0-9_]+$", message = "英数小文字で入力してください。")
    private String loginId;

    @NotEmpty(message = "{login.password.notEmpty}")
    private String password;

    /**
     * ログイン状態維持
     */
    private boolean loggingPersistent;
}
